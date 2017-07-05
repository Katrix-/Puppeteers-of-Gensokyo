package net.katsstuff.puppeteermod.entity.ai

import net.katsstuff.puppeteermod.entity.EntityDoll
import net.minecraft.entity.ai.EntityAIBase
import net.minecraft.util.text.TextComponentString

class EntityDollAIBase(doll: EntityDoll) extends EntityAIBase {

  def shouldExecute:             Boolean = false
  override def startExecuting(): Unit    = doll.chatMessage(new TextComponentString(s"$getAIName.start"))
  override def resetTask():      Unit    = doll.chatMessage(new TextComponentString(s"$getAIName.reset"))

  def getAIName: String = this.getClass.getSimpleName
}
