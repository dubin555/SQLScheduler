package util

/**
  * Util of string related.
  */
object StringUtil {

  def getAllExpression(SQLString: String): Seq[String] = {
    SQLString.replace("\n", "").split(";").toSeq
  }

  def lastElementStartsWith(sqlQuerys: Seq[String], flag: String): Boolean = {
    sqlQuerys.last.startsWith(flag)
  }

}
