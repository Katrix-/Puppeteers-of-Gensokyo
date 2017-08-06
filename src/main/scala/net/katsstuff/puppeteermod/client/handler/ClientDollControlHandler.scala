package net.katsstuff.puppeteermod.client.handler

import scala.collection.mutable

import net.katsstuff.puppeteermod.entity.EntityDoll
import net.katsstuff.puppeteermod.handler.ControlledData
import net.katsstuff.puppeteermod.helper.LogHelper
import net.katsstuff.puppeteermod.network.{MountToDoll, PuppeteersPacketHandler}
import net.katsstuff.puppeteermod.network.scalachannel.TargetPoint
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent.{KeyInputEvent, MouseInputEvent}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
class ClientDollControlHandler {

  private val controlledDolls = mutable.WeakHashMap.empty[EntityPlayer, EntityDoll]

  @SubscribeEvent
  def onKey(event: KeyInputEvent): Unit = {
    controlledDolls.get(Minecraft.getMinecraft.player).foreach { doll =>
      val settings = Minecraft.getMinecraft.gameSettings
      val dollType = doll.dollType

      dollType.onJump(doll, settings.keyBindJump.isPressed)
      dollType.onSneak(doll, settings.keyBindSneak.isPressed)

    }
  }

  @SubscribeEvent
  def onMouse(event: MouseInputEvent): Unit = {
    controlledDolls.get(Minecraft.getMinecraft.player).foreach { doll =>
      val settings = Minecraft.getMinecraft.gameSettings
      val dollType = doll.dollType

      dollType.onAttack(doll, settings.keyBindAttack.isPressed)
      dollType.onUseItem(doll, settings.keyBindUseItem.isPressed)
      dollType.onPickBlock(doll, settings.keyBindPickBlock.isPressed)
    }
  }

  def addControlledDoll(player: EntityPlayer, doll: EntityDoll): Unit = {
    controlledDolls.put(player, doll)
  }

  def removeControlledDoll(player: EntityPlayer): Unit = {
    controlledDolls.remove(player)
  }
}
