package GUI

import java.io.PrintWriter
import java.net.URL

import Game.{Board, Player, Pos}

import scala.io.Source
import scala.swing.{Action, Dialog, Dimension, FileChooser, Frame, ListView, MainFrame, Menu, MenuBar, MenuItem, ScrollPane}

class MyMenuBar(parent: MainFrame) {

  val settings = new Settings

  val loadGameMenu = new MenuItem(Action("Load game") {
    val fileChooser = new FileChooser
    fileChooser.showDialog(parent, "Load")
    if (fileChooser.selectedFile != null) {
      val lines = Source.fromFile(fileChooser.selectedFile).getLines.toList

      val header: String = lines.head
      val file: List[String] = lines.tail
      val (boardInfo, fileRest): (List[String], List[String]) = file.span(_ != "###Players Info:###")
      val (playersInfo, gameHistory): (List[String], List[String]) = fileRest.span(_ != "###Game History:###")

      if (header == "###Gomoku(by motek) saved game###" &&
        boardInfo.nonEmpty &&
        boardInfo.head == "###Game Spec:###" &&
        playersInfo.nonEmpty &&
        playersInfo.head == "###Players Info:###" &&
        gameHistory.nonEmpty &&
        gameHistory.head == "###Game History:###") {

        try {
          val (nBoardSize, nSequeceToWin) = (boardInfo.tail.head.toInt, boardInfo.tail.tail.head.toInt)
          val (nPlayers, nPlayerToType) = playersInfo.tail.foldLeft((Array(): Array[Player], Map(): Map[Player, String])) {
            case ((accPlayers, accPlayersToType), line) =>

              val info = line.split(' ')
              val playerName = info(0)
              val playerCounter = info(1)(0)
              val playerType = info(2)
              val nPlayer = Player(playerName, playerCounter)

              (accPlayers :+ nPlayer, accPlayersToType + (nPlayer -> playerType))
          }

          val nGameHistory = gameHistory.tail.map { str =>
            val tmpStr = str.split(' ')
            val (x, y) = (tmpStr(0).toInt, tmpStr(1).toInt)
            Pos(x, y)
          }

          gameSpecs.setGameInfo(nPlayers, nPlayerToType, nGameHistory)
          gameSpecs.setBoardInfo(nBoardSize, nSequeceToWin)

          val nBoard = Board.initBoard(nBoardSize, nSequeceToWin, nPlayers)

          val almostLoadedBoard = gameSpecs.currGameHistory.dropRight(1).foldLeft(nBoard)((board, pos) => board.makeMove(pos))
          val lastMove = gameSpecs.currGameHistory.last

          parent.contents.foreach(_.publish(gameLoaded(almostLoadedBoard, lastMove)))
        } catch {
          case _: NumberFormatException =>
            Dialog.showMessage(parent, "Error occured while loading saved file  :(")
        }
      }
      else Dialog.showMessage(parent, "This is not Gomoku saved game! :(")
    }
  })


  val saveGameMenu = new MenuItem(Action("Save game") {
    if (gameSpecs.currGameHistory.isEmpty) {
      Dialog.showMessage(parent,"There is nothing to save :(")
    }else {
      val fileChooser = new FileChooser
      fileChooser.showDialog(parent, "Save")

      if (fileChooser.selectedFile != null) {
        val pw = new PrintWriter(fileChooser.selectedFile)

        val playersInfo = gameSpecs.currGamePlayerToType.map {
          case (player, aiType) => player.name + " " + player.counter + " " + aiType
        }.mkString("\n")

        val gameHistory = gameSpecs.currGameHistory.map(pos => pos.x + " " + pos.y).mkString("\n")

        pw.write("###Gomoku(by motek) saved game###" +
          "\n###Game Spec:###\n" +
          gameSpecs.currGameBoardSize + "\n" + gameSpecs.currGameSequenceToWin +
          "\n###Players Info:###\n" +
          playersInfo +
          "\n###Game History:###\n" +
          gameHistory)

        pw.close()
      }
    }
  })

  val historyMenu = new MenuItem(Action("Show game history") {
    val len = gameSpecs.currGamePlayers.length
    val message: List[String] = if (gameSpecs.currGameHistory.isEmpty) List("There is nothing to show")
    else gameSpecs.currGameHistory.zipWithIndex.map {
      case (pos, index) => ((pos, gameSpecs.currGamePlayers(index % len)), index)
    }.map {
      case ((pos, player), index) => (index + 1) + ". " + player + " ==> " + pos
    }

    val frame = new Frame {
      title = "Game history"

      preferredSize = new Dimension(300, 200)
      resizable = true

      contents = new ScrollPane(new ListView(message))

    }
    frame.peer.setLocationRelativeTo(parent.peer)
    frame.visible = true

  })

  val gameRulesMenu = new MenuItem(Action("Game rules") {
    val rules = "Gomoku toczy się na planszy do go zwanej goban, o rozmiarach 15x15.\n" +
      "Niekiedy używane są większe plansze (np. 19x19). Gracze kładą na planszy na przemian\n" +
      "po jednym swoim kamieniu. Celem każdego z graczy jest ułożenie nieprzerwanego łańcucha\n" +
      "łańcucha dokładnie pięciu kamieni własnego koloru (w poziomie, pionie lub po przekątnej).\n" +
      "Ustawienie więcej niż N kamieni w linii, zwane overline, nie daje wygranej.\n"
    Dialog.showMessage(parent, rules)
  })

  val gitHubMenu = new MenuItem(Action("Github") {
    import java.awt.Desktop
    try
      Desktop.getDesktop.browse(new URL("https://github.com/tomaboro/Gomoku").toURI)
    catch {
      case e: Exception =>
        e.printStackTrace()
    }
  })

  val menuBar: MenuBar = new MenuBar {
    contents += new Menu("File") {
      contents += loadGameMenu
      contents += saveGameMenu
      contents += historyMenu
    }
    contents += new Menu("Settings") {
      contents += new MenuItem(Action("Change settings") {
        settings.peer.setLocationRelativeTo(parent.peer)
        settings.visible = true
      })
    }
    contents += new Menu("Help") {
      contents += gameRulesMenu
      contents += gitHubMenu
    }
  }
}
