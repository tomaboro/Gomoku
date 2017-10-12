package GUI

import javax.swing.ImageIcon

import Game.Player

import scala.swing.{BorderPanel, BoxPanel, Button, ComboBox, FlowPanel, Label, Orientation, Swing, TextField}

class RightPanel {

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

  def collectInfo: Map[Player,String] = {

    val players =
      playersNamesArray.zip(playersCountersArray).
        take(numberOfPlayersComboBox.selection.item).
        map{case (name,counter) => (Player(name.text,counter.selection.item),"Player")}

    val ais =
      aisPromptArray.zip(aisCountersArray).
        take(numberOfAIComboBox.selection.item).
        map{case (name,counter) => Player(name.text.filter(_ != ' '),counter.selection.item)}.
        zip(aisTypesArray.map(_.selection.item))


    val eval = gameTypeComboBox.selection.index match {
      case 0 => players
      case 1 => players ++ ais
      case 2 => ais
    }

    if(eval.groupBy(_._1.counter).exists(_._2.length > 1) || eval.groupBy(_._1.name).exists(_._2.length > 1)) Map()
    else eval.toMap
  }

  val startButton = new Button("Start")

  val panel = new BorderPanel() {
    add(new BoxPanel(Orientation.Vertical) {
      contents += gameType
      contents += playersBox
      contents += aisBox }, BorderPanel.Position.North )
    add(new FlowPanel(){
      contents += startButton
    }, BorderPanel.Position.South)
  }

  def modePrinter(): Unit = {
    gameTypeComboBox.selection.index match {
      case 0 => //PVP
        aisBox.visible = false
        playersBox.visible = true

        numberOfPlayersComboBox.peer.setModel(ComboBox.newConstantModel(2 to 5))
        playersList.take(2).foreach(_.visible)
      case 1 => //PVC
        aisBox.visible = true
        playersBox.visible = true

        numberOfPlayersComboBox.peer.setModel(ComboBox.newConstantModel(1 to 5))
        playersList.head.visible
        playersList.tail.foreach(_.visible = false)

        numberOfAIComboBox.peer.setModel(ComboBox.newConstantModel(1 to 5))
        aisList.head.visible
        aisList.tail.foreach(_.visible = false)
      case 2 => // CVC
        aisBox.visible = true
        playersBox.visible = false

        numberOfAIComboBox.peer.setModel(ComboBox.newConstantModel(2 to 5))
        aisList.take(2).foreach(_.visible = true)
    }
  }

  def refreshPlayersList(): Unit = {
    val (visible,invisible) = playersList.splitAt(numberOfPlayersComboBox.selection.item)
    visible.foreach(_.visible = true)
    invisible.foreach(_.visible = false)
  }

  def refreshAIList(): Unit = {
    val (visible,invisible) = aisList.splitAt(numberOfAIComboBox.selection.item)
    visible.foreach(_.visible = true)
    invisible.foreach(_.visible = false)
  }
}
