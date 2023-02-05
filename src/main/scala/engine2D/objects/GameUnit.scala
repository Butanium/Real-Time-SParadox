package engine2D.objects

import engine2D.GameEngine
import sfml.system.Vector2
import engine2D.objects.DeleteState.*

/** A GameUnit is a MovingObject that has health and can die.
  *
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
    val maxHealth: Int,
    speed: Float,
    engine: GameEngine,
    baseRotation: Float = 0,
    active: Boolean = true
) extends MovingObject(speed, engine, baseRotation, active) {
  var health = maxHealth

  /** Called when this GameUnit dies (health <= 0). Will be checked only when
    * the GameUnit is updated.
    */
  def onDeath() = deleteState = ToDelete

  override def onUpdate() = {
    if (health <= 0) then {
      active = false
      onDeath()
    }
    super.onUpdate() /* Call the super method. This will (for instance) call
    the onUpdate() method of the MovingObject class. Which
    will make the GameUnit move and call the onUpdate()
    smethod of the GameObject class. Which will call the
    onUpdate() method of the children of this GameUnit.*/
  }

}
