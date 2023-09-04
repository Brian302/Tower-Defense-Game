package ch.makery.address.model

import scalafx.beans.property.ObjectProperty
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.image.Image

import scala.collection.mutable.ListBuffer

abstract class Structure {
  val xcoord: Int
  val ycoord: Int
  val width: Int = 64
  val height: Int = 64
  val img: Image
  var timeToShoot: Double
  var range: ObjectProperty[Int]
  var speedLevel: Int = 0
  var rangeLevel: Int = 0

  def shoot(): Unit
  def draw(gc: GraphicsContext): Unit
  def update(delta: Double): Unit
  def updateBs(newBs: ListBuffer[Balloon]): Unit
}
