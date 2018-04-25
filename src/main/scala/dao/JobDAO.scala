package dao

import org.slf4j.LoggerFactory
import web.{AcceptedJob, AcceptedJobs}


/**
  * JobDAO, should be implemented.
  */
object JobDAO {

  private val logger = LoggerFactory.getLogger(classOf[JobDAO])

}

trait JobDAO {

  /**
    * Create
    */
  def createJob(job: AcceptedJob): Int

  /**
    * Delete
    */
  def deleteJob(jobId: String): Int

  /**
    * Get
    */
  def getJob(jobId: String): Option[AcceptedJob]

  /**
    * Get the jobs belong to the category
    */
  def getCategory(category: String): AcceptedJobs

  /**
    * Delete the jobs belong to the category
    */
  def deleteCategory(category: String): Int

  /**
    * Get all jobs
    */
  def getAllJobs(): AcceptedJobs

}
