package rtsp.objects
import engine2D.*
import rtsp.Player
import engine2D.objects.GameObject
import engine2D.objects.RectangleObject
import sfml.graphics.Color
import engine2D.objects.TextObject

// When a warrior or an effect is dropped on a SellingBin, it is sold for half its price
// It is a rectangle with the text "Drop here to sell" in it

class SellingBin(engine: GameEngine, player: Player)
    extends GameObject(engine) {
  position = (engine.window.size.x * 0.17f, engine.window.size.y * 0.60f)
  val rectangle = RectangleObject(
    engine.window.size.x * 0.3f,
    engine.window.size.y * 0.1f,
    engine
  )
  rectangle.outlineColor = Color(236, 151, 22)
  rectangle.outlineThickness = 5
  rectangle.fillColor = Color(165, 245, 73, 80)
  rectangle.zIndex = 2
  addChildren(rectangle)
  val text = new TextObject("Drop here to sell", engine)
  text.fillColor = (Color(236, 191, 42))
  text.zIndex = 2
  addChildren(text)
  setOriginToCenter(rectangle.globalBounds)
  text.position = (engine.window.size.x * 0.03f, engine.window.size.y * 0.03f)
  def sell(entity: GameObject): Unit = {
    if (entity.isInstanceOf[RTSPWarrior]) {
      player.money += entity.asInstanceOf[RTSPWarrior].price / 2
    } else if (entity.isInstanceOf[Effect]) {
      player.money += entity.asInstanceOf[Effect].price / 2
    }
    entity.markForDeletion()
  }
}
