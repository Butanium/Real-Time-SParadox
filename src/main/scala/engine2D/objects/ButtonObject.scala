package engine2D.objects

import sfml.window.Mouse.Button
import sfml.system.Vector2
import sfml.graphics.Rect

class ButtonObject(
    val text: String,
    var onClicked: () => Unit,
    engine: engine2D.GameEngine,
    var legend: String = ""
) extends GameObject(engine)
    with RectBounds
    with Boundable {
  val padding = 0
  val textObject = new TextObject(text, engine)
  textObject.position = (padding / 2f, padding / 2f)
  textObject.zIndex = 1
  textObject.fillColor = sfml.graphics.Color(255, 255, 255)
  var background: RectangleObject = RectangleObject(0, 0, engine) // dummy
  def changeBackground(width: Float, height: Float) =
    background.destroy()
    background = RectangleObject(
      width,
      height,
      engine
    )
    background.fillColor = sfml.graphics.Color(0, 0, 0, 150)
    background.outlineColor = sfml.graphics.Color(150, 150, 150, 200)
    background.outlineThickness = 3f
    addChildren(background)
    textObject.position = (
      (width - textObject.text.localBounds.width) / 2f,
      (height - textObject.text.localBounds.height) / 2f
    )
  def resetOutline() = background.outlineColor =
    sfml.graphics.Color(150, 150, 150, 200)
  private def activated() =
    onClicked()
  listenToBoundsClicked(Button.Left, this, true, this.activated)
  changeBackground(
    textObject.text.localBounds.width + padding + 10,
    textObject.text.localBounds.height + padding + 10
  )
  // legend is put below the background
  val legendObject = new TextObject(legend, engine)
  legendObject.position = (
    background.width / 2f - legendObject.text.localBounds.width / 2f,
    background.height + 5
  )
  legendObject.zIndex = 1
  addChildren(textObject, legendObject)

  def makeSquare() = {
    val size = math.max(
      textObject.text.localBounds.width,
      textObject.text.localBounds.height
    ) + padding
    changeBackground(size, size)
    legendObject.position = (
      background.width / 2f - legendObject.text.localBounds.width / 2f,
      background.height + 5
    )
  }
  def changeText(
      newText: String,
      adaptBackground: Boolean = false,
      adaptText: Boolean = false
  ) = {
    textObject.text.string = newText
    if (adaptBackground)
      changeBackground(
        textObject.text.localBounds.width + padding,
        textObject.text.localBounds.height + padding
      )
      legendObject.position = (
        background.width / 2f - legendObject.text.localBounds.width / 2f,
        background.height + 5
      )
    if (adaptText)
      textObject.boundDimensions(
        background.globalBounds.width + 10,
        background.globalBounds.height + 10
      )

  }

  def changeLegend(newLegend: String) = {
    legendObject.text.string = newLegend
    legendObject.position = (
      background.width / 2f - legendObject.text.localBounds.width / 2f,
      background.height + 5
    )
  }

  def contains(point: Vector2[Float]): Boolean = background.contains(point)

  def globalBounds: Rect[Float] = background.globalBounds
}
