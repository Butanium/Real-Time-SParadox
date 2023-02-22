package demo

import engine2D.objects.SpriteObject
import engine2D.GameEngine
import engine2D.graphics.TextureManager

class Background(engine: GameEngine)
    extends SpriteObject(TextureManager.getTexture("sfml-logo.png"), engine) {

  setWidth(engine.window.size.x)

}
