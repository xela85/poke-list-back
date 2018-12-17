package com.xela.model

import java.net.URI

import com.xela.api.{NameAndUrl, PokemonDetails}

case class PokemonListItem(name: String, url: URI, imageUrl: Option[URI])

object PokemonListItem {

  def apply(nameAndUrl: NameAndUrl, pokemonDetails: PokemonDetails): PokemonListItem = {
    println(nameAndUrl)
    PokemonListItem(nameAndUrl.name, nameAndUrl.url, pokemonDetails.imageUrl)
  }

}
