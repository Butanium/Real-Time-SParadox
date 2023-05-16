package rtsp.objects

import engine2D.GameEngine
import engine2D.objects.GameObject
import engine2D.objects.SpriteObject
import engine2D.graphics.TextureManager

// An arrow that goes from an archer type warrior to its target when it attacks
// It is used for the visual animation of the attack of an archer
// It goes from the archer to the target, and then disappears

class Arrow(
    engine: GameEngine,
    val archer: RTSPWarrior,
    val target: RTSPWarrior
) extends GameObject(engine) {
  val sprite = new SpriteObject(TextureManager.getTexture("arrow.png"), engine)
  sprite.boundDimensions(16f, 16f)
  setOriginToCenter(sprite.globalBounds)
  addChildren(sprite)
  position = archer.position
  val speed = 10f
  override def onUpdate() = {
    // update the direction of the arrow
    val angle = math
      .atan2(target.position.y - position.y, target.position.x - position.x)
      .toFloat
    rotation = angle * 180f / math.Pi.toFloat
    // move the arrow towards the target
    val dx = math.cos(angle) * speed
    val dy = math.sin(angle) * speed
    position = (position.x + dx, position.y + dy)
    // if the arrow is close enough to the target, destroy it
    if (distanceTo(target) < 10) {
      markForDeletion()
    }
    super.onUpdate()
  }
}
