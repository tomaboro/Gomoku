package GUI

import java.io.PrintWriter
import java.net.URL
import javax.swing.ImageIcon

import Game._

import scala.swing.{MenuItem, _}
import scala.swing.event._
import ArtificialIntelligence.Random._

import scala.io.Source

case class aiMadeMove(board: Board, pos: Pos) extends Event
case class gameLoaded(board: Board, pos: Pos) extends Event

class UI extends MainFrame with GameDef  {

  var gameSpec: (Int, Int) = (17,5)
  var currGameInfo: Map[Player, String] = Map()
  var currGameHistory: List[Pos] = List()
  var currGamePlayers: Array[Player] = Array()
  var aiThread: Thread = new Thread();

  val LoadGameMenu = new MenuItem(Action("Load game") {
    val fileChooser = new FileChooser
    fileChooser.showDialog(this,"Load")
    println(fileChooser.selectedFile)

    val lines = Source.fromFile(fileChooser.selectedFile).getLines.toList

    val (header,file) = lines.span(_ == "###Gomoku(by motek) saved game###")
    val (gameSpecc,fileRest) = file.span(_ != "###Players Info:###")
    val (playersInfo,gameHistory) = fileRest.span(_ != "###Game History:###")

    currGameInfo = Map()
    currGamePlayers = Array()

    gameSpec = (gameSpecc.tail.head.toInt, gameSpecc.tail.tail.head.toInt)
    playersInfo.tail.foreach {
      line => {
        val info = line.split(' ')
        val nPlayer = new Player(info(0), info(1)(0))

        currGameInfo = currGameInfo + (nPlayer -> info(2))
        currGamePlayers = currGamePlayers :+ nPlayer
      }
    }

    val lBoard = Board.initBoard(gameSpec._1,gameSpec._2,currGamePlayers)

    val currBoard = gameHistory.tail.map{ str =>
      val tmpStr = str.split(' ')
      val (x,y) = (tmpStr(0).toInt,tmpStr(1).toInt)
      Pos(x,y)
    }.dropRight(1).foldLeft(lBoard)((board,pos) => board.makeMove(pos))

    val lastMove = Pos(gameHistory.last.split(' ')(0).toInt,gameHistory.last.split(' ')(1).toInt)

    this.contents.foreach(_.publish(gameLoaded(currBoard,lastMove)))

  })

  def updateGameSpec(boardSizeStr: String, sequenceToWinStr: String): Unit ={
    if(boardSizeStr.isEmpty || sequenceToWinStr.isEmpty) Dialog.showMessage(this,"You need to fill all fields!","ERROR")
    else {
      gameSpec = (boardSizeStr.toInt,sequenceToWinStr.toInt)
      settings.visible = false
    }
  }

  preferredSize = new java.awt.Dimension(800,500)
  minimumSize = new java.awt.Dimension(500,350)
  title = "Gomoku"

  val settings = new Settings

  val futureBoard: Label = new Label {
    icon = new ImageIcon(getClass.getClassLoader.getResource("mem.jpg").getPath)
    border=Swing.EtchedBorder(Swing.Lowered)
  }
  val downBar = new DownBar()
  val rightPanel = new RightPanel()

  contents = new BorderPanel() {

    def startGame(): Unit = {
      aiThread.stop()

      currGameInfo = rightPanel.collectInfo
      if (currGameInfo.isEmpty) Dialog.showMessage(this,"All players must have different names and counters","ERROR")
      else {
        val nBoard = Board.initBoard(gameSpec._1, gameSpec._2, currGameInfo.keys.toArray)

        currGameHistory = List()
        currGamePlayers = nBoard.players

        reprintBoard(nBoard)
        
      }
    }


    def reprintBoard(board: Board) = {
      val nBoardPrinter = new BoardPrinter(board)
      add(nBoardPrinter, BorderPanel.Position.Center)

      revalidate()
      repaint()

      currGameInfo(board.currentPlayer) match {
        case "Player" => listenTo(nBoardPrinter)
        case "Random" => {
          aiThread = new Thread {
            override def run {
              downBar.updateBar("Random AI is making move")
              val nxtMove = RandomAI.RandomAIFunc(board, List())
              publish(aiMadeMove(board, nxtMove))
            }
          }
          aiThread.start()
        }
        case "Naive" => {
          aiThread = new Thread {
            override def run = {
              downBar.updateBar("Naive AI is making move")
              val nxtMove = RandomAI.RandomAIFunc(board, List())
              publish(aiMadeMove(board, nxtMove))
            }
          }
          aiThread.start();
        }
      }

    }

    def makeMove(pos: Pos,board: Board): Unit = {
      val nBoard = board.makeMove(pos)
      currGameHistory = currGameHistory ++ List(pos)

      reprintBoard(nBoard)

      if(isWinning(nBoard,pos)) {
        aiThread.stop
        Dialog.showMessage(this,board.currentPlayer + " wins the game","THE END")
        downBar.clear
        deafTo(layout.find(_._2 == BorderPanel.Position.Center).get._1)
      }else if(isDraw(nBoard)){
        aiThread.stop
        Dialog.showMessage(this,"Draw","THE END")
        deafTo(layout.find(_._2 == BorderPanel.Position.Center).get._1)
        downBar.clear
      }
    }

    add(futureBoard, BorderPanel.Position.Center)
    add(downBar.bar, BorderPanel.Position.South)
    add(new ScrollPane(rightPanel.panel){verticalScrollBarPolicy = ScrollPane.BarPolicy.Always}, BorderPanel.Position.East)

    listenTo(settings.applyButton)
    listenTo(rightPanel.gameTypeComboBox.selection)
    listenTo(rightPanel.numberOfPlayersComboBox.selection)
    listenTo(rightPanel.numberOfAIComboBox.selection)
    listenTo(mouse.moves)
    listenTo(rightPanel.startButton)
    listenTo(LoadGameMenu)

    reactions += {
      case SelectionChanged(rightPanel.gameTypeComboBox) => rightPanel.modePrinter()
      case SelectionChanged(rightPanel.numberOfPlayersComboBox) => rightPanel.refreshPlayersList()
      case SelectionChanged(rightPanel.numberOfAIComboBox) => rightPanel.refreshAIList()
      case CursorMoved(pos) => downBar.printPos(pos)
      case PosSelected(pos,board) => makeMove(pos, board)
      case ButtonClicked(button) =>
        if (button == rightPanel.startButton) startGame()
        else updateGameSpec(settings.boardSizeField.text,settings.sequenceToWin.text)
      case aiMadeMove(board,move) => makeMove(move,board)
      case gameLoaded(board,move) => {aiThread.stop;makeMove(move,board)}
    }
  }

  menuBar = new MenuBar{
    contents += new Menu("File") {
      contents += LoadGameMenu
      contents += new MenuItem(Action("Save game") {
        val fileChooser = new FileChooser
        fileChooser.showDialog(this,"Save")

        val pw = new PrintWriter(fileChooser.selectedFile)

        val playersInfo = currGameInfo.map{
          case (player,aiType) => player.name + " " + player.counter + " " + aiType
        }.mkString("\n")
        val gameHistory = currGameHistory.map(pos => pos.x + " " + pos.y).mkString("\n")

        pw.write("###Gomoku(by motek) saved game###" +
          "\n###Game Spec:###\n" +
          gameSpec._1 + "\n" + gameSpec._2 +
          "\n###Players Info:###\n" +
          playersInfo +
          "\n###Game History:###\n" +
          gameHistory)

        pw.close

      })
      contents += new MenuItem(Action("Show game history") {
        val message = if(currGameHistory.isEmpty) "There is nothing to show"
                  else
                    currGameHistory.indices.zip(currGameHistory).map{
                    case (index, pos) => (index,(currGamePlayers(index % currGamePlayers.length),pos))
                    }.map{
                      case (index, (player,pos)) => (index+1) + ". " + player.toString + " --> " + pos.toString
                    }.mkString("\n")

        Dialog.showMessage(this,message)
      })
    }
    contents += new Menu("Settings") {
      contents += new MenuItem(Action("Change settings") {
        settings.visible = true
      })
    }
    contents += new Menu("Help") {
      contents += new MenuItem(Action("Game rules") {
        val rules = "Gomoku toczy się na planszy do go zwanej goban, o rozmiarach 15x15.\n" +
          "Niekiedy używane są większe plansze (np. 19x19). Gracze kładą na planszy na przemian\n" +
          "po jednym swoim kamieniu. Celem każdego z graczy jest ułożenie nieprzerwanego łańcucha\n" +
          "łańcucha dokładnie pięciu kamieni własnego koloru (w poziomie, pionie lub po przekątnej).\n" +
          "Ustawienie więcej niż N kamieni w linii, zwane overline, nie daje wygranej.\n"
        Dialog.showMessage(this,rules)
      })
      contents += new MenuItem(Action("Github") {
        import java.awt.Desktop
        try
          Desktop.getDesktop.browse(new URL("https://github.com/tomaboro/Gomoku").toURI)
        catch {
          case e: Exception =>
            e.printStackTrace()
        }
      })
    }
  }


}

object GomokuGUI {
  def main(args: Array[String]) {
    val ui = new UI
    ui.visible = true
  }
}


