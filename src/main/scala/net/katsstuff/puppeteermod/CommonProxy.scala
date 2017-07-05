package net.katsstuff.puppeteermod

import net.katsstuff.puppeteermod.entity.dolltype.{DollType, DollTypeBare}
import net.katsstuff.puppeteermod.item.{ItemDoll, ItemDollCore}
import net.minecraft.item.Item
import net.minecraftforge.event.RegistryEvent

object CommonProxy {

	def registerItems(event: RegistryEvent.Register[Item]): Unit = {
		event.getRegistry.registerAll(
			new ItemDoll,
			new ItemDollCore
		)
	}

	def registerDolls(event: RegistryEvent.Register[DollType]): Unit = {
		event.getRegistry.registerAll(
			new DollTypeBare
		)
	}
}

class CommonProxy {

	def bakeDoll(dollType: DollType): Unit = ()

	def registerRenderers(): Unit = ()

}