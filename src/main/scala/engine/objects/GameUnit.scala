package engine.objects

import engine.GameEngine
import sfml.system.Vector2
import engine.objects.DeleteState.*

/** A GameUnit is a GameObject that can move and has health.
  * @param engine
  *   The GameEngine that this GameUnit belongs to.
  * @param parent
  *   The parent GameObject of this GameUnit.
  * @param active
  *   Whether or not this GameUnit is active.
  * @param maxHealth
  *   The maximum health of this GameUnit.
  * @param speed
  *   The speed of this GameUnit.
  */
class GameUnit(
    engine: GameEngine,
    parent: Option[GameObject] = None,
    active: Boolean = true,
    var maxHealth: Int,
    var speed: Float
) extends GameObject(engine, parent, active) {
  var health = maxHealth
  var rooted = false
  var direction: Vector2[Float] = (0, 0)

  def move() = {
    this.position += direction * speed * engine.gameInfo.deltaTime
  }

  /** Called when this GameUnit dies (health <= 0). Will be checked only when
    * the GameUnit is updated.
    */
  def onDeath() = deleteState = ToDelete

  override def update() = {
    if (health <= 0) then {
      active = false
      onDeath()
    }
    if !rooted then move()
    super.update() // Update children
  }

}
