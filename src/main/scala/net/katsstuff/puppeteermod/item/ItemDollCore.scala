package net.katsstuff.puppeteermod.item

import net.katsstuff.danmakucore.data.Vector3
import net.katsstuff.puppeteermod.lib.LibItemName
import net.katsstuff.puppeteermod.PuppeteerMod
import net.katsstuff.puppeteermod.entity.EntityDoll
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.item.{EnumAction, ItemStack}
import net.minecraft.world.World
import net.katsstuff.puppeteermod.helper.JavaHelper._
import net.katsstuff.puppeteermod.helper.LogHelper
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.{ActionResult, EnumActionResult, EnumHand}

class ItemDollCore extends ItemModBase(LibItemName.DollCore) {
  setCreativeTab(PuppeteerMod.CreativeTab)

  override def getMaxItemUseDuration(stack: ItemStack): Int = 32

  override def onItemRightClick(worldIn: World, playerIn: EntityPlayer, handIn: EnumHand): ActionResult[ItemStack] = {
    playerIn.setActiveHand(handIn)
    new ActionResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn))
  }

  override def onItemUseFinish(stack: ItemStack, worldIn: World, entityLiving: EntityLivingBase): ItemStack = {
    if(!entityLiving.world.isRemote) {
      entityLiving match {
        case player: EntityPlayerMP =>
          Vector3.getEntityLookedAt(player, e => e.isInstanceOf[EntityDoll], 4).asScala.foreach {
            case doll: EntityDoll =>
              val isOwner = doll.ownerUuid == player.getUniqueID
              val isStringed = doll.stringedTo.contains(player.getUniqueID)

              if(isOwner && isStringed) PuppeteerMod.proxy.dollControlHandler.addControlledDoll(player, doll)
              else if(!isStringed) player.sendMessage(new TextComponentTranslation("doll.notStringed"))
              else if(!isOwner) player.sendMessage(new TextComponentTranslation("doll.nonOwner"))
          }
        case _ =>
      }
    }

    stack
  }

  override def getItemUseAction(stack: ItemStack): EnumAction = EnumAction.BOW

}
