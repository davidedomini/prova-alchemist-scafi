package it.unibo

import it.unibo.alchemist.boundary.interfaces.OutputMonitor
import it.unibo.alchemist.core.implementations.Engine
import it.unibo.alchemist.core.interfaces.Status
import it.unibo.alchemist.loader.LoadAlchemist
import it.unibo.alchemist.model.implementations.molecules.SimpleMolecule
import it.unibo.alchemist.model.interfaces.{Actionable, Environment, Position, Time}

import java.io.File
import java.util.concurrent.{Semaphore, TimeUnit}
import java.util.concurrent.locks.ReentrantLock
import scala.jdk.CollectionConverters.IteratorHasAsScala
import scala.language.existentials

object TestLauncher extends App {

  val file = new File("/home/gianluca/Programming/IdeaProjects/prova-alchemist-scafi/src/main/yaml/simple.yml")
  val lock = new Semaphore(0)
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
                  ): Unit = {
        if (time.toDouble >= stopWhen ) {
          lock.release()
          engine.pause()
        }
      }

      override def finished(environment: Environment[Any, P], time: Time, step: Long): Unit = {}

      override def initialized(environment: Environment[Any, P]): Unit = {
        println("here..")
      }
    }

    var outputMonitor = timeToPause(1)
    engine.addOutputMonitor(outputMonitor)

    engine.play()
    new Thread(() => engine.run()).start()
    new Thread(() => while(true) {
      engine.play()
      Thread.sleep(1000)
    }).start()
    while (true) {
      lock.acquire()
      //engine.waitFor(Status.PAUSED, Long.MaxValue, TimeUnit.SECONDS)
      val getTime = engine.getTime
      println(getTime)
      engine.removeOutputMonitor(outputMonitor)
      outputMonitor = timeToPause(getTime.toDouble + 1)
      engine.addOutputMonitor(outputMonitor)
      // TODO FIX IN ALCHEMIST
      engine.play()
      /*
      do {
        engine.play()
        Thread.sleep(100)
        engine.waitFor(Status.RUNNING, 1, TimeUnit.MILLISECONDS)
      } while(engine.getStatus == Status.PAUSED)*/
    }
  }
}