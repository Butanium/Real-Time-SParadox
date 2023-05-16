package rtsp.objects

import engine2D.GameEngine
import engine2D.objects.GameObject
import engine2D.objects.SpriteObject
import engine2D.graphics.TextureManager
import scala.collection.mutable.ListBuffer

// An arrow that goes from an archer type warrior to its target when it attacks
// It is used for the visual animation of the attack of an archer
// It goes from the archer to the target, and then disappears

class Arrow(
    engine: GameEngine,
    _shooter: RTSPWarrior,
    _target: RTSPWarrior
) extends Projectile(
      _shooter,
      _target,
      50f,
      "arrow.png",
      engine
    ) {
  override def onImpact() = {
    Arrow.listArrows += this
    active = false
    target.takeDamage(shooter.attackDamage)
  }
  engine.spawn(this)
}

object Arrow {
  var listArrows = ListBuffer[Arrow]()
  def factory(archer: RTSPWarrior, target: RTSPWarrior) =
    // We use listArrows to reuse arrows that are not used anymore instead of creating new ones (saves memory)
    if listArrows.isEmpty then {
      Arrow(archer.engine, archer, target)
    }
    else {
      val arrow = listArrows.head
      listArrows -= arrow
      arrow.active = true
      arrow.shooter = archer
      arrow.target = target
      arrow
    }
}