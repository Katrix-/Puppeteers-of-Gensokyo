package net.katsstuff.puppeteermod.entity.dolltype

import net.katsstuff.puppeteermod.client.ModelDollBase
import net.katsstuff.puppeteermod.entity.EntityDoll
import net.katsstuff.puppeteermod.entity.ai.{EntityDollAIFollowOwner, EntityDollAILookIdle, EntityDollAISwimming, EntityDollAIWander, EntityDollAIWatchClosest, EntityDollAIWatchOwner}
import net.katsstuff.puppeteermod.lib.LibMod
import net.minecraft.client.model.ModelBiped
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

trait DollTypeDefault extends DollType {

	override def health: Double = 8D
	override def speed: Double = 0.5D

	override def width: Float = 0.35F
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
		entityDoll.addAI(0,  new EntityDollAISwimming(entityDoll))
		entityDoll.addAI(12, new EntityDollAIFollowOwner(entityDoll, speed, 2.5F, 10F))
		entityDoll.addAI(13, new EntityDollAIWander(entityDoll, speed, 40))
		entityDoll.addAI(14, new EntityDollAIWatchOwner(entityDoll, 8, 0.02F))
		entityDoll.addAI(15, new EntityDollAIWatchClosest(entityDoll, classOf[EntityLivingBase], 8, 0.02F))
		entityDoll.addAI(16, new EntityDollAILookIdle(entityDoll))
	}
}
