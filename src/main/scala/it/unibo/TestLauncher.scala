package it.unibo

import it.unibo.alchemist.boundary.interfaces.OutputMonitor
import it.unibo.alchemist.core.implementations.Engine
import it.unibo.alchemist.core.interfaces.Status
import it.unibo.alchemist.loader.LoadAlchemist
import it.unibo.alchemist.model.implementations.molecules.SimpleMolecule
import it.unibo.alchemist.model.interfaces.{Environment, Position, Actionable, Time}
import java.io.File
import java.util.concurrent.TimeUnit
import scala.jdk.CollectionConverters.IteratorHasAsScala
import scala.language.existentials

object TestLauncher extends App {

  val file = new File("/Users/davidedomini/Desktop/prova-scafi-alchemist/src/main/yaml/simple.yml")
  start(file)

  private def start[P <: Position[P]](file: File): Unit = {
    val env = LoadAlchemist.from(file).getDefault[Any, P]().getEnvironment
    val engine = new Engine(env)

    def timeToPause(stopWhen: Double) : OutputMonitor[Any, P] = new OutputMonitor[Any, P]() {
      override def stepDone(
                    environment: Environment[Any, P],
                    reaction: Actionable[Any],
                    time: Time,
                    step: Long
                  ): Unit =
        if (time.toDouble >= stopWhen ) {
          engine.pause()
        }
    }

    var outputMonitor = timeToPause(1)
    engine.addOutputMonitor(outputMonitor)

    engine.play()
    new Thread(() => engine.run()).start()

    while (true) {
      println("Paused")
      engine.waitFor(Status.PAUSED, Long.MaxValue, TimeUnit.SECONDS)
      println("Wake up")
      val getTime = engine.getTime
      println(getTime)
      engine.removeOutputMonitor(outputMonitor)
      outputMonitor = timeToPause(getTime.toDouble + 1)
      engine.addOutputMonitor(outputMonitor)
      engine.play()
      println("Here..")
      engine.waitFor(Status.RUNNING, Long.MaxValue, TimeUnit.SECONDS)
      println("Run again")
    }
  }
}