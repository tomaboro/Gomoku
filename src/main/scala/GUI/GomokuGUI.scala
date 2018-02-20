package GUI

import javax.swing.ImageIcon

import ArtificialIntelligence.AIInfo
import Game._

import scala.swing._
import scala.swing.event._

case class aiMadeMove(board: Board, pos: Pos) extends Event
case class gameLoaded(board: Board, pos: Pos) extends Event

class UI extends MainFrame with GameDef   {
  
  val myMenuBar = new MyMenuBar(this)
  val rightPanel = new RightPanel(this)
  val downBar = new DownBar(this)

  def updateGameSpec(boardSizeStr: String, sequenceToWinStr: String): Unit ={
    if(boardSizeStr.isEmpty || sequenceToWinStr.isEmpty)
      Dialog.showMessage(this,"You need to fill all fields!","ERROR")
    else if(boardSizeStr.toInt > sequenceToWinStr.toInt)
      Dialog.showMessage(this,"Board size can't be greater then sequence to win!")
    else {
      gameSpecs.setBoardInfo(boardSizeStr.toInt,sequenceToWinStr.toInt)
      myMenuBar.settings.visible = false
    }
  }

  preferredSize = new java.awt.Dimension(800,500)
  minimumSize = new java.awt.Dimension(500,350)
  title = "Gomoku"

  val futureBoard: Label = new Label {
    icon = new ImageIcon(getClass.getClassLoader.getResource("mem.jpg").getPath)
    border=Swing.EtchedBorder(Swing.Lowered)
  }

  contents = new BorderPanel() {

    def startGame(): Unit = {
      gameSpecs.aiThread.stop()
      gameSpecs.setGameInfo(rightPanel.collectInfo.keys.toArray,rightPanel.collectInfo,List())
      if (gameSpecs.currGamePlayerToType.isEmpty)
        Dialog.showMessage(this,"All players must have different names and counters","ERROR")
      else {
        val nBoard =
          Board.initBoard(gameSpecs.currGameBoardSize, gameSpecs.currGameSequenceToWin,
            gameSpecs.currGamePlayers)

        reprintBoard(nBoard)
        
      }
    }

    def reprintBoard(board: Board): Unit = {
      val nBoardPrinter = new BoardPrinter(board)
      add(nBoardPrinter, BorderPanel.Position.Center)

      revalidate()
      repaint()

      downBar.playerStr = board.currentPlayer.toString

      gameSpecs.currGamePlayerToType(board.currentPlayer) match {
        case "Player" => listenTo(nBoardPrinter)
        case key => gameSpecs.aiThread = new Thread {
          override def run(): Unit = {
            downBar.updateBar("AI is making move")
            val nxtMove = AIInfo.namesMap(key).makeMove(board, gameSpecs.currGameHistory)
            downBar.updateBar("Move cursor")
            publish(aiMadeMove(board, nxtMove))
          }
        }
          gameSpecs.aiThread.start()
      }
    }

    def makeMove(pos: Pos,board: Board): Unit = {
      val nBoard = board.makeMove(pos)
      gameSpecs.currGameHistory = gameSpecs.currGameHistory ++ List(pos)

      reprintBoard(nBoard)

      if(isWinning(nBoard,pos)) {
        gameSpecs.aiThread.stop
        deafTo(layout.find(_._2 == BorderPanel.Position.Center).get._1)
        Dialog.showMessage(this,board.currentPlayer + " wins the game","THE END")
        new Thread() {
          override def run(): Unit = {
            Thread.sleep(30)
            publish(CursorMoved(Pos(-134,-134)))
          }
        }.start()
      }else if(isDraw(nBoard)){
        gameSpecs.aiThread.stop
        deafTo(layout.find(_._2 == BorderPanel.Position.Center).get._1)
        Dialog.showMessage(this,"Draw","THE END")
        publish(CursorMoved(Pos(-134,-134)))
      }
    }

    add(futureBoard, BorderPanel.Position.Center)
    add(downBar.bar, BorderPanel.Position.South)
    add(new ScrollPane(rightPanel.panel){
      verticalScrollBarPolicy = ScrollPane.BarPolicy.Always
    },BorderPanel.Position.East)

    listenTo(myMenuBar.settings.applyButton)
    listenTo(rightPanel.gameTypeComboBox.selection)
    listenTo(rightPanel.numberOfPlayersComboBox.selection)
    listenTo(rightPanel.numberOfAIComboBox.selection)
    listenTo(mouse.moves)
    listenTo(rightPanel.startButton)

    reactions += {
      case SelectionChanged(rightPanel.numberOfPlayersComboBox) =>
        rightPanel.refreshPlayersList()
      case SelectionChanged(rightPanel.numberOfAIComboBox) =>
        rightPanel.refreshAIList()
      case SelectionChanged(rightPanel.gameTypeComboBox) =>
        rightPanel.modePrinter()
      case CursorMoved(pos) =>
        if (pos == Pos(-134,-134)) downBar.clear
        else downBar.printPos(pos)
      case PosSelected(pos,board) =>
        makeMove(pos, board)
        publish(CursorMoved(pos))
      case ButtonClicked(button) =>
        if (button == rightPanel.startButton)
          startGame()
        if(button == myMenuBar.settings.applyButton)
          updateGameSpec(myMenuBar.settings.boardSizeField.text,myMenuBar.settings.sequenceToWin.text)
      case aiMadeMove(board,move) =>
        makeMove(move,board)
      case gameLoaded(board,move) =>
        gameSpecs.aiThread.stop()
        makeMove(move,board)}
  }

  menuBar = myMenuBar.menuBar


}

object GomokuGUI {
  def main(args: Array[String]) {
    val ui = new UI
    ui.visible = true
  }
}


