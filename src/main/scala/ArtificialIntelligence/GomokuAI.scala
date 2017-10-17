package ArtificialIntelligence

import ArtificialIntelligence.Random.RandomAI.GameHistory
import Game.{Board, Pos}

abstract class GomokuAI {
  def makeMove(board: Board,history: GameHistory) :Pos
}

object AIInfo {
  val namesMap: Map[String,GomokuAI] = Map(("Random",Random.RandomAI),("HaskellNaive",HaskellNaive.NaiveAI))
  val names: Array[String] = namesMap.keys.toArray
}