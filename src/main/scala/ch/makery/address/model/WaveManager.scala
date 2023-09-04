package ch.makery.address.model

import scalafx.scene.canvas.GraphicsContext

class WaveManager(gc: GraphicsContext, grid: TileGrid) {
  var waveNumber = 0
  var currentWave: Wave = null
  val maximumBalloons: Int = 35
  val balloonsStartingWave: Map[BalloonType, Int] = Map(RedBalloon -> 0,BlueBalloon -> 4, GreenBalloon -> 9, YellowBalloon -> 14, PurpleBalloon -> 19)
  var red: Int = 5
  var blue: Int = 0
  var green: Int = 0
  var yellow: Int = 0
  var purple: Int = 0

  this.newWave()

  def getCurrentWave: Wave = this.currentWave

  def newWave(): Unit = {
    if (waveNumber == 0) {
      currentWave = new Wave(grid, red, blue, green, yellow, purple, gc)
    } else {
      if (waveNumber >= balloonsStartingWave(PurpleBalloon)) {
        purple = waveNumber - balloonsStartingWave(PurpleBalloon) + 1
      }
      if (waveNumber >= balloonsStartingWave(YellowBalloon)) {
        yellow = waveNumber - balloonsStartingWave(YellowBalloon) + 1
      }
      if (waveNumber >= balloonsStartingWave(GreenBalloon)) {
        green = waveNumber - balloonsStartingWave(GreenBalloon) + 1
      }
      if (waveNumber >= balloonsStartingWave(BlueBalloon)) {
        blue = waveNumber - balloonsStartingWave(BlueBalloon) + 1
      }
      red = 5 + waveNumber

      while (red > 0 && red+blue+green+yellow+purple > maximumBalloons) red -= 1
      while (blue > 0 && red+blue+green+yellow+purple > maximumBalloons) blue -= 1
      while (green > 0 && red+blue+green+yellow+purple > maximumBalloons) green -= 1
      while (yellow > 0 && red+blue+green+yellow+purple > maximumBalloons) yellow -= 1
      while (purple > 0 && red+blue+green+yellow+purple > maximumBalloons) purple -= 1

      currentWave = new Wave(grid, red, blue, green, yellow, purple, gc)
    }
    waveNumber += 1
  }

  def update(delta: Double): Unit = if(!currentWave.isCompleted) currentWave.update(delta)
}
