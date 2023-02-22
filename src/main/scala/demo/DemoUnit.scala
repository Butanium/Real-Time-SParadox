package demo
import engine2D.objects.GameUnit
import engine2D.objects.GameObject
import engine2D.GameEngine
import sfml.system.Vector2
import engine2D.graphics.TextureManager
import sfml.window.Mouse
import scala.collection.mutable.ListBuffer
import engine2D.eventHandling.MouseEvent
import scala.scalanative.posix.net.`if`
import sfml.window.Event.MouseButtonPressed
import engine2D.objects.SpriteObject

/** An example object that extends GameUnit. It will cross the screen from top
  * left to bottom right and back. When it dies, it will spawn two children. The
  * children will have the opposite direction and the same speed. Also, the
  * amount of DemoUnits spawned is limited.
  */
class DemoUnit(
    engine: GameEngine,
    speed: Float = 0.2,
    direction: Vector2[Float] = (1, 1)
) extends GameUnit(
      maxHealth = (1131 / Math.abs(speed)).toInt,
      speed,
      engine
    ) {
  changeDirection(direction)
  var debug = true
  val sprite = engine2D.objects.SpriteObject(
    TextureManager.getTexture("aircraft.png"),
    engine
  )

  DemoUnit.nb_spawn += 1
  sprite.scale = (0.4f, 0.4f)
  add(sprite)
  def onFlip() = sprite.color = sfml.graphics.Color.Red()
  def onFlop() = sprite.color = sfml.graphics.Color.Blue()

  /* First way to register a mouse event. Here ClickBoundFlipFlopEvent is
   * used. It will register a click event on the sprite. When the sprite is
   * clicked, the onFlip function will be called. When the sprite is
   * clicked again, the onFlop function will be called.
   * You HAVE to add the returned listeners to the listeners list of the
   * object. Otherwise, the listeners won't be deleted when the object is deleted.
   */
  listeners ++= engine.mouseManager
    .registerClickBoundFlipFlopEvent(
      Mouse.Button.Left,
      sprite,
      onFlip,
      onFlop
    )
    .toList

  override def onDeath() = {
    DemoUnit.nb_spawn -= 1
    if (DemoUnit.nb_spawn < DemoUnit.SPAWN_LIMIT) {
      for (i <- 0 to 1) {
        val child = DemoUnit(engine,speed, direction * -1)
        val randx: Float =
          (Math.random() * 0.33 + 0.66).toFloat * (if Math.random() > 0.5 then 1
                                                   else -1)
        val randy: Float =
          (Math.random() * 0.33 + 0.66).toFloat * (if Math.random() > 0.5 then 1
                                                   else -1)
        child.position = this.position + Vector2(10 * randx, 10 * randy)
        engine.spawn(child)
      }
    }
    health = maxHealth
    changeDirection(direction * -1)
    super.onDeath()
  }
  override def onUpdate(): Unit = {
    // Second way to check for mouse events. Here, the mouseState is checked in
    // order to know if the mouse is over the object. If it is, the object will
    // be scaled up. If it isn't, the object will be scaled down.
    // if (sprite.globalBounds.contains(engine.mouseState.worldPos)) {
    //   scale = (1.1f, 1.1f)
    // } else {
    //   scale = (1f, 1f)
    // }

    health -= 1
    super.onUpdate()
  }
}

object DemoUnit {
  var nb_spawn = 0
  val SPAWN_LIMIT = 100
}
