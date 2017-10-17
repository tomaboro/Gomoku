package GUI

import Game.{Player, Pos}

object gameSpecs {
  var currGameBoardSize: Int = 17
  var currGameSequenceToWin: Int = 5
  var currGamePlayers: Array[Player] = Array()
  var currGamePlayerToType: Map[Player, String] = Map()
  var currGameHistory: List[Pos] = List()
  var aiThread: Thread = new Thread()

  def print = {
    println(currGameBoardSize)
    println(currGameSequenceToWin)
    println(currGamePlayers)
    println(currGamePlayerToType)
    println(currGameHistory)
  }

  def resetAll = {
    currGameBoardSize = 17
    currGameSequenceToWin = 5
    currGamePlayers = Array()
    currGamePlayerToType = Map()
    currGameHistory = List()
    aiThread = new Thread()
  }

  def resetGameInfo = {
    currGamePlayers = Array()
    currGamePlayerToType = Map()
    currGameHistory = List()
    aiThread = new Thread()
  }

  def resetBoardInfo = {
    currGameBoardSize = 17
    currGameSequenceToWin = 5
  }

  def setBoardInfo(nBoardSize: Int, nSequenceToWin: Int) = {
    currGameBoardSize = nBoardSize
    currGameSequenceToWin = nSequenceToWin
  }

  def setGameInfo(nPlayers: Array[Player], nPlayerToType: Map[Player,String], nGameHistory: List[Pos]) = {
    currGamePlayers = nPlayers
    currGamePlayerToType = nPlayerToType
    currGameHistory = nGameHistory
    aiThread = new Thread()
  }

  def setAll(nBoardSize: Int, nSequenceToWin: Int,
             nPlayers: Array[Player], nPlayerToType: Map[Player,String], nGameHistory: List[Pos]) = {
    currGameBoardSize = nBoardSize
    currGameSequenceToWin = nSequenceToWin
    currGamePlayers = nPlayers
    currGamePlayerToType = nPlayerToType
    currGameHistory = nGameHistory
    aiThread = new Thread()
  }
}
