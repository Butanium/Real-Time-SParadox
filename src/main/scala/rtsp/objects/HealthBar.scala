package rtsp.objects

import engine2D.objects.RectangleObject
import engine2D.GameEngine
import engine2D.objects.GameUnit
import rtsp.Constants

class HealthBar(var target: GameUnit, engine: GameEngine)
    extends RectangleObject(
      Constants.HEALT_BAR_WIDTH,
      Constants.HEALT_BAR_HEIGHT,
      engine
    ) {
  val currentHealthRectangle = new RectangleObject(
    Constants.HEALT_BAR_WIDTH,
    Constants.HEALT_BAR_HEIGHT,
    engine
  )
  private var positionOffset =
    sfml.system.Vector2[Float](0, -Constants.HEALT_BAR_HEIGHT)
  def setOffset(offset: sfml.system.Vector2[Float]) = positionOffset = offset
  def addOffset(offset: sfml.system.Vector2[Float]) = positionOffset += offset
  currentHealthRectangle.outlineThickness = 0
  currentHealthRectangle.fillColor = sfml.graphics.Color(255, 0, 0, 100)
  this.fillColor = sfml.graphics.Color.Transparent()
  this.outlineColor = sfml.graphics.Color.Black()
  this.outlineThickness = 1
  addChildren(currentHealthRectangle)
  override def onUpdate() = {
    position = target.position + positionOffset
    currentHealthRectangle.scale =
      (target.health.toFloat / target.maxHealth.toFloat, 1f)
    super.onUpdate()
  }
}
