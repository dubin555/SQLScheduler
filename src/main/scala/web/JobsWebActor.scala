package web

import actor.Messages.{AddJobInitial, DeleteCategory, DeleteJob}
import akka.actor.{Actor, ActorRef}
import dao.JobDAO
import util.Logging
import web.JobsWebActor._

/**
  * Actor to deal with the rest API.
  * Interact with DAO and jobsManagerActor.
  * The Database and tasks running may differ if the code crush, should be monitor.
  */

// Raw job, no jobId
final case class Job(startTime: Int, freq: Int, category: String, jobs: String)
// Job already accepted, have jobId
final case class AcceptedJob(jobId: String, startTime: Int, freq: Int, category: String, jobs: String)
// Seq
final case class AcceptedJobs(acceptedJobs: Seq[AcceptedJob])

class JobsWebActor(jobDAO: JobDAO, jobsManagerActor: ActorRef) extends Actor with Logging {

  override def receive: Receive = {

    // Create Job
    case CreateJobRequest(job) => {
      val jobId = java.util.UUID.randomUUID().toString
      val acceptedJob = AcceptedJob(jobId, job.startTime, job.freq, job.category, job.jobs)
      try {
        logInfo(s"Web Actor try createJob $acceptedJob")
        jobDAO.createJob(acceptedJob)

        // Add job to the ringSet
        jobsManagerActor ! AddJobInitial(acceptedJob)

        sender() ! CreateJobResponse(s"${jobId} create success!")
      } catch {
        case e: Throwable => {
          logError(s"Web Actor createJob failed, $acceptedJob, ${e.getStackTrace}, ${e.getMessage}")
          sender() ! CreateJobResponse("create job failed")
        }
      }
    }
      // Delete Job
    case DeleteJobRequest(jobId) => {
      try {
        logInfo(s"Web Actor try deleteJob $jobId")
        val count = jobDAO.deleteJob(jobId)

        // Delete job in the ringSet
        jobsManagerActor ! DeleteJob(jobId)

        sender() ! DeleteJobResponse(s"${jobId} delete success, ${count} job deleted !")
      } catch {
        case e: Throwable => {
          logError(s"Web Actor deleteJob failed, $jobId, ${e.getStackTrace}, ${e.getMessage}")
          sender() ! DeleteJobResponse("delete job failed")
        }
      }
    }

      // Get Job
    case GetJobRequest(jobId) => {
      try {
        logInfo(s"Web Actor try getJob $jobId")
        val job = jobDAO.getJob(jobId)
        sender() ! GetJobResponse(job)
      } catch {
        case e: Throwable => {
          logError(s"Web Actor getJob failed, $jobId, ${e.getStackTrace}, ${e.getMessage}")
          sender() ! GetJobResponse(None)
        }
      }
    }
      // Get category
    case GetCategoryRequest(category) => {
      try {
        logInfo(s"Web Actor try getCategory $category")
        val jobs = jobDAO.getCategory(category)
        sender() ! GetCategoryResponse(jobs)
      } catch {
        case e: Throwable => {
          logError(s"Web Actor getCategory failed, $category, ${e.getStackTrace}, ${e.getMessage}")
          sender() ! GetCategoryResponse(AcceptedJobs(Seq()))
        }
      }
    }

      // Delete category
    case DeleteCategoryRequest(category) => {
      try {
        logInfo(s"Web Actor try deleteCategory $category")
        val count = jobDAO.deleteCategory(category)

        // Delete category in the ringSet
        jobsManagerActor ! DeleteCategory(category)

        sender() ! DeleteCategoryResponse(s"${category} category deleted, ${count} jobs delete !")
      } catch {
        case e: Throwable => {
          logError(s"Web Actor deleteCategory failed, $category, ${e.getStackTrace}, ${e.getMessage}")
          sender() ! DeleteCategoryResponse(s"delete category:$category fail !")
        }
      }
    }
      // Get all jobs
    case GetAllJobsRequest() => {
      try {
        logInfo(s"Web Actor try get all Jobs")
        val jobs = jobDAO.getAllJobs()
        sender() ! GetAllJobsResponse(jobs)
      } catch {
        case e: Throwable => {
          logError(s"Web Actor get all jobs failed, ${e.getStackTrace}, ${e.getMessage}")
          sender() ! GetAllJobsResponse(AcceptedJobs(Seq()))
        }
      }
    }
  }
}

object JobsWebActor {

  // Abstraction of the request and response
  final case class CreateJobRequest(job: Job)
  final case class CreateJobResponse(detail: String)

  final case class DeleteJobRequest(jobId: String)
  final case class DeleteJobResponse(result: String)

  final case class GetJobRequest(jobId: String)
  final case class GetJobResponse(acceptedJobOption: Option[AcceptedJob])

  final case class GetAllJobsRequest()
  final case class GetAllJobsResponse(allJobs: AcceptedJobs)

  final case class GetCategoryRequest(category: String)
  final case class GetCategoryResponse(jobsInCategory: AcceptedJobs)

  final case class DeleteCategoryRequest(category: String)
  final case class DeleteCategoryResponse(detail: String)

  final case class StartRequest()
  final case class StopRequest()

}
