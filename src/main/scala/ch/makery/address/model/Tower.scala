package ch.makery.address.model

import scalafx.beans.property.ObjectProperty
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.image.Image

import scala.collection.mutable.ListBuffer

class Tower(val xcoord: Int, val ycoord: Int, gc: GraphicsContext, var bs: ListBuffer[Balloon]) extends Structure{
  var timeSinceLastShot: Double = 0.0
  var timeToShoot: Double = 2.0
  var range: ObjectProperty[Int] = ObjectProperty[Int](2 * 64)
  var targetBalloon: Balloon = null
  var projectiles: ListBuffer[Projectile] = ListBuffer[Projectile]()
  val img = new Image("images/Game_Tower.png")

  this.chooseTarget()
  if (targetBalloon != null) this.shoot()

  def updateBs(newBs: ListBuffer[Balloon]): Unit = bs = newBs

  def inRange(b: Balloon): Boolean = {
    val xDist = math.abs(b.x + b.balloonType.width/2 - xcoord)
    val yDist = math.abs(b.y + b.balloonType.height/2 - ycoord)
    val dist = math.hypot(xDist, yDist)
    dist <= range()
  }

  def chooseTarget(): Unit = {
    for (b <- bs) {
      if (b.alive && targetBalloon == null && this.inRange(b) && b.targetedCount < b.health) {
        b.targetedCount += 1
        targetBalloon = b
      }
    }
  }

  def shoot(): Unit = {
    targetBalloon.targetedCount -= 1
    timeSinceLastShot = 0.0
    projectiles += new Projectile(xcoord, ycoord, 750, targetBalloon, gc, bs, range())
    targetBalloon = null
  }

  def draw(gc: GraphicsContext): Unit = {
    gc.drawImage(img, xcoord - width/2, ycoord - 3 * height/4, width, height)
  }

  def update(delta: Double): Unit = {
    this.draw(gc)
    if (targetBalloon == null) this.chooseTarget()
    if (targetBalloon != null && (!this.inRange(targetBalloon) || !targetBalloon.alive)) {
      targetBalloon.targetedCount -= 1
      targetBalloon = null
    }

    timeSinceLastShot += delta
    if (timeSinceLastShot > timeToShoot && targetBalloon != null && targetBalloon.alive) this.shoot()

    projectiles.foreach(p => {
      p.update(delta)
      p.draw(gc)
    })
  }
}

object Tower {
  val startingRange: Int = 2 * 64
  val startingRangeCost: Int = 150
  val increaseRangeCost: Int = 50
  val increaseRange: Double = 0.5
  val startingSpeedCost: Int = 200
  val increaseSpeedCost: Int = 60
  val increaseSpeed: Double = 0.2
  val maximumRange: Int = 4 * 64
  val maximumSpeed: Double = 0.6
}