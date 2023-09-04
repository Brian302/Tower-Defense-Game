package ch.makery.address.model

import ch.makery.address.model.Balloon.incrementHit
import scalafx.scene.canvas.GraphicsContext

import scala.collection.mutable.ListBuffer

class Balloon(val balloonType: BalloonType, grid: TileGrid, wp: ListBuffer[Waypoint] = ListBuffer[Waypoint]()){
  val waypoints: ListBuffer[Waypoint] = wp
  var currentWaypointIndex: Int = 0
  val startXCoord: Int = -64
  val startYCoord: Int = 64
  var x: Double = startXCoord.toDouble
  var y: Double = startYCoord.toDouble
  val previousDirection: (Int, Int) = (1, 0)
  var health: Int = balloonType.health
  var speed: Int = balloonType.speed
  var outOfBounds = false
  var targetedCount: Int = 0

  if (waypoints.isEmpty) this.populateWaypoints()

  def populateWaypoints(): ListBuffer[Waypoint] = {
    val nextX: Int = startXCoord + previousDirection._1 * 64
    val nextY: Int = startYCoord + previousDirection._2 * 64
    var nextTile: Tile = grid.getTile(nextX, nextY)
    var direction = nextDirection(nextX, nextY, previousDirection)

    waypoints += new Waypoint(nextX, nextY, nextTile, direction)

    while (direction != (0,0)) {
      while (grid.getTile(nextTile.xcoord + direction._1 * 64, nextTile.ycoord + direction._2 * 64).tileType == waypoints.last.getTile.tileType) {
        nextTile = grid.getTile(nextTile.xcoord + direction._1 * 64, nextTile.ycoord + direction._2 * 64)
      }
      direction = nextDirection(nextTile.xcoord, nextTile.ycoord, direction)
      if (direction != (0,0)) waypoints += new Waypoint(nextTile.xcoord, nextTile.ycoord, grid.getTile(nextTile.xcoord, nextTile.ycoord), direction)
    }
    waypoints
  }

  def nextDirection(xcoord: Int, ycoord: Int, prev: (Int, Int)): (Int, Int) = {
    val current = grid.getTile(xcoord, ycoord)
    val up = grid.getTile(xcoord, ycoord - 64)
    val down = grid.getTile(xcoord, ycoord + 64)
    val left = grid.getTile(xcoord - 64, ycoord)
    val right = grid.getTile(xcoord + 64, ycoord)

    if (up.tileType == current.tileType && prev != (0, 1)) (0, -1)
    else if (down.tileType == current.tileType && prev != (0, -1)) (0, 1)
    else if (left.tileType == current.tileType && prev != (1, 0)) (-1, 0)
    else if (right.tileType == current.tileType && prev != (-1, 0)) (1, 0)
    else (0, 0)
  }

  def waypointReached: Boolean = {
    if (currentWaypointIndex > waypoints.length - 1) return false
    val currentWaypoint = waypoints(currentWaypointIndex)

    if (x > currentWaypoint.x - 10 && x < currentWaypoint.x + 10 && y > currentWaypoint.y - 10 && y < currentWaypoint.y + 10) {
      x = currentWaypoint.x
      y = currentWaypoint.y
      true
    } else false
  }

  def draw(gc: GraphicsContext): Unit = {
    gc.drawImage(balloonType.imgList(health-1), x, y, balloonType.width, balloonType.height)
  }

  def currentTile: Tile = grid.getTile(x.toInt, y.toInt)

  def alive: Boolean = {
    if (this.currentTile.tileType.eq(Outside)) outOfBounds = true
    if (this.y == startYCoord && this.x >= startXCoord && this.x <= 0) outOfBounds = false
    !this.outOfBounds && health > 0
  }

  def hit(): Unit = {
    incrementHit()
    health -= 1
  }

  def update(delta: Double): Unit = {
    if (currentWaypointIndex > 0) {
      x += delta * waypoints(currentWaypointIndex - 1).getNextDir._1 * speed
      y += delta * waypoints(currentWaypointIndex - 1).getNextDir._2 * speed
    } else {
      x += delta * waypoints(currentWaypointIndex).getNextDir._1 * speed
      y += delta * waypoints(currentWaypointIndex).getNextDir._2 * speed
    }

    if (this.waypointReached) {
      currentWaypointIndex += 1
    }
  }
}

object Balloon {
  var totalHits = 0

  def incrementHit(): Unit = totalHits += 1
}