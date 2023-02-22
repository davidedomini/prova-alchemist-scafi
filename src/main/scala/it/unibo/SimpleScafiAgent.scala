package it.unibo

import it.unibo.alchemist.model.scafi.ScafiIncarnationForAlchemist._

class SimpleScafiAgent 
  extends AggregateProgram 
  with FieldUtils
  with StandardSensors
  with BlockG
  with ScafiAlchemistSupport
  with CustomSpawn {

  override def main(): Unit = println(mid())

}
