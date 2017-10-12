package GUI

import scala.swing._
import java.awt.{Color, Dimension}
import java.awt.geom.Line2D
import javax.swing.ImageIcon

import Game.{Board, Player, Pos}

import scala.swing.event.{Event, MouseClicked, MouseMoved}

case class CursorMoved(pos: Pos) extends Event
case class PosSelected(pos: Pos,board: Board) extends Event

class BoardPrinter(board: Board) extends Component {
  preferredSize = new Dimension(320,320)

  def getBoard: Board = board

  val futureBoard = new Label {
    icon = new ImageIcon(getClass.getClassLoader.getResource("mem.jpg").getPath)
    border=Swing.EtchedBorder(Swing.Lowered)
  }

  val actionsHistory = new Label {
    text = "Last moves Here"
    border=Swing.EtchedBorder(Swing.Lowered)
  }


  val gameTypeComboBox = new ComboBox(List("Player vs Player", "Player vs Computer", "Computer vs Computer"))
  val numberOfPlayersComboBox = new ComboBox(2 to 5)
  val numberOfAIComboBox = new ComboBox(2 to 5)

  val playersPromptArray: Array[Label] = (1 to 5).map(index => new Label("Player " + index)).toArray
  val playersNamesArray: Array[TextField] = (1 to 5).map(_ => new TextField()).toArray
  val playersCountersArray: Array[ComboBox[Char]] = (1 to 5).map(_ => new ComboBox(Array('X','O','C','N','B'))).toArray

  val aisPromptArray: Array[Label] = (1 to 5).map(index => new Label("AI " + index)).toArray
  val aisTypesArray: Array[ComboBox[String]] = (1 to 5).map(_ => new ComboBox(List("Random","Naive"))).toArray
  val aisCountersArray: Array[ComboBox[Char]] = (1 to 5).map(_ => new ComboBox(Array('X','O','C','N','B'))).toArray

  val playersList: List[BoxPanel] = (0 to 4).map(index => new BoxPanel(Orientation.Vertical) {
    visible = false

    contents += Swing.VStrut(5)
    contents += playersPromptArray(index)
    contents += new BoxPanel(Orientation.Horizontal) {
      contents += new Label("Name: ")
      contents += Swing.HStrut(10)
      contents += playersNamesArray(index)
    }
    contents += new BoxPanel(Orientation.Horizontal) {
      contents += new Label("Counter: ")
      contents += Swing.HStrut(10)
      contents += playersCountersArray(index)
    }
    contents += Swing.VStrut(5)
  }
  ).toList

  playersList.take(2).foreach(_.visible = true)

  val aisList: List[BoxPanel] = (0 to 4).map(index => new BoxPanel(Orientation.Vertical) {
    visible = false

    contents += Swing.VStrut(5)
    contents += aisPromptArray(index)
    contents += new BoxPanel(Orientation.Horizontal) {
      contents += new Label("Algorithm: ")
      contents += Swing.HStrut(10)
      contents += aisTypesArray(index)
    }
    contents += new BoxPanel(Orientation.Horizontal) {
      contents += new Label("Counter: ")
      contents += Swing.HStrut(10)
      contents += aisCountersArray(index)
    }
    contents += Swing.VStrut(5)
  }
  ).toList

  aisList.head.visible = true

  val gameType = new BoxPanel(Orientation.Horizontal){
    contents += new Label("Type: ")
    contents += Swing.HStrut(10)
    contents += gameTypeComboBox
  }


  val playersBox = new BoxPanel(Orientation.Vertical) {
    border = Swing.EtchedBorder(Swing.Lowered)

    contents += new BoxPanel(Orientation.Horizontal) {
      contents += new Label("Number of players: ")
      contents += Swing.HStrut(10)
      contents += numberOfPlayersComboBox
    }
    playersList.foreach(elem => contents += elem)
  }


  val aisBox = new BoxPanel(Orientation.Vertical) {
    visible = false
    border = Swing.EtchedBorder(Swing.Lowered)

    contents += new BoxPanel(Orientation.Horizontal) {
      contents += new Label("Number of AIs: ")
      contents += Swing.HStrut(10)
      contents += numberOfAIComboBox
    }
    aisList.foreach(contents += _)
  }

  def loadDimensions: (Int,Int,Int,Int) = {
    def round(squareSide: Int): Int = if (squareSide % board.size == 0) squareSide else round(squareSide-1)

    val d = size
    val squareSide = round(d.width min d.height)
    val offsetX = (d.width - squareSide)/2
    val offsetY = (d.height - squareSide)/2
    val poolSide = squareSide / board.size
    (offsetX,offsetY,squareSide,poolSide)
  }

  def isInBoard(x: Int, y: Int): Boolean = {
    val (offsetX,offsetY,squareSide,poolSide) = loadDimensions
    x > offsetX && x < offsetX+squareSide && y > offsetY && y < offsetY+squareSide
  }

  def pointToPos(x: Int, y: Int): Pos = {
    val (offsetX,offsetY,squareSide,poolSide) = loadDimensions
    Pos((x - offsetX) /poolSide,(y - offsetY) / poolSide)
  }

  override def paintComponent(g: Graphics2D): Unit = {
    val d = size
    val (offsetX,offsetY,squareSide,poolSide) = loadDimensions

    g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
      java.awt.RenderingHints.VALUE_ANTIALIAS_ON)
    g.setColor(Color.WHITE)
    g.fillRect(0,0,d.width,d.height)

    g.setColor(Color.BLACK)
    for (x <- 1 until board.size) {
      g.draw(new Line2D.Double(x*poolSide+offsetX,offsetY,x*poolSide+offsetX,squareSide+offsetY))
      g.draw(new Line2D.Double(offsetX,x*poolSide+offsetY,squareSide+offsetX,x*poolSide+offsetY))
    }

    (0 until board.size*board.size).map(board.reverseComputePos).zip(board.boardStr).filter(_._2 != ' ').foreach{case (pos,counter) => {
      val x0 = pos.x*poolSide + offsetX
      val y0 = pos.y*poolSide + offsetY

      counter match {
        case 'X' => g.setColor(Color.RED)
        case 'O' => g.setColor(Color.BLUE)
        case 'C' => g.setColor(Color.YELLOW)
        case 'N' => g.setColor(Color.GREEN)
        case 'B' => g.setColor(Color.PINK)
      }
      g.fillRect(x0,y0,poolSide,poolSide)
    }}
  }


  def mouseClick(x: Int, y: Int): Unit = {
    if(isInBoard(x,y) && board.possibleMoves.contains(pointToPos(x,y)))
      publish(PosSelected(pointToPos(x,y),board))
  }

  def mouseMove(x: Int, y: Int): Unit = {
    if (isInBoard(x,y)) publish(CursorMoved(pointToPos(x,y)))
  }

  listenTo(mouse.clicks)
  listenTo(mouse.moves)

  reactions += {
    case MouseClicked(_, p, _, _, _) => mouseClick(p.x, p.y)
    case MouseMoved(_,p,_) => mouseMove(p.x,p.y)
  }
}