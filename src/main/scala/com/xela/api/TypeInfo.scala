package com.xela.api

case class TypeInfo(name: String, pokemon: List[TypeInfoPokemon])

case class TypeInfoPokemon(pokemon: NameAndUrl, slot: Int)