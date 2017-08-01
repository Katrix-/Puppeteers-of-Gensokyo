package net.katsstuff.puppeteermod.item

import scala.collection.JavaConverters._

import net.katsstuff.puppeteermod.PuppeteerMod
import net.katsstuff.puppeteermod.entity.EntityDoll
import net.katsstuff.puppeteermod.entity.dolltype.{DollRegistry, DollType, PuppeteerDolls}
import net.katsstuff.puppeteermod.items.PuppeteerItems
import net.katsstuff.puppeteermod.lib.LibItemName
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.{BlockPos, Vec3d}
import net.minecraft.util.{EnumActionResult, EnumFacing, EnumHand, NonNullList, ResourceLocation}
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

class ItemDoll extends ItemModBase(LibItemName.Doll) {
  setHasSubtypes(true)
  setCreativeTab(PuppeteerMod.CreativeTab)

  @SideOnly(Side.CLIENT)
  override def getSubItems(itemIn: Item, tab: CreativeTabs, list: NonNullList[ItemStack]): Unit =
    list.addAll(DollRegistry.registry.getValues.asScala.map(ItemDoll.createStack).asJava)

  override def getUnlocalizedName(stack: ItemStack): String =
    super.getUnlocalizedName(stack) + "." + ItemDoll.dollType(stack).getOrElse(PuppeteerDolls.Bare).getRegistryName.toString

  override def onItemUse(
      player: EntityPlayer,
      world: World,
      pos: BlockPos,
      hand: EnumHand,
      facing: EnumFacing,
      hitX: Float,
      hitY: Float,
      hitZ: Float
  ): EnumActionResult = {
    if (!world.isRemote) {
      val stack = player.getHeldItem(hand)

      if (!player.capabilities.isCreativeMode) {
        stack.shrink(1)
      }

      val doll = new EntityDoll(world, new Vec3d(pos.up()), ItemDoll.dollType(stack).getOrElse(PuppeteerDolls.Bare), Some(player.getUniqueID))
      world.spawnEntity(doll)
    }

    EnumActionResult.SUCCESS
  }
}
object ItemDoll {
  def createStack(doll: DollType): ItemStack = {
    val s   = new ItemStack(PuppeteerItems.Doll)
    val nbt = Option(s.getTagCompound).getOrElse(new NBTTagCompound)
    nbt.setString("dollType", doll.getRegistryName.toString)
    s.setTagCompound(nbt)
    s
  }

  def dollType(stack: ItemStack): Option[DollType] =
    Option(stack.getTagCompound)
      .flatMap(nbt => Option(DollRegistry.registry.getValue(new ResourceLocation(nbt.getString("dollType")))))
}
