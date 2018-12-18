package com.xela

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, Sink, Source}
import cats.instances.bigDecimal._
import cats.instances.int._
import cats.instances.map._
import cats.instances.tuple._
import cats.kernel.Monoid
import com.xela.api.TypeInfo

import scala.concurrent.{ExecutionContextExecutor, Future}

case class PokemonTypeAverageInfo(name: String, skills: Map[String, BigDecimal])


trait TypeInfoService {

  this: HasPokeApi =>

  implicit val actorSystem: ActorSystem

  implicit lazy val actorMaterializer: ActorMaterializer = ActorMaterializer()(actorSystem)
  implicit lazy val dispatcher: ExecutionContextExecutor = actorSystem.dispatcher

  private val SkillMap = Monoid[Map[String, (Int, BigDecimal)]]


  def getAverageSkillsOfPokemon(id: Int): Future[List[PokemonTypeAverageInfo]] = {
    Source.fromFuture(pokeApi.getPokemonDetails(id))
      .mapConcat(_.types)
      .mapAsync[TypeInfo](40) { pokeType =>
      pokeApi.getTypeInfo(pokeType.`type`.url)
    }
      .mapAsync(1)(getAverageStatsForType)
      .fold[List[PokemonTypeAverageInfo]](List.empty)(_ :+ _)
      .toMat(Sink.last)(Keep.right)
      .run()
  }


  private def getAverageStatsForType(typeInfo: TypeInfo): Future[PokemonTypeAverageInfo] = {
    Source.fromIterator(() => typeInfo.pokemon.iterator)
      .mapAsync(20)(typeInfoPokemon => pokeApi.getPokemonDetails(typeInfoPokemon.pokemon.url))
      .map { details =>
        details.stats.map(stat => stat.stat.name -> (1, stat.base_stat)).toMap
      }
      .fold(SkillMap.empty)(SkillMap.combine)
      .map {
        _.mapValues { case (nOccurences, stat) => stat / nOccurences }
      }
      .toMat(Sink.last)(Keep.right)
      .run()
      .map(PokemonTypeAverageInfo(typeInfo.name, _))
  }


}
