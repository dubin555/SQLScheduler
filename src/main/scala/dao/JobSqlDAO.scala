package dao

import slick.driver.MySQLDriver.api._
import web.{AcceptedJob, AcceptedJobs}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.duration.Duration
import slick.lifted.Shape._
import util.Logging

/**
  * Implement of JobDAO
  */
class SQLSchedulerJobs(tag: Tag) extends Table[AcceptedJob](tag, "SQLSchedulerJobs") {
  def jobId = column[String]("JOBID", O.PrimaryKey, O.AutoInc)
  def startTime = column[Int]("STARTTIME")
  def freq = column[Int]("FREQ")
  def category = column[String]("CATEGORY")
  def tasks = column[String]("TASKS", O.SqlType("TEXT"))

  def * = (jobId, startTime, freq, category, tasks) <> (AcceptedJob.tupled, AcceptedJob.unapply)
}

class JobSqlDAO extends JobDAO with Logging {

  implicit val timeout = Duration(30, SECONDS)

  val sqlSchedulerJobs = TableQuery[SQLSchedulerJobs]

  val db = Database.forConfig("mysqlDB")

  /**
    * Create
    */
  override def createJob(acceptedJob: AcceptedJob): Int = {
    logInfo(s"SQL DAO: try createJob $acceptedJob")
    Await.result(db.run(sqlSchedulerJobs.insertOrUpdate(acceptedJob)), timeout)
  }

  /**
    * Delete
    */
  override def deleteJob(jobId: String): Int = {
    logInfo(s"SQL DAO: try deleteJob $jobId")
    Await.result(db.run(sqlSchedulerJobs.filter(_.jobId === jobId).delete), timeout)
  }

  /**
    * Get
    */
  override def getJob(jobId: String): Option[AcceptedJob] = {
    logInfo(s"SQL DAO: try getJob $jobId")
    Await.result(db.run(sqlSchedulerJobs.filter(_.jobId === jobId).result.headOption), timeout)
  }

  /**
    * Get the jobs belong to the category
    */
  override def getCategory(category: String): AcceptedJobs = {
    logInfo(s"SQL DAO: try getCategory $category")
    val res = Await.result(db.run(sqlSchedulerJobs.filter(_.category === category).result), timeout)
    AcceptedJobs(res)
  }

  /**
    * Delete the jobs belong to the category
    */
  override def deleteCategory(category: String): Int = {
    logInfo(s"SQL DAO: try deleteCategory $category")
    Await.result(db.run(sqlSchedulerJobs.filter(_.category === category).delete), timeout)
  }

  /**
    * Get all Jobs
    */
  override def getAllJobs(): AcceptedJobs = {
    logInfo(s"SQL DAO: try getAllJobs")
    val res = Await.result(db.run(sqlSchedulerJobs.result), timeout)
    AcceptedJobs(res)
  }

}
