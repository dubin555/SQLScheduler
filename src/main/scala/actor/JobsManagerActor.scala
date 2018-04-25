package actor

import akka.actor.Actor
import util.{Logging, TaskRingSet}
import web.AcceptedJob
import Messages._
import task.TaskBus

import scala.collection.JavaConversions._

/**
  * Actor to control the RingSet.
  */
class JobsManagerActor(val taskBus: TaskBus[AcceptedJob]) extends Actor with Logging {

  val taskRingSet: TaskRingSet[AcceptedJob] = new TaskRingSet[AcceptedJob]()

  override def receive: Receive = {

    // add job to this ring
    case AddJob(acceptedJob) => {
      logInfo(s"JobsManagerActor received AddJob, ${acceptedJob}")
      taskRingSet.add(acceptedJob)
    }

    case AddJobInitial(acceptedJob) => {
      logInfo(s"JobsManagerActor received AddJobInitial, $acceptedJob")
      taskRingSet.add(acceptedJob.startTime, acceptedJob)
    }

    case DeleteJob(jobId) => {
      taskRingSet.remove(jobId)
    }

    case DeleteCategory(category) => {
      taskRingSet.removeCategory(category)
    }

    case Tick() => {
      val tasksToBeFired = taskRingSet.tick()
      for (task <- tasksToBeFired) {
        taskBus.add(task)
      }

      logInfo(s"taskBus size -> ${taskBus.size()}")

    }

    case Forward(step) => {
      taskRingSet.forward(step)
    }
  }
}
