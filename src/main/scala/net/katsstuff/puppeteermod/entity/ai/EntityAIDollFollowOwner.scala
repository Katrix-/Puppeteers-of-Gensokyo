package net.katsstuff.puppeteermod.entity.ai

import scala.annotation.tailrec

import net.katsstuff.puppeteermod.PuppeteerMod
import net.katsstuff.puppeteermod.entity.EntityDoll
import net.katsstuff.puppeteermod.helper.LogHelper
import net.minecraft.block.material.Material
import net.minecraft.entity.ai.EntityAIBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.pathfinding.{PathNavigateGround, PathNodeType}
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.{BlockPos, MathHelper}

class EntityAIDollFollowOwner(val doll: EntityDoll, val followSpeed: Double, var minDist: Float, var maxDist: Float) extends EntityAIBase {
  private var owner: EntityPlayer = _
  private val world               = doll.world
  final private val petPathfinder = doll.getNavigator
  private var timeToRecalcPath    = 0
  private var oldWaterCost        = 0F

  this.setMutexBits(3)
  if (!doll.getNavigator.isInstanceOf[PathNavigateGround]) throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal")

  override def shouldExecute: Boolean = {
    val owner = doll.stringedToPlayer

    owner.exists { p =>
      if (PuppeteerMod.proxy.dollControlHandler.isControlling(p, doll) || p.isSpectator || doll.getDistanceSqToEntity(p) < (minDist * minDist)) false
      else {
        this.owner = owner.get
        true
      }
    }
  }

  override def shouldContinueExecuting: Boolean = !petPathfinder.noPath && doll.getDistanceSqToEntity(owner) > (maxDist * maxDist)

  override def startExecuting(): Unit = {
    timeToRecalcPath = 0
    oldWaterCost = doll.getPathPriority(PathNodeType.WATER)
    doll.setPathPriority(PathNodeType.WATER, 0.0F)
  }

  override def resetTask(): Unit = {
    owner = null
    petPathfinder.clearPathEntity()
    doll.setPathPriority(PathNodeType.WATER, oldWaterCost)
  }
  private def isEmptyBlock(pos: BlockPos) = {
    val state = this.world.getBlockState(pos)
    if (state.getMaterial == Material.AIR) true else !state.isFullCube
  }

  override def updateTask(): Unit = {
    this.doll.getLookHelper.setLookPositionWithEntity(owner, 10.0F, doll.getVerticalFaceSpeed)
    timeToRecalcPath -= 1
    if (timeToRecalcPath <= 0) {
      timeToRecalcPath = 10
      if (!petPathfinder.tryMoveToEntityLiving(owner, followSpeed) && !doll.getLeashed && doll.getDistanceSqToEntity(owner) >= 144.0D) {
        val i = MathHelper.floor(owner.posX) - 2
        val j = MathHelper.floor(owner.posZ) - 2
        val k = MathHelper.floor(owner.getEntityBoundingBox.minY)

        @tailrec
        def findPos(l: Int, i1: Int): Unit = {
          val baseBlock = new BlockPos(i + l, k, j + i1)
          val downBlock = baseBlock.down
          if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && world.getBlockState(downBlock).isSideSolid(world, downBlock, EnumFacing.UP) &&
            isEmptyBlock(baseBlock) && isEmptyBlock(baseBlock.up)) {
            doll.setLocationAndAngles((i + l) + 0.5F, k, (j + i1) + 0.5F, doll.rotationYaw, doll.rotationPitch)
            petPathfinder.clearPathEntity()
          }
          else {
            if(l < 4) {
              if(i1 < 4) findPos(l, i1 + 1)
              else findPos(l + 1, 0)
            }
            else if(i1 < 4) findPos(l, i1 + 1)
          }
        }

        findPos(0, 0)
      }
    }
  }
}
