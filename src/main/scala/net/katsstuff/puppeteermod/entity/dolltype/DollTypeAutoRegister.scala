package net.katsstuff.puppeteermod.entity.dolltype

import net.katsstuff.puppeteermod.PuppeteerMod
import net.minecraftforge.fml.common.registry.GameRegistry

abstract class DollTypeAutoRegister(name: String) extends DollType {
	setRegistryName(name)
	PuppeteerMod.proxy.bakeDoll(this)
	GameRegistry.addRecipe(recipe)
}
