package ch.makery.address.view

import scalafx.event.ActionEvent
import scalafx.stage.Stage
import scalafxml.core.macros.sfxml

@sfxml
class GuideController() {
  var dialogStage: Stage = null

  def handleBackMenu(action: ActionEvent): Unit = {
    dialogStage.close()
  }
}
