package com.xela
import java.net.URI

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.xela.api.{ListPokemonResult, NameAndUrl, PokemonDetails, TypeInfo}

import scala.concurrent.Future

trait PokeApi {

  def getPokemonsList: Future[List[NameAndUrl]]

  def getPokemonDetails(id: Int): Future[PokemonDetails]

  def getPokemonDetails(url: URI): Future[PokemonDetails]

  def getTypeInfo(url: URI): Future[TypeInfo]

}

trait HasPokeApi {

  def pokeApi: PokeApi

}