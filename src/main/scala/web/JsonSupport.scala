package web

//#json-support
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

/**
  * Json support defined in implicit.
  */
trait JsonSupport extends SprayJsonSupport {

  import DefaultJsonProtocol._

  implicit val JobJsonFormat = jsonFormat4(Job)
  implicit val AcceptedJobJsonFormat = jsonFormat5(AcceptedJob)
  implicit val AcceptedJobsJsonFormat = jsonFormat1(AcceptedJobs)

}
