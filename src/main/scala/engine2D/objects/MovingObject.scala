package engine2D.objects

import engine2D.GameEngine
import sfml.system.Vector2
import engine2D.objects.DeleteState.*

/** A MovingObject is a GameObject that can move.
  * @param engine
  *   The GameEngine that this MovingObject belongs to.
  * @param parent
  *   The parent GameObject of this MovingObject.
  * @param active
  *   Whether or not this MovingObject is active.
  * @param speed
  *   The speed of this MovingObject.
  */
class MovingObject(
    var speed: Float,
    engine: GameEngine,
    var baseRotation: Float = 0,
    active: Boolean = true
) extends GameObject(engine, active) {
  var rooted = false
  var canRotate = true
  private var direction: Vector2[Float] = (0, 0)

  /** Sets the direction of this MovingObject. The direction will be normalized.
    * @param newDirection
    *   The new direction of this MovingObject.
    */
  def changeDirection(newDirection: Vector2[Float]) = {
    if newDirection.x == 0 && newDirection.y == 0 then direction = newDirection
    else
      direction = newDirection * (1 / math.sqrt(
        newDirection.x * newDirection.x + newDirection.y * newDirection.y
      )).toFloat
      if canRotate then
        rotation = baseRotation +
          math.atan2(-direction.y, direction.x).toFloat * 180 / math.Pi.toFloat
  }

  /** Returns the direction of this MovingObject.
    * @return
    *   The direction of this MovingObject.
    */
  def getDirection(): Vector2[Float] = direction

  override def onUpdate() = {
    if !rooted then move(direction * speed)
    super.onUpdate()
  }

}
