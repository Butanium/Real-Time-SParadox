import engine2D.Game
import sfml.graphics.Texture
import sfml.graphics.Sprite
import engine2D.graphics.Group
import engine2D.GameEngine
import engine2D.objects.GraphicObject
import engine2D.Game
import sfml.graphics.RenderWindow
class DemoGame(window: RenderWindow, debug: Boolean = true)
    extends Game(window, 60, sfml.graphics.Color(0, 0, 100), debug = debug) {
  val engine = GameEngine(1, debug = debug)
  override def init() = {
    val ourObject = DemoObject(engine = engine)
    engine.spawn(ourObject)
    super.init()
  }
}
