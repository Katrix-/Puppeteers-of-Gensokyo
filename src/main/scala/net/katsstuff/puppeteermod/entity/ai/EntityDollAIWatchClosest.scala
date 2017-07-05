package net.katsstuff.puppeteermod.entity.ai

import net.katsstuff.puppeteermod.entity.DollMode
import net.katsstuff.puppeteermod.entity.EntityDoll
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer

//Copy of EntityAIWatchClosest
class EntityDollAIWatchClosest(doll: EntityDoll, watchTarget: Class[_ <: Entity], maxDistance: Float, chance: Float) extends EntityDollAIBase(doll) {

	private var closestEntity: Entity = _
	private var lookTime              = 0

	this.setMutexBits(2)

	def this(doll: EntityDoll, watchTargetClass: Class[_ <: Entity], maxDistance: Float) {
		this(doll, watchTargetClass, maxDistance, 0.02F)
	}

	override def shouldExecute: Boolean = {
		if(doll.getRNG.nextFloat >= chance) false
		else {
			if(doll.getAttackTarget != null) closestEntity = doll.getAttackTarget

			if(watchTarget == classOf[EntityPlayer]) closestEntity = doll.world.getClosestPlayerToEntity(doll, maxDistance)
			else closestEntity = doll.world.findNearestEntityWithinAABB(watchTarget, doll.getEntityBoundingBox.expand(maxDistance, 3.0D, maxDistance), doll)

			closestEntity != null
		}
	}

	override def continueExecuting: Boolean = if(!closestEntity.isEntityAlive) false
	else if(doll.getDistanceSqToEntity(closestEntity) > (maxDistance * maxDistance)) false
	else lookTime > 0

	override def startExecuting(): Unit = lookTime = 40 + doll.getRNG.nextInt(40)
	override def resetTask(): Unit = closestEntity = null

	override def updateTask(): Unit = {
		val offset = if(doll.dollMode == DollMode.RideOn && doll.isOwner(closestEntity)) -1.1F else 0F

		doll.getLookHelper.setLookPosition(closestEntity.posX, closestEntity.posY + closestEntity.getEyeHeight + offset, closestEntity.posZ,
			doll.getHorizontalFaceSpeed, doll.getVerticalFaceSpeed)
		lookTime -= 1
	}
}