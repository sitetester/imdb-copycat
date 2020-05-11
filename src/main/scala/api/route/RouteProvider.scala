package api.route

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.{Directives, Route}
import db.repository.TitlesRepository
import db._
import service.KevinNumberCalculator
import spray.json._

case class CalculatedValue(inputNumber: Int, calculatedValue: String)

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val titleBasics: RootJsonFormat[TitleBasics] = jsonFormat9(TitleBasics)
  implicit val nameBasics: RootJsonFormat[NameBasics] = jsonFormat6(NameBasics)
  implicit val titleRatings: RootJsonFormat[TitleRatings] = jsonFormat3(TitleRatings)
  implicit val titleBasicsWithNameBasics: RootJsonFormat[TitleBasicsWithNameBasics] = jsonFormat2(
    TitleBasicsWithNameBasics)
  implicit val titleBasicsWithRatings: RootJsonFormat[TitleBasicsWithRatings] = jsonFormat2(
    TitleBasicsWithRatings)

  implicit val titleNotFound: RootJsonFormat[TitleNotFound] = jsonFormat2(TitleNotFound)
  implicit val titleGenreNotFound: RootJsonFormat[TitleGenreNotFound] = jsonFormat2(
    TitleGenreNotFound)

  implicit val kevinNumber: RootJsonFormat[KevinNumber] = jsonFormat3(KevinNumber)
}

final case class TitleNotFound(title: String, message: String = "Title not found!")
final case class TitleGenreNotFound(title: String, message: String = "Title's genre not found!")

object RouteProvider extends Directives with JsonSupport {

  def getRoute: Route = {

    val route = concat(
      path("titles" / Segment) { title =>
        get {
          val result = TitlesRepository.getByTitle(title.toLowerCase)
          if (result.isEmpty) {
            complete(TitleNotFound(title))
          } else {
            val titleBasicsWithNameBasics = result.get
            complete(titleBasicsWithNameBasics.titleBasics, titleBasicsWithNameBasics.nameBasics)
          }
        }
      },
      path("titlesLike" / Segment) { title =>
        concat {
          get {
            val result = TitlesRepository.getByTitleLike(title.toLowerCase)
            if (result.isEmpty) {
              complete(TitleNotFound(title))
            } else {
              complete(result)
            }
          }
        }
      },
      path("genres" / Segment) { genre =>
        concat {
          get {
            val result = TitlesRepository.getByGenre(genre)
            if (result.isEmpty) {
              complete(TitleGenreNotFound(genre))
            } else {
              complete(result.get)
            }
          }
        }
      },
      path("kevinNumber" / Segment) { actor =>
        concat {
          get {
            complete(
              KevinNumberCalculator.findKevinNumber(actor)
            )
          }
        }
      }
    )

    route
  }

}
