package ch.makery.address.model

import javafx.scene.Node
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color

import scala.collection.mutable.ListBuffer

class Player(val waveManager: WaveManager) {
  var score = 0
  var health = 30
  var money = 500
  val towerPrice = 250

  val minDist = 48
  var placedStructures: ListBuffer[Structure] = ListBuffer[Structure]()
  var unlockedStrucures: Int = 1

  def updateHealthAndMoney(): Unit = {
    val passed: Int = waveManager.getCurrentWave.balloonsPassed
    health -= passed
    waveManager.getCurrentWave.balloonsPassed = 0

    val hit: Int = Balloon.totalHits
    money += hit
    Balloon.totalHits = 0
  }

  def setStructure(grid: TileGrid, x: Double, y: Double, structure: Node, gc: GraphicsContext): Boolean = {
    val tile = grid.getTile((x/64).toInt * 64, (y/64).toInt * 64)
    if (tile.buildable && structure.getAccessibleText != null) {
      if (structure.getAccessibleText.equals("Tower") && money >= towerPrice && checkPlace(x, y)) {
        placedStructures += new Tower(x.toInt, y.toInt, gc, waveManager.getCurrentWave.balloons)
        money -= towerPrice
        true
      } else false
    } else false
  }

  def selectStructure(x: Double, y: Double, gc: GraphicsContext): Structure = {
    var st: Structure = null
    for (s <- placedStructures) {
      if (dist(x, y, s.xcoord, s.ycoord - s.height/4) <= 24) {
        if (s.isInstanceOf[Tower]) {
          gc.fill = Color.rgb(255, 255, 255, 0.4)
          gc.fillOval(s.xcoord - s.range(), s.ycoord - s.range(), s.range() * 2, s.range() * 2)
        }
        st = s
      }
    }
    st
  }

  def upgradeRange(st: Structure, cost: Int): Boolean = {
    st match {
      case st: Tower =>
        if (money >= cost) {
          st.rangeLevel += 1
          st.range() += (Tower.increaseRange * 64).toInt
          money -= cost
          true
        } else false
    }
  }

  def upgradeSpeed(st: Structure, cost: Int): Boolean = {
    st match {
      case st: Tower =>
        if (money >= cost) {
          st.speedLevel += 1
          st.timeToShoot -= Tower.increaseSpeed
          money -= cost
          true
        } else false
    }
  }

  def drawRange(grid: TileGrid, x: Double, y: Double, structure: Option[Node], gc: GraphicsContext, bool: Boolean): Unit = {
    if (bool) {
      val tile = grid.getTile((x / 64).toInt * 64, (y / 64).toInt * 64)
      structure match {
        case Some(structure) => if (tile.buildable && structure.getAccessibleText != null) {
          if (structure.getAccessibleText.equals("Tower")) {
            if (money >= towerPrice && checkPlace(x, y)) {
              gc.fill = Color.rgb(255, 255, 255, 0.4)
              gc.fillOval(x - Tower.startingRange, y - Tower.startingRange, Tower.startingRange * 2, Tower.startingRange * 2)
            } else {
              gc.fill = Color.rgb(255, 0, 0, 0.5)
              gc.fillOval(x - Tower.startingRange, y - Tower.startingRange, Tower.startingRange * 2, Tower.startingRange * 2)
            }
          }
        }
        case None =>
      }
    }
  }

  def dist(x1: Double, y1: Double, x2: Double, y2: Double): Double = math.hypot(math.abs(x1 - x2), math.abs(y1 - y2))

  def checkPlace(x: Double, y: Double): Boolean = {
    var result = true
    placedStructures.foreach(s => if(dist(s.xcoord, s.ycoord, x, y) <= minDist) result = false)
    result
  }

  def update(delta: Double): Unit = {
    this.updateHealthAndMoney()
    placedStructures.foreach(s => {
      s.update(delta)
      s.updateBs(waveManager.getCurrentWave.balloons)
    })
  }
}