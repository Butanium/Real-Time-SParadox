package engine2D.eventHandling

import scala.collection.mutable.HashMap
import sfml.system.Vector2
import sfml.window.Mouse.Button
import sfml.graphics.RenderWindow

/** Stores the state of the mouse
  *
  * @param window
  *   the window to get the mouse position from
  */
class MouseState(window: RenderWindow) {

  /** The position of the mouse in the window
    */
  private var _mousePos: Vector2[Int] = Vector2(0, 0)

  /** The position of the mouse in the world
    */
  private var _worldPos: Vector2[Float] = Vector2(0, 0)

  /** The state of the mouse buttons. True if pressed, false if not
    */
  private val mouseButtons: HashMap[Button, Boolean] =
    HashMap.empty[Button, Boolean]

  /** Returns the position of the mouse in the window
    */
  def mousePos: Vector2[Int] = _mousePos

  /** Sets the position of the mouse in the window. Also updates the world
    * position
    *
    * @param newPos
    *   the new position of the mouse
    */
  def mousePos_=(newPos: Vector2[Int]) =
    _mousePos = newPos
    _worldPos = window.mapPixelToCoords(newPos)

  /** @return
    *   the position of the mouse in the world
    */
  def worldPos: Vector2[Float] = _worldPos

  /** @param button
    *   the button to check
    * @return
    *   true if the button is pressed, false if not
    */
  def isButtonPressed(button: Button): Boolean =
    mouseButtons.getOrElse(button, false)

  /** Makes the button appear as pressed
    * @param button
    *   the button to change
    */
  def pressButton(button: Button): Unit = mouseButtons(button) = true

  /** Makes the button appear as released
    *
    * @param button
    */
  def releaseButton(button: Button): Unit = mouseButtons(button) = false

}
