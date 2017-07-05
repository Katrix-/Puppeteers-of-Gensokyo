package net.katsstuff.puppeteermod.entity.ai

import net.katsstuff.puppeteermod.entity.DollMode.{Patrol, StandBy}
import net.katsstuff.puppeteermod.entity.DollMode
import net.katsstuff.puppeteermod.entity.EntityDoll
import net.minecraft.pathfinding.PathNavigateGround

//Copy of EntityAISwimming
class EntityDollAISwimming(doll: EntityDoll) extends EntityDollAIBase(doll) {

  this.setMutexBits(4)
  doll.getNavigator.asInstanceOf[PathNavigateGround].setCanSwim(true)

  override def shouldExecute: Boolean = doll.dollMode != DollMode.RideOn && doll.isInWater || doll.isInLava
  override def updateTask(): Unit = {
    if (doll.dollMode == StandBy) doll.dollMode = Patrol

    if (doll.getRNG.nextFloat < 0.8F) doll.getJumpHelper.setJumping()
  }
}
