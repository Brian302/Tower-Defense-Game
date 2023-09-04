package ch.makery.address.model

import scalafx.scene.canvas.GraphicsContext

class TileGrid(arr: Array[Array[Int]]) {
  val map: Array[Array[Tile]] = createMap

  def createMap: Array[Array[Tile]] = {
    val tileMap = Array.ofDim[Tile](arr.length, arr(0).length)
    for (x <- arr.indices) {
      for (y <- arr(x).indices) {
        arr(x)(y) match {
          case 0 => tileMap(x)(y) = new Tile(y * 64, x * 64, Grass)
          case 1 => tileMap(x)(y) = new Tile(y * 64, x * 64, Sand)
        }
      }
    }
    tileMap
  }

  def draw(gc: GraphicsContext): Unit = {
    map.foreach(_.foreach(_.draw(gc)))
  }

  def getTile(xcoord: Int, ycoord:Int): Tile = {
    if (xcoord >= 0 && ycoord >= 0 && xcoord <= (map(0).length - 1) * 64 && ycoord <= (map.length - 1) * 64)
      map(ycoord / 64)(xcoord / 64)
    else new Tile(-1, -1, Outside)
  }
}
