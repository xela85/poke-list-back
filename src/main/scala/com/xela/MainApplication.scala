package com.xela

object MainApplication extends App {

  val pokeServer = new PokeServer with HasPokeApi with TypeInfoService {
    override def pokeApi: PokeApi = new AkkaPokeApi("https://pokeapi.co/api/v2", actorSystem, actorMaterializer)
  }

  pokeServer.startServer("0.0.0.0", 8080)

}
