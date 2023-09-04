package ch.makery.address

import ch.makery.address.model.Record
import ch.makery.address.util.Database
import ch.makery.address.view.{GuideController, HistoryController}
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import scalafx.scene.Scene
import scalafx.Includes._
import javafx.{scene => jfxs}
import scalafx.collections.ObservableBuffer
import scalafx.scene.image.Image
import scalafx.scene.media.MediaPlayer
import scalafx.stage.{Modality, Stage}

object MainApp extends JFXApp{
  Database.setupDB()
  val recordData = new ObservableBuffer[Record]()
  recordData ++= Record.getAllRecords

  val rootResource = getClass.getResource("view/RootLayout.fxml")
  val loader = new FXMLLoader(rootResource, NoDependencyResolver)
  loader.load()

  val roots = loader.getRoot[jfxs.layout.BorderPane]
  val mainMenuScene = new Scene() {
    root = roots
  }
  stage = new PrimaryStage {
    title = "Tower Defense"
    icons += new Image("images/Icon.png")
    scene = mainMenuScene
    resizable = false
  }

  def showMainMenu(): Unit = {
    val mainMenuLoader = new FXMLLoader(getClass.getResource("view/MainMenu.fxml"), NoDependencyResolver)
    mainMenuLoader.load()

    val roots = mainMenuLoader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(roots)
  }

  def showHistory(): Unit = {
    val resource = getClass.getResource("view/History.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val roots2 = loader.getRoot[jfxs.Parent]
    val control = loader.getController[HistoryController#Controller]

    val dialog = new Stage() {
      initModality(Modality.ApplicationModal)
      initOwner(stage)
      title = "Tower Defense History"
      icons += new Image("images/Icon.png")
      scene = new Scene {
        root = roots2
        resizable = false
      }
    }

    control.dialogStage = dialog
    dialog.showAndWait()
  }

  def showGuide(): Unit = {
    val resource = getClass.getResource("view/Guide.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val roots4 = loader.getRoot[jfxs.Parent]
    val control = loader.getController[GuideController#Controller]

    val dialog = new Stage() {
      initModality(Modality.ApplicationModal)
      initOwner(stage)
      title = "Tower Defense Guide"
      icons += new Image("images/Icon.png")
      scene = new Scene {
        root = roots4
        resizable = false
      }
    }

    control.dialogStage = dialog
    dialog.showAndWait()
  }

  def startGame(): Unit = {
    val resource = getClass.getResource("view/Game.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val roots3 = loader.getRoot[jfxs.Parent]

    stage.scene = new Scene() {
      root = roots3
    }
  }

  def playAudio(mp: MediaPlayer): Unit = {
    mp.setCycleCount(MediaPlayer.Indefinite)
    mp.play()
  }

  def playEffect(mp: MediaPlayer): Unit = {
    mp.volume = 0.5
    mp.play()
  }

  showMainMenu()
}
