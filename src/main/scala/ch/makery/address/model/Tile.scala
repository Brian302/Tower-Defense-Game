package ch.makery.address.model

import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.image.Image

class Tile(val xcoord: Int, val ycoord: Int, val tileType: TileType) {
  def buildable: Boolean = tileType.buildable
  def img: Image = tileType.img

  def draw(gc: GraphicsContext): Unit = {
    gc.drawImage(img, xcoord, ycoord, tileType.width, tileType.height)
  }
}
