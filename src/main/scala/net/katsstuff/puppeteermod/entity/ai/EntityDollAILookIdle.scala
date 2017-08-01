package net.katsstuff.puppeteermod.entity.ai

import net.katsstuff.puppeteermod.entity.EntityDoll

//Copy from EntityAILookIdle
class EntityDollAILookIdle(doll: EntityDoll) extends EntityDollAIBase(doll) {

  this.setMutexBits(3)
  private var lookX    = 0D
  private var lookZ    = 0D
  private var idleTime = 0

  override def shouldExecute:           Boolean = doll.getRNG.nextFloat < 0.02F
  override def shouldContinueExecuting: Boolean = idleTime >= 0

  override def startExecuting(): Unit = {
    val d0 = (Math.PI * 2D) * doll.getRNG.nextDouble
    lookX = Math.cos(d0)
    lookZ = Math.sin(d0)
    idleTime = 20 + doll.getRNG.nextInt(20)
  }

  override def updateTask(): Unit = {
    idleTime -= 1
    doll.getLookHelper.setLookPosition(
      doll.posX + lookX,
      doll.posY + doll.getEyeHeight,
      doll.posZ + lookZ,
      doll.getHorizontalFaceSpeed,
      doll.getVerticalFaceSpeed
    )
  }
}
