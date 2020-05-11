package db.repository

import db._
import slick.jdbc.SQLiteProfile.api._
import slick.lifted.TableQuery

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._

object TitlesRepository {

  val db = Database.forConfig("imdbConfig")

  val titlePrincipalsQuery = TableQuery[TitlePrincipalsTable]
  val titleBasicsQuery = TableQuery[TitleBasicsTable]
  val titleRatingsQuery = TableQuery[TitleRatingsTable]
  val titlesCrewQuery = TableQuery[TitlesCrewTable]
  val nameBasicsQuery = TableQuery[NameBasicsTable]

  def getByTitle(title: String): Option[TitleBasicsWithNameBasics] = {

    // http://scala-slick.org/doc/3.0.0/queries.html
    val q = for {
      tb <- titleBasicsQuery
      if tb.primaryTitle.toLowerCase === title || tb.originalTitle.toLowerCase === title
      tc <- titlesCrewQuery if tb.tconst === tc.tconst
      nb <- nameBasicsQuery if tc.directors === nb.nconst
    } yield (tb, nb)

    val action = q.result

    // only for debugging
    // println(action.statements.mkString)

    val resultOption = DbHelper.exec(action).headOption
    if (resultOption.nonEmpty) {
      val result = resultOption.get
      Some(TitleBasicsWithNameBasics(result._1, result._2))
    } else {
      None
    }
  }

  def getByTitleLike(title: String): Option[Seq[TitleBasicsWithNameBasics]] = {
    // skip common words e.g.
    val commonWords = List("a", "the", "in")
    if (commonWords.contains(title.toLowerCase)) {
      return None
    }

    val q = for {
      tb <- titleBasicsQuery
      tc <- titlesCrewQuery if tb.tconst === tc.tconst
      nb <- nameBasicsQuery if tc.directors === nb.nconst
    } yield (tb, nb)

    val filtered = q.filter(tb =>
      (tb._1.primaryTitle.toLowerCase like "%" + title + "%") || (tb._1.originalTitle.toLowerCase like "%" + title + "%"))

    // Anything we run against a database is a DBIO[T] (or a DBIOAction, more generally)
    // https://books.underscore.io/essential-slick/essential-slick-3.html
    val action = filtered.result

    // only for debugging
    // println(action.statements.mkString)

    val resultList = DbHelper.exec(action)
    if (resultList.nonEmpty) {
      var lb = ListBuffer[TitleBasicsWithNameBasics]()
      resultList.foreach(r => {
        lb = lb :+ TitleBasicsWithNameBasics(r._1, r._2)
      })
      Some(lb)
    } else {
      None
    }
  }

  def getByGenre(genre: String): Option[Seq[TitleBasicsWithRatings]] = {

    val q = for {
      // tb <- titleBasicsQuery if tb.genres like "%" + genre + "%"
      tb <- titleBasicsQuery if tb.genres.toLowerCase === genre.toLowerCase
      tr <- titleRatingsQuery if tb.tconst === tr.tconst
    } yield (tb, tr)

    val action = q.result

    // only for debugging
    // println(action.statements.mkString)

    val resultList = DbHelper.exec(action)
    if (resultList.nonEmpty) {
      var lb = ListBuffer[TitleBasicsWithRatings]()

      // Sorting at DB level is slow (using SQL query)
      // For better performance, we should optimize at application level
      val sortedResultList = resultList.sortWith((x, y) => x._2.averageRating > y._2.averageRating)

      sortedResultList.foreach(r => {
        lb = lb :+ TitleBasicsWithRatings(r._1, r._2)
      })

      Some(lb)
    } else {
      None
    }
  }

  def getActorNConst(primaryName: String = "Kevin Bacon"): String = {
    val q = for {
      nb <- nameBasicsQuery if nb.primaryName === primaryName
    } yield nb.nconst

    val action = q.take(1).result
    // println(action.statements.mkString)

    val result = DbHelper.exec(action, 3.seconds)
    if (result.isEmpty) {
      throw new Exception(s"No actor found for primaryName: $primaryName")
    }

    // println(s"$primaryName -> getActorNConst -> $result\n")
    result.head
  }

  def getActorTitles(nconst: String): Seq[String] = {
    val q = for {
      tp <- titlePrincipalsQuery if tp.primaryTitle === nconst
      tb <- titleBasicsQuery if tb.tconst === tp.tconst
    } yield tb.tconst

    val action = q.result
    // println(action.statements.mkString + "\n")

    DbHelper.exec(action)
  }

  def getActors(tconst: Set[String], skip: Set[String]): Seq[String] = {
    val q = titlePrincipalsQuery
      .filter(x => x.tconst.inSet(tconst))
      .filterNot(x => x.primaryTitle.inSet(skip))
      .map(_.primaryTitle)
      .distinct

    val action = q.result
    // println(action.statements.mkString + "\n")

    DbHelper.exec(action)
  }

  def getTitles(primaryTitle: Set[String], skip: Set[String]): Seq[String] = {
    val q = titlePrincipalsQuery
      .filter(x => x.primaryTitle.inSet(primaryTitle))
      .filterNot(x => x.tconst.inSet(skip))
      .map(_.tconst)
      .distinct

    val action = q.result
    // println(action.statements.mkString + "\n")

    DbHelper.exec(action)
  }
}
