package ArtificialIntelligence.HaskellNaive

import Game._

trait Types extends GameDef {

  /**
    * Game Tree definition
    */
  abstract sealed class GameTree

  /**
    * Class representing Node of the tree
    *
    * @param board         board in the node
    * @param lastMove      move that lead to this situation
    * @param possibilities all moves possible for the current player
    * @param value         value expressing player situation
    */
  case class Node(board: Board, lastMove: Pos,
                  possibilities: List[GameTree], value: Int, bestMove: Pos) extends GameTree {
    override def toString: String =
      "Node " + board.hashCode() +
        possibilities.map(subTree => subTree.toString).mkString(" {", ",", " }")
  }

  /**
    * Tree Leaf
    */
  case class Leaf() extends GameTree {
    override def toString: String = "()"
  }

}