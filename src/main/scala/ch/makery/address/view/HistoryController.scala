package ch.makery.address.view

import ch.makery.address.MainApp
import ch.makery.address.model.Record
import scalafx.event.ActionEvent
import scalafx.scene.control.{TableColumn, TableView}
import scalafx.stage.Stage
import scalafxml.core.macros.sfxml

@sfxml
class HistoryController(
                       private val recordTable: TableView[Record],
                       private val dateColumn: TableColumn[Record, String],
                       private val scoreColumn: TableColumn[Record, String]
                       ) {
  var dialogStage: Stage = null

  recordTable.items = MainApp.recordData
  dateColumn.cellValueFactory = {_.value.date}
  scoreColumn.cellValueFactory = {_.value.score}

  def handleBack(ae: ActionEvent): Unit = {
    dialogStage.close()
  }
}
