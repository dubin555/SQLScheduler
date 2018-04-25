package messages

import scala.collection.mutable
import scala.collection.mutable.Map

/**
  * Context for job running, which to get the variable and put result.
  */
class Parameters(parameters: Map[String, String]) {

  def this() = {
    this(new mutable.HashMap[String, String]())
  }

  def hasParameter(parameter: String): Boolean = this.parameters.contains(parameter)

  def getParameter(parameter: String): String = this.parameters.getOrElse(parameter, "")

  def getParameters = this.parameters

  def addParameter(key: String, value: String): Unit = {
    this.parameters(key) = value
  }

  def merge(otherParameters: Parameters): Parameters = {
    val res = new Parameters()

    for ((k, v) <- this.parameters) {
      res.addParameter(k, v)
    }
    for ((k, v) <- otherParameters.getParameters) {
      res.addParameter(k, v)
    }
    res
  }

  def putAll(otherParameters: Parameters) = {
    for ((k, v) <- otherParameters.getParameters) {
      this.addParameter(k, v)
    }
  }

  override def toString = s"Parameters($getParameters)"

}

object Parameters {
  def apply() = new Parameters()
}


