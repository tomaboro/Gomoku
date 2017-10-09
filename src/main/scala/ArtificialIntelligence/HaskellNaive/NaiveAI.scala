package ArtificialIntelligence.HaskellNaive

import Game._

trait NaiveAI extends Types {

  def evaluateBoard(board: Board): Int = {
    val player = board.currentPlayer

    def helper(pos: Pos,direction: (Pos,Int) => Pos, oppositeDirection: (Pos,Int) => Pos): Int = {
      val s = helperInn(pos,direction)
      val n = helperInn(pos,oppositeDirection)

      if(s.head + n.head - 1 == board.sequenceToWin) 1000000
      else if(s.head + n.head - 1 > board.sequenceToWin) - 500000
      else s.sum + n.sum
    }

    def helperInn(pos: Pos, direction: (Pos,Int) => Pos): List[Int] = {
      if (!board.isLegal(pos)) List()
      else {
        val counter = board.getCounter(pos)
        if (counter != ' ' || counter != player.counter) List()
        else {
          val seqDirection: Int = check(board, pos, x => direction(x,1))
          val multi: Int = if (counter == ' ') 5 else 10
          val nxtPos = direction(pos,seqDirection)
          seqDirection * multi :: helperInn(nxtPos, direction)
        }
      }
    }

    (0 until board.size * board.size).map(board.reverseComputePos)
      .map(pos => helper(pos,Direction.north_,Direction.south_) + helper(pos,Direction.east_,Direction.west_)
        + helper(pos,Direction.southwest_,Direction.northwest_) + helper(pos,Direction.northeast_,Direction.southwest_))
      .sum
  }

  def generateGameTree(board: Board,lastMove: Pos, depth: Int): GameTree = {
    if(depth == 0) Leaf()
    else {
      val subTrees: List[GameTree] = board.possibleMoves
        .map(pos => generateGameTree(
          board.makeMove(pos),pos, depth-1))
        .toList

      val minMax = subTrees.map{
        case Leaf() => (0,Pos(-1,-1))
        case Node(_,lastMove1,_,value,_) => (value,lastMove1)
      }.maxBy(_._1)

      if(minMax._1 != 0) Node(board,lastMove,subTrees,minMax._1 ,minMax._2)
      else Node(board,lastMove,subTrees,evaluateBoard(board), minMax._2)
    }
  }

  def chooseBestMove(gameTree: GameTree,player: Player): Pos = {
    gameTree match {
      case Leaf() => Pos(-1,-1)
      case Node(_,_,_,_,bestMove) => bestMove
    }
  }
}
