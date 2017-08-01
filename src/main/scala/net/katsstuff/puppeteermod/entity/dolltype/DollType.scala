package net.katsstuff.puppeteermod.entity.dolltype

import net.katsstuff.puppeteermod.entity.EntityDoll
import net.minecraft.client.model.ModelBiped
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

trait DollType extends IForgeRegistryEntry.Impl[DollType] {

  def name: String

  def health: Double
  def speed:  Double

  def width:  Float
  def height: Float

  def inventorySize: Int

  def texture:            ResourceLocation
  def textureArmorFolder: ResourceLocation

  @SideOnly(Side.CLIENT)
  def model: ModelBiped
  @SideOnly(Side.CLIENT)
  def modelArmor: ModelBiped
  @SideOnly(Side.CLIENT)
  def modelLeggings: ModelBiped

  def initializeAI(entityDoll: EntityDoll): Unit
  def recipe:                               IRecipe

  def heldItem: ItemStack

  def itemModel: ModelResourceLocation

}
