import engine2D.Game
import sfml.graphics.Texture
import sfml.graphics.Sprite
import engine2D.graphics.Group
import engine2D.GameEngine
import engine2D.objects.GraphicObject
import engine2D.Game
class DemoGame(debug: Boolean = true)
    extends Game(60, sfml.graphics.Color(0, 0, 100), debug = debug) {
  val engine = GameEngine(1, debug = debug)
  def init() = {
    val ourObject = DemoObject(engine = engine)
    engine.spawn(ourObject)
  }
}
