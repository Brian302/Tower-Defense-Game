package ch.makery.address.model

import ch.makery.address.util.Database
import scalafx.beans.property.StringProperty
import scalikejdbc._

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Record(scoreS: Int) extends Database{
  val f = DateTimeFormatter.ofPattern("MMM dd, yyyy h:mm a")
  val score = StringProperty(scoreS.toString)
  val date = StringProperty(f.format(LocalDateTime.now))

  def save() = {
    DB autoCommit { implicit session =>
      sql"""
           insert into record (date, score) values (${date.value}, ${score.value})
         """.update.apply()
    }
  }
}

object Record extends Database {
  def apply(dateS: String, score: Int): Record = {
    new Record(score) {
      date.value = dateS
    }
  }

  def initializeTable() = {
    DB autoCommit { implicit session =>
      sql"""
           create table record (
           date varchar(64),
           score varchar(5)
           )
         """.execute.apply()
    }
  }

  def getAllRecords: List[Record] = {
    DB readOnly { implicit session =>
      sql"select * from record".map(rs => Record(rs.string("date"), rs.int("score"))).list.apply()
    }
  }
}
