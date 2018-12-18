package com
package xela

import java.net.URI

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.model.headers.`Access-Control-Allow-Origin`
import akka.http.scaladsl.server.{HttpApp, Route}
import akka.stream.ActorMaterializer
import io.circe.generic.auto._
import io.circe.syntax._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

trait PokeServer extends HttpApp {

  this: HasPokeApi with TypeInfoService =>

  override implicit lazy val actorSystem: ActorSystem = ActorSystem("PokemonApi")
  private implicit lazy val actorMaterializer: ActorMaterializer = ActorMaterializer()(actorSystem)
  private implicit lazy val dispatcher: ExecutionContextExecutor = actorSystem.dispatcher

  lazy val log = actorSystem.log

  override protected def routes: Route = get {

    respondWithHeaders(`Access-Control-Allow-Origin`.*) {

      pathPrefix("pokemons") {

        path(IntNumber) { id =>

          onComplete(getAverageSkillsOfPokemon(id)) {
            case Success(allTypes) =>
              actorSystem.log.info(allTypes.toString)
              complete(allTypes.asJson)
            case Failure(error) =>
              log.error(error, "Unexpected error while executing request")
              complete(StatusCodes.BadRequest)
          }
        }


      }
    }

  }
}
