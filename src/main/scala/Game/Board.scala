package Game

/**
  * class representing game board
  * @param boardStr String containing current board situation
  */
case class Board(boardStr: String,sequenceToWin: Int, players: List[Player]){
  require(players.map(x => boardStr.count(_ == x.counter)).sum + boardStr.count(_ == ' ') == boardStr.length,
    "Board shouldn't contain elements other than players counters and ' '")

  /**
    * Number of pools in one face of board square
    */
  val size = math.sqrt(boardStr.length).toInt

  /**
    * transforms board to more readable format
    * @return nice formatted board
    */
  override def toString: String = {
    val upperLowerBorder = (0 until size).map(_ => "#").mkString("#","#","#\n")
    val inBorder = (0 until size).map(_ => "-").mkString("#","-","#\n")
    (0 until size).map(n => boardStr.substring(n * size,(n+1)*size).mkString("#","|","#\n")).mkString(upperLowerBorder,inBorder,upperLowerBorder)
  }

  /**
    * Checks if the field is empty
    * @param pos position to check
    * @return true if field is empty, false if already contains counter,
    */
  def isEmpty(pos: Pos): Boolean = {
    require(isLegal(pos))
    boardStr.charAt(pos.indexInBoard(this)) == ' '
  }

  /**
    * computes player move
    * @param pos position to place the counter
    * @return board after the move is made
    */
  def makeMove(pos: Pos): Board = {
    require(isLegal(pos) && isEmpty(pos),"Illegal move position")
    val computed: Int = pos.indexInBoard(this)
    Board(boardStr.take(computed) + currentPlayer.counter + boardStr.drop(computed+1),
      sequenceToWin,
      players.tail ++ players.take(1))
  }

  lazy val previousPlayer = players.last
  lazy val currentPlayer = players.head
  lazy val nextPlayer = players(1)


  /**
    * @return sequence of all moves that are possible
    */
  def possibleMoves: Seq[Pos] = {
    val posMov = for {
      row <- 0 until size
      col <- 0 until size
      if isEmpty(Pos(row,col))
    } yield Pos(row,col)
    posMov.toSeq
  }

  def getCounter(pos: Pos): Char = {
    boardStr.charAt(pos.indexInBoard(this))
  }

  def getPlayer(pos: Pos): Player = {
    val counter = getCounter(pos)
    val name = players.filter(p => p.counter == counter).head.name
    Player(name,counter)
  }

  def isLegal(pos: Pos): Boolean = {
    !(pos.x < 0 || pos.y < 0 || pos.x > size - 1 || pos.y > size - 1)
  }

  /**
    * Given position in strings computes position on board
    * @param posInStr index in boardStr
    * @return Pos on board
    */
  def reverseComputePos(posInStr: Int): Pos = {
    val y = posInStr /size
    val x = posInStr % size

    Pos(x,y)
  }

}
object Board {
  /**
    * Initialize empty board
    *
    * @return board string
    */
  def initBoard(size: Int, sequenceToWin: Int, players: List[Player]): Board = Board((0 until (size * size)).map(_ => " ").mkString, sequenceToWin, players)
}