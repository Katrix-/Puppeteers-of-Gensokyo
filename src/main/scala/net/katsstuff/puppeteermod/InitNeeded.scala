package net.katsstuff.puppeteermod

import net.katsstuff.puppeteermod.helper.LogHelper

trait InitNeeded {

	def init(): Unit = LogHelper.debug(s"Loaded ${getClass.getName}")

}
