package rtsp.editor

import sfml.system.Vector2
import sfml.graphics.Color
import engine2D.objects.ShapeObject
import engine2D.objects.RectangleObject
import sfml.window.Mouse
import engine2D.objects.GameObject

class LineObject(
    var thickness: Float,
    var target1: NodeObject,
    var target2: GameObject,
    var addPos1: Vector2[Float],
    var addPos2: Vector2[Float],
    engine: engine2D.GameEngine
) extends ShapeObject(
      sfml.graphics.RectangleShape((1f, 1f)),
      engine
    ) {
  val line = drawable.asInstanceOf[sfml.graphics.RectangleShape]
  line.fillColor = Color(128,128,128)
  val deletionSquare: RectangleObject =
    RectangleObject(4f * thickness, 4f * thickness, engine)
  deletionSquare.fillColor = Color.Yellow()
  engine.spawn(deletionSquare)
  def delete() =
    target1.childrenNode -= target2.asInstanceOf[NodeObject]
    target2.asInstanceOf[NodeObject].parentsNode -= target1
    target1.linesLinked -= this
    target2.asInstanceOf[NodeObject].linesLinked -= this
    this.markForDeletion()
    deletionSquare.markForDeletion()
  def deleteWithout(node: NodeObject) =
    target1.childrenNode -= target2.asInstanceOf[NodeObject]
    target2.asInstanceOf[NodeObject].parentsNode -= target1
    if node != target1 then target1.linesLinked -= this
    if node != target2.asInstanceOf[NodeObject] then
      target2.asInstanceOf[NodeObject].linesLinked -= this
    this.markForDeletion()
    deletionSquare.markForDeletion()
  listenToBoundsClicked(Mouse.Button.Left, deletionSquare, false, delete)
  override def onUpdate(): Unit = {
    var dx =
      -(target1.position.x + addPos1.x) + (target2.position.x + addPos1.x)
    var dy =
      -(target1.position.y + addPos1.y) + (target2.position.y + addPos2.y)
    deletionSquare.position = Vector2[Float](
      target1.position.x + addPos1.x + dx / 2f - 2f * thickness,
      target1.position.y + addPos1.y + dy / 2f - 2f * thickness
    )
    var dist = Math.sqrt(dx * dx + dy * dy).toFloat
    val angle = Math.atan2(-dy, -dx)
    rotation = (angle * 180f / Math.PI + 180f).toFloat
    position = target1.position + addPos1
    scale = (dist, thickness)
    super.onUpdate()
  }

}
