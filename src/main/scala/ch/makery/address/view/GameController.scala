package ch.makery.address.view

import ch.makery.address.MainApp
import ch.makery.address.model.{Game, Player, Record, Structure, Tower, WaveManager}
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.layout.{Background, BackgroundFill, CornerRadii}
import scalafx.animation.AnimationTimer
import scalafx.event.ActionEvent
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.control.{Alert, Button, ButtonType, DialogEvent, Label}
import scalafx.scene.control.Alert.AlertType
import scalafxml.core.macros.sfxml
import scalafx.Includes._
import scalafx.beans.property.StringProperty
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{AnchorPane, ColumnConstraints, GridPane}
import scalafx.scene.media.{Media, MediaPlayer}
import scalafx.scene.paint.Color
import scalafx.scene.text.Font
import scalafx.util.Duration

import java.io.File

@sfxml
class GameController (
                     private var moneyLabel: Label,
                     private var healthLabel: Label,
                     private var waveLabel: Label,
                     private var gameCanvas: Canvas,
                     private var gameStartButton: Button,
                     private var structureButtons: GridPane,
                     private var textPane: AnchorPane,
                     private var pauseButton: Button,
                     private var game_0: ImageView, private var game_1: ImageView, private var game_2: ImageView,
                     private var game_3: ImageView, private var game_4: ImageView, private var game_5: ImageView
                     ) {
  //TODO update list
  val gameMedia = new Media(new File("src/main/resources/audio/Game_GainMoney.mp3").toURI.toString)
  val gameEndMedia = new Media(new File("src/main/resources/audio/Game_End.mp3").toURI.toString)
  val gameMediaPlayer = new MediaPlayer(gameMedia)
  val gameEndMediaPlayer = new MediaPlayer(gameEndMedia)
  gameMediaPlayer.setOnEndOfMedia(() => {
    gameMediaPlayer.seek(Duration.Zero)
    gameMediaPlayer.pause()
  })
  val availableStructures: List[String] = List("Tower", "", "", "", "", "")
  var alive = true
  val gc: GraphicsContext = gameCanvas.graphicsContext2D
  val game = new Game()
  var firstWaveSpawn = false
  var addMoney = true
  var selectedStructure: Option[Node] = None
  var editingStructure: Structure = null
  var okSet: Boolean = false
  game.waveManager = new WaveManager(gc, game.grid)
  game.player = new Player(game.waveManager)

  gameStartButton.onAction = (_: ActionEvent) => {
    firstWaveSpawn = true
    addMoney = true
    if (game.waveManager.getCurrentWave.isCompleted) game.waveManager.newWave()
    balloonsText = s"Incoming balloons: ${game.waveManager.red} red, " +
      s"${game.waveManager.blue} blue, " +
      s"${game.waveManager.green} green, " +
      s"${game.waveManager.yellow} yellow, " +
      s"${game.waveManager.purple} purple."
    if (!game.waveManager.getCurrentWave.isCompleted) gameText.value = balloonsText
  }

  structureButtons.children.forEach(b => b.onMouseEntered = (_: MouseEvent) => {
    b.accessibleText() match {
      case "Tower" => gameText.value = towerText
      case null => if (!game.waveManager.getCurrentWave.isCompleted && game.waveManager.waveNumber != 1) gameText.value = balloonsText else gameText.value = welcomeText
    }
  })

  structureButtons.onMouseExited = (_: MouseEvent) => {
    selectedStructure = structureButtons.children.find(_.asInstanceOf[javafx.scene.control.ToggleButton].isSelected)
    if (!game.waveManager.getCurrentWave.isCompleted) if(!firstWaveSpawn) gameText.value = welcomeText else gameText.value = balloonsText else gameText.value = welcomeText
  }

  gameCanvas.onMouseClicked = (me: MouseEvent) => {
    selectedStructure match {
      case Some(selectedstructure) =>
        okSet = game.player.setStructure(game.grid, me.x, me.y, selectedstructure, gc)
        if (okSet) {
          structureButtons.children.foreach(_.asInstanceOf[javafx.scene.control.ToggleButton].setSelected(false))
          selectedStructure = None
          circleDraw = false
          okSet = false
        }
      case None =>
        mouseX = me.x
        mouseY = me.y
        editingStructure = game.player.selectStructure(mouseX, mouseY, gc)
        if (editingStructure != null) {
          editingStructure match {
            case tower: Tower =>

              if (editingStructure.range() < Tower.maximumRange) rangeCost.value = (Tower.startingRangeCost + editingStructure.rangeLevel * Tower.increaseRangeCost).toString else rangeCost.value = "MAX"
              rangeText.value = (tower.range().toDouble / 64.0).toString + " -> " + (tower.range().toDouble / 64.0 + Tower.increaseRange).toString
              if (BigDecimal(tower.timeToShoot).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble > Tower.maximumSpeed) speedCost.value = (Tower.startingSpeedCost + editingStructure.speedLevel * Tower.increaseSpeedCost).toString else speedCost.value = "MAX"
              speedText.value = BigDecimal(tower.timeToShoot).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble.toString + " -> " + BigDecimal(tower.timeToShoot-Tower.increaseSpeed).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble.toString
            case _ =>
          }
          statsPane.disable = false
        } else {
          rangeCost.value = ""
          rangeText.value = ""
          speedCost.value = ""
          speedText.value = ""
          statsPane.disable = true
        }
    }
  }

  var mouseX: Double = 0
  var mouseY: Double = 0
  var circleX: Double = 0
  var circleY: Double = 0
  var circleDraw: Boolean = false

  gameCanvas.onMouseEntered = (_: MouseEvent) => circleDraw = true

  gameCanvas.onMouseExited = (_: MouseEvent) => circleDraw = false

  gameCanvas.onMouseMoved = (me: MouseEvent) => {
    circleX = me.x
    circleY = me.y
  }

  val welcomeText = "Welcome to a game I created that was inspired by Bloons TD 1!\n" +
    "The rules are simple: Place towers, level them up, pop balloons!\n\nNote: lag will cause issues, prevent lag by minimizing balloons on screen!"
  val towerText = "Tower: The most basic tower in the game.\nShoots a singular bullet at balloons' current location every time it fires, may miss!\n\nCost: 250"
  var balloonsText = ""
  var rangeText = new StringProperty("")
  var rangeCost = new StringProperty("")
  var speedText = new StringProperty("")
  var speedCost = new StringProperty("")
  val defaultCost = new StringProperty("Cost: ")

  var gameText = new StringProperty(welcomeText)
  val gameLabel = new Label()
  val rangeLabel = new Label("Current range: ")
  val rangeNumber = new Label()
  val rangeButton = new Button(defaultCost())
  rangeButton.onAction = (action: ActionEvent) => {
    if (!rangeCost().equals("MAX")) {
      val okUp = game.player.upgradeRange(editingStructure, rangeCost().toInt)
      if (okUp) {
        editingStructure match {
          case tower: Tower =>
            if (tower.range() < Tower.maximumRange) rangeCost.value = (Tower.startingRangeCost + editingStructure.rangeLevel * Tower.increaseRangeCost).toString else rangeCost.value = "MAX"
            rangeText.value = (tower.range().toDouble / 64.0).toString + " -> " + (tower.range().toDouble / 64.0 + Tower.increaseRange).toString
        }
      }
    }
  }
  val speedLabel = new Label("Current time to fire: ")
  val speedNumber = new Label()
  val speedButton = new Button(defaultCost())
  speedButton.onAction = (action: ActionEvent) => {
    if (!speedCost().equals("MAX")) {
      val okUp = game.player.upgradeSpeed(editingStructure, speedCost().toInt)
      if (okUp) {
        editingStructure match {
          case tower: Tower =>
            if (BigDecimal(tower.timeToShoot).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble > Tower.maximumSpeed) speedCost.value = (Tower.startingSpeedCost + editingStructure.speedLevel * Tower.increaseSpeedCost).toString else speedCost.value = "MAX"
            speedText.value = BigDecimal(tower.timeToShoot).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble.toString + " -> " + BigDecimal(tower.timeToShoot - Tower.increaseSpeed).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble.toString
        }
      }
    }
  }

  gameLabel.text <== gameText
  rangeNumber.text <== rangeText
  rangeButton.text <== defaultCost + rangeCost
  speedNumber.text <== speedText
  speedButton.text <== defaultCost + speedCost

  gameLabel.setWrapText(true)
  gameLabel.setPadding(new Insets(10))
  gameLabel.setBackground(new Background(new BackgroundFill(Color.rgb(151, 254, 237), CornerRadii.EMPTY, Insets.EMPTY)))
  gameLabel.setFont(new Font("Comic Sans MS", 17))
  rangeLabel.setFont(new Font("Comic Sans MS", 16))
  rangeNumber.setFont(new Font("Comic Sans MS", 16))
  speedLabel.setFont(new Font("Comic Sans MS", 16))
  speedNumber.setFont(new Font("Comic Sans MS", 16))
  gameLabel.minHeight = textPane.minHeight()
  gameLabel.prefWidth = 2 * textPane.minWidth() / 3
  val statsPane = new GridPane()
  statsPane.add(rangeLabel, 0, 0, 1, 1)
  statsPane.add(rangeNumber, 1, 0, 1, 1)
  statsPane.add(rangeButton, 0, 1, 2, 1)
  statsPane.add(speedLabel, 0, 2, 1, 1)
  statsPane.add(speedNumber, 1, 2, 1, 1)
  statsPane.add(speedButton, 0, 3, 2, 1)
  statsPane.setVgap(10.0)
  statsPane.setHgap(10.0)
  statsPane.setPadding(new Insets(5))
  statsPane.getColumnConstraints.add(new ColumnConstraints(170.0))
  statsPane.disable = true

  textPane.children = List(gameLabel, statsPane)
  AnchorPane.setTopAnchor(statsPane, 20.0)
  AnchorPane.setLeftAnchor(statsPane, 610.0)

  var lastTime = 0L
  var timer: AnimationTimer = AnimationTimer { t =>
    if (lastTime != 0) {
      val delta = (t - lastTime) / 1e9
      if (alive && game.waveManager.waveNumber <= 55) {
        game.grid.draw(gc)
        if (firstWaveSpawn) game.waveManager.update(delta)
        game.player.update(delta)
        game.player.drawRange(game.grid, circleX, circleY, selectedStructure, gc, circleDraw)
        game.player.selectStructure(mouseX, mouseY, gc)

        healthLabel.text = game.player.health.toString
        waveLabel.text = game.waveManager.waveNumber.toString
        moneyLabel.text = game.player.money.toString

        if (game.waveManager.currentWave.isCompleted) {
          if (addMoney) {
            gameText.value = welcomeText
            game.player.money += 100
            MainApp.playEffect(gameMediaPlayer)
            game.player.score += game.waveManager.waveNumber * game.player.health
            addMoney = false
          }
        }
        if (game.player.health <= 0) alive = false
      } else {
        timer.stop()
        MainApp.playEffect(gameEndMediaPlayer)
        val alert = new Alert(AlertType.Information) {
          title = "Tower Defense End"
          headerText = "Game Ended"
          contentText = s"Congratulations! You got a score of ${game.player.score.toString}"
          if (game.waveManager.waveNumber == 55) contentText.value += " after beating every wave!"
          onHidden = (e: DialogEvent) => {
            val newRecord = new Record(game.player.score)
            newRecord.save()
            MainApp.recordData += newRecord
            MainApp.stage.scene = MainApp.mainMenuScene
            MainApp.showMainMenu()
          }
        }
        alert.show()
      }
    }
    lastTime = t
  }
  timer.start()

  def showConfirmationPopUp(t: String, ht: String): Boolean = {
    val alert = new Alert(AlertType.Confirmation) {
      title = t
      headerText = ht
      buttonTypes = Seq(ButtonType.Yes, ButtonType.No)
    }

    val result = alert.showAndWait()
    result match {
      case Some(ButtonType.Yes) => true
      case _ => false
    }
  }

  def handleQuitGame(action: ActionEvent): Unit = {
    if (showConfirmationPopUp("Confirmation Dialog", "Are you sure to quit the game and exit to main menu?\nGame progress will be lost!")) {
      timer.stop()
      MainApp.stage.scene = MainApp.mainMenuScene
      MainApp.showMainMenu()
    }
  }

  pauseButton.disable = true
  def handlePauseGame(action: ActionEvent): Unit = {
    if (pauseButton.text().equals("Pause Game")) {
      timer.stop()
      pauseButton.text = "Continue Game"
    } else {
      timer.start()
      pauseButton.text = "Pause Game"
    }
  }

  //TODO update images
  def showStructures(): Unit = {
    for (i <- 0 until game.player.unlockedStrucures) {
      structureButtons.children(i).accessibleText = availableStructures(i)
      i match {
        case 0 => game_0.image = new Image("images/Game_Tower.png")
        case 1 => game_1.image = new Image("images/Game_Tower.png")
        case 2 => game_2.image = new Image("images/Game_Tower.png")
        case 3 => game_3.image = new Image("images/Game_Tower.png")
        case 4 => game_4.image = new Image("images/Game_Tower.png")
        case 5 => game_5.image = new Image("images/Game_Tower.png")
      }
    }
  }

  showStructures()
}