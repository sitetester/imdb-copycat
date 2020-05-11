package db

import slick.dbio.DBIO
import slick.jdbc.SQLiteProfile.api._
import slick.lifted.TableQuery

object SchemaImporter extends App {

  val titlePrincipals = TableQuery[TitlePrincipalsTable]
  val titleBasics = TableQuery[TitleBasicsTable]
  val nameBasics = TableQuery[NameBasicsTable]
  val titlesCrew = TableQuery[TitlesCrewTable]
  val titleRatings = TableQuery[TitleRatingsTable]
  val kevinNumbers = TableQuery[KevinNumbersTable]

  val tablesSchema = kevinNumbers.schema ++ titlePrincipals.schema ++ titleBasics.schema ++ nameBasics.schema ++ titlesCrew.schema ++ titleRatings.schema

  // drop tables
  val dropAction: DBIO[Unit] = DBIO.seq(tablesSchema.dropIfExists)
  DbHelper.exec(dropAction)
  println("drop - Done!")

  // setup schema
  val setupAction: DBIO[Unit] = DBIO.seq(tablesSchema.createIfNotExists)
  DbHelper.exec(setupAction)
  println("schema - Done!")

}
