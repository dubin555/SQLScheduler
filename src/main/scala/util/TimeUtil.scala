package util

import org.joda.time.{DateTime, DateTimeZone, LocalDateTime}

/**
  * Util of Time related.
  */
object TimeUtil {

  def getCurrentMinute(): Int = {
    val dt = new DateTime(DateTimeZone.UTC)
    val nowLocal = new LocalDateTime()
    val nowUTC = nowLocal.toDateTime(DateTimeZone.UTC)
    nowUTC.getMinuteOfDay
  }


}
