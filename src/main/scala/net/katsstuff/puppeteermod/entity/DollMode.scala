package net.katsstuff.puppeteermod.entity

sealed trait DollMode
object DollMode {
	case object Follow extends DollMode
	case object StandBy extends DollMode
	case object RideOn extends DollMode
	case object Patrol extends DollMode
}