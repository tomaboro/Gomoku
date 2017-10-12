package TextMode

import Game.{Board, GameDef, Player, Pos}

object GameLoop extends GameDef {

  type GameHistory = List[(Pos,Player)]
  type AI = (Board,GameHistory) => Pos

  val playersTmp = Array(Player("Tomek",'X'),Player("Iza",'O'))

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


  def PVPGameLoop: Unit = {
    def looper(board: Board): Unit = {
      if (isDraw(board)) println("REMIS")
      else {
        val nxtMove: Pos = playerMove(board)
        val nBoard: Board = board.makeMove(nxtMove)

        println(nBoard)

        if (isWinning(board, nxtMove)) println("GRATULACJE " + board.currentPlayer.name + " !!!")
        else if (isDraw(board)) println("REMIS")
        else looper(board.makeMove(nxtMove))
      }
    }

    val clearBoard = Board.initBoard(17,5,playersTmp)
    println(clearBoard)
    looper(clearBoard)
  }


  def PVCGameLoop(players: List[Player], computers: Map[Player,AI]): Unit = {

    def looper(board: Board, gameHistory: GameHistory): Unit = {
      if(players.contains(board.currentPlayer)) {
        val nxtMove: Pos = playerMove(board)
        val nBoard: Board = board.makeMove(nxtMove)

        println(nBoard)

        if (isWinning(board, nxtMove)) println("GRATULACJE " + board.currentPlayer.name + " !!!")
        else if (isDraw(nBoard)) println("REMIS")
        else looper(nBoard,(nxtMove,board.currentPlayer) :: gameHistory)
      }
      else {
        val ai: AI = computers(board.currentPlayer)
        val computerMove: Pos = ai(board,gameHistory)
        val nBoard: Board = board.makeMove(computerMove)

        println(board.currentPlayer + ": " + computerMove)
        println(nBoard)

        if (isWinning(board, computerMove)) println("GRATULACJE " + board.currentPlayer.name + " !!!")
        else if (isDraw(nBoard)) println("REMIS")
        else looper(nBoard,(computerMove,board.currentPlayer) :: gameHistory)
      }
    }

    /*def looper(board: Board, player: Player, history: GameHistory): Unit = {
      if (isDraw(board)) println("REMIS")
      else {

        val playerMove: Pos = playerMove(board)
        val nBoard: Board = board.makeMove(playerMove)

        println(nBoard)

        if (isWinning(board, nxtPlayerMove)) println("GRATULACJE " + player.name + " !!!")
        else if (isDraw(nBoard)) println("REMIS")
        else {

          val computerPlayer: Player = nextPlayer
          val nHistory: GameHistory = (player, nxtPlayerMove) :: history
          val nxtComputerMove: Pos = ai(board, player, history)
          val nnBoard: Board = nBoard.makeMove(nxtComputerMove, computerPlayer)

          println(computerPlayer.name+ ": " +nxtComputerMove)
          println(nnBoard)

          if (isWinning(nBoard, nxtComputerMove, computerPlayer)) println("GRATULACJE " + computerPlayer.name + " !!!")
          else looper(nnBoard, player, (computerPlayer, nxtComputerMove) :: nHistory)
        }
      }
    }*/

    val clearBoard = Board.initBoard(17,5,playersTmp)
    println(clearBoard)
    looper(clearBoard,List())
  }


  def CVCGameLoop(computers: Map[Player,AI]): Unit = {

    def looper(board: Board, gameHistory: GameHistory): Unit = {

      val ai: AI = computers(board.currentPlayer)
      val computerMove: Pos = ai(board,gameHistory)
      val nBoard: Board = board.makeMove(computerMove)

      println(board.currentPlayer + ": " + computerMove)
      println(nBoard)

      if (isWinning(board, computerMove)) println("GRATULACJE " + board.currentPlayer.name + " !!!")
      else if (isDraw(nBoard)) println("REMIS")
      else looper(nBoard,(computerMove,board.currentPlayer) :: gameHistory)

    }

    /*def looper(board: Board, player: Player, history: GameHistory): Unit = {
      if (isDraw(board)) println("REMIS")
      else {

        val nxtPlayerMove: Pos = ai1(board, player, history)
        val nBoard: Board = board.makeMove(nxtPlayerMove, player)

        println(player.name+ ": " +nxtPlayerMove)
        println(nBoard)

        if (isWinning(board, nxtPlayerMove, player)) println("GRATULACJE " + player.name + " !!!")
        else if (isDraw(nBoard)) println("REMIS")
        else {

          val computerPlayer: Player = player.nextPlayer
          val nHistory: GameHistory = (player, nxtPlayerMove) :: history
          val nxtComputerMove: Pos = ai2(nBoard, player, history)
          val nnBoard: Board = nBoard.makeMove(nxtComputerMove, computerPlayer)

          println(computerPlayer.name+ ": " +nxtComputerMove)
          println(nnBoard)

          if (isWinning(nBoard, nxtComputerMove, computerPlayer)) println("GRATULACJE " + computerPlayer.name + " !!!")
          else looper(nnBoard, player, (computerPlayer, nxtComputerMove) :: nHistory)
        }
      }
    }*/

    val clearBoard = Board.initBoard(17,5,playersTmp)
    println(clearBoard)
    looper(clearBoard,List())
  }
}
