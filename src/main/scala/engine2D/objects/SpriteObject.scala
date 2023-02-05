package engine2D.objects

import engine2D.GameEngine

/** A SpriteObject is a GraphicObject that is instantiated with a Texture.
  */
class SpriteObject(
    texture: sfml.graphics.Texture,
    engine: GameEngine,
    active: Boolean = true
) extends GraphicObject(sfml.graphics.Sprite(texture), engine, active) {}
