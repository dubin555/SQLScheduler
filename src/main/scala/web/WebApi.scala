package web
import actor.{JobsManagerActor, PhoenixActor, ShellActor}
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.routing.RoundRobinPool
import akka.stream.ActorMaterializer
import config.Constants._
import dao.JobSqlDAO
import task.{JobsManagerBuilder, TaskBus}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Main Class of the Application.
  * @todo make some change to be configurable.
  */
object WebApi extends App with WebRoutes {

  override implicit def system: ActorSystem = ActorSystem("JobsAkkaHttpServer")
  implicit val materializer = ActorMaterializer()

  // Make it configurable later
  val jobDAO = new JobSqlDAO()

  val shellActor: ActorRef =
    system.actorOf(
      Props.create(classOf[ShellActor])
        .withRouter(new RoundRobinPool(CONCURRENCY_SHELL))
    )

  val phoenixActor: ActorRef =
    system.actorOf(
      Props.create(classOf[PhoenixActor])
        .withRouter(new RoundRobinPool(CONCURRENCY_PHOENIX))
    )

  val taskBus = new TaskBus[AcceptedJob]()

  // only one jobsManagerActor
  val jobsManagerActor = system.actorOf(Props(classOf[JobsManagerActor], taskBus), name = "jobsManagerActor")

  val jobManager = new JobsManagerBuilder()
    .setShellActor(shellActor)
    .setPhoenixActor(phoenixActor)
    .setJobsManagerActor(jobsManagerActor)
    .setJobDAO(jobDAO)
    .setTaskBus(taskBus)
    .createJobManager()

  jobManager.start()

  override def jobsWebActor: ActorRef = system.actorOf(Props(classOf[JobsWebActor], jobDAO, jobsManagerActor), "jobsWebActor")

  lazy val routes: Route = webRoutes

  Http().bindAndHandle(routes, BIND_ADDRESS, BIND_PORT)

  // Run forever...
  Await.result(system.whenTerminated, Duration.Inf)
}
