package engine2D.objects

import engine2D.GameEngine

/** A SpriteObject is a GraphicObject that is instantiated with a Texture.
  * @param texture
  *   The Texture to use.
  * @param engine
  *   The GameEngine that this GameObject belongs to.
  * @param active
  *   Whether or not this GameObject is active. If it's not active, it won't be
  *   updated or drawn.
  */
class SpriteObject(
    texture: sfml.graphics.Texture,
    engine: GameEngine,
    active: Boolean = true
) extends GraphicObject(sfml.graphics.Sprite(texture), engine, active) {}
