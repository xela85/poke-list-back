package com
package xela

import java.net.URI

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{StatusCodes, Uri}
import akka.http.scaladsl.server.{HttpApp, Route}
import com.xela.model.PokemonListItem
import io.circe.generic.auto._
import io.circe.syntax._

import scala.concurrent.{ExecutionContextExecutor, Future}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import CirceExtensions._
import akka.stream.ActorMaterializer
import cats.kernel.Monoid
import com.xela.api.{NameAndUrl, PokemonDetailsStat}

import scala.util.{Failure, Success}

object PokeServer extends HttpApp {


  private implicit lazy val actorSystem: ActorSystem = ActorSystem("PokemonApi")
  private implicit lazy val actorMaterializer: ActorMaterializer = ActorMaterializer()(actorSystem)
  private implicit lazy val dispatcher: ExecutionContextExecutor = actorSystem.dispatcher

  lazy val log = actorSystem.log


  val pokeApi = new PokeApi("https://pokeapi.co/api/v2", actorSystem, actorMaterializer)

  override protected def routes: Route = get {

    pathPrefix("pokemons") {

      path(IntNumber) { id =>

        def getTypeAverageInfo(typeUrl: URI) =
          for {
            typeInfo <- pokeApi.getTypeInfo(typeUrl)
            pokemonsUrls = typeInfo.pokemon.map(_.pokemon.url)
            pokemonsDetails <- Future.sequence(pokemonsUrls.map(pokeApi.getPokemonDetails))
          } yield PokemonDetailsStat.computeAverage(pokemonsDetails)

        val allTypes = for {
          pokeDetails <- pokeApi.getPokemonDetails(id)
          typesUrls = pokeDetails.types.map(_.`type`).map(`type` => getTypeAverageInfo(`type`.url).map(`type`.name -> _))
        } yield Future.sequence(typesUrls)

        onComplete(allTypes) {
          case Success(allTypes) =>
            actorSystem.log.info(allTypes.toString)
            complete(allTypes)
          case Failure(error) =>
            log.error(error, "Unexpected error while executing request")
            complete(StatusCodes.BadRequest)
        }
      }


    }

  }
}
