package TextMode

import ArtificialIntelligence.GomokuAI
import Game._
import TextMode.GameLoop.GameHistory


//TODO: Checking for duplicates in players list
object Main extends App{

  type AI = (Board,GameHistory) => Pos

  def selectPlayers: List[Player] = {
    print("How many players will play the game: ")
    val playersNum: Int = scala.io.StdIn.readInt()

    (for{num <- 1 to playersNum} yield {
      print("Enter player " + num + " name: ")
      val pName: String = scala.io.StdIn.readLine()
      print("Enter player" + num + " counter: ")
      val pCounter: Char = scala.io.StdIn.readChar()
      Player(pName,pCounter)
    }).toList
  }

  def selectAIs: List[(Player,GomokuAI)] = {
    print("How many ai will play the game: ")
    val playersNum: Int = scala.io.StdIn.readInt()

    (for{num <- 1 to playersNum} yield {
      print("Enter player" + num + " counter: ")
      val aiCounter: Char = scala.io.StdIn.readChar()
      println("Select AI type:")
      ArtificialIntelligence.AIInfo.names.foreach(x => println(num + ". " + x))
      val selected: Int = scala.io.StdIn.readInt()
      (Player("AI"+num,aiCounter),ArtificialIntelligence.AIInfo.namesMap(ArtificialIntelligence.AIInfo.names(selected-1)))
    }).toList
  }

  def changeSettings: (Int,Int) = {
    print("Board size: ")
    val boardSize = scala.io.StdIn.readInt()
    print("Sequence to win: ")
    val sequenceToWin = scala.io.StdIn.readInt()

    if (boardSize < sequenceToWin) {
      println("ERROR-->TRY AGAIN")
      changeSettings
    }
    else (boardSize,sequenceToWin)
  }

  def mainLoop(): Unit = {
    println(lineSeparator)
    println(lineSeparator)
    println("What do you want to do ?")
    println("1. Start Player vs Player game.")
    println("2. Start Player vs Computer game.")
    println("3. Start Computer vs Computer game.")
    println("4. Change board settings.")
    println("5. Quit")
    println(lineSeparator)
    print("Enter command number: ")
    val selected: Int = scala.io.StdIn.readInt()

    selected match {
      case 1 =>
        GameLoop.PVPGameLoop(selectPlayers)
        mainLoop()
      case 2 =>
        GameLoop.PVCGameLoop(selectPlayers,selectAIs.map(_._1),selectAIs.toMap)
        mainLoop()
      case 3 =>
        GameLoop.CVCGameLoop(selectAIs.map(_._1),selectAIs.toMap)
        mainLoop()
      case 4 =>
        val nSettings = changeSettings
        GameLoop.boardSize = nSettings._1
        GameLoop.sequenceToWin = nSettings._2
        mainLoop()
      case 5 =>
        println("Thank you for your time :)")
        println(lineSeparator)
      case _ =>
        println("Enter recognizable command!")
        mainLoop()
    }
  }

  val lineSeparator: String = "############################################"
  println(lineSeparator)
  println("Welcome in Gomoku written in Scala by Motek!")
  println(lineSeparator)
  mainLoop()
}


