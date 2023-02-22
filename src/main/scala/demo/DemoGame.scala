import demo.*
import engine2D.Game
import sfml.graphics.Texture
import sfml.graphics.Sprite
import engine2D.graphics.Group
import engine2D.GameEngine
import engine2D.objects.GraphicObject
import engine2D.Game
import sfml.graphics.RenderWindow
import sfml.system.Vector2
import engine2D.eventHandling.MouseEvent.*
import sfml.window.Mouse
import engine2D.graphics.TextureManager
import engine2D.objects.SpriteObject
class DemoGame(window: RenderWindow, debug: Boolean = false)
    extends Game(window, 60, sfml.graphics.Color(0, 100, 0), debug = debug) {
  val engine = GameEngine(1, window, debug = debug)
  override def init() = {
    val ourObject = DemoUnit(engine = engine)
    val background = Background(engine)
    val grabable = GrabableDemoUnit(engine, debug = true)
    val test_sprite =
      SpriteObject("sfml-logo.png", engine)
    val test_sprite2 =
      SpriteObject("sfml-logo.png", engine)
    val test_sprite3 =
      SpriteObject("sfml-logo.png", engine)
    // center the sprite
    test_sprite.scale(2f, 2f)
    test_sprite.position = (
      Vector2(
        (window.size.x - test_sprite.globalBounds.width) / 2,
        (window.size.y - test_sprite.globalBounds.height) / 2
      )
    )
    test_sprite.color = sfml.graphics.Color.Green()
    // center the sprite without bounds
    test_sprite2.position = (
      Vector2(
        (window.size.x) / 2,
        (window.size.y) / 2
      )
    )
    // center using origin
    test_sprite3.scale(2f, 2f)
    test_sprite3.setOriginToCenter(test_sprite3.localBounds)
    test_sprite3.position = (
      Vector2(
        (window.size.x) / 2,
        (window.size.y) / 2
      )
    )
    test_sprite3.color = sfml.graphics.Color(0, 0, 255, 50)
    test_sprite2.color = sfml.graphics.Color.Red()

    engine.spawn(
      background,
      test_sprite,
      test_sprite2,
      test_sprite3,
      ourObject,
      grabable
    )
    super.init()
  }
}
