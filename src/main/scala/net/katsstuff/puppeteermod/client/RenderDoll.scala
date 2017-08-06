package net.katsstuff.puppeteermod.client

import org.lwjgl.opengl.GL11

import net.katsstuff.puppeteermod.PuppeteerMod
import net.katsstuff.puppeteermod.entity.EntityDoll
import net.katsstuff.puppeteermod.dolltype.{DollType, PuppeteerDolls}
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.entity.{RenderBiped, RenderManager}
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.{GlStateManager, Tessellator}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.{MathHelper, Vec3d}
import net.minecraft.util.{EnumHandSide, ResourceLocation}

class RenderDoll(renderManager: RenderManager) extends RenderBiped[EntityDoll](renderManager, ModelDollBase, 0.4F) {

  private var _activeDoll: DollType = PuppeteerDolls.Bare

  override def doRender(entity: EntityDoll, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float): Unit = {
    setCurrentDoll(entity.dollType, entity)
    val clientControlHandler = PuppeteerMod.proxy.asInstanceOf[ClientProxy].clientControlHandler
    if(!clientControlHandler.isBeingControlled(Minecraft.getMinecraft.player, entity) || Minecraft.getMinecraft.gameSettings.thirdPersonView != 0) {
      super.doRender(entity, x, y, z, entityYaw, partialTicks)
    }
    entity.stringedToPlayer.foreach(renderString(entity, _, x, y, z, partialTicks))
  }

  def renderString(doll: EntityDoll, player: EntityPlayer, x: Double, y: Double, z: Double, partialTicks: Float): Unit = {
    //Taken from RenderFish
    val tes  = Tessellator.getInstance
    val vb   = tes.getBuffer
    val side = if (player.getPrimaryHand == EnumHandSide.RIGHT) 1 else -1

    var playerX       = 0D
    var playerY       = 0D
    var playerZ       = 0D
    var playerYOffset = 0D

    if ((renderManager.options == null || renderManager.options.thirdPersonView <= 0) && (player == Minecraft.getMinecraft.player)) {
      val fov   = renderManager.options.fovSetting / 100F
      var vec3d = new Vec3d(side * -0.32D * fov, -0.195D * fov, 0.4D)
      val swing = player.getSwingProgress(partialTicks)
      val f8    = MathHelper.sin(MathHelper.sqrt(swing) * Math.PI.toFloat)

      vec3d = vec3d.rotatePitch(-(player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks) * 0.0175F)
      vec3d = vec3d.rotateYaw(-(player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks) * 0.0175F)
      vec3d = vec3d.rotateYaw(f8 * 0.5F)
      vec3d = vec3d.rotatePitch(-f8 * 0.7F)
      playerX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks + vec3d.x
      playerY = player.prevPosY + (player.posY - player.prevPosY) * partialTicks + vec3d.y
      playerZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks + vec3d.z
      playerYOffset = player.getEyeHeight
    } else {
      val yawOffset    = (player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * partialTicks) * 0.0175F
      val yawOffsetSin = MathHelper.sin(yawOffset)
      val yawOffsetCos = MathHelper.cos(yawOffset)
      val sideOffset   = side * 0.35D

      playerX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks - yawOffsetCos * sideOffset - yawOffsetSin * 0.1D
      playerY = player.prevPosY + player.getEyeHeight + (player.posY - player.prevPosY) * partialTicks - 0.8D
      playerZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks - yawOffsetSin * sideOffset + yawOffsetCos * 0.1D
      playerYOffset = if (player.isSneaking) -0.1875D else 0D
    }

    val dollX = doll.prevPosX + (doll.posX - doll.prevPosX) * partialTicks
    val dollY = doll.prevPosY + (doll.posY - doll.prevPosY) * partialTicks + 0.5D
    val dollZ = doll.prevPosZ + (doll.posZ - doll.prevPosZ) * partialTicks

    val xDiff = playerX - dollX
    val yDiff = (playerY - dollY) + playerYOffset
    val zDiff = playerZ - dollZ

    GlStateManager.disableTexture2D()
    GlStateManager.disableLighting()
    vb.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR)

    for (i <- 0 to 16) {
      val ratio = i / 16F
      vb.pos(x + xDiff * ratio, y + yDiff * (Math.pow(ratio, 2D) + ratio) * 0.5D + 0.5D, z + zDiff * ratio)
        .color(0F, 0F, 0F, 1F)
        .endVertex()
    }

    tes.draw()
    GlStateManager.enableLighting()
    GlStateManager.enableTexture2D()
  }

  override def getEntityTexture(entity: EntityDoll): ResourceLocation = entity.dollType.texture(entity)

  def setCurrentDoll(dollType: DollType, doll: EntityDoll): Unit = {
    if (_activeDoll != dollType) {
      _activeDoll = dollType
      mainModel = dollType.model(doll)

      layerRenderers.clear()

      //TODO
      val layerHeldItem = new LayerDollHeldItem(this)
      //addLayer(layerHeldItem)
    }
  }

  def activeDoll: DollType = _activeDoll
}
