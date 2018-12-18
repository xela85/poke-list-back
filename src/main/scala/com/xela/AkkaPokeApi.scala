package com.xela

import java.net.URI

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.xela.model.PokemonListItem
import io.circe.generic.auto._
import CirceExtensions._
import com.xela.api.{ListPokemonResult, NameAndUrl, PokemonDetails, TypeInfo}

import scala.concurrent.{ExecutionContextExecutor, Future}

class AkkaPokeApi(val rootPath: String, implicit val actorSystem: ActorSystem, implicit val mat: ActorMaterializer) extends PokeApi {

  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

  implicit val executionContext: ExecutionContextExecutor = actorSystem.dispatcher

  def getPokemonsList: Future[List[NameAndUrl]] =
    Http().singleRequest(HttpRequest(uri = Uri(s"$rootPath/pokemon/")))
      .map(resp => resp)
      .flatMap(Unmarshal(_).to[ListPokemonResult])
      .map(_.results)

  def getPokemonDetails(id: Int): Future[PokemonDetails] = {
    getPokemonDetails(URI.create(s"$rootPath/pokemon/$id/"))
  }

  def getPokemonDetails(url: URI): Future[PokemonDetails] = {
    actorSystem.log.info("Get pokemon details of {}", url)
    Http().singleRequest(HttpRequest(uri = Uri(url.toString)))
      .map { resp =>
        actorSystem.log.info(s"Response received from {}", url)
        resp
      }
      .flatMap(Unmarshal(_).to[PokemonDetails])
  }

  def getTypeInfo(url: URI): Future[TypeInfo] = {
    actorSystem.log.info("Get type info of {}", url)
    Http().singleRequest(HttpRequest(uri = Uri(url.toString)))
      .flatMap(Unmarshal(_).to[TypeInfo])
  }

}
