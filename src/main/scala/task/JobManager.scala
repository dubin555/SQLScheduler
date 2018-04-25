package task

import java.util.concurrent.{Executors, TimeUnit}

import actor.Messages._
import akka.actor.ActorRef
import messages.Parameters
import util.{JsonUtil, Logging, TimeUtil}
import web.AcceptedJob
import akka.pattern.ask
import akka.actor._
import akka.util.Timeout
import dao.JobDAO
import define.TaskDefine

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import config.Constants._

/**
  * Manager to manager all the Actor, and communication.
  */
class JobManager(val shellActor: ActorRef,
                  val phoenixActor: ActorRef,
                  val jobsManagerActor: ActorRef,
                  val jobDAO: JobDAO,
                  val taskBus: TaskBus[AcceptedJob]) extends Logging {


  implicit val timeout = Timeout(100 seconds)

  def start() = {
    logInfo("Job Manager start")
    logInfo("Job Manager init")
    init()
    val size = CONCURRENCY_WORKERS
    val es = Executors.newFixedThreadPool(size)
    for (_ <- 0 until size) {
      es.submit(new worker(taskBus))
    }
    tickOneMinuteAlways()
  }

  /**
    * Put jobs into RingSet and Set the slot correct
    */
  private def init() = {
    this.loadAllJobs()
    val minuteOfDay = TimeUtil.getCurrentMinute()
    logInfo(s"Job Manager move steps: $minuteOfDay, and send message")
    jobsManagerActor ! Forward(minuteOfDay)
  }

  /**
    * Always tick every minutes
    */
  private def tickOneMinuteAlways() = {
    val es = Executors.newScheduledThreadPool(1)
    es.scheduleAtFixedRate(new Runnable {
      override def run(): Unit = {
        logInfo("Tick...")
        jobsManagerActor ! Tick()
      }},
      0,
      1,
      TimeUnit.MINUTES)
  }

  /**
    * Put all the jobs in Database into the RingSet
    */
  private def loadAllJobs() = {
    val allJobs = jobDAO.getAllJobs().acceptedJobs
    logInfo(s"load all jobs -> $allJobs")
    for (job <- allJobs) {
      jobsManagerActor ! AddJobInitial(job)
    }
  }

  /**
    * Worker to get the task from bus and set to the Actor
    */
  class worker(val taskBus: TaskBus[AcceptedJob]) extends Runnable {

    def toInternalJob(taskDefine: TaskDefine): JobRequest = {

      try {
        if (taskDefine.jobType.equalsIgnoreCase("shell")) ShellJobRaw(taskDefine.command, taskDefine.outputs(0))
        else if (taskDefine.jobType.equalsIgnoreCase("phoenix")) PhoenixJobRaw(taskDefine.sql, taskDefine.jdbc, taskDefine.outputs)
        else null
      } catch {
        case e: Throwable => {
          logError(e.getMessage)
          null
        }
      }

    }

    override def run(): Unit = {
      while (true) {
        val task = taskBus.take()
        logInfo(s"Worker: Get a task $task")
        if (task != null) {

          val jobs: Array[JobRequest] = JsonUtil.getJsonTasksDefineFromJson(task.jobs).tasks.map(task => TaskConverter.toInternalJob(task))

          logInfo(s"Analysis the task string, $jobs")
          val parameters = Parameters()

          for (job <- jobs) {
            job match {
              case shellJobRaw @ ShellJobRaw(_,_) => {
                logInfo(s"execute the shell Job, $shellJobRaw, $parameters")
                val additionalParametersFuture: Future[Parameters] = (shellActor ? ShellJob(shellJobRaw, parameters)).mapTo[Parameters]
                val additionalParameters: Parameters = Await.result(additionalParametersFuture, Duration.Inf)
                logInfo(s"get the shell result, $additionalParameters")
                parameters.putAll(additionalParameters)
                logInfo(s"Final result of shell, $parameters")
              }
              case phoenixJobRaw @ PhoenixJobRaw(_,_,_) => {
                logInfo(s"execute the phoenix Job, $phoenixJobRaw, $parameters")
                val additionalParametersFuture: Future[Parameters] = (phoenixActor ? PhoenixJob(phoenixJobRaw, parameters)).mapTo[Parameters]
                val additionalParameters: Parameters = Await.result(additionalParametersFuture, Duration.Inf)
                logInfo(s"get the phoenix result, $additionalParameters")
                parameters.putAll(additionalParameters)
                logInfo(s"Final result of phoenix, $parameters")
              }
            }
          }
        }

      }
    }
  }

}

/**
  * Builder pattern for JobsManager
  */
class JobsManagerBuilder() {
  var shellActor: ActorRef = _
  var phoenixActor: ActorRef = _
  var jobsManagerActor: ActorRef = _
  var jobDAO: JobDAO = _
  var taskBus: TaskBus[AcceptedJob] = _

  def setShellActor(shellActor: ActorRef): JobsManagerBuilder = {
    this.shellActor = shellActor
    this
  }

  def setPhoenixActor(phoenixActor: ActorRef): JobsManagerBuilder = {
    this.phoenixActor = phoenixActor
    this
  }

  def setJobsManagerActor(jobsManagerActor: ActorRef): JobsManagerBuilder = {
    this.jobsManagerActor = jobsManagerActor
    this
  }

  def setJobDAO(jobDAO: JobDAO): JobsManagerBuilder = {
    this.jobDAO = jobDAO
    this
  }

  def setTaskBus(taskBus: TaskBus[AcceptedJob]): JobsManagerBuilder = {
    this.taskBus = taskBus
    this
  }

  def createJobManager(): JobManager = {
    new JobManager(
      shellActor,
      phoenixActor,
      jobsManagerActor,
      jobDAO,
      taskBus
    )
  }
}
