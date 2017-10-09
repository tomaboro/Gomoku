package Game

object Direction {
  /**
    * defines next position heading north direction
    *
    * @return next position in north direction
    */
  def north_(pos: Pos, distance: Int): Pos = Pos(pos.x, pos.y - distance)
  def north(pos: Pos): Pos = north_(pos,1)

  /**
    * defines next position heading south direction
    *
    * @return next position in south direction
    */
  def south_(pos: Pos, distance: Int): Pos = Pos(pos.x,pos.y + distance)
  def south(pos: Pos): Pos = south_(pos,1)

  /**
    * defines next position heading west direction
    *
    * @return next position in north direction
    */
  def west_(pos: Pos, distance: Int): Pos = Pos(pos.x - distance,pos.y)
  def west(pos: Pos): Pos = west_(pos,1)

  /**
    * defines next position heading east direction
    *
    * @return next position in east direction
    */
  def east_(pos: Pos, distance: Int): Pos = Pos(pos.x + distance, pos.y)
  def east(pos: Pos): Pos = east_(pos,1)

  /**
    * defines next position heading north-west direction
    *
    * @return next position in north-west direction
    */
  def northwest_(pos: Pos, distance: Int): Pos = Pos(pos.x - distance, pos.y - distance)
  def northwest(pos: Pos): Pos = northwest_(pos,1)

  /**
    * defines next position heading north-east direction
    *
    * @return next position in north-east direction
    */
  def northeast_(pos: Pos, distance: Int): Pos = Pos(pos.x + distance, pos.y - distance)
  def northeast(pos: Pos): Pos = northeast_(pos,1)

  /**
    * defines next position heading south-west direction
    *
    * @return next position in south-west direction
    */
  def southwest_(pos: Pos, distance: Int): Pos = Pos(pos.x - distance, pos.y + distance)
  def southwest(pos: Pos): Pos = southwest_(pos,1)

  /**
    * defines next position heading south-east direction
    *
    * @return next position in south-east direction
    */
  def southeast_(pos: Pos, distance: Int): Pos = Pos(pos.x + distance, pos.y + distance)
  def southeast(pos: Pos): Pos = southeast_(pos,1)
}
