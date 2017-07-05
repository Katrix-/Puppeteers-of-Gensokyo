package net.katsstuff.puppeteermod

import net.katsstuff.puppeteermod.client.ClientProxy
import net.katsstuff.puppeteermod.entity.EntityDoll
import net.katsstuff.puppeteermod.entity.dolltype.DollRegistry
import net.katsstuff.puppeteermod.items.PuppeteerItems
import net.katsstuff.puppeteermod.lib.{LibEntityName, LibMod, LibModJ}
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.registry.EntityRegistry
import net.minecraftforge.fml.common.{FMLCommonHandler, Mod, SidedProxy}
import net.minecraftforge.fml.relauncher.Side

@Mod(modid = LibMod.Id, name = LibMod.Name, version = LibMod.Version, modLanguage = "scala")
object PuppeteerMod {
  MinecraftForge.EVENT_BUS.register(CommonProxy)
  MinecraftForge.EVENT_BUS.register(DollRegistry)
  if(FMLCommonHandler.instance().getSide == Side.CLIENT) {
    MinecraftForge.EVENT_BUS.register(ClientProxy)
  }

  assert(LibMod.Id == LibModJ.ID)

  @SidedProxy(clientSide = LibMod.ClientProxy, serverSide = LibMod.CommonProxy)
  var proxy: CommonProxy = _

  lazy val CreativeTab = new CreativeTabs(LibMod.Id) {
    override def getTabIconItem: ItemStack = new ItemStack(PuppeteerItems.Doll)
  }

  @EventHandler
  def preInit(event: FMLPreInitializationEvent): Unit = {
    EntityRegistry.registerModEntity(
      new ResourceLocation(LibMod.Id, LibEntityName.Doll),
      classOf[EntityDoll],
      LibEntityName.Doll,
      0,
      this,
      64,
      1,
      false
    )

    proxy.registerRenderers()
  }

  @EventHandler
  def init(event: FMLPreInitializationEvent): Unit = ()

  @EventHandler
  def postInit(event: FMLPreInitializationEvent): Unit = ()

}
