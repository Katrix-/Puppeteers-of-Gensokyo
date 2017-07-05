package net.katsstuff.puppeteermod.item

import scala.collection.JavaConverters._

import net.katsstuff.puppeteermod.PuppeteerMod
import net.katsstuff.puppeteermod.entity.EntityDoll
import net.katsstuff.puppeteermod.entity.dolltype.{DollRegistry, DollType}
import net.katsstuff.puppeteermod.lib.LibItemName
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.util.math.{BlockPos, Vec3d}
import net.minecraft.util.{EnumActionResult, EnumFacing, EnumHand, NonNullList}
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

class ItemDoll extends ItemModBase(LibItemName.Doll) {
	setHasSubtypes(true)
	setCreativeTab(PuppeteerMod.CreativeTab)

	@SideOnly(Side.CLIENT)
	override def getSubItems(itemIn: Item, tab: CreativeTabs, list: NonNullList[ItemStack]): Unit = {
		list.addAll(DollRegistry.registry.getValues.asScala.map(d => new ItemStack(this, 1, DollRegistry.registry.getId(d))).asJava)
	}

	override def getUnlocalizedName(stack: ItemStack): String = {
		super.getUnlocalizedName(stack) + "." + DollRegistry.registry.getObjectById(stack.getMetadata).getRegistryName.toString
	}

	override def onItemUse(player: EntityPlayer, world: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, noIdea: Float,
			hitX: Float, hitY: Float): EnumActionResult = {
		if(!world.isRemote) {
			val stack = player.getHeldItem(hand)
			if(!player.capabilities.isCreativeMode && !stack.isEmpty) {
				stack.shrink(1)
			}
			val doll = new EntityDoll(world, new Vec3d(pos.up()), DollRegistry.registry.getObjectById(stack.getItemDamage), Some(player.getUniqueID))
			world.spawnEntity(doll)
		}
		EnumActionResult.SUCCESS
	}

	def createStack(dollType: DollType): ItemStack = new ItemStack(this, 1, DollRegistry.registry.getId(dollType))
}