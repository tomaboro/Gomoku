package Game

trait GameDef {

  type Direction = Pos => Pos

  /**
    * Checks if player move is winning move.
    * For optimization reasons this function doesn't check if winning sequence occurs anywhere on the given board.
    * It only checks if player move creates winning sequence.
    * As a result you need to run this function before computing every move
    *
    * @param board  board after player move
    * @param move   position where player wants to place his counter
    * @return true if player wins, otherwise false
    */
  def isWinning(board: Board, move: Pos): Boolean = {

    val (horizontal, vertical, diagonalLR, diagonalRL) =
      (checkHorizontal(board, move),
        checkVertical(board, move),
        checkDiagonalNorthWestToSouthEast(board, move),
        checkDiagonalSouthWestToNorthEast(board, move))

    if (horizontal > board.sequenceToWin || vertical > board.sequenceToWin || diagonalLR > board.sequenceToWin || diagonalRL > board.sequenceToWin) false
    else if (horizontal == board.sequenceToWin || vertical == board.sequenceToWin || diagonalLR == board.sequenceToWin || diagonalRL == board.sequenceToWin) true
    else false
  }

  /**
    * Checks if any of the players can still win the game
    * Not working as supposed
    * @param board actual game board
    * @return
    */
  def isDraw(board: Board): Boolean = board.boardStr.count(_ == ' ') == 0


  /**
    * Computes how long is the sequence of counter (or empty fields) starting in the given position heading given direction
    *
    * @param board     actual board
    * @param pos       starting position
    * @param direction direction in which we are checking the sequence
    * @return length of the sequence
    */
  def check(board: Board, pos: Pos, direction: Direction): Int = {
    val nxtPos = direction(pos)
    val counter = board.getCounter(pos)
    if (!board.isLegal(nxtPos)) 0
    else {
      val counterAtNxtPos = board.boardStr.charAt(nxtPos.indexInBoard(board))
      if (counter == counterAtNxtPos) 1 + check(board, nxtPos, direction)
      else 0
    }
  }

  /**
    * computes how long is horizontal sequence containing given position
    *
    * @param board actual board
    * @param pos   starting position
    * @return length of the horizontal sequence containing given position 
    */
  def checkHorizontal(board: Board, pos: Pos): Int = {
    check(board, pos, Direction.west) + 1 + check(board, pos, Direction.east)
  }

  /**
    * computes how long is vertical sequence containing given position
    *
    * @param board actual board
    * @param pos   starting position
    * @return length of the vertical sequence containing given position 
    */
  def checkVertical(board: Board, pos: Pos): Int = {
    check(board, pos, Direction.south) + 1 + check(board, pos, Direction.north)
  }

  /**
    * computes how long is diagonal sequence containing given position
    *
    * @param board actual board
    * @param pos   starting position
    * @return length of the diagonal sequence containing given position 
    */
  def checkDiagonalNorthWestToSouthEast(board: Board, pos: Pos): Int = {
    check(board, pos, Direction.southeast) + 1 + check(board, pos, Direction.northwest)
  }

  /**
    * computes how long is diagonal sequence containing given position
    *
    * @param board actual board
    * @param pos   starting position
    * @return length of the diagonal sequence containing given position 
    */
  def checkDiagonalSouthWestToNorthEast(board: Board, pos: Pos): Int = {
    check(board, pos, Direction.southwest) + 1 + check(board, pos, Direction.northeast)
  }


}
