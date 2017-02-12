package net.mikolak.pomisos.quality

import java.time.Instant

import gremlin.scala.ScalaGraph
import gremlin.scala._
import smile.regression._
import ConsecutiveQuality._
import net.mikolak.pomisos.data.DB

class ConsecutiveQuality(db: DB) {
  def predict(): Option[Double] = {
    val lastQualities = db().V
      .hasLabel[PomodoroQuality]
      .toCC[PomodoroQuality]
      .toList
      .sortBy(_.timestamp)(Ordering.fromLessThan(_ isBefore _))
      .takeRight(LastQualitiesCount)
    if (lastQualities.isEmpty) {
      None
    } else {
      //TODO clean this up - breaks for LastQualitiesCount identical consecutive values (esp. 10)
      if (lastQualities.map(_.quality).distinct.length == 1) {
        Some(lastQualities.head.quality)
      } else {
        val OLS = ols(
          lastQualities.map(a => Array(a.timestamp.getEpochSecond.toDouble)).toArray,
          lastQualities.map(_.quality.toDouble).toArray
        )
        Some(
          OLS.predict(
            Array(Instant.now().getEpochSecond.toDouble)
          )
        )
      }
    }

  }
}

object ConsecutiveQuality {
  val LastQualitiesCount = 5
}
