package net.katsstuff.puppeteermod.entity.ai

import net.katsstuff.puppeteermod.entity.DollMode
import net.katsstuff.puppeteermod.entity.EntityDoll
import net.minecraft.entity.player.EntityPlayer

class EntityDollAIWatchOwner(doll: EntityDoll, maxDistance: Float, chance: Float) extends EntityDollAIBase(doll) {

  protected var owner: EntityPlayer = _
  private var lookTime = 0

  this.setMutexBits(2)

  def this(doll: EntityDoll, maxDistance: Float) {
    this(doll, maxDistance, 0.02F)
  }

  override def shouldExecute: Boolean = {
    if (doll.dollMode == DollMode.RideOn || doll.getRNG.nextFloat >= chance) false
    else {
      doll.ownerEntity match {
        case None => false
        case Some(ownerEntity) =>
          owner = ownerEntity
          doll.getDistanceSqToEntity(owner) < (maxDistance * maxDistance);
      }
    }
  }

  override def shouldContinueExecuting: Boolean =
    owner.isEntityAlive && doll.getDistanceSqToEntity(owner) <= (maxDistance * maxDistance) && doll.dollMode != DollMode.RideOn && lookTime > 0

  override def startExecuting(): Unit = lookTime = 40 + doll.getRNG.nextInt(40)
  override def resetTask():      Unit = owner = null

  override def updateTask(): Unit = {
    val offset = if (doll.dollMode == DollMode.RideOn && doll.isOwner(owner)) -1.1F else 0F

    doll.getLookHelper.setLookPosition(
      owner.posX,
      owner.posY + owner.getEyeHeight + offset,
      owner.posZ,
      doll.getHorizontalFaceSpeed,
      doll.getVerticalFaceSpeed
    )
    lookTime -= 1
  }
}
