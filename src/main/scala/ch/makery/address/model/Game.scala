package ch.makery.address.model

class Game() {
  val map: Array[Array[Int]] = Array(
    Array(0, 0, 0, 0, 0, 1, 1, 1, 1, 0),
    Array(1, 1, 1, 1, 0, 1, 0, 0, 1, 0),
    Array(0, 0, 0, 1, 0, 1, 1, 0, 1, 0),
    Array(0, 1, 1, 1, 0, 0, 1, 0, 1, 0),
    Array(0, 1, 0, 0, 0, 0, 1, 0, 1, 0),
    Array(0, 1, 0, 1, 1, 1, 1, 0, 1, 0),
    Array(0, 1, 0, 1, 0, 0, 0, 0, 1, 0),
    Array(0, 1, 1, 1, 0, 0, 0, 0, 1, 0),
    Array(0, 0, 0, 0, 0, 0, 0, 0, 1, 0))
  val grid: TileGrid = new TileGrid(map)
  var waveManager: WaveManager = null
  var player: Player = null
}
