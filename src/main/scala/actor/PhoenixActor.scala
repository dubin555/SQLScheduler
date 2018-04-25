package actor

import akka.actor.Actor
import actor.Messages.PhoenixJob
import messages.Parameters
import util.{CommandUtil, Logging, PhoenixConnFactory, StringUtil}

/**
  * Phoenix Actor to execute Phoenix SQL, return result Parameters.
  */
class PhoenixActor extends Actor with Logging {
  override def receive: Receive = {
    case PhoenixJob(phoenixJobRaw, parameters) => {
      val sqlQuerys = StringUtil.getAllExpression(phoenixJobRaw.SQLQuery).map {
        // render the SQL with parameter
        sql => CommandUtil.renderCommand(sql, parameters)
      }

      logInfo(s"get the sqlQuerys, $sqlQuerys")

      val res = Parameters()
      val conn = PhoenixConnFactory.getConn(phoenixJobRaw.jdbc)
      val stmt = conn.createStatement()
      conn.setAutoCommit(false)

      if (phoenixJobRaw.output.size == 0) {
        // means all statement are not `Select` like. Execute the batch!
        for (i <- 0 until sqlQuerys.size) {
          stmt.addBatch(sqlQuerys(i))
        }

        if (sqlQuerys.size >= 1) {
          stmt.executeBatch()
        }

        conn.commit()
      } else {

        // means the last statement is `Select` like, Execute the batch except the last, execute the last one finally.
        if (sqlQuerys.size > 1) {
          for (i <- 0 until sqlQuerys.size - 1) {
            stmt.addBatch(sqlQuerys(i))
          }
          stmt.executeBatch()
          conn.commit()
        }

        val rs = stmt.executeQuery(sqlQuerys.last)
        val rsmd = rs.getMetaData()

        if (rs.next()) {
          for (o <- phoenixJobRaw.output) {
            val columnValue = rs.getString(o.toUpperCase())
            res.addParameter(o, columnValue.toString)
          }
        }
      }

      logInfo(s"return result of phoenix actor -> $res")
      sender() ! res
    }
  }
}
