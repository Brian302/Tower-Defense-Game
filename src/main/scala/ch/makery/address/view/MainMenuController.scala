package ch.makery.address.view

import ch.makery.address.MainApp
import scalafx.event.ActionEvent
import scalafx.scene.control.Slider
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.MouseEvent
import scalafx.scene.media.{Media, MediaPlayer}
import scalafxml.core.macros.sfxml

import java.io.File

@sfxml
class MainMenuController(
                          private val mainMenuAudioSlider: Slider,
                          private var mainMenuAudio: ImageView
                        ) {
  private val mainMenuMedia = new Media(new File("src/main/resources/audio/MainMenuMusic.mp3").toURI.toString)
  private val mainMenuMediaPlayer = new MediaPlayer(mainMenuMedia)
  private var previousVolume: Double = mainMenuAudioSlider.value()

  def handleAudio(me: MouseEvent): Unit = {
    if (mainMenuMediaPlayer.volume() == 0.0) {
      mainMenuMediaPlayer.volume() = this.previousVolume
    } else {
      this.previousVolume = mainMenuMediaPlayer.volume()
      mainMenuMediaPlayer.volume = 0.0
    }
  }

  def showHistory(action: ActionEvent): Unit = {
    MainApp.showHistory()
  }

  def handleStartGame(action:ActionEvent): Unit = {
    mainMenuMediaPlayer.stop()
    MainApp.startGame()
  }

  def handleQuit(action:ActionEvent): Unit = {
    MainApp.stage.close()
  }

  def showGuide(action: ActionEvent): Unit = {
    MainApp.showGuide()
  }

  private val mute = new Image("images/MainMenuAudio_Muted.png")
  private val low = new Image("images/MainMenuAudio_Low.png")
  private val medium = new Image("images/MainMenuAudio_Medium.png")
  private val high = new Image("images/MainMenuAudio_High.png")

  mainMenuMediaPlayer.volume <==> mainMenuAudioSlider.value
  mainMenuAudioSlider.value.onChange(
    mainMenuAudioSlider.value() match {
      case 0.0 => mainMenuAudio.image = mute
      case x if x <= 0.33 => mainMenuAudio.image = low
      case x if x <= 0.66 => mainMenuAudio.image = medium
      case x if x <= 1.0 => mainMenuAudio.image = high
    }
  )

  MainApp.playAudio(mainMenuMediaPlayer)
}
