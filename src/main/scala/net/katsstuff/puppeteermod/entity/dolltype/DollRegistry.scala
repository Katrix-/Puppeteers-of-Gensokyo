package net.katsstuff.puppeteermod.entity.dolltype

import net.katsstuff.puppeteermod.lib.LibMod
import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.{FMLControlledNamespacedRegistry, RegistryBuilder}

@EventBusSubscriber
object DollRegistry {

  var registry: FMLControlledNamespacedRegistry[DollType] = _

  @SubscribeEvent
  def registerRegisters(event: RegistryEvent.NewRegistry): Unit = {
    registry = new RegistryBuilder[DollType]
      .setIDRange(0, Short.MaxValue)
      .setName(new ResourceLocation(LibMod.Id, "doll"))
      .setType(classOf[DollType])
      .create()
      .asInstanceOf[FMLControlledNamespacedRegistry[DollType]]
  }
}
