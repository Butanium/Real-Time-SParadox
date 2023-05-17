package rtsp.objects

import engine2D.objects.MovingObject
import engine2D.GameEngine
import engine2D.objects.Boundable
import engine2D.objects.SpriteObject
import engine2D.graphics.TextureManager
import scala.collection.mutable.ListBuffer

// A general class of projectiles that goes from a shooter to a target
abstract class Projectile(
    var shooter: RTSPWarrior,
    var target: RTSPWarrior,
    speed: Float,
    spriteTexture: String,
    engine: GameEngine
) extends MovingObject(speed, engine) {
  val sprite =
    new SpriteObject(TextureManager.getTexture(spriteTexture), engine)
  sprite.boundDimensions(16f, 16f)
  setOriginToCenter(sprite.globalBounds)
  addChildren(sprite)
  def onImpact() = {
    markForDeletion()
  }
  override def onUpdate(): Unit = {
    if (target.contains(position)) {
      onImpact()
    } else {
      changeDirectionTo(target.position)
      val angle = math
      .atan2(target.position.y - position.y, target.position.x - position.x)
      .toFloat
      rotation = angle * 180f / math.Pi.toFloat
      super.onUpdate()
    }
  }
}
