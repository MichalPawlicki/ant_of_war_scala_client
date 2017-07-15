# Scala client for AntOfWar
https://github.com/u2i/ant_of_war

## Writing a strategy
Every time your turn comes, you have to decide what moves your ants should make and whether to spawn a new one. To achieve this, you have to implement a strategy that encapsulates this logic.

1. Create a file with your strategy under the `src/main/scala` directory in the package `com.u2i.antofwar`.
2. Implement the `com.u2i.antofwar.Strategy` trait, for example:
```scala
package com.u2i.antofwar

import com.u2i.antofwar.game.BoardState
import com.u2i.antofwar.model.Move

class DoNothingStrategy extends Strategy {
  override def moves(boardState: BoardState): Seq[Move] = Seq.empty

  override def shouldSpawn(boardState: BoardState): Boolean = false
}
```
Methods `moves` and `shouldSpawn` are invoked in each turn and are passed the current game state. The first one has to return the moves of your ants, and the other should answer the question whether to spawn a new ant.

## Joining a game

1. Create a `/src/main/resources/application.conf` file, and specify the server's URL and, optionally, the strategy name:
```
server-url = "ws://localhost:4000/socket/websocket"
strategy-name = doNothing
```
2. Instantiate your strategy in the `Main` object. If you provided the strategy name in `application.conf`, you can use it to determine which strategy to use:
```scala
  val config = ConfigFactory.load()
  val settings = new Settings(config)
  val strategy: Strategy = settings.strategyName match {
    case "random" => new CompletelyRandom
    case "doNothing" => new DoNothingStrategy
  }
```
3. Launch `sbt` from a terminal:
`$ sbt`
4. Compile your code before joining the game:
`> compile`
5. Run the client and join the game:
`> run`
