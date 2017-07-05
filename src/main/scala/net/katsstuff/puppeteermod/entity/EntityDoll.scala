package net.katsstuff.puppeteermod.entity

import java.util.UUID

import scala.collection.JavaConverters._

import io.netty.buffer.ByteBuf
import net.katsstuff.puppeteermod.entity.DollMode.{Follow, Patrol, RideOn, StandBy}
import net.katsstuff.puppeteermod.entity.ai.EntityDollAIBase
import net.katsstuff.puppeteermod.entity.dolltype.{DollRegistry, DollType, PuppeteerDolls}
import net.katsstuff.puppeteermod.helper.LogHelper
import net.minecraft.client.resources.I18n
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.{Entity, EntityLiving, SharedMonsterAttributes}
import net.minecraft.init.{Blocks, Items, SoundEvents}
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.{ITextComponent, TextComponentString, TextComponentTranslation}
import net.minecraft.util.{EnumFacing, EnumHand}
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.{Capability, ICapabilityProvider}
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData
import net.minecraftforge.items.wrapper.CombinedInvWrapper
import net.minecraftforge.items.{CapabilityItemHandler, IItemHandlerModifiable, ItemHandlerHelper, ItemStackHandler}

class EntityDoll(_world: World, pos: Vec3d, private var _dollType: DollType, private var _owner: Option[UUID]) extends EntityLiving(_world)
	with ICapabilityProvider with IEntityAdditionalSpawnData {

	var dollMode: DollMode = DollMode.Follow

	private val inventory      = new ItemStackHandler(dollType.inventorySize)
	private val allInventories = {
		val hand = getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP).asInstanceOf[IItemHandlerModifiable]
		val armor = getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH).asInstanceOf[IItemHandlerModifiable]
		new CombinedInvWrapper(hand, armor, inventory)
	}

	setSize(dollType.width, dollType.height)
	setHeldItem(EnumHand.MAIN_HAND, dollType.heldItem)
	setPosition(pos.xCoord, pos.yCoord, pos.zCoord)

	def this(world: World) {
		this(world, Vec3d.ZERO, PuppeteerDolls.Bare, None)
	}

	override def writeSpawnData(buf: ByteBuf): Unit = {
		buf.writeInt(DollRegistry.registry.getId(_dollType))
	}

	override def readSpawnData(buf: ByteBuf): Unit = {
		_dollType = DollRegistry.registry.getObjectById(buf.readInt())
	}

	override def initEntityAI(): Unit = {
		dollType.initializeAI(this)
	}

	def addAI(index: Int, ai: EntityDollAIBase): Unit = {
		tasks.addTask(index, ai)
	}

	override def applyEntityAttributes(): Unit = {
		super.applyEntityAttributes()
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(dollType.health)
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(dollType.speed)
	}

	override def onLivingUpdate(): Unit = {
		super.onLivingUpdate()
		if(!world.isRemote) {
			if(dollMode == RideOn) {
				val ridingEntity = getRidingEntity
				if(ridingEntity == null || !isOwner(ridingEntity)) {
					dismountRidingEntity()
					dollMode = StandBy
				}
				else heal(1F)
			}
			pickupItem()
		}
	}

	def dollType: DollType = _dollType
	def owner: Option[UUID] = _owner

	def toggleMode(): Unit = {
		dismountRidingEntity()
		playSound(SoundEvents.UI_BUTTON_CLICK, 0.3F, 0.6F)
		dollMode match {
			case Follow =>
				chatMessage(new TextComponentTranslation("doll.mode.standby"))
				dollMode = StandBy
			case StandBy =>
				chatMessage(new TextComponentTranslation("doll.mode.patrol"))
				dollMode = Patrol
			case RideOn =>
				chatMessage(new TextComponentTranslation("doll.mode.patrol"))
				dollMode = Patrol
			case Patrol =>
				chatMessage(new TextComponentTranslation("doll.mode.follow"))
				dollMode = Follow
		}
	}

	def setRideOnMode(player: EntityPlayer): Unit = {
		if(!world.isRemote) {
			playSound(SoundEvents.UI_BUTTON_CLICK, 0.3F, 0.6F)
			chatMessage(new TextComponentTranslation("doll.mode.rideOn"))
			dollMode = RideOn
			startRiding(player, true) //FIXME: Won't appear to be actually riding player
			LogHelper.info(getRidingEntity)
		}
	}

	protected def pickupItem(): Unit = {
		if(!world.isRemote && !dead) {
			val list = world.getEntitiesWithinAABB(classOf[EntityItem], this.getEntityBoundingBox.expand(1.0D, 0.0D, 1.0D)).asScala

			for(entityItem <- list if !entityItem.isDead) {
				val itemstack = entityItem.getEntityItem
				addStackToInventory(itemstack) match {
					case remaining if !remaining.isEmpty && remaining != itemstack =>
						entityItem.setEntityItemStack(remaining)
						playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.2F, ((rand.nextFloat - rand.nextFloat) * 0.7F + 1.0F) * 2.0F)
					case none if none.isEmpty =>
						playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.2F, ((rand.nextFloat - rand.nextFloat) * 0.7F + 1.0F) * 2.0F)
						entityItem.setDead()
					case _ =>
				}
			}
		}
	}

	private def addStackToInventory(stack: ItemStack): ItemStack = {
		ItemHandlerHelper.insertItem(inventory, stack, false)
	}

	override def processInteract(player: EntityPlayer, hand: EnumHand): Boolean = {
		if(!world.isRemote) {
			if(isOwner(player)) {
				if(!player.isSneaking) {
					if(dollMode == RideOn) {
						dismountRidingEntity()
						chatMessage(new TextComponentTranslation("doll.mode.follow"))
						dollMode = Follow
						true
					}
					else {
						setRideOnMode(player)
						true
					}
				}
				else {
					val stack = player.getHeldItem(stack)
					if(stack.isEmpty) {
						toggleMode()
						true
					}
					else stack.getItem match {
						case Items.NAME_TAG =>
							chatMessage(new TextComponentTranslation("doll.changeName").appendText(stack.getDisplayName))
							setCustomNameTag(stack.getDisplayName)
							true
						case item if item == Item.getItemFromBlock(Blocks.CHEST) =>
							///TODO: Gui
							//player.openGui(DollMod, GuiAliceDollInventory.GuiID, this.worldObj, this.getEntityId, 0, 0)
							true
						case _ =>
							toggleMode()
							true
					}
				}
			}
			else {
				chatMessage(player, new TextComponentTranslation("doll.nonOwner"))
				false
			}
		}
		else true
	}

	def chatMessage(msg: ITextComponent) {
		ownerEntity.foreach(p => chatMessage(p, msg))
	}

	def chatMessage(player: EntityPlayer, msg: ITextComponent) {
		player.sendMessage(new TextComponentString(s"$getName: ").appendSibling(msg))
	}

	def ownerEntity: Option[EntityPlayer] = owner.flatMap(uuid => Option(world.getPlayerEntityByUUID(uuid)))
	def isOwner(entity: Entity): Boolean = owner.contains(entity.getUniqueID)

	override def getName: String = {
		if(this.hasCustomName) getCustomNameTag
		else I18n.format(s"entity.dollsmod.doll.${dollType.name}.name")
	}

	override def getCapability[A](capability: Capability[A], facing: EnumFacing): A = {
		val itemCapability = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
		if(capability == itemCapability) {
			if(facing == null) itemCapability.cast(allInventories)
			else if(facing == EnumFacing.DOWN) itemCapability.cast(inventory)
			else super.getCapability(capability, facing)
		}
		else super.getCapability(capability, facing)
	}

	override def hasCapability(capability: Capability[_], facing: EnumFacing): Boolean = {
		val itemCapability = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
		if(capability == itemCapability) {
			if(facing == null) true
			else if(facing == EnumFacing.DOWN) true
			else super.hasCapability(capability, facing)
		}
		else super.hasCapability(capability, facing)
	}

	override def readEntityFromNBT(compound: NBTTagCompound): Unit = {
		super.readEntityFromNBT(compound)
		_owner = Option(compound.getUniqueId("Owner"))
	}

	override def writeEntityToNBT(compound: NBTTagCompound): Unit = {
		super.writeEntityToNBT(compound)
		owner.foreach(uuid => compound.setUniqueId("Owner", uuid))
	}
}