package ArtificialIntelligence.Random

import ArtificialIntelligence.GomokuAI
import Game._

object RandomAI extends GomokuAI with GameDef {

  type GameHistory = List[Pos]

  /**
    * AI making moves in complete random way
    * @param board actual board
    * @param history histpry of all moves during the game
    * @return position chosen by the random algorithe
    */
  def RandomAIFunc(board: Board,history: GameHistory): Pos = {
    val randomizer = scala.util.Random
    val possiblities = board.possibleMoves.toArray

    Thread.sleep(1000)
    possiblities(randomizer.nextInt(possiblities.length))
  }

  override def makeMove(board: Board, history: GameHistory) = RandomAIFunc(board,history)
}
