package ch.makery.address.model

import scalafx.scene.image.Image

trait TileType {
  def buildable: Boolean
  def img: Image
  def width: Int = 64
  def height: Int = 64
}

object Grass extends TileType {
  def buildable = true
  def img = new Image("images/Game_Grass.png")
}

object Sand extends TileType {
  def buildable = false
  def img = new Image("images/Game_Sand.png")
}

object Outside extends TileType {
  def buildable = false
  def img = null
}

