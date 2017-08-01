package net.katsstuff.puppeteermod.entity.ai

import net.katsstuff.puppeteermod.entity.DollMode
import net.katsstuff.puppeteermod.entity.EntityDoll
import net.minecraft.block.material.Material
import net.minecraft.entity.EntityLivingBase
import net.minecraft.pathfinding.{PathNavigateGround, PathNodeType}
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.{BlockPos, MathHelper}

//Copy from EntityAIFollowOwner
class EntityDollAIFollowOwner(doll: EntityDoll, speed: Double, var minDist: Float, var maxDist: Float) extends EntityDollAIBase(doll) {

  private val pathFinder = doll.getNavigator
  private var owner: EntityLivingBase = _
  private val world            = doll.world
  private var timeToRecalcPath = 0
  private var oldWaterPriority = 0F

  this.setMutexBits(3)
  if (!doll.getNavigator.isInstanceOf[PathNavigateGround]) throw new IllegalArgumentException("Unsupported mob type for EntityDollAIFollowOwner")

  override def shouldExecute: Boolean = {
    if (doll.dollMode != DollMode.Follow) false
    else
      doll.ownerEntity match {
        case None => false
        case Some(ownerEntity) =>
          if (ownerEntity.isSpectator) false
          else if (doll.getDistanceSqToEntity(ownerEntity) < (minDist * minDist)) false
          else {
            owner = ownerEntity
            true
          }
      }
  }

  override def shouldContinueExecuting: Boolean =
    doll.dollMode == DollMode.Follow &&
      !pathFinder.noPath &&
      doll.getDistanceSqToEntity(owner) > (maxDist * maxDist)

  override def startExecuting(): Unit = {
    timeToRecalcPath = 0
    oldWaterPriority = doll.getPathPriority(PathNodeType.WATER)
    doll.setPathPriority(PathNodeType.WATER, 0.0F)
  }

  override def resetTask(): Unit = {
    owner = null
    pathFinder.clearPathEntity()
    doll.setPathPriority(PathNodeType.WATER, oldWaterPriority)
  }

  override def updateTask(): Unit = {
    doll.getLookHelper.setLookPositionWithEntity(owner, 10.0F, doll.getVerticalFaceSpeed)

    timeToRecalcPath -= 1

    if (timeToRecalcPath <= 0) {
      timeToRecalcPath = 10
      if (!pathFinder.tryMoveToEntityLiving(owner, speed * 2) /*TODO Fix for moving slowly*/ ) {
        if (!doll.getLeashed && doll.getDistanceSqToEntity(owner) >= 144.0D) {
          val i = MathHelper.floor(owner.posX) - 2
          val j = MathHelper.floor(owner.posZ) - 2
          val k = MathHelper.floor(owner.getEntityBoundingBox.minY)

          for {
            l  <- 0 to 4
            i1 <- 0 to 4
          } {
            val pos     = new BlockPos(i + l, k, j + i1)
            val posDown = pos.down()
            val posUp   = pos.up()
            if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && world.isSideSolid(posDown, EnumFacing.UP) && isEmptyBlock(pos) && isEmptyBlock(posUp)) {
              doll.setLocationAndAngles((i + l) + 0.5F, k, (j + i1) + 0.5F, doll.rotationYaw, doll.rotationPitch)
              pathFinder.clearPathEntity()
              return
            }
          }
        }
      }
    }
  }

  private def isEmptyBlock(pos: BlockPos): Boolean = {
    val state = world.getBlockState(pos)
    if (state.getMaterial == Material.AIR) true else !state.isFullCube
  }
}
