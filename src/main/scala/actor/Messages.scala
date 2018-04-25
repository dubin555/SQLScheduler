package actor

import messages.Parameters
import web.AcceptedJob

/**
  * Message define between the Actor.
  */
object Messages {

  // messages for Job manager actor
  case class AddJob(acceptedJob: AcceptedJob)
  case class AddJobInitial(acceptedJob: AcceptedJob)
  case class DeleteJob(jobId: String)
  case class DeleteCategory(category: String)
  case class Tick()
  case class Forward(step: Int)

  // messages for job worker
  sealed trait JobRequest

  case class ShellJobRaw(command: String, output: String) extends JobRequest
  case class ShellJob(shellJobRaw: ShellJobRaw, parameters: Parameters) extends JobRequest

  case class PhoenixJobRaw(SQLQuery: String, jdbc: String, output: Seq[String]) extends JobRequest
  case class PhoenixJob(phoenixJobRaw: PhoenixJobRaw, parameters: Parameters) extends JobRequest

  case class JobResult(parameters: Parameters)

}
