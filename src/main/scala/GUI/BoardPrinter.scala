package GUI

import scala.swing._
import java.awt.{Color, Dimension}
import java.awt.geom.{Ellipse2D, Line2D}
import javax.swing.ImageIcon

import Game.{Board, Player, Pos}

import scala.swing.event.{Event, MouseClicked, MouseMoved}

case class CursorMoved(pos: Pos) extends Event
case class PosSelected(pos: Pos,board: Board) extends Event

class BoardPrinter(board: Board) extends Component {
  preferredSize = new Dimension(320,320)

  def getBoard: Board = board

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
        case 'R' => g.setColor(Color.RED)
        case 'B' => g.setColor(Color.BLUE)
        case 'Y' => g.setColor(Color.YELLOW)
        case 'G' => g.setColor(Color.GREEN)
        case 'P' => g.setColor(Color.PINK)
      }
      //g.fill(new Ellipse2D.Double(x0, y0, poolSide, poolSide))
      g.fillRect(x0,y0,poolSide,poolSide)
    }}
  }


  def mouseClick(x: Int, y: Int): Unit = {
    if(isInBoard(x,y) && board.possibleMoves.contains(pointToPos(x,y))) {
      publish(PosSelected(pointToPos(x, y), board))
    }
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