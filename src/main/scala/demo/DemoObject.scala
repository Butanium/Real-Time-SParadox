import engine2D.objects.GameUnit
import engine2D.objects.GameObject
import engine2D.GameEngine
import sfml.system.Vector2

/** An example object that extends GameUnit.
  */
class DemoObject(
    speed: Float = 3,
    engine: GameEngine,
    direction: Vector2[Float] = (1, 1)
) extends GameUnit(
      maxHealth = (1131 / Math.abs(speed)).toInt,
      speed,
      engine
    ) {
  changeDirection(direction)
  var debug = true
  override def onCreation() = {
    DemoObject.nb_spawn += 1
    val sprite =
      engine2D.objects.SpriteObject(
        engine.textureManager.getTexture("aircraft.png"),
        engine
      )
    sprite.scale(0.1f, 0.1f)
    add(sprite)
    super.onCreation()
  }
  override def onDeath() = {
    DemoObject.nb_spawn -= 1
    if (DemoObject.nb_spawn < DemoObject.SPAWN_LIMIT) {
      for (i <- 0 to 1) {
        val child = DemoObject(speed, engine, direction * -1)
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
  }
  override def onUpdate(): Unit = {
    health -= 1
    super.onUpdate()
  }
}

object DemoObject {
  var nb_spawn = 0
  val SPAWN_LIMIT = 100
}
