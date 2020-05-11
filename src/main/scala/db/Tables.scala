// Schema in this file is based on https://www.imdb.com/interfaces/
package db

import slick.jdbc.SQLiteProfile.api._

case class NameBasics(nconst: String,
                      primaryName: String,
                      birthYear: String,
                      deathYear: String,
                      primaryProfession: String,
                      knownForTitles: String)

class NameBasicsTable(tag: Tag) extends Table[NameBasics](tag, "name_basics") {
  // Every table needs a * projection with the same type as the table's type parameter
  def * =
    (
      nconst,
      primaryName,
      birthYear,
      deathYear,
      primaryProfession,
      knownForTitles
    ).mapTo[NameBasics]

  def nconst: Rep[String] = column[String]("nconst", O.Unique)
  def primaryName: Rep[String] = column[String]("primaryName")
  def birthYear: Rep[String] = column[String]("birthYear")
  def deathYear: Rep[String] = column[String]("deathYear")
  def primaryProfession: Rep[String] = column[String]("primaryProfession")
  def knownForTitles: Rep[String] = column[String]("knownForTitles")

  def primaryNameIndex = index("primaryName_idx", primaryName)
}

case class TitlesCrew(tconst: String, directors: String, writers: String)

class TitlesCrewTable(tag: Tag) extends Table[TitlesCrew](tag, "titles_crew") {
  // Every table needs a * projection with the same type as the table's type parameter
  def * = (tconst, directors, writers).mapTo[TitlesCrew]
  def tconst: Rep[String] = column[String]("tconst", O.Unique)
  def directors: Rep[String] = column[String]("directors")
  def writers: Rep[String] = column[String]("writers")
}

case class TitleRatings(tconst: String, averageRating: Double, numVotes: Int)

class TitleRatingsTable(tag: Tag) extends Table[TitleRatings](tag, "title_ratings") {
  // Every table needs a * projection with the same type as the table's type parameter
  def * = (tconst, averageRating, numVotes).mapTo[TitleRatings]
  def tconst: Rep[String] = column[String]("tconst", O.Unique)
  def averageRating: Rep[Double] = column[Double]("averageRating")
  def numVotes: Rep[Int] = column[Int]("numVotes")

  def averageRatingIndex = index("averageRating_idx", averageRating)
}

case class TitleBasics(tconst: String,
                       titleType: String,
                       primaryTitle: String,
                       originalTitle: String,
                       isAdult: String,
                       startYear: String,
                       endYear: String,
                       runtimeMinutes: String,
                       genres: String)

class TitleBasicsTable(tag: Tag) extends Table[TitleBasics](tag, "title_basics") {
  // Every table needs a * projection with the same type as the table's type parameter
  def * =
    (
      tconst,
      titleType,
      primaryTitle,
      originalTitle,
      isAdult,
      startYear,
      endYear,
      runtimeMinutes,
      genres
    ).mapTo[TitleBasics]

  def tconst: Rep[String] = column[String]("tconst", O.Unique)
  def titleType: Rep[String] = column[String]("titleType")
  def primaryTitle: Rep[String] = column[String]("primaryTitle")
  def originalTitle: Rep[String] = column[String]("originalTitle")
  def isAdult: Rep[String] = column[String]("isAdult")
  def startYear: Rep[String] = column[String]("startYear")
  def endYear: Rep[String] = column[String]("endYear")
  // `String` type as there is "N/A" in data for "runtimeMinutes" field
  def runtimeMinutes: Rep[String] = column[String]("runtimeMinutes")
  def genres: Rep[String] = column[String]("genres")

  def primaryTitleOriginalTitleIndex =
    index("primaryTitle_originalTitle_idx", (primaryTitle, originalTitle))

  def genresIndex = index("genres_idx", genres)
}

case class TitleBasicsWithRatings(titleBasics: TitleBasics, titleRatings: TitleRatings)
case class TitleBasicsWithNameBasics(titleBasics: TitleBasics, nameBasics: NameBasics)

case class TitlePrincipals(tconst: String,
                           ordering: String,
                           nconst: String,
                           category: String,
                           job: String,
                           characters: String)

class TitlePrincipalsTable(tag: Tag) extends Table[TitlePrincipals](tag, "title_principals") {
  // Every table needs a * projection with the same type as the table's type parameter
  def * =
    (
      tconst,
      titleType,
      primaryTitle,
      originalTitle,
      isAdult,
      startYear
    ).mapTo[TitlePrincipals]

  def tconst: Rep[String] = column[String]("tconst")
  def titleType: Rep[String] = column[String]("titleType")
  def primaryTitle: Rep[String] = column[String]("primaryTitle")
  def originalTitle: Rep[String] = column[String]("originalTitle")
  def isAdult: Rep[String] = column[String]("isAdult")
  def startYear: Rep[String] = column[String]("startYear")

  def tconstIndex = index("tconst_idx", tconst)
  def primaryTitleIndex = index("primaryTitle_idx", primaryTitle)
}

case class KevinNumber(nconst: String, primaryName: String, kevinNumber: Int)
class KevinNumbersTable(tag: Tag) extends Table[KevinNumber](tag, "kevin_numbers") {
  // Every table needs a * projection with the same type as the table's type parameter
  def * =
    (
      nconst,
      primaryName,
      kevinNumber
    ).mapTo[KevinNumber]

  def nconst: Rep[String] = column[String]("nconst", O.Unique)
  def primaryName: Rep[String] = column[String]("primaryName")
  def kevinNumber: Rep[Int] = column[Int]("kevinNumber")
}
