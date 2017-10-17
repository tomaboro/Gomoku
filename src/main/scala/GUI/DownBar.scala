package GUI

import Game.Pos

import scala.swing.{BorderPanel, Label, MainFrame, Swing}

//TODO: Rewrite it!
class DownBar(parent: MainFrame) {

  var playerStr: String = ""

  val bar = new Label {
    text = "Last moves Here"
    border=Swing.EtchedBorder(Swing.Lowered)
  }

  def updateBar(text: String): Unit = {
    bar.text = playerStr + ":    " + text
  }

  def printPos(pos: Pos): Unit = {
    bar.text = playerStr + ":    " + pos.toString
  }

  def clear: Unit = {
    bar.text = " "
  }

}
