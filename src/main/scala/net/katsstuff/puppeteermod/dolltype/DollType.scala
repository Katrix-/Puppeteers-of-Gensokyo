package net.katsstuff.puppeteermod.dolltype

import net.katsstuff.puppeteermod.entity.EntityDoll
import net.katsstuff.puppeteermod.item.ItemDoll
import net.minecraft.client.model.ModelBiped
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

abstract class DollType extends IForgeRegistryEntry.Impl[DollType] {

  def name:      String
  def health:    Double
  def speed:     Double
  def width:     Float
  def height:    Float
  def eyeHeight: Float

  def itemModel: ModelResourceLocation
  def texture(doll: EntityDoll):   ResourceLocation
  @SideOnly(Side.CLIENT)
  def model(doll: EntityDoll): ModelBiped

  def initializeAI(entityDoll: EntityDoll): Unit

  def heldItem: ItemStack

  def createStack(doll: EntityDoll): ItemStack

  def canControlOtherDolls(doll: EntityDoll): Boolean

  @SideOnly(Side.CLIENT) def onJump(doll: EntityDoll, isPressed: Boolean): Unit
  @SideOnly(Side.CLIENT) def onSneak(doll: EntityDoll, isPressed: Boolean): Unit
  @SideOnly(Side.CLIENT) def onAttack(doll: EntityDoll, isPressed: Boolean): Unit
  @SideOnly(Side.CLIENT) def onUseItem(doll: EntityDoll, isPressed: Boolean): Unit
  @SideOnly(Side.CLIENT) def onPickBlock(doll: EntityDoll, isPressed: Boolean): Unit

  def onStringed(doll: EntityDoll): Unit
  def onUnstringed(doll: EntityDoll): Unit

  def needStringToMove(doll: EntityDoll): Boolean

  def onTick(doll: EntityDoll): Unit

}
