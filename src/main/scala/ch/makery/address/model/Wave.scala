package ch.makery.address.model

import scalafx.scene.canvas.GraphicsContext

import scala.collection.mutable.ListBuffer
import scala.util.Random

class Wave(
            grid: TileGrid, var numOfRed: Int, var numOfBlue: Int,
            var numOfGreen: Int, var numOfYellow: Int, var numOfPurple: Int,
            gc: GraphicsContext
          ) {
  var balloons: ListBuffer[Balloon] = ListBuffer[Balloon]()
  var totalBalloons: Int = numOfRed + numOfBlue + numOfGreen + numOfYellow + numOfPurple
  var waveCompleted = false
  var balloonsPassed = 0
  var timeBetweenSpawn: Double = 0
  val minimumSpawnDelay = 0.2
  val maximumSpawnDelay = 1.2

  var wps = ListBuffer[Waypoint]()
  if (wps.isEmpty && balloons.nonEmpty) wps = balloons.head.waypoints

  def isCompleted: Boolean = waveCompleted

  def checkPassed(): Unit = {
    balloons.foreach(b => if (!b.alive && b.outOfBounds) balloonsPassed += 1)
  }

  def update(delta: Double): Unit = {
    this.checkPassed()
    if (totalBalloons > 0) {
      timeBetweenSpawn += delta
      if (timeBetweenSpawn > minimumSpawnDelay + Random.nextDouble() * (maximumSpawnDelay - minimumSpawnDelay) && totalBalloons != 0) {
        this.spawn()
        totalBalloons -= 1
        timeBetweenSpawn = 0
      }
    }

    balloons = balloons.filter(_.alive)
    balloons.foreach(b => {
      b.update(delta)
      b.draw(gc)
    })

    if (totalBalloons == 0 && balloons.isEmpty) waveCompleted = true
  }

  def spawn(): ListBuffer[Balloon] = {
    val choice = Random.nextInt(totalBalloons)
    if ((0 until numOfRed).contains(choice) && numOfRed > 0) {
      numOfRed -= 1
      balloons += new Balloon(RedBalloon, grid, wps)
    } else if ((numOfRed until numOfRed+numOfBlue).contains(choice) && numOfBlue > 0){
      numOfBlue -= 1
      balloons += new Balloon(BlueBalloon, grid, wps)
    } else if ((numOfRed+numOfBlue until totalBalloons-numOfYellow-numOfPurple).contains(choice) && numOfGreen > 0) {
      numOfGreen -= 1
      balloons += new Balloon(GreenBalloon, grid, wps)
    } else if ((totalBalloons-numOfYellow-numOfPurple until totalBalloons-numOfPurple).contains(choice) && numOfYellow > 0) {
      numOfYellow -= 1
      balloons += new Balloon(YellowBalloon, grid, wps)
    } else if ((totalBalloons-numOfPurple until totalBalloons).contains(choice) && numOfPurple > 0) {
      numOfPurple -= 1
      balloons += new Balloon(PurpleBalloon, grid, wps)
    } else spawn()
  }
}
