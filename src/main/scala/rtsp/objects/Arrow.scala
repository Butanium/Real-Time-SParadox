package rtsp.objects

import engine2D.GameEngine
import engine2D.objects.GameObject
import engine2D.objects.SpriteObject
import engine2D.graphics.TextureManager
import scala.collection.mutable.ListBuffer

/** An arrow that goes from an shooter type warrior to its target when it
  * attacks It is used for the visual animation of the attack of an shooter It
  * goes from the shooter to the target, and then disappears
  */
class Arrow(
    engine: GameEngine,
    _shooter: RTSPWarrior,
    _target: RTSPWarrior,
    spriteTexture: String = "arrow.png"
) extends Projectile(
      _shooter,
      _target,
      50f,
      spriteTexture,
      engine
    ) {
  zIndex = 1
  override def onImpact() = {
    Arrow.hashmapLists(shooter.name) += this
    Arrow.activeArrows -= this
    active = false
    target.takeDamage(shooter.attackDamage)
  }
  engine.spawn(this)
}

object Arrow {
  val activeArrows = ListBuffer[Arrow]()
  // We reuse arrows that are not used anymore instead of creating new ones when possible (saves memory)
  // They are stored in a hashmap of lists, each list corresponding to a type of warrior
  var hashmapLists = Map[String, ListBuffer[Arrow]]()
  hashmapLists += ("Archer" -> ListBuffer[Arrow]())
  hashmapLists += ("Mage" -> ListBuffer[Arrow]())
  hashmapLists += ("Base" -> ListBuffer[Arrow]())
  hashmapLists += ("Healer" -> ListBuffer[Arrow]())

  /** Create an arrow from a shooter to a target If there is an arrow that is
    * not used anymore, reuse it Otherwise, create a new one
    * @note
    *   The arrow is not spawned in the engine automatically
    */
  def factory(shooter: RTSPWarrior, target: RTSPWarrior): Arrow =
    if hashmapLists(shooter.name).isEmpty then {
      shooter.name match {
        case "Archer" =>
          Arrow(shooter.engine, shooter, target, "arrow.png")
        case "Mage" =>
          Arrow(shooter.engine, shooter, target, "magicball.png")
        case "Base" =>
          Arrow(shooter.engine, shooter, target, "bullet.png")
        case "Healer" =>
          Arrow(shooter.engine, shooter, target, "heal.png")
        case _ =>
          throw new Exception(s"Invalid ranged warrior: ${shooter.name}")
      }
    } else {
      val arrow = hashmapLists(shooter.name).head
      hashmapLists(shooter.name) -= arrow
      activeArrows += arrow
      arrow.active = true
      arrow.shooter = shooter
      arrow.target = target
      arrow
    }

  /** Disable all arrows */
  def disableAll() = {
    activeArrows.foreach({ a =>
      a.active = false
      hashmapLists(a.shooter.name) += a
    })
    activeArrows.clear()
  }
}
