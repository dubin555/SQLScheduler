package util
import web.AcceptedJob

/**
  * Implement of RingSet with element type of AcceptedJob.
  */
class TaskRingSet[T <: AcceptedJob] extends RingSet[T] {

  /**
    * Find the target slot of the task which should be put info.
    */
  override def getSelfSlot(acceptedJob: T): Int = {
    val stepsForward = acceptedJob.freq
    val slots = this.getSlotsCount()
    val currentSlot = this.getCurrentSlot
    return (currentSlot + stepsForward) % slots;
  }

  /**
    * Remove a job base on jobId
    */
  def removeAcceptedJob(jobId: String) = {
    this.remove(jobId)
  }

  /**
    * Implement the getJobId
    */
  override def getJobId(t: T): String = {
    t.jobId
  }

  /**
    * Implement the getCategory
    */
  override def getCategory(t: T): String = {
    t.category
  }
}
