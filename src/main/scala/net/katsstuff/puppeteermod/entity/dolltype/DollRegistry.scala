package net.katsstuff.puppeteermod.entity.dolltype

import net.katsstuff.puppeteermod.lib.LibMod
import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.{FMLControlledNamespacedRegistry, IForgeRegistry, RegistryBuilder}

object DollRegistry {

  var registry: IForgeRegistry[DollType] = _

  @SubscribeEvent
  def registerRegisters(event: RegistryEvent.NewRegistry): Unit = {
    registry = new RegistryBuilder[DollType]
      .setIDRange(0, Short.MaxValue)
      .setName(new ResourceLocation(LibMod.Id, "doll"))
      .setType(classOf[DollType])
      .create()
  }

  def getId(doll: DollType): Int = registry.asInstanceOf[FMLControlledNamespacedRegistry[DollType]].getId(doll)
  def dollFromId(id: Int): Option[DollType] = Option(registry.asInstanceOf[FMLControlledNamespacedRegistry[DollType]].getObjectById(id))
}
