package net.katsstuff.puppeteermod.entity.ai

import net.katsstuff.puppeteermod.entity.DollMode
import net.katsstuff.puppeteermod.entity.EntityDoll
import net.minecraft.util.math.MathHelper

//Copy from EntityAIWander
class EntityDollAIWander(doll: EntityDoll, speed: Double, var executionChance: Int) extends EntityDollAIBase(doll) {

  this.setMutexBits(1)
  private var xPosition  = 0D
  private var yPosition  = 0D
  private var zPosition  = 0D
  private var mustUpdate = false

  def this(creatureIn: EntityDoll, speedIn: Double) {
    this(creatureIn, speedIn, 120)
  }

  override def shouldExecute: Boolean = {
    if (doll.dollMode != DollMode.Patrol) false
    else {
      if (!mustUpdate) {
        if (doll.getAge >= 100) return false
        if (doll.getRNG.nextInt(executionChance) != 0) return false
      }

      val width  = 10
      val height = 7
      xPosition = (doll.getRNG.nextInt(2 * width) - width) + MathHelper.floor(doll.posX)
      yPosition = (doll.getRNG.nextInt(2 * height) - height) + MathHelper.floor(doll.posY)
      zPosition = (doll.getRNG.nextInt(2 * width) - width) + MathHelper.floor(doll.posZ)
      true
    }
  }

  override def continueExecuting: Boolean = !doll.getNavigator.noPath && doll.dollMode == DollMode.Patrol
  override def startExecuting():  Unit    = doll.getNavigator.tryMoveToXYZ(xPosition, yPosition, zPosition, speed)

  def makeUpdate(): Unit = mustUpdate = true
}
