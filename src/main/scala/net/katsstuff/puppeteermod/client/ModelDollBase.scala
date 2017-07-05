package net.katsstuff.puppeteermod.client

import net.minecraft.client.model.{ModelBiped, ModelRenderer}
import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.util.EnumHandSide
import net.minecraft.util.math.MathHelper

object ModelDollBase extends ModelBiped {

  val ribbon = new ModelRenderer(this, 36, 0)
  val skirt1 = new ModelRenderer(this, 16, 0)
  val skirt2 = new ModelRenderer(this, 16, 6)

  bipedRightArm = new ModelRenderer(this, 0, 15)
  bipedRightLeg = new ModelRenderer(this, 0, 21)
  bipedLeftArm = new ModelRenderer(this, 8, 15)
  bipedLeftLeg = new ModelRenderer(this, 8, 21)
  bipedHead = new ModelRenderer(this, 0, 0)
  bipedBody = new ModelRenderer(this, 0, 8)

  textureWidth = 64
  textureHeight = 32

  skirt1.setRotationPoint(0.0F, 5.0F, 0.0F)
  skirt1.addBox(-3.0F, -1.0F, -2.0F, 6, 2, 4, 0.0F)
  ribbon.setRotationPoint(0.0F, -3.5F, 1.3F)
  ribbon.addBox(-3.0F, -1.5F, 0.0F, 6, 3, 1, 0.0F)
  setRotateAngle(ribbon, 0.5235987755982988F, 0.0F, 0.0F)
  bipedRightArm.setRotationPoint(-3.0F, 1.0F, 0.0F)
  bipedRightArm.addBox(-1.0F, -1.0F, -1.0F, 2, 4, 2, 0.0F)
  setRotateAngle(bipedRightArm, 0.0F, 0.0F, 0.10000736613927509F)
  bipedRightLeg.setRotationPoint(-1.0F, 5.0F, 0.0F)
  bipedRightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 5, 2, 0.0F)
  bipedLeftArm.setRotationPoint(3.0F, 1.0F, -0.0F)
  bipedLeftArm.addBox(-1.0F, -1.0F, -1.0F, 2, 4, 2, 0.0F)
  setRotateAngle(bipedLeftArm, 0.0F, 0.0F, -0.10000736613927509F)
  bipedLeftLeg.setRotationPoint(1.0F, 5.0F, 0.0F)
  bipedLeftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 5, 2, 0.0F)
  bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F)
  bipedHead.addBox(-2.0F, -4.0F, -2.0F, 4, 4, 4, 0.0F)
  bipedBody.setRotationPoint(0.0F, 14.0F, 0.0F)
  bipedBody.addBox(-2.0F, 0.0F, -1.0F, 4, 5, 2, 0.0F)
  skirt2.setRotationPoint(0.0F, 2.0F, 0.0F)
  skirt2.addBox(-4.0F, -1.0F, -3.0F, 8, 2, 6, 0.0F)
  bipedBody.addChild(skirt1)
  bipedHead.addChild(ribbon)
  bipedBody.addChild(bipedRightArm)
  bipedBody.addChild(bipedRightLeg)
  bipedBody.addChild(bipedLeftArm)
  bipedBody.addChild(bipedLeftLeg)
  bipedBody.addChild(bipedHead)
  skirt1.addChild(skirt2)

  override def render(entity: Entity, limbSwing: Float, limbSwingAmount: Float, age: Float, headYaw: Float, headPitch: Float, scale: Float): Unit = {
    setRotationAngles(limbSwing, limbSwingAmount, age, headYaw, headPitch, scale, entity)
    bipedBody.render(scale)
  }

  def setRotateAngle(modelRenderer: ModelRenderer, x: Float, y: Float, z: Float) {
    modelRenderer.rotateAngleX = x
    modelRenderer.rotateAngleY = y
    modelRenderer.rotateAngleZ = z
  }

  override def setRotationAngles(
      limbSwing: Float,
      limbSwingAmount: Float,
      age: Float,
      headYaw: Float,
      headPitch: Float,
      scale: Float,
      entity: Entity
  ): Unit = {
    val flag = entity match {
      case living: EntityLivingBase => living.getTicksElytraFlying > 4
      case _                        => false
    }
    this.bipedHead.rotateAngleY = headYaw * 0.017453292F

    if (flag) this.bipedHead.rotateAngleX = -(Math.PI.toFloat / 4F)
    else this.bipedHead.rotateAngleX = headPitch * 0.017453292F

    this.bipedBody.rotateAngleY = 0.0F
    this.bipedRightArm.rotationPointZ = 0.0F
    this.bipedRightArm.rotationPointX = -3.0F
    this.bipedLeftArm.rotationPointZ = 0.0F
    this.bipedLeftArm.rotationPointX = 3.0F
    var f = 1.0F

    if (flag) {
      f = (entity.motionX * entity.motionX + entity.motionY * entity.motionY + entity.motionZ * entity.motionZ).toFloat
      f = f / 0.2F
      f = f * f * f
    }

    if (f < 1.0F) f = 1.0F

    this.bipedRightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + Math.PI.toFloat) * 2.0F * limbSwingAmount * 0.5F / f
    this.bipedLeftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F / f
    this.bipedRightArm.rotateAngleZ = 0.0F
    this.bipedLeftArm.rotateAngleZ = 0.0F
    this.bipedRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount / f
    this.bipedLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + Math.PI.toFloat) * 1.4F * limbSwingAmount / f
    this.bipedRightLeg.rotateAngleY = 0.0F
    this.bipedLeftLeg.rotateAngleY = 0.0F
    this.bipedRightLeg.rotateAngleZ = 0.0F
    this.bipedLeftLeg.rotateAngleZ = 0.0F

    if (this.isRiding) {
      this.bipedRightArm.rotateAngleX += -(Math.PI.toFloat / 5F)
      this.bipedLeftArm.rotateAngleX += -(Math.PI.toFloat / 5F)
      this.bipedRightLeg.rotateAngleX = -1.4137167F
      this.bipedRightLeg.rotateAngleY = Math.PI.toFloat / 10F
      this.bipedRightLeg.rotateAngleZ = 0.07853982F
      this.bipedLeftLeg.rotateAngleX = -1.4137167F
      this.bipedLeftLeg.rotateAngleY = -(Math.PI.toFloat / 10F)
      this.bipedLeftLeg.rotateAngleZ = -0.07853982F
    }

    this.bipedRightArm.rotateAngleY = 0.0F
    this.bipedRightArm.rotateAngleZ = 0.0F

    this.leftArmPose match {
      case ModelBiped.ArmPose.EMPTY =>
        this.bipedLeftArm.rotateAngleY = 0.0F
      case ModelBiped.ArmPose.BLOCK =>
        this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - 0.9424779F
        this.bipedLeftArm.rotateAngleY = 0.5235988F
      case ModelBiped.ArmPose.ITEM =>
        this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - (Math.PI.toFloat / 10F)
        this.bipedLeftArm.rotateAngleY = 0.0F
      case _ =>
    }

    this.rightArmPose match {
      case ModelBiped.ArmPose.EMPTY =>
        this.bipedRightArm.rotateAngleY = 0.0F
      case ModelBiped.ArmPose.BLOCK =>
        this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - 0.9424779F
        this.bipedRightArm.rotateAngleY = -0.5235988F
      case ModelBiped.ArmPose.ITEM =>
        this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - (Math.PI.toFloat / 10F)
        this.bipedRightArm.rotateAngleY = 0.0F
      case _ =>
    }

    if (this.swingProgress > 0.0F) {
      val enumHandSide  = this.getMainHand(entity)
      val modelRenderer = this.getArmForSide(enumHandSide)
      var f1            = this.swingProgress
      this.bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt(f1) * (Math.PI.toFloat * 2F)) * 0.2F
      if (enumHandSide == EnumHandSide.LEFT) this.bipedBody.rotateAngleY *= -1.0F
      this.bipedRightArm.rotationPointZ = MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F
      this.bipedRightArm.rotationPointX = -MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F
      this.bipedLeftArm.rotationPointZ = -MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F
      this.bipedLeftArm.rotationPointX = MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F
      this.bipedRightArm.rotateAngleY += this.bipedBody.rotateAngleY
      this.bipedLeftArm.rotateAngleY += this.bipedBody.rotateAngleY
      this.bipedLeftArm.rotateAngleX += this.bipedBody.rotateAngleY
      f1 = 1.0F - this.swingProgress
      f1 = f1 * f1
      f1 = f1 * f1
      f1 = 1.0F - f1
      val f2 = MathHelper.sin(f1 * Math.PI.toFloat)
      val f3 = MathHelper.sin(this.swingProgress * Math.PI.toFloat) * -(this.bipedHead.rotateAngleX - 0.7F) * 0.75F
      modelRenderer.rotateAngleX = (modelRenderer.rotateAngleX.toDouble - (f2.toDouble * 1.2D + f3.toDouble)).toFloat
      modelRenderer.rotateAngleY += this.bipedBody.rotateAngleY * 2.0F
      modelRenderer.rotateAngleZ += MathHelper.sin(this.swingProgress * Math.PI.toFloat) * -0.4F
    }

    if (this.isSneak) {
      this.bipedBody.rotateAngleX = 0.5F
      this.bipedRightArm.rotateAngleX += 0.4F
      this.bipedLeftArm.rotateAngleX += 0.4F
      this.bipedRightLeg.rotationPointZ = 4.0F
      this.bipedLeftLeg.rotationPointZ = 4.0F
      this.bipedRightLeg.rotationPointY = 4.0F
      this.bipedLeftLeg.rotationPointY = 4.0F
      this.bipedHead.rotationPointY = 1.0F
    } else {
      this.bipedBody.rotateAngleX = 0.0F
      this.bipedRightLeg.rotationPointZ = 0.1F
      this.bipedLeftLeg.rotationPointZ = 0.1F
      this.bipedRightLeg.rotationPointY = 5.0F
      this.bipedLeftLeg.rotationPointY = 5.0F
      this.bipedHead.rotationPointY = 0.0F
    }

    this.bipedRightArm.rotateAngleZ += MathHelper.cos(age * 0.09F) * 0.05F + 0.05F
    this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(age * 0.09F) * 0.05F + 0.05F
    this.bipedRightArm.rotateAngleX += MathHelper.sin(age * 0.067F) * 0.05F
    this.bipedLeftArm.rotateAngleX -= MathHelper.sin(age * 0.067F) * 0.05F

    if (this.rightArmPose == ModelBiped.ArmPose.BOW_AND_ARROW) {
      this.bipedRightArm.rotateAngleY = -0.1F + this.bipedHead.rotateAngleY
      this.bipedLeftArm.rotateAngleY = 0.1F + this.bipedHead.rotateAngleY + 0.4F
      this.bipedRightArm.rotateAngleX = -(Math.PI.toFloat / 2F) + this.bipedHead.rotateAngleX
      this.bipedLeftArm.rotateAngleX = -(Math.PI.toFloat / 2F) + this.bipedHead.rotateAngleX
    } else if (this.leftArmPose == ModelBiped.ArmPose.BOW_AND_ARROW) {
      this.bipedRightArm.rotateAngleY = -0.1F + this.bipedHead.rotateAngleY - 0.4F
      this.bipedLeftArm.rotateAngleY = 0.1F + this.bipedHead.rotateAngleY
      this.bipedRightArm.rotateAngleX = -(Math.PI.toFloat / 2F) + this.bipedHead.rotateAngleX
      this.bipedLeftArm.rotateAngleX = -(Math.PI.toFloat / 2F) + this.bipedHead.rotateAngleX
    }
  }
}
