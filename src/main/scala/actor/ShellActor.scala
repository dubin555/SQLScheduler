package actor

import akka.actor.Actor
import actor.Messages.ShellJob
import messages.Parameters
import util.{CommandUtil, Logging}

import sys.process._

/**
  * Actor to execute Shell Job, return the result into Parameters.
  */
class ShellActor extends Actor with Logging {

  override def receive: Receive = {
    case ShellJob(shellJobRaw, parameters) => {
      logInfo(s"raw command ${shellJobRaw.command}")
      val actualCommand = CommandUtil.renderCommand(shellJobRaw.command, parameters)
      logInfo(s"decorated command $actualCommand")
      val value = actualCommand !!
      val res: Parameters = Parameters()
      res.addParameter(shellJobRaw.output, value.trim)
      logInfo(s"shell get result -> $res")
      sender() ! res
    }
  }

}
