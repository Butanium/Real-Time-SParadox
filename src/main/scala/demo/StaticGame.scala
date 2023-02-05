package demo

import engine2D.GameEngine
import sfml.graphics.Texture
import sfml.graphics.Sprite
import engine2D.objects.GraphicObject

class StaticGame extends engine2D.Game {
  val engine = GameEngine(1)
  def init(): Unit = {
    val sfml_img = Texture()
    sfml_img.loadFromFile("src/main/resources/sfml-logo.png")
    val sprite = Sprite(sfml_img)
    sprite.position = (400, 400)
    engine.spawn(GraphicObject(sprite, engine))
  }
}
