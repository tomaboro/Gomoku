package GUI

import Game.Pos

import scala.swing.{Label, Swing}

class DownBar {

  val bar = new Label {
    text = "Last moves Here"
    border=Swing.EtchedBorder(Swing.Lowered)
  }

  def updateBar(text: String): Unit = {
    bar.text = text
  }

  def printPos(pos: Pos): Unit = {
    bar.text = pos.toString
  }

  def clear: Unit = {
    bar.text = " "
  }

}
