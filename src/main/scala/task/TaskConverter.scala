package task

import actor.Messages.{JobRequest, PhoenixJobRaw, ShellJobRaw}
import define.TaskDefine
import util.Logging

/**
  * Convert the TaskDefine to the specified job.
  */
object TaskConverter extends Logging {

  def toInternalJob(taskDefine: TaskDefine): JobRequest = {

    try {
      if (taskDefine.jobType.equalsIgnoreCase("shell")) {
        if (taskDefine.outputs.size > 0) {
          ShellJobRaw(taskDefine.command, taskDefine.outputs(0))
        } else {
          ShellJobRaw(taskDefine.command, "")
        }
      }
      else if (taskDefine.jobType.equalsIgnoreCase("phoenix")) PhoenixJobRaw(taskDefine.sql, taskDefine.jdbc, taskDefine.outputs)
      else null
    } catch {
      case e: Throwable => {
        logError(e.getMessage)
        null
      }
    }

  }

}
