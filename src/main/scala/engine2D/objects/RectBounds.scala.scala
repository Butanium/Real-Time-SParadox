package engine2D.objects

import sfml.graphics.Rect
import sfml.graphics.Transform
import sfml.system.Vector2

/** A trait that defines a sprite that has bounds. Implements functions to edit
  * the scale and position of the sprite using the bounds.
  *
  * @note
  *   You must implement the following methods:
  *   - scale(x: Float, y: Float): Unit
  *   - position: Vector2[Float]
  *   - globalBounds: Rect[Float]
  */
trait RectBounds extends Boundable {

  def scale(x: Float, y: Float): Unit

  var position: Vector2[Float]

  /** Get the global bounds of the sprite.
    * @return
    *   The global bounds of the sprite.
    * @note
    *   The global bounds are the bounds of the sprite after it is transformed.
    *   It is therefore relative to the global transform of the sprite.
    */
  def globalBounds: Rect[Float]

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

    /** Set the size of the sprite. It will use the largest scale ratio to
      * ensure that the sprite fills the given rectangle.
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
