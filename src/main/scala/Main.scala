import sfml.graphics.*
import sfml.window.*
import engine.objects.Group
import Constants.*
@main def main =
  scala.util.Using.Manager { use =>
    val window = use(RenderWindow(VideoMode(WINDOW_WIDTH, WINDOW_HEIGHT), "Real Time SParadox"))
    val sfml_img = use(Texture())
    sfml_img.loadFromFile("src/main/resources/sfml-logo.png")
    val sprite = use(Sprite(sfml_img))
    sprite.position = (600, 0)
    val spriteMid = use(Sprite(sfml_img))
    spriteMid.scale(0.1, 0.1)
    val spriteRight = use(Sprite(sfml_img))
    spriteRight.position = (100, 0)
    val spriteBottom = use(Sprite(sfml_img))
    spriteBottom.move(0, 100)
    spriteBottom.scale(.5, .5)
    val group = Group()
    group.position = (300, 300)
    group.scale(0.5, 0.5)
    group.add(spriteMid, spriteBottom, spriteRight)

    while window.isOpen() do
      for event <- window.pollEvent() do
        event match {
          case _: Event.Closed => window.closeWindow()
          case _               => ()
        }

      window.clear(Color.Black())
      window.draw(group)
      window.draw(sprite)
      // window.draw(spriteBottom)
      // window.draw(spriteRight)
      window.display()
  }
