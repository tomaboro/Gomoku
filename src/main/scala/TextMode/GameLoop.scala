package TextMode

import ArtificialIntelligence.GomokuAI
import Game.{Board, GameDef, Player, Pos}

object GameLoop extends GameDef {

  var boardSize = 17
  var sequenceToWin = 5

  type GameHistory = List[Pos]

  /**
    * Prints prompt and read player move from keyboard
    * @param board actual board used to check if move is legal
    * @return
    */
  def playerMove(board: Board): Pos = {
    var move = Pos(-1, -1)
    do {
      println("Player " + board.currentPlayer.name + ":")
      print("x: ")
      val x = scala.io.StdIn.readInt()
      print("y: ")
      val y = scala.io.StdIn.readInt()
      move = Pos(x, y)
    } while (!board.isLegal(move) && !board.isEmpty(move))
    move
  }


  def PVPGameLoop(players: List[Player]): Unit = {
    def looper(board: Board): Unit = {
      if (isDraw(board)) println("REMIS")
      else {
        val nxtMove: Pos = playerMove(board)
        val nBoard: Board = board.makeMove(nxtMove)

        println(nBoard)

        if (isWinning(nBoard, nxtMove)) println("GRATULACJE " + board.currentPlayer.name + " !!!")
        else if (isDraw(board)) println("REMIS")
        else looper(board.makeMove(nxtMove))
      }
    }

    val clearBoard = Board.initBoard(boardSize,sequenceToWin,players.toArray)
    println(clearBoard)
    looper(clearBoard)
  }


  def PVCGameLoop(players: List[Player], ais: List[Player], aisMap: Map[Player,GomokuAI]): Unit = {

    def looper(board: Board, gameHistory: GameHistory): Unit = {
      if(players.contains(board.currentPlayer)) {
        val nxtMove: Pos = playerMove(board)
        val nBoard: Board = board.makeMove(nxtMove)

        println(nBoard)

        if (isWinning(board, nxtMove)) println("GRATULACJE " + board.currentPlayer.name + " !!!")
        else if (isDraw(nBoard)) println("REMIS")
        else looper(nBoard,nxtMove :: gameHistory)
      }
      else {
        val ai: GomokuAI = aisMap(board.currentPlayer)
        val computerMove: Pos = ai.makeMove(board,gameHistory)
        val nBoard: Board = board.makeMove(computerMove)

        println(board.currentPlayer + ": " + computerMove)
        println(nBoard)

        if (isWinning(board, computerMove)) println("GRATULACJE " + board.currentPlayer.name + " !!!")
        else if (isDraw(nBoard)) println("REMIS")
        else looper(nBoard,computerMove :: gameHistory)
      }
    }

    val clearBoard = Board.initBoard(boardSize,sequenceToWin,(players++ais).toArray)
    println(clearBoard)
    looper(clearBoard,List())
  }


  def CVCGameLoop(ais: List[Player], aisMap: Map[Player,GomokuAI]): Unit = {

    def looper(board: Board, gameHistory: GameHistory): Unit = {

      val ai: GomokuAI = aisMap(board.currentPlayer)
      val computerMove: Pos = ai.makeMove(board,gameHistory)
      val nBoard: Board = board.makeMove(computerMove)

      println(board.currentPlayer + ": " + computerMove)
      println(nBoard)

      if (isWinning(board, computerMove)) println("GRATULACJE " + board.currentPlayer.name + " !!!")
      else if (isDraw(nBoard)) println("REMIS")
      else looper(nBoard,computerMove :: gameHistory)

    }

    val clearBoard = Board.initBoard(boardSize,sequenceToWin,ais.toArray)
    println(clearBoard)
    looper(clearBoard,List())
  }
}
