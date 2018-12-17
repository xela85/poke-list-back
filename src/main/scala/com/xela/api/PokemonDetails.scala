package com.xela.api

import java.net.URI

import cats.Monoid
import cats.instances.map._
import cats.instances.int._
import cats.instances.tuple._

case class PokemonDetails(imageUrl: Option[URI], types: List[PokemonDetailsType], stats: List[PokemonDetailsStat])

case class PokemonDetailsType(slot: Int, `type`: NameAndUrl)

case class PokemonDetailsStat(base_stat: Int, effort: Int, stat: NameAndUrl)


object PokemonDetailsStat {

  def computeAverage(details: List[PokemonDetails]) =
    Monoid.combineAll(details.map(_.stats.map(stat => stat.stat.name -> stat.base_stat).toMap.mapValues(_ -> 1)))
        .mapValues { case (sum, numberOfOccurences) => sum / numberOfOccurences }

}