package rtsp.editor

import engine2D.GameEngine
import engine2D.objects.ButtonObject
import rtsp.Constants

class MultipleChoiceMenu(
    buttons: List[ButtonObject],
    uiParent: Option[Menu],
    saveState: Boolean,
    engine: GameEngine
) extends Menu(uiParent, engine) {
  if uiParent.isDefined then active = false
  buttons.foreach({ b =>
    b.makeSquare()
    b.zIndex = 1
    if saveState then
      val onClicked = b.onClicked
      b.onClicked = () => {
        buttons.foreach(_.resetOutline())
        b.background.outlineColor = sfml.graphics.Color.Green()
        onClicked()
      }
  })
  addChildren(buttons: _*)
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
