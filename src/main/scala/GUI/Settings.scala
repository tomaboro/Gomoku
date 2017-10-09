package GUI


import scala.swing.event.{ButtonClicked, Event, KeyTyped}
import scala.swing.{BoxPanel, Button, CheckBox, Component, Dimension, FlowPanel, Frame, GridBagPanel, Label, MainFrame, Orientation, ScrollPane, Swing, TextArea, TextField, ToggleButton}

case class GameInfoChanged(boardSize: Int, sequenceToWin: Int) extends Event

class Settings extends Frame {

  title = "Game options"

  preferredSize = new Dimension(300,200)
  resizable = false

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

  val applyButton = new Button("Apply")

  contents = new GridBagPanel {
    border = Swing.EmptyBorder(15,15,15,15)

    def constraints(x: Int, y: Int,
                    gridwidth: Int = 1, gridheight: Int = 1,
                    weightx: Double = 1.0, weighty: Double = 0.0,
                    fill: GridBagPanel.Fill.Value = GridBagPanel.Fill.None)
    : Constraints = {
      val c = new Constraints
      c.gridx = x
      c.gridy = y
      c.gridwidth = gridwidth
      c.gridheight = gridheight
      c.weightx = weightx
      c.weighty = weighty
      c.fill = fill
      c
    }

    add(new Label("Board size: "),
      constraints(0,0, weightx = 0.0))
    add(boardSizeField ,
      constraints(1, 0, fill = GridBagPanel.Fill.Horizontal))
    add(new Label("Sequence to win: "),
      constraints(0,1, weightx = 0.0))
    add(sequenceToWin ,
      constraints(1, 1, fill = GridBagPanel.Fill.Horizontal))
    add(Swing.VGlue,
      constraints(0,2,gridwidth = 2,weighty = 1.0))
    add(applyButton ,
      constraints(0, 3, gridwidth = 2))


    listenTo(applyButton)

    reactions += {
      case ButtonClicked(_) => {
        println(boardSizeField.text.toInt)
        publish(GameInfoChanged(boardSizeField.text.toInt, sequenceToWin.text.toInt))
        println(boardSizeField.text.toInt)
        dispose()
      }
      }
    }
}

