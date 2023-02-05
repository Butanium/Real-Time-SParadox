import engine2D.Game
import sfml.graphics.Texture
import sfml.graphics.Sprite
import engine2D.graphics.Group
import engine2D.GameEngine
import engine2D.objects.GraphicObject
import engine2D.Game
class DemoGame extends Game(60, sfml.graphics.Color.apply(0, 0, 100)) {
  val engine = GameEngine(1)
  def init() = {
    val ourObject = DemoObject(engine = engine)
    engine.spawn(ourObject)
  }
}
