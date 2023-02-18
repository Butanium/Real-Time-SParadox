package engine2D.objects

import engine2D.GameEngine
import sfml.system.Vector2

/** A MovingObject is a GameObject that can move.
  * @param speed
  *   The speed of this MovingObject.
  * @param engine
  *   The GameEngine that this MovingObject belongs to.
  * @param baseRotation
  *   The base rotation of this MovingObject. The rotation of this MovingObject
  *   will be set to baseRotation + directionAngle.
  * @param rooted
  *   Whether or not this MovingObject is rooted. If it's rooted, it won't move.
  * @param rotationEnabled
  *   Whether or not this MovingObject can rotate. If it is set to false, the
  *   game object will keep its current rotation.
  * @param active
  *   Whether or not this MovingObject is active.
  */
class MovingObject(
    var speed: Float,
    engine: GameEngine,
    var baseRotation: Float = 0,
    var rooted: Boolean = false,
    var rotationEnabled: Boolean = true,
    active: Boolean = true
) extends GameObject(engine, active) {
  private var _direction: Vector2[Float] = (0, 0)
  rotation = baseRotation

  /** Sets the direction of this MovingObject. The direction will be normalized.
    * @param newDirection
    *   The new direction of this MovingObject.
    */
  def changeDirection(newDirection: Vector2[Float]) = {
    if newDirection.x == 0 && newDirection.y == 0 then _direction = newDirection
    else
      _direction = newDirection * (1 / math.sqrt(
        newDirection.x * newDirection.x + newDirection.y * newDirection.y
      )).toFloat
      if rotationEnabled then
        rotation = baseRotation +
          math
            .atan2(-_direction.y, _direction.x)
            .toFloat * 180 / math.Pi.toFloat
  }

  /** Resets the rotation of this MovingObject to the base rotation.
    */
  def resetRotation() = rotation = baseRotation

  /** The direction of this MovingObject.
    */
  def direction: Vector2[Float] = _direction

  /** Changes the direction of this MovingObject.
    * @param newDirection
    *   The new direction of this MovingObject.
    */
  def direction_(newDirection: Vector2[Float]) = changeDirection(newDirection)

  def changeDirectionTo(target: GameTransform) = {
    changeDirection(target.position + (position * -1))
  }

  /** Called when this MovingObject is updated. Will move the MovingObject if it
    * is not rooted.
    */
  override def onUpdate() = {
    if !rooted then move(_direction * speed * engine.deltaTime)
    super.onUpdate()
  }

}
