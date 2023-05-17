package rtsp.editor

import engine2D.GameEngine
import engine2D.objects.ButtonObject
import rtsp.Constants

/** A menu with multiple choice buttons
  *
  * @param buttons
  *   The buttons to display
  * @param uiParent
  *   The parent menu
  * @param saveState
  *   If true, the last selected button will be highlighted
  * @param engine
  *   The game engine
  */
class MultipleChoiceMenu(
    buttons: List[ButtonObject],
    uiParent: Option[Menu],
    saveState: Boolean,
    engine: GameEngine
) extends Menu(uiParent, engine) {
  buttons.foreach({ b =>
    b.makeSquare()
    b.zIndex = 1
    if saveState then
      // Save the current state of the button
      val onClicked = b.onClicked
      b.onClicked = () => {
        buttons.foreach(_.resetOutline())
        b.background.outlineColor = sfml.graphics.Color.Green()
        onClicked()
      }
  })
  addChildren(buttons: _*)
  if uiParent.isDefined then active = false
  // Resize and position the buttons
  val buttonWidth =
    (engine.window.size.x.toFloat - Constants.EditorC.MENU_PADDING * buttons.length.toFloat) / buttons.length.toFloat
  buttons.zipWithIndex.foreach { case (button, index) =>
    button.position = (
      Constants.EditorC.MENU_PADDING + index * (buttonWidth + Constants.EditorC.MENU_PADDING),
      engine.window.size.y.toFloat / 2f - buttonWidth / 2f
    )
    button.changeBackground(buttonWidth, buttonWidth)
  }

}
