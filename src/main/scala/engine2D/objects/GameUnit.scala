package engine2D.objects

import engine2D.GameEngine
import sfml.system.Vector2

/** A GameUnit is a MovingObject that has health and can die.
  *
  * @param maxHealth
  *   The maximum health of this GameUnit.
  * @param speed
  *   The speed of this GameUnit.
  * @param engine
  *   The GameEngine that this GameUnit belongs to.
  */
class GameUnit(
    val maxHealth: Int,
    speed: Float,
    engine: GameEngine
) extends MovingObject(speed, engine) {

  /** The current health of this GameUnit.
    */
  protected var health_ = maxHealth

  def health = health_

  def takeDamage(damage: Int) =
    health_ -= damage
    health_ = health_.min(maxHealth)

  /** Called when this GameUnit dies (health <= 0). Will be checked only when
    * the GameUnit is updated.
    */
  def onDeath() = markForDeletion()

  def isDead = health_ <= 0

  def isAlive = health_ > 0

  override def onUpdate() = {
    if (health_ <= 0) then {
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
