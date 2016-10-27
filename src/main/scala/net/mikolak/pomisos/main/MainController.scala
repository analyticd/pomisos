package net.mikolak.pomisos.main

import java.time.Instant

import gremlin.scala.ScalaGraph
import net.mikolak.pomisos.crud.{AddNew, AddNewController}
import net.mikolak.pomisos.data.Id
import net.mikolak.pomisos.prefs._
import net.mikolak.pomisos.run.{RunView, RunViewController}
import net.mikolak.pomisos.utils.Notifications

import scala.concurrent.duration._
import scala.language.{implicitConversions, postfixOps}
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.scene.layout.VBox
import scalafxml.core.macros.{nested, sfxml}

@sfxml
class MainController(@nested[AddNewController] val newPomodoroController: AddNew,
                     @nested[PrefsController] val prefsController: PrefsPage,
                     @nested[PomodoroTableController] pomodoroTableController: PomodoroTable,
                     @nested[RunViewController] runStatusController: RunView,
                     val management: VBox,
                     val runStatus: VBox,
                     val db: ScalaGraph,
                     val icon: MainIcon,
                     notifications: Notifications) {

  runStatusController.runningPomodoro <==> pomodoroTableController.pomodoroToRun

  newPomodoroController.newName.onChange((_, _, newValue) => newValue.foreach(pomodoroTableController.addItem))

  lazy val prefsVisible = prefsController.visible

  management.visible <== !runStatusController.isRunning && !prefsVisible
  runStatus.visible <== runStatusController.isRunning

  def generalPrefMenu(event: ActionEvent): Unit = {
    prefsController.mode.value = GeneralPreferences
    prefsVisible.value = true
  }

  def appMenu(event: ActionEvent): Unit = {
    prefsController.mode.value = AppPreferences
    prefsVisible.value = true
  }

}

case class TimerPeriod(id: Option[Id],name: String, duration: Duration)

case class PomodoroRun(endTime: Instant, duration: Duration)