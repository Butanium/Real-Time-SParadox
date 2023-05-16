package rtsp.objects

import engine2D.objects.MovingObject
import engine2D.GameEngine
import engine2D.objects.Boundable

abstract class Projectile(
    shooter: RTSPWarrior,
    target: Boundable,
    speed: Float,
    engine: GameEngine
) extends MovingObject(speed, engine) {
  def onImpact(): Unit

  override def onUpdate(): Unit = {
    if (target.contains(position)) {}
  }
}
