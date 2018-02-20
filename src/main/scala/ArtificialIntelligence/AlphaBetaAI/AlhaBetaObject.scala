package ArtificialIntelligence.AlphaBetaAI

import ArtificialIntelligence.AlphaBetaAI.AlhaBetaObject.alphaBetaPruning
import ArtificialIntelligence.GomokuAI
import ArtificialIntelligence.HaskellNaive.NaiveAI
import ArtificialIntelligence.Random.RandomAI.GameHistory
import Game.{Board, Player, Pos}

object AlhaBetaObject extends GomokuAI {
  private val postiveInfinity =   1000000000
  private val negativeInfinity = -1000000000

  override def makeMove(board: Board, history: GameHistory) = alphaBetaPruning(negativeInfinity,postiveInfinity,2,board,board.currentPlayer)._1

  private def alphaBetaPruning(alpha: Int, beta: Int, depth: Int, board: Board, player: Player): (Pos, Int) = {

    def myFold(v: Int, bestMove: Pos, list: List[Pos]): (Pos, Int) = {
      if (list.isEmpty) (bestMove, v)
      else {
        if (player == board.currentPlayer) {
          if (v > beta) (bestMove, v)
          else {
            val nv = if (depth != 0) alphaBetaPruning(v, beta, depth - 1, board.makeMove(list.head), player)
            else (Pos(-1, -1), evaluateBoard(board, player))
            if (nv._2 > v) myFold(nv._2, list.head, list.tail)
            else myFold(v, bestMove, list.tail)
          }
        } else {
          if (v < alpha) (bestMove, v)
          else {
            val nv = if (depth != 0) alphaBetaPruning(alpha, v, depth - 1, board.makeMove(list.head), player)
            else (Pos(-1, -1), evaluateBoard(board, player))
            if (nv._2 < v) myFold(nv._2, list.head, list.tail)
            else myFold(v, bestMove, list.tail)
          }
        }
      }
    }

    if (player == board.currentPlayer) {
      myFold(negativeInfinity, Pos(-1, -1), board.possibleMoves.toList)
    } else {
      myFold(postiveInfinity, Pos(-1, -1), board.possibleMoves.toList)
    }
  }

  private def evaluateBoard(board: Board,player: Player): Int = 15 //TO DO ;) 

}

