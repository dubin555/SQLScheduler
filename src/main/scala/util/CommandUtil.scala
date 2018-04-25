package util

import config.Constants
import messages.Parameters

import scala.util.matching.Regex
import scala.util.matching.Regex.Groups

/**
  * Util of command related.
  */
object CommandUtil {

  def renderCommand(command: String, parameters: Parameters): String = {
    Constants.VARIABLE_SIGN.r.replaceAllIn(command, _ match {
      case Groups(parameter) => parameters.getParameter(parameter)
    })
  }

  def renderCommand(pattern: Regex, command: String, parameters: Parameters): String = {
    pattern.replaceAllIn(command, _ match {
      case Groups(parameter) => parameters.getParameter(parameter)
    })
  }

}
