package db

import slick.jdbc.SQLiteProfile.api._
import slick.lifted.TableQuery

import scala.collection.mutable

object DataImporter extends App {

  // these limits are just to read/load partial data
  val until = 1000001
  val limit = 50000
  val db = Database.forConfig("imdbConfig")

  val titlePrincipalsQuery = TableQuery[TitlePrincipalsTable]
  val titleBasicsQuery = TableQuery[TitleBasicsTable]
  val titlesCrewQuery = TableQuery[TitlesCrewTable]
  val nameBasicsQuery = TableQuery[NameBasicsTable]
  val titleRatingsQuery = TableQuery[TitleRatingsTable]

  // delete all data
  val tablesQuery =
    Seq(
      titlePrincipalsQuery,
      titleBasicsQuery,
      titlesCrewQuery,
      nameBasicsQuery,
      titleRatingsQuery
    )
  tablesQuery.foreach(tableQuery => {
    DbHelper.exec(tableQuery.delete)
  })

  // TODO: make use of futures, if db engine supports. Currently SQLLite gives "database is locked" error
  // https://alvinalexander.com/scala/concurrency-with-scala-futures-tutorials-examples/
  importTitlePrincipalsData()
  importTitleBasicsData()
  importTitlesCrewData()
  importNameBasicsData()
  importTitleRatingsData()

  println("import - Done!")

  def importTitlePrincipalsData(): Unit = {

    var titlePrincipals = mutable.Seq[TitlePrincipals]()
    var counter = 0

    scala.io.Source
      .fromResource("data/title_principals.tsv")
      .getLines()
      .slice(1, until)
      .foreach(line => {
        counter = counter + 1

        val rowArray = line.split("\t")
        titlePrincipals = titlePrincipals :+ TitlePrincipals(
          rowArray(0),
          rowArray(1),
          rowArray(2),
          rowArray(3),
          rowArray(4),
          rowArray(5)
        )

        if (counter == limit) {
          counter = 0

          DbHelper.exec(titlePrincipalsQuery ++= titlePrincipals)
          titlePrincipals = mutable.Seq[TitlePrincipals]()
        }
      })

    println("importTitleBasicsData - Done!")
  }

  def importTitleBasicsData(): Unit = {

    var titleBasics = mutable.Seq[TitleBasics]()
    var counter = 0

    scala.io.Source
      .fromResource("data/title_basics.tsv")
      .getLines()
      .slice(1, until)
      .foreach(line => {
        counter = counter + 1

        val rowArray = line.split("\t")
        titleBasics = titleBasics :+ TitleBasics(
          rowArray(0),
          rowArray(1),
          rowArray(2),
          rowArray(3),
          rowArray(4),
          rowArray(5),
          rowArray(6),
          rowArray(7),
          rowArray(8)
        )

        if (counter == limit) {
          counter = 0

          DbHelper.exec(titleBasicsQuery ++= titleBasics)
          titleBasics = mutable.Seq[TitleBasics]()
        }
      })

    println("importTitleBasicsData - Done!")
  }

  def importTitlesCrewData(): Unit = {

    var titlesCrew = mutable.Seq[TitlesCrew]()
    var counter = 0

    scala.io.Source
      .fromResource("data/title_crew.tsv")
      .getLines()
      .slice(1, until)
      .foreach(line => {
        counter = counter + 1

        val rowArray = line.split("\t")
        titlesCrew = titlesCrew :+ TitlesCrew(
          rowArray(0),
          rowArray(1),
          rowArray(2)
        )

        if (counter == limit) {
          counter = 0

          DbHelper.exec(titlesCrewQuery ++= titlesCrew)
          titlesCrew = mutable.Seq[TitlesCrew]()
        }
      })

    println("importTitlesCrewData - Done!")
  }

  def importNameBasicsData(): Unit = {

    var nameBasics = mutable.Seq[NameBasics]()
    var counter = 0

    scala.io.Source
      .fromResource("data/name_basics.tsv")
      .getLines()
      .slice(1, until)
      .foreach(line => {
        counter = counter + 1

        val rowArray = line.split("\t")
        nameBasics = nameBasics :+ NameBasics(
          rowArray(0),
          rowArray(1),
          rowArray(2),
          rowArray(3),
          rowArray(4),
          rowArray(5)
        )

        if (counter == limit) {
          counter = 0

          DbHelper.exec(nameBasicsQuery ++= nameBasics)
          nameBasics = mutable.Seq[NameBasics]()
        }
      })

    println("importNameBasicsData - Done!")
  }

  def importTitleRatingsData(): Unit = {

    var titleRatings = mutable.Seq[TitleRatings]()
    var counter = 0

    scala.io.Source
      .fromResource("data/title_ratings.tsv")
      .getLines()
      .slice(1, until)
      .foreach(line => {
        counter = counter + 1

        val rowArray = line.split("\t")
        titleRatings = titleRatings :+ TitleRatings(
          rowArray(0),
          rowArray(1).toDouble,
          rowArray(2).toInt
        )

        if (counter == limit) {
          counter = 0

          DbHelper.exec(titleRatingsQuery ++= titleRatings)
          titleRatings = mutable.Seq[TitleRatings]()
        }
      })

    println("importTitleRatingsData - Done!")
  }
}
