package GUI


import scala.swing.event.KeyTyped
import scala.swing.{BoxPanel, Button, Component, Dimension, FlowPanel, Label, MainFrame, Orientation, Swing, TextField}

class Settings extends MainFrame {

  def restrictHeight(s: Component) {
    s.maximumSize = new Dimension(Short.MaxValue, s.preferredSize.height)
  }

  title = "Game options"

  val boardSizeField = new TextField() {
    listenTo(keys)
    reactions +=
      {
        case e: KeyTyped =>
        {
          if (!e.char.isDigit)
            e.consume
        }
      }
  }

  val sequenceToWin = new TextField() {
    listenTo(keys)
    reactions +=
      {
        case e: KeyTyped =>
        {
          if (!e.char.isDigit)
            e.consume
        }
      }
  }

  restrictHeight(boardSizeField)
  restrictHeight(sequenceToWin)


  contents = new BoxPanel(Orientation.Vertical) {
    contents += new BoxPanel(Orientation.Horizontal) {
      border = Swing.EmptyBorder(10,10,10,10)
      contents += new Label("Board size: ")
      contents += Swing.HStrut(5)
      contents += boardSizeField
    }
    contents += new BoxPanel(Orientation.Horizontal) {
      border = Swing.EmptyBorder(10,10,10,10)

      contents += new Label("Sequence to win: ")
      contents += Swing.HStrut(5)
      contents += sequenceToWin
    }

    contents += Swing.Box(new Dimension(20,20), new Dimension (100,100), new Dimension(1000,1000))
    contents += Button("Apply"){}
  }
}

