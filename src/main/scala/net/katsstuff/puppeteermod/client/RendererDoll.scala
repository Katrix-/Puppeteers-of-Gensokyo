package net.katsstuff.puppeteermod.client

import net.katsstuff.puppeteermod.entity.EntityDoll
import net.katsstuff.puppeteermod.entity.dolltype.{DollType, PuppeteerDolls}
import net.minecraft.client.renderer.entity.{RenderBiped, RenderManager}
import net.minecraft.util.ResourceLocation

class RendererDoll(renderManager: RenderManager) extends RenderBiped[EntityDoll](renderManager, ModelDollBase, 0.4F) {

  private var _activeDoll: DollType = PuppeteerDolls.Bare

  override def doRender(entity: EntityDoll, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float): Unit = {
    setCurrentDoll(entity.dollType)
    super.doRender(entity, x, y, z, entityYaw, partialTicks)
  }

  override def getEntityTexture(entity: EntityDoll): ResourceLocation = entity.dollType.texture

  def setCurrentDoll(dollType: DollType): Unit = {
    if (_activeDoll != dollType) {
      _activeDoll = dollType
      val newModel = dollType.model
      mainModel = newModel

      layerRenderers.clear()

      //TODO
      val layerHead     = new LayerDollCustomHead(ModelDollBase.bipedHead)
      val layerHeldItem = new LayerDollHeldItem(this)
      val layerArmor    = new LayerDollArmor(this)

      /*
			addLayer(layerHead)
			addLayer(layerHeldItem)
			addLayer(layerArmor)
			*/
    }
  }

  def activeDoll: DollType = _activeDoll
}
