package com.xela.api

import java.net.URI

case class ListPokemonResult(results: List[NameAndUrl])

case class NameAndUrl(name: String, url: URI)