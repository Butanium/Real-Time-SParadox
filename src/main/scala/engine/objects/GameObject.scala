package engine.objects

import engine.GameInfo
import sfml.graphics.Drawable
import sfml.graphics.RenderTarget
import sfml.graphics.RenderStates
import sfml.graphics.Transformable
import collection.mutable.ListBuffer
import engine.GameEngine

abstract class GameObject(
    var engine: GameEngine,
    var active: Boolean = true,
    var parent: Option[GameObject] = None
) extends Transformable
    with Drawable {
  onCreation()
  val children: ListBuffer[GameObject] = ListBuffer.empty[GameObject]
  def draw(target: RenderTarget, states: RenderStates): Unit = ()
  def update(): Unit
  def addChildren(newChildren: GameObject*): Unit = {
    children ++= newChildren
    newChildren.foreach(_.parent = Some(this))
  }
  def removeChildren(child: GameObject*): Unit = {
    children --= child
    child.foreach(_.parent = None)
  }
  def onDeletion(): Unit = ()
  def onCreation(): Unit = ()
  def delete(): Unit =
    onDeletion()
    parent.foreach(_.removeChildren(this))
    parent = None
    children.foreach(_.delete())

}
