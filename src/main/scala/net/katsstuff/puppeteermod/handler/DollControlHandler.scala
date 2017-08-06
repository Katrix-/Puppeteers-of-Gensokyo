package net.katsstuff.puppeteermod.handler

import java.util.UUID

import scala.collection.mutable

import org.apache.commons.io.Charsets

import com.mojang.authlib.GameProfile

import net.katsstuff.danmakucore.data.Vector3
import net.katsstuff.puppeteermod.entity.EntityDoll
import net.katsstuff.puppeteermod.helper.LogHelper
import net.katsstuff.puppeteermod.network.scalachannel.TargetPoint
import net.katsstuff.puppeteermod.network.{DismountFromDoll, MountToDoll, PuppeteersPacketHandler}
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.network.play.server.SPacketPlayerListItem
import net.minecraft.server.management.PlayerInteractionManager
import net.minecraft.util.text.TextComponentTranslation
import net.minecraftforge.event.entity.EntityMountEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent
import net.minecraftforge.event.entity.player.{AttackEntityEvent, EntityItemPickupEvent, PlayerInteractEvent, PlayerPickupXpEvent}
import net.minecraftforge.event.world.BlockEvent
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.eventhandler.{EventPriority, SubscribeEvent}
import net.minecraftforge.fml.common.gameevent.PlayerEvent

class DollControlHandler {

  private val controlledDolls = mutable.WeakHashMap.empty[EntityPlayer, ControlledData]

  @SubscribeEvent
  def onLiving(event: LivingUpdateEvent): Unit = {
    val world = event.getEntity.world
    if(!world.isRemote) {
      event.getEntityLiving match {
        case doll: EntityDoll =>
          doll.stringedToPlayer.foreach { stringedTo =>
            val length = 32D
            if(doll.getDistanceSqToEntity(stringedTo) >= length * length) {
              val pos = new Vector3(doll).offset(Vector3.directionToEntity(doll, stringedTo), length / 2D)
              val item = new EntityItem(world, pos.x, pos.y, pos.z, new ItemStack(Items.STRING))
              item.setDefaultPickupDelay()
              world.spawnEntity(item)

              controlledDolls.filter(_._2.doll == doll).foreach(_._1.dismountRidingEntity())
              doll.stringedTo = None
            }
          }
        case _ =>
      }
    }
  }

  @SubscribeEvent
  def onDismount(event: EntityMountEvent): Unit = {
    if (!event.getWorldObj.isRemote) {
      event.getEntityMounting match {
        case player: EntityPlayer =>
          if (event.isDismounting && controlledDolls.contains(player)) {
            LogHelper.info("DismountAll")
            removeControlledDoll(player)
            PuppeteersPacketHandler.sendToAllAround(DismountFromDoll(player.getUniqueID), TargetPoint.around(player, 32D))
          }
        case _ =>
      }
    }
  }

  @SubscribeEvent
  def onBlockBreak(event: BlockEvent): Unit = {
    def cancel(player: EntityPlayer): Unit = if (controlledDolls.contains(player)) event.setCanceled(true)

    event match {
      case event: BlockEvent.BreakEvent => cancel(event.getPlayer)
      case event: BlockEvent.PlaceEvent => cancel(event.getPlayer)
      case _                            =>
    }
  }

  @SubscribeEvent
  def onLogout(event: PlayerEvent.PlayerLoggedOutEvent): Unit = {
    val player = event.player
    if (!player.world.isRemote && controlledDolls.contains(player)) {
      player.dismountRidingEntity()
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  def onDeath(event: LivingDeathEvent): Unit = {
    if (!event.getEntityLiving.world.isRemote) {
      event.getEntityLiving match {
        case player: EntityPlayer =>
          removeControlledDoll(player)
          player.setDead()
        case _ =>
      }
    }
  }

  @SubscribeEvent
  def onInteract(event: PlayerInteractEvent): Unit = {
    if(controlledDolls.contains(event.getEntityPlayer) && event.isCancelable) {
      event.setCanceled(true)
    }
  }

  @SubscribeEvent
  def onPickup(event: EntityItemPickupEvent): Unit = {
    if(controlledDolls.contains(event.getEntityPlayer)) {
      event.setCanceled(true)
    }
  }

  @SubscribeEvent
  def onXpPickup(event: PlayerPickupXpEvent): Unit = {
    if(controlledDolls.contains(event.getEntityPlayer)) {
      event.setCanceled(true)
    }
  }

  @SubscribeEvent
  def onPickup(event: AttackEntityEvent): Unit = {
    if(controlledDolls.contains(event.getEntityPlayer)) {
      event.setCanceled(true)
    }
  }

  def addControlledDoll(player: EntityPlayerMP, doll: EntityDoll): Unit = {
    PuppeteersPacketHandler.sendToAllAround(MountToDoll(doll.getEntityId, player.getUniqueID), TargetPoint.around(player, 32D))
    controlledDolls.get(player) match {
      case Some(ControlledData(oldDoll, dummy)) =>
        if(doll.dollType.canControlOtherDolls(doll)) {
          LogHelper.info("AddControlledExisting")
          controlledDolls.remove(player)
          player.dismountRidingEntity()

          controlledDolls.put(player, ControlledData(doll, dummy))
          player.startRiding(doll)
        }
        else {
          player.sendMessage(new TextComponentTranslation("doll.control.alreadyControllingDoll"))
        }
      case None =>
        LogHelper.info("AddControlledNew")
        val dummy = createFakePlayer(player)

        player.inventory.clear()
        player.setPositionAndRotation(doll.posX, doll.posY, doll.posZ, doll.rotationYaw, doll.rotationPitch)

        doll.stringedTo = Some(dummy.getUniqueID)
        player.startRiding(doll)
        controlledDolls.put(player, ControlledData(doll, dummy))
    }
  }

  private def createFakePlayer(player: EntityPlayerMP): EntityPlayerMP = {
    val server     = FMLCommonHandler.instance().getMinecraftServerInstance
    val world      = server.getWorld(player.dimension)
    val profile    = player.getGameProfile
    val newProfile = new GameProfile(UUID.nameUUIDFromBytes(profile.getName.getBytes(Charsets.UTF_8)), profile.getName)
    newProfile.getProperties.putAll(profile.getProperties)

    val dummy = new EntityPlayerMP(server, world, newProfile, new PlayerInteractionManager(world))

    val keepInv = world.getGameRules.getBoolean("keepInventory")
    world.getGameRules.setOrCreateGameRule("keepInventory", "true")
    dummy.clonePlayer(player, false)
    world.getGameRules.setOrCreateGameRule("keepInventory", keepInv.toString)

    dummy.capabilities.disableDamage = player.capabilities.disableDamage
    dummy.capabilities.allowEdit = player.capabilities.allowEdit

    player.capabilities.disableDamage = true
    player.capabilities.allowEdit = false
    player.sendPlayerAbilities()

    dummy.connection = player.connection
    dummy.dimension = player.dimension

    dummy.setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch)
    server.getPlayerList.sendPacketToAllPlayers(new SPacketPlayerListItem(SPacketPlayerListItem.Action.ADD_PLAYER, dummy))
    world.spawnEntity(dummy)

    dummy
  }

  def removeControlledDoll(player: EntityPlayer): Unit = {
    controlledDolls.remove(player).foreach {
      case ControlledData(doll, dummy) =>
        applyDataFromFake(player, dummy)
        controlledDolls.filter(_._2.dummy == dummy).foreach { t =>
          val otherDoll = t._2.doll
          otherDoll.stringedTo = Some(player.getUniqueID)
        }
        doll.stringedTo = Some(player.getUniqueID)
    }
  }

  private def applyDataFromFake(player: EntityPlayer, dummy: EntityPlayerMP): Unit = {
    val server = FMLCommonHandler.instance().getMinecraftServerInstance
    val world  = dummy.world

    val keepInv = world.getGameRules.getBoolean("keepInventory")
    world.getGameRules.setOrCreateGameRule("keepInventory", "true")
    player.clonePlayer(dummy, false)
    world.getGameRules.setOrCreateGameRule("keepInventory", keepInv.toString)

    player.capabilities.disableDamage = dummy.capabilities.disableDamage
    player.capabilities.allowEdit = dummy.capabilities.allowEdit
    player.sendPlayerAbilities()

    player.setPositionAndRotation(dummy.posX, dummy.posY, dummy.posZ, dummy.rotationYaw, dummy.rotationPitch)
    player.setPositionAndUpdate(dummy.posX, dummy.posY, dummy.posZ)

    server.getPlayerList.sendPacketToAllPlayers(new SPacketPlayerListItem(SPacketPlayerListItem.Action.REMOVE_PLAYER, dummy))
    dummy.setDead()
  }

  def isControlling(player: EntityPlayer, doll: EntityDoll): Boolean = controlledDolls.get(player).exists(_.doll == doll)
}
