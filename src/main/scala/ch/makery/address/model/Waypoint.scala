package ch.makery.address.model

class Waypoint(xcoord: Int, ycoord: Int, tile: Tile, nextDir: (Int, Int)) {
  def x: Int = xcoord
  def y: Int = ycoord
  def getTile: Tile = tile
  def getNextDir: (Int, Int) = nextDir
}
