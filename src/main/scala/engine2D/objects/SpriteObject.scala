package engine2D.objects

import engine2D.GameEngine
import sfml.graphics.Rect
import sfml.graphics.Sprite
import sfml.system.Vector2
import engine2D.graphics.TextureManager

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
    val texture: sfml.graphics.Texture,
    engine: GameEngine
) extends GraphicObject(sfml.graphics.Sprite(texture), engine)
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

  /** Check if the sprite contains a point.
    * @param point
    *   The point to check.
    * @return
    *   True if the sprite contains the point, false if not.
    */
  override def contains(point: Vector2[Float]): Boolean =
    globalBounds.contains(point)

  def resize(width: Float, height: Float): Unit =
    scale(
      width / globalBounds.width,
      height / globalBounds.height
    )

  def width: Float = globalBounds.width
  def height: Float = globalBounds.height

  /** Set the width of the sprite.
    * @param width
    *   The new width of the sprite.
    * @param keepRatio
    *   Whether or not to keep the ratio of the sprite.
    */
  def setWidth(width: Float, keepRatio: Boolean = true): Unit =
    val scaleRatio = width / globalBounds.width
    if keepRatio then scale(scaleRatio, scaleRatio)
    else scale(scaleRatio, 1)

  /** Set the height of the sprite.
    * @param height
    *   The new height of the sprite.
    * @param keepRatio
    *   Whether or not to keep the ratio of the sprite.
    */
  def setHeight(height: Float, keepRatio: Boolean = true): Unit =
    val scaleRatio = height / globalBounds.height
    if keepRatio then scale(scaleRatio, scaleRatio)
    else scale(1, scaleRatio)

  /** Set the size of the sprite. It will use the smallest scale ratio to ensure
    * that the sprite fits in the given dimensions.
    * @param width
    *   The new width of the sprite.
    * @param height
    *   The new height of the sprite.
    * @note
    *   This method will not change the position of the sprite.
    */
  def boundDimensions(width: Float, height: Float): Unit =
    val scaleRatio = math.min(
      width / globalBounds.width,
      height / globalBounds.height
    )
    scale(scaleRatio, scaleRatio)

  /** Set the size of the sprite. It will use the smallest scale ratio to ensure
    * that the sprite fits in the given dimensions. It will also center the
    * sprite in the given dimensions.
    * @param rect
    *   The new dimensions of the sprite.
    * @note
    *   This methode WILL change the position of the sprite.
    */
  def boundAndCenter(rect: Rect[Float]): Unit =
    val scaleRatio = math.min(
      rect.width / globalBounds.width,
      rect.height / globalBounds.height
    )
    scale(scaleRatio, scaleRatio)
    position = Vector2(
      rect.left + (rect.width - globalBounds.width) / 2f,
      rect.top + (rect.height - globalBounds.height) / 2f
    )

  /** Set the size of the sprite. It will use the largest scale ratio to ensure
    * that the sprite fills the given rectangle.
    * @param height
    *   The new minimal height of the sprite.
    * @param width
    *   The new minimal width of the sprite.
    * @note
    *   This method will not change the position of the sprite. Neither the
    *   aspect ratio.
    */
  def fillDimensions(width: Float, height: Float): Unit =
    val scaleRatio = math.max(
      width / globalBounds.width,
      height / globalBounds.height
    )
    scale(scaleRatio, scaleRatio)
}

object SpriteObject {

  /** Create a new SpriteObject from a file.
    * @param filename
    *   The path to the file.
    * @param engine
    *   The GameEngine that this GameObject belongs to.
    * @param active
    *   Whether or not this GameObject is active. If it's not active, it won't
    *   be updated or drawn.
    */
  def apply(filename: String, engine: GameEngine): SpriteObject =
    new SpriteObject(TextureManager.getTexture(filename), engine)

  /** Create a new SpriteObject from a texture.
    * @param texture
    *   The texture to use.
    * @param engine
    *   The GameEngine that this GameObject belongs to.
    */
  def apply(texture: sfml.graphics.Texture, engine: GameEngine): SpriteObject =
    new SpriteObject(texture, engine)
}
