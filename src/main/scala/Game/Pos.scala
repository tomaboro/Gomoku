package Game

case class Pos(x: Int, y: Int) {
  def this(index: Int,board: Board) = this(index % board.size, index / board.size)
  def indexInBoard(board: Board): Int = y*board.size+x
}
