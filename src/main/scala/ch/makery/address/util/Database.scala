package ch.makery.address.util

import scalikejdbc._
import ch.makery.address.model.Record

trait Database {
  val derbyDriverClassname = "org.apache.derby.jdbc.EmbeddedDriver"
  val dbURL = "jdbc:derby:myDB;create=true;"; // initialize JDBC driver & connection pool Class.forName(derbyDriverClassname)
  ConnectionPool.singleton(dbURL, "me", "mine") // ad-hoc session provider on the REPL
  implicit val session = AutoSession
}

object Database extends Database {
  def setupDB() = {
    if (!hasDBInitialize) Record.initializeTable()

    def hasDBInitialize: Boolean = {
      DB getTable "Record" match {
        case Some(x) => true
        case None => false
      }
    }
  }
}