package net.katsstuff.puppeteermod

import scala.reflect.ClassTag

import net.katsstuff.puppeteermod.dolltype.{DollType, DollTypeBare}
import net.katsstuff.puppeteermod.handler.DollControlHandler
import net.katsstuff.puppeteermod.item.{ItemDoll, ItemDollCore}
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object CommonProxy {

  @SubscribeEvent
  def registerItems(event: RegistryEvent.Register[Item]): Unit = {
    event.getRegistry.registerAll(new ItemDoll, new ItemDollCore)
  }

  @SubscribeEvent
  def registerDolls(event: RegistryEvent.Register[DollType]): Unit =
    event.getRegistry.registerAll(new DollTypeBare)
}

class CommonProxy {

  lazy val dollControlHandler = new DollControlHandler

  def preInit(event: FMLPreInitializationEvent): Unit = {
    MinecraftForge.EVENT_BUS.register(dollControlHandler)
  }

  def bakeDoll(dollType: DollType): Unit = ()

  def registerRenderers(): Unit = ()
}
