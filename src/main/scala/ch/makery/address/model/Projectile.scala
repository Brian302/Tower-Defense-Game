package ch.makery.address.model

import ch.makery.address.MainApp
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.media.{Media, MediaPlayer}
import scalafx.scene.paint.Color

import java.io.File
import scala.collection.mutable.ListBuffer

class Projectile (var xcoord: Double, var ycoord: Double, val speed: Int, val target: Balloon, gc: GraphicsContext, bs: ListBuffer[Balloon], range: Int){
  val initX = xcoord
  val initY = ycoord
  val d: Int = 10
  var alive: Boolean = true
  var xVelocity: Double = 0.0
  var yVelocity: Double = 0.0
  val popEffectMedia = new Media(new File("src/main/resources/audio/Balloon_Pop.mp3").toURI.toString)
  val popEffectMediaPlayer = new MediaPlayer(popEffectMedia)

  //TODO find a better way
  def calculateTrajectory(): Unit = {
    val total = 1.0
    val xDist = math.abs(target.x + target.balloonType.width/2 - xcoord)
    val yDist = math.abs(target.y + target.balloonType.height/2 - ycoord)
    xVelocity = xDist / (xDist + yDist)
    yVelocity = total - xVelocity

    if (target.x < xcoord) xVelocity *= -1
    if (target.y < ycoord) yVelocity *= -1
  }

  this.calculateTrajectory()

  def draw(gc: GraphicsContext): Unit = {
    if (alive) {
      gc.fill = Color.Black
      gc.globalAlpha = 200.0
      gc.fillOval(xcoord, ycoord, d, d)
    }
  }

  def update(delta: Double): Unit = {
    def isCollide(x1: Double, y1: Double, w1: Double, h1: Double, x2: Double, y2: Double, w2: Double, h2: Double) = {
      (x1 + w1 > x2) && (x1 < x2 + w2) && (y1 + h1 > y2) && (y1 < y2 + h2)
    }

    if (alive) {
      xcoord += xVelocity * speed * delta
      ycoord += yVelocity * speed * delta

      if (math.hypot(math.abs(initX - xcoord), math.abs(initY - ycoord)) > range) alive = false

      for (b <- bs) {
        if (isCollide(xcoord, ycoord, d, d, b.x, b.y, b.balloonType.width, b.balloonType.height) && this.alive) {
          alive = false
          b.hit()
          MainApp.playEffect(popEffectMediaPlayer)
        }
      }
      this.draw(gc)
    }
  }
}
