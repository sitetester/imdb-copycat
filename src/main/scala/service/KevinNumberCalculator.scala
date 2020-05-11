package service

import db.repository.TitlesRepository
import db.{DbHelper, KevinNumber, KevinNumbersTable}
import slick.jdbc.SQLiteProfile.api._
import slick.lifted.TableQuery

import scala.collection.mutable

object KevinNumberCalculator {

  val kevinNConst: String = TitlesRepository.getActorNConst()
  val kevinTitles: Seq[String] = TitlesRepository.getActorTitles(kevinNConst)

  // cache num to avoid calculation
  def cacheNumber(kevinNumbersQuery: TableQuery[KevinNumbersTable],
                  nconst: String,
                  primaryName: String,
                  kevinNumber: Int): Unit = {

    var kevinNumbers = mutable.Seq[KevinNumber]()
    kevinNumbers = kevinNumbers :+ KevinNumber(nconst, primaryName, kevinNumber)
    DbHelper.exec(kevinNumbersQuery ++= kevinNumbers)
  }

  def findKevinNumber(name: String): KevinNumber = {
    val nconst = TitlesRepository.getActorNConst(name)

    val kevinNumbersQuery = TableQuery[KevinNumbersTable]
    val q = kevinNumbersQuery.filter(_.nconst === nconst)
    val action = q.result

    val result = DbHelper.exec(action).headOption
    if (result.nonEmpty) {
      return result.get
    }

    val actorTitles = TitlesRepository.getActorTitles(nconst)
    if (actorTitles.isEmpty) {
      println(s"`$name` doesn't have any movies in database")
      return KevinNumber(nconst, name, 0)
    }

    val common = workedWithKevin(kevinTitles, actorTitles)
    if (common) {
      cacheNumber(kevinNumbersQuery, nconst, name, 1)
      return KevinNumber(nconst, name, 1)
    }

    val num = checkNetwork(Seq(nconst), actorTitles)
    cacheNumber(kevinNumbersQuery, nconst, name, num)

    KevinNumber(nconst, name, num)
  }

  @scala.annotation.tailrec
  private def checkNetwork(skipActors: Seq[String],
                           actorTitles: Seq[String],
                           roundCount: Int = 1): Int = {

    val actors = TitlesRepository.getActors(actorTitles.toSet, skipActors.toSet)
    val titles = TitlesRepository.getTitles(actors.toSet, actorTitles.toSet)

    val workedWith = workedWithKevin(kevinTitles, titles)
    if (!workedWith) {
      val skippedActors = getSkippedActors(skipActors, actors)
      checkNetwork(skippedActors, titles, roundCount + 1)
    } else {
      roundCount + 1
    }
  }

  private def getSkippedActors(skipActors: Seq[String], actors: Seq[String]): Seq[String] = {
    var skippedActors: Seq[String] = Seq()

    skipActors.foreach(actor => {
      skippedActors = skippedActors :+ actor
    })

    actors.foreach(actor => {
      skippedActors = skippedActors :+ actor
    })

    skippedActors
  }

  // print statements are only for debugging purpose
  private def workedWithKevin(kevinTitles: Seq[String], actorsTitles: Seq[String]): Boolean = {
    val commonTitles = kevinTitles.intersect(actorsTitles)
    if (commonTitles.nonEmpty) {
      // println(s"commonTitles: " + commonTitles)
      true
    } else {
      // each EMPTY check will result in +1 kevin number
      // println(s"commonTitles is EMPTY\n")
      false
    }
  }
}
