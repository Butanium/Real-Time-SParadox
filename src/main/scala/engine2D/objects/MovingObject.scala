package engine2D.objects

import engine2D.GameEngine
import sfml.system.Vector2
import engine2D.objects.DeleteState.*

/** A MovingObject is a GameObject that can move.
  * @param speed
  *   The speed of this MovingObject.
  * @param engine
  *   The GameEngine that this MovingObject belongs to.
  * @param baseRotation
  *   The base rotation of this MovingObject. The rotation will be set to this
  *   value when the direction is (0, 0).
  * @param active
  *   Whether or not this MovingObject is active.
  * @param rooted
  *   Whether or not this MovingObject is rooted. If it's rooted, it won't move.
  * @param rotationEnabled
  *   Whether or not this MovingObject can rotate. If it is set to false, the
  *   game object will keep its current rotation.
  */
class MovingObject(
    var speed: Float,
    engine: GameEngine,
    var baseRotation: Float = 0,
    active: Boolean = true
) extends GameObject(engine, active) {
  private var direction: Vector2[Float] = (0, 0)
  rotation = baseRotation
  var rooted: Boolean = false
  var rotationEnabled: Boolean = true

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
      if rotationEnabled then
        rotation = baseRotation +
          math.atan2(-direction.y, direction.x).toFloat * 180 / math.Pi.toFloat
  }

  /** Resets the rotation of this MovingObject to the base rotation.
    */
  def resetRotation() = rotation = baseRotation

  /** Returns the direction of this MovingObject.
    * @return
    *   The direction of this MovingObject.
    */
  def getDirection(): Vector2[Float] = direction

  /** Called when this MovingObject is updated. Will move the MovingObject if it
    * is not rooted.
    */
  override def onUpdate() = {
    if !rooted then move(direction * speed)
    super.onUpdate()
  }

}
