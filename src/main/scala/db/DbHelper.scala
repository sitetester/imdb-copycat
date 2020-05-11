package db

import slick.dbio.DBIO
import slick.jdbc.SQLiteProfile.api._

import scala.concurrent.Await
import scala.concurrent.duration._

object DbHelper {
  val db = Database.forConfig("imdbConfig")

  // https://stackoverflow.com/questions/31266102/multiple-inserts-in-same-transaction-with-slick-3-0
  // https://scala-slick.org/doc/3.0.0/dbio.html#transactions-and-pinned-sessions

  // This is needed for FAST SQLite INSERT operation, otherwise, it takes ages
  def exec[T](action: DBIO[T], seconds: Duration = 0.seconds): T = {
    if (seconds.length > 0) {
      Await.result(db.run(action.transactionally), seconds)
    } else {
      Await.result(db.run(action.transactionally), Duration.Inf)
    }
  }
}
