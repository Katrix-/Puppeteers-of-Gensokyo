package net.katsstuff.puppeteermod.client

import scala.reflect.ClassTag

import net.katsstuff.puppeteermod.CommonProxy
import net.katsstuff.puppeteermod.client.handler.ClientDollControlHandler
import net.katsstuff.puppeteermod.dolltype.{DollType, PuppeteerDolls}
import net.katsstuff.puppeteermod.item.ItemDoll
import net.katsstuff.puppeteermod.items.PuppeteerItems
import net.minecraft.client.renderer.block.model.{ModelBakery, ModelResourceLocation => MRL}
import net.minecraft.client.renderer.entity.{Render, RenderManager}
import net.minecraft.entity.Entity
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.{IRenderFactory, RenderingRegistry}
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object ClientProxy {

  @SubscribeEvent
  def registerModels(event: ModelRegistryEvent): Unit = {
    ModelLoader.setCustomMeshDefinition(PuppeteerItems.Doll, stack => dollMRL(ItemDoll.dollType(stack).getOrElse(PuppeteerDolls.Bare)))
    registerItem(PuppeteerItems.DollCore, 0)
  }

  private def dollMRL(dollType: DollType): MRL = {
    val name = dollType.getRegistryName
    new MRL(new ResourceLocation(name.getResourceDomain, s"doll/${name.getResourcePath}"), "inventory")
  }

  private def registerItem(item: Item, damage: Int) {
    ModelLoader.setCustomModelResourceLocation(item, damage, new MRL(item.getRegistryName, "inventory"))
  }
}

class ClientProxy extends CommonProxy {
  val clientControlHandler = new ClientDollControlHandler
  MinecraftForge.EVENT_BUS.register(clientControlHandler)

  override def bakeDoll(dollType: DollType): Unit = ModelBakery.registerItemVariants(PuppeteerItems.Doll, dollType.itemModel)

  override def registerRenderers(): Unit =
    registerEntityRenderer(new RenderDoll(_))

  private def registerEntityRenderer[A <: Entity: ClassTag](factory: RenderManager => Render[A]): Unit = {
    val clazz = implicitly[ClassTag[A]].runtimeClass.asInstanceOf[Class[A]]
    RenderingRegistry.registerEntityRenderingHandler(clazz, (manager => factory(manager)): IRenderFactory[A])
  }
}
