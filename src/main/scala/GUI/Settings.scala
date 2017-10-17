package GUI

import scala.swing._
import scala.swing.event._

case class GameInfoChanged(boardSize: Int, sequenceToWin: Int) extends Event

class Settings extends Frame {

  title = "Game options"

  preferredSize = new Dimension(300, 200)
  resizable = false

  val boardSizeField: TextField = new TextField() {
    text = "17"
    listenTo(keys)
    reactions += {
      case e: KeyTyped => {
        if (!e.char.isDigit)
          e.consume
      }
    }
  }

  val sequenceToWin: TextField = new TextField() {
    text = "5"
    listenTo(keys)
    reactions += {
      case e: KeyTyped => {
        if (!e.char.isDigit)
          e.consume
      }
    }
  }

  val applyButton = new Button("Apply")

  contents = new GridBagPanel {
    border = Swing.EmptyBorder(15, 15, 15, 15)

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
      constraints(0, 0, weightx = 0.0))
    add(boardSizeField,
      constraints(1, 0, fill = GridBagPanel.Fill.Horizontal))
    add(new Label("Sequence to win: "),
      constraints(0, 1, weightx = 0.0))
    add(sequenceToWin,
      constraints(1, 1, fill = GridBagPanel.Fill.Horizontal))
    add(Swing.VGlue,
      constraints(0, 2, gridwidth = 2, weighty = 1.0))
    add(applyButton,
      constraints(0, 3, gridwidth = 2))

  }
}

