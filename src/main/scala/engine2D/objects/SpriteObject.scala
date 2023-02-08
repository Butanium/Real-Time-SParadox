package engine2D.objects

import engine2D.GameEngine
import sfml.graphics.Rect
import sfml.graphics.Sprite

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
) extends GraphicObject(sfml.graphics.Sprite(texture), engine, active)
    with Boundable {

  /** The Sprite of this SpriteObject.
    */
  val sprite: Sprite = this.drawable.asInstanceOf[Sprite]

  /** The color of the sprite.
    */
  def color: sfml.graphics.Color = sprite.color

  /** Set the color of the sprite.
    * @param c
    *   The new color of the sprite.
    */
  def color_=(c: sfml.graphics.Color): Unit = sprite.color = c

  /** Get the local bounds of the sprite.
    * @return
    *   The local bounds of the sprite.
    * @note
    *   The local bounds are the bounds of the sprite before it is transformed.
    */
  def localBounds: Rect[Float] =
    sprite.localBounds

  /** Get the global bounds of the sprite.
    * @return
    *   The global bounds of the sprite.
    * @note
    *   The global bounds are the bounds of the sprite after it is transformed.
    *   It is therefore relative to the global transform of the sprite.
    */
  def globalBounds: Rect[Float] =
    globalTransform.transformRect(localBounds)

  def bounds = globalBounds

}
