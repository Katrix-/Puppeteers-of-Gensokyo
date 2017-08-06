package net.katsstuff.puppeteermod.entity

import java.util.UUID

import com.google.common.base.Optional

import net.katsstuff.danmakucore.data.Vector3
import net.katsstuff.danmakucore.entity.living.EntityDanmakuCreature
import net.katsstuff.puppeteermod.PuppeteerMod
import net.katsstuff.puppeteermod.dolltype.{DollType, PuppeteerDolls}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.{Entity, EntityLivingBase, SharedMonsterAttributes}
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.datasync.{DataParameter, DataSerializers, EntityDataManager}
import net.minecraft.util.EnumHand
import net.minecraft.util.math.MathHelper
import net.minecraft.util.text.translation.I18n
import net.minecraft.util.text.{ITextComponent, TextComponentString, TextComponentTranslation}
import net.minecraft.world.World

import net.katsstuff.puppeteermod.helper.JavaHelper._

object EntityDoll {
  val StringedTo: DataParameter[Optional[UUID]] = EntityDataManager.createKey(classOf[EntityDoll], DataSerializers.OPTIONAL_UNIQUE_ID)
}
class EntityDoll(_world: World, pos: Vector3, private var _dollType: DollType, private var _owner: UUID) extends EntityDanmakuCreature(_world) {

  {
    setSize(dollType.width, dollType.height)
    setHeldItem(EnumHand.MAIN_HAND, dollType.heldItem)
    setPosition(pos.x, pos.y, pos.z)
  }

  def this(world: World) {
    this(world, Vector3.Zero, PuppeteerDolls.Bare, null)
  }

  override def initEntityAI(): Unit = dollType.initializeAI(this)

  override def applyEntityAttributes(): Unit = {
    super.applyEntityAttributes()
    getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(dollType.health)
    getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(dollType.speed)
  }

  override def onLivingUpdate(): Unit = {
    super.onLivingUpdate()
    if (!world.isRemote) {
      if (_owner == null) {
        setDead()
        return
      }
    }
  }

  override def getEyeHeight: Float = dollType.eyeHeight

  override def getJumpUpwardsMotion: Float = 0.5F

  override def getControllingPassenger: Entity = if (getPassengers.isEmpty) null else getPassengers.get(0)

  override def canBeSteered: Boolean = true

  override def canPassengerSteer: Boolean = getControllingPassenger match {
    case player: EntityPlayer => player.isUser || PuppeteerMod.proxy.dollControlHandler.isControlling(player, this)
    case _ => false
  }

  override def moveEntityWithHeading(strafe: Float, forward: Float): Unit = {
    if (isBeingRidden && canBeSteered) {
      val controller = this.getControllingPassenger.asInstanceOf[EntityLivingBase]
      rotationYaw = controller.rotationYaw
      prevRotationYaw = rotationYaw
      rotationPitch = controller.rotationPitch
      prevRotationPitch = rotationPitch
      setRotation(rotationYaw, rotationPitch)
      renderYawOffset = rotationYaw
      rotationYawHead = renderYawOffset

      val newStrafe = controller.moveStrafing
      val newForward = controller.moveForward

      if (canPassengerSteer) {
        setAIMoveSpeed(getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue.toFloat)
        super.moveEntityWithHeading(newStrafe, newForward)
      }
      else if (controller.isInstanceOf[EntityPlayer]) {
        motionX = 0.0D
        motionY = 0.0D
        motionZ = 0.0D
      }

      prevLimbSwingAmount = limbSwingAmount
      val xDis = posX - prevPosX
      val zDiz = posZ - prevPosZ
      val dist = MathHelper.sqrt(xDis * xDis + zDiz * zDiz) * 4.0F
      val clampedDist = if(dist > 1F) 1F else dist

      limbSwingAmount += (clampedDist - limbSwingAmount) * 0.4F
      limbSwing += limbSwingAmount
    }
    else {
      super.moveEntityWithHeading(strafe, forward)
    }
  }

  override def entityInit(): Unit = {
    super.entityInit()
    dataManager.register(EntityDoll.StringedTo, Optional.absent[UUID])
  }

  def stringedTo: Option[UUID] = dataManager.get(EntityDoll.StringedTo).asScala
  def stringedTo_=(uuid: Option[UUID]): Unit = dataManager.set(EntityDoll.StringedTo, uuid.asGuava)
  def stringedToPlayer: Option[EntityPlayer] = stringedTo.flatMap(uuid => Option(world.getPlayerEntityByUUID(uuid)))

  override def dropFewItems(wasRecentlyHit: Boolean, lootingModifier: Int): Unit = {
    if(stringedTo.isDefined) entityDropItem(new ItemStack(Items.STRING), 0F)
    entityDropItem(dollType.createStack(this), 0F)
  }

  def dollType:  DollType = _dollType
  def ownerUuid: UUID     = _owner

  override def processInteract(player: EntityPlayer, hand: EnumHand): Boolean = {
    if(!world.isRemote) {
      if(isOwner(player)) {
        val stack = player.getHeldItem(hand)

        if(stack.getItem == Items.STRING) {
          if(stringedTo.isEmpty) {
            stringedTo = Some(player.getUniqueID)
          }
          else {
            stringedTo = None
          }
          true
        }
        else false
      }
      else {
        player.sendMessage(new TextComponentTranslation("doll.nonOwner"))
        false
      }
    }
    else false
  }

  def chatMessage(msg: ITextComponent):                       Unit = ownerEntity.foreach(chatMessage(_, msg))
  def chatMessage(player: EntityPlayer, msg: ITextComponent): Unit = player.sendMessage(new TextComponentString(s"$getName: ").appendSibling(msg))

  def ownerEntity:             Option[EntityPlayer] = Option(world.getPlayerEntityByUUID(_owner))
  def isOwner(entity: Entity): Boolean              = _owner == entity.getUniqueID

  override def getName: String =
    if (this.hasCustomName) getCustomNameTag
    else I18n.translateToLocal(s"entity.puppeteermod.doll.${dollType.name}.name")

  override def readEntityFromNBT(compound: NBTTagCompound): Unit = {
    super.readEntityFromNBT(compound)
    _owner = compound.getUniqueId("Owner")
    stringedTo = Option(compound.getUniqueId("StringedTo"))

    if (!world.isRemote && _owner == null) {
      setDead()
    }
  }

  override def writeEntityToNBT(compound: NBTTagCompound): Unit = {
    super.writeEntityToNBT(compound)
    compound.setUniqueId("Owner", _owner)
    stringedTo.foreach(compound.setUniqueId("StringedTo", _))
  }
}
