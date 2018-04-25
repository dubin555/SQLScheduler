package web

import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
import akka.pattern.ask
import akka.util.Timeout

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.post
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.directives.PathDirectives.path

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

import spray.json.JsValue

import util.JsonUtil
import web.JobsWebActor._

/**
  * The router for the rest API
  *
  * /jobs                   get
  * /job/jobId              get/delete/post
  * /category/category      get/delete
  *
  */
trait WebRoutes extends JsonSupport {

  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[WebRoutes])

  // Actor to deal with the message from the router
  def jobsWebActor: ActorRef

  // timeout setting for this web actor
  implicit lazy val timeout = Timeout(10.seconds)

  // Total routes for this rest API
  lazy val webRoutes: Route = jobsRoutes ~ createRoutes ~ categoryRoutes ~jobRoutes

  // route for get /jobs
  lazy val jobsRoutes: Route =
    // get jobs request
    get {
      path("jobs") {
        val getAllJobsResponse: Future[GetAllJobsResponse] = (jobsWebActor ? GetAllJobsRequest()).mapTo[GetAllJobsResponse]
        complete(Await.result(getAllJobsResponse, Duration.Inf).allJobs)
      }
    }

  // route for post /job
  lazy val createRoutes: Route =
    post {
      path("job") {
        entity(as[JsValue]) { job =>
          val jobStr = job.toString

          // job json define not reasonable
          if (!JsonUtil.isJobRequestJsonReasonable(jobStr)) complete("job json define not reasonable")

          val jobJson = JsonUtil.getJsonTasksDefineFromJson(jobStr)
          val notAcceptedJob = Job(jobJson.starttime, jobJson.freq, jobJson.category, jobStr)
          val createJobResponse: Future[CreateJobResponse] = (jobsWebActor ? CreateJobRequest(notAcceptedJob)).mapTo[CreateJobResponse]

          complete(Await.result(createJobResponse, Duration.Inf).detail)
        }
      }
    }

  // route for get and delete /job/jodId
  lazy val jobRoutes: Route =
    pathPrefix("job") {
      path(Segment) {jobId =>
        get {
          val getJobResponse: Future[GetJobResponse] = (jobsWebActor ? GetJobRequest(jobId)).mapTo[GetJobResponse]
          val jobResponse = Await.result(getJobResponse, Duration.Inf)
          complete(jobResponse.acceptedJobOption)
        } ~
        delete {
          val deleteJobResponse: Future[DeleteJobResponse] = (jobsWebActor ? DeleteJobRequest(jobId)).mapTo[DeleteJobResponse]

          complete(Await.result(deleteJobResponse, Duration.Inf).result)
        }
      }
    }

  // route for get and delete /category/categoryName
  lazy val categoryRoutes: Route =
    pathPrefix("category") {
      path(Segment) {category =>
        get {
          val getCategoryResponse: Future[GetCategoryResponse] = (jobsWebActor ? GetCategoryRequest(category)).mapTo[GetCategoryResponse]
          complete(Await.result(getCategoryResponse, Duration.Inf).jobsInCategory)
        } ~
        delete {
          val deleteCategoryResponse: Future[DeleteCategoryResponse] = (jobsWebActor ? DeleteCategoryRequest(category)).mapTo[DeleteCategoryResponse]

          complete(Await.result(deleteCategoryResponse, Duration.Inf).detail)
        }
      }
    }


}
