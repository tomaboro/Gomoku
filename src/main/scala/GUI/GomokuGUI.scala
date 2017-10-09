package GUI

import javax.swing.ImageIcon

import Game._

import scala.swing._
import scala.swing.event._

import ArtificialIntelligence.Random._

case class aiMadeMove(board: Board, pos: Pos) extends Event

class UI extends MainFrame with GameDef  {

  var gameSpec: (Int, Int) = (17,5)
  var currGameInfo: Map[Player, String] = Map()

  preferredSize = new java.awt.Dimension(800,500)
  minimumSize = new java.awt.Dimension(500,350)
  title = "Gomoku"

  val futureBoard: Label = new Label {
    icon = new ImageIcon(getClass.getClassLoader.getResource("mem.jpg").getPath)
    border=Swing.EtchedBorder(Swing.Lowered)
  }
  val downBar = new DownBar()
  val rightPanel = new RightPanel()

  contents = new BorderPanel() {

    def startGame(): Unit = {
      currGameInfo = rightPanel.collectInfo
      if (currGameInfo.isEmpty) Dialog.showMessage(this,"All players must have different names and counters","ERROR")
      else {
        val nBoardPrinter = new BoardPrinter(Board.initBoard(gameSpec._1, gameSpec._2, currGameInfo.keys.toList))
        add(nBoardPrinter, BorderPanel.Position.Center)
        revalidate()
        listenTo(nBoardPrinter)
      }
    }

    def makeMove(pos: Pos,board: Board): Unit = {
      println("make move")

      val nBoard = board.makeMove(pos)
      val nBoardPrinter = new BoardPrinter(nBoard)

      add(nBoardPrinter, BorderPanel.Position.Center)

      revalidate()
      repaint()

      if(isWinning(nBoard,pos)) {
        Dialog.showMessage(this,board.currentPlayer + " wins the game","THE END")
        downBar.clear
      }else if(isDraw(nBoard)){
        Dialog.showMessage(this,"Draw","THE END")
        downBar.clear
      }else {
        currGameInfo(nBoard.currentPlayer) match {
          case "Player" => listenTo(nBoardPrinter)
          case "Random" => new Thread {
            override def run {
              downBar.updateBar("Random AI is making move")
              val nxtMove = RandomAI.RandomAIFunc(nBoard, List())
              publish(aiMadeMove(nBoard, nxtMove))
            }
          }.start()
          case "Naive" => new Thread {
            override def run = {
              downBar.updateBar("Naive AI is making move")
              val nxtMove = RandomAI.RandomAIFunc(nBoard, List())
              publish(aiMadeMove(nBoard, nxtMove))
            }
          }.start()
        }
      }
    }

    add(futureBoard, BorderPanel.Position.Center)
    add(downBar.bar, BorderPanel.Position.South)
    add(new ScrollPane(rightPanel.panel){verticalScrollBarPolicy = ScrollPane.BarPolicy.Always}, BorderPanel.Position.East)

    listenTo(rightPanel.gameTypeComboBox.selection)
    listenTo(rightPanel.numberOfPlayersComboBox.selection)
    listenTo(rightPanel.numberOfAIComboBox.selection)
    listenTo(mouse.moves)
    listenTo(rightPanel.startButton)

    reactions += {
      case SelectionChanged(rightPanel.gameTypeComboBox) => rightPanel.modePrinter()
      case SelectionChanged(rightPanel.numberOfPlayersComboBox) => rightPanel.refreshPlayersList()
      case SelectionChanged(rightPanel.numberOfAIComboBox) => rightPanel.refreshAIList()
      case cursorMoved(pos) => downBar.printPos(pos)
      case posSelected(pos,board) => makeMove(pos, board)
      case ButtonClicked(_) => startGame()
      case aiMadeMove(board,move) => makeMove(move,board)

    }
  }

  menuBar = new MenuBar{
    contents += new Menu("File")
    contents += new Menu("Settings") {
      contents += new MenuItem(Action("Game Settings") {
        val settings = new Settings
        settings.visible = true
      })
    }
    contents += new Menu("Help")
  }


}

object GomokuGUI {
  def main(args: Array[String]) {
    val ui = new UI
    ui.visible = true
  }
}


