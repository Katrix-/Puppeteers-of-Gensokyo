package net.katsstuff.puppeteermod

import net.katsstuff.puppeteermod.entity.EntityDoll
import net.katsstuff.puppeteermod.items.PuppeteerItems
import net.katsstuff.puppeteermod.lib.{LibEntityName, LibMod}
import net.katsstuff.puppeteermod.lib.LibModJ
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.registry.EntityRegistry
import net.minecraftforge.fml.common.{Mod, SidedProxy}

@Mod(modid = LibMod.Id, name = LibMod.Name, version = LibMod.Version, modLanguage = "scala")
object PuppeteerMod {

	assert(LibMod.Id == LibModJ.ID)

	@SidedProxy(clientSide = LibMod.ClientProxy, serverSide = LibMod.CommonProxy)
	var proxy: CommonProxy = _

	lazy val CreativeTab = new CreativeTabs(LibMod.Id) {
		lazy val getTabIconItem: Item = PuppeteerItems.Doll
	}

	@EventHandler
	def preInit(event: FMLPreInitializationEvent): Unit = {
		EntityRegistry.registerModEntity(new ResourceLocation(LibMod.Id, LibEntityName.Doll),
			classOf[EntityDoll], LibEntityName.Doll, 0, this, 64, 1, false)

		proxy.registerRenderers()
	}

	@EventHandler
	def init(event: FMLPreInitializationEvent): Unit = ()

	@EventHandler
	def postInit(event: FMLPreInitializationEvent): Unit = ()

}