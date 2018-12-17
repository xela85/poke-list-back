package com.xela.api

case class TypeInfo(pokemon: List[TypeInfoPokemon])

case class TypeInfoPokemon(pokemon: NameAndUrl, slot: Int)