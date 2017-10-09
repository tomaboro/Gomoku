package Game

case class Player(name: String, counter: Char){
  override def toString: String = name + "(" + counter + ")"
}

