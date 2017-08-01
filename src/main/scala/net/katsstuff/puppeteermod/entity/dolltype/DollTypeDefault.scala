package net.katsstuff.puppeteermod.entity.dolltype

import net.katsstuff.puppeteermod.client.ModelDollBase
import net.katsstuff.puppeteermod.entity.EntityDoll
import net.katsstuff.puppeteermod.entity.ai._
import net.katsstuff.puppeteermod.lib.LibMod
import net.minecraft.client.model.ModelBiped
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

trait DollTypeDefault extends DollType {

  override def health: Double = 8D
  override def speed:  Double = 0.5D

  override def width:  Float = 0.35F
  override def height: Float = 0.9F

  override def inventorySize: Int = 9

  @SideOnly(Side.CLIENT)
  override def model: ModelBiped = ModelDollBase
  @SideOnly(Side.CLIENT)
  override def modelArmor: ModelBiped = model
  @SideOnly(Side.CLIENT)
  override def modelLeggings: ModelBiped = model

  override def textureArmorFolder: ResourceLocation = new ResourceLocation(LibMod.Id, "textures/dolls/armor")

  override def initializeAI(entityDoll: EntityDoll): Unit = {
    entityDoll.tasks.addTask(0, new EntityDollAISwimming(entityDoll))
    entityDoll.tasks.addTask(12, new EntityDollAIFollowOwner(entityDoll, speed, 2.5F, 10F))
    entityDoll.tasks.addTask(13, new EntityDollAIWander(entityDoll, speed, 40))
    entityDoll.tasks.addTask(14, new EntityDollAIWatchOwner(entityDoll, 8, 0.02F))
    entityDoll.tasks.addTask(15, new EntityDollAIWatchClosest(entityDoll, classOf[EntityLivingBase], 8, 0.02F))
    entityDoll.tasks.addTask(16, new EntityDollAILookIdle(entityDoll))
  }

  override def itemModel: ModelResourceLocation = {
    val name = getRegistryName
    new ModelResourceLocation(new ResourceLocation(name.getResourceDomain, s"doll/${name.getResourcePath}"), "inventory")
  }
}
