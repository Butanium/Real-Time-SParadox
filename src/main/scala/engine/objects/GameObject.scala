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
  val id: Int = GameObject.getId // Unique ID
  onCreation()
  val children: ListBuffer[GameObject] = ListBuffer.empty[GameObject]
  def draw(target: RenderTarget, states: RenderStates): Unit = ()
  def update(): Unit
  def addChildren(newChildren: GameObject*): Unit = {
    children ++= newChildren
    newChildren.foreach(_.parent = Some(this))
  }
  def removeChildren(to_remove: GameObject*): Unit = {
    children --= to_remove
    to_remove.foreach(_.parent = None)
  }
  def onDeletion(): Unit = ()
  def onCreation(): Unit = ()
  def delete(): Unit =
    onDeletion()
    parent.foreach(
      _.removeChildren(this)
    ) // If parent exists, remove self from parent's children
    parent = None
    children.foreach(_.delete())
    children.clear()
  override def equals(x: Any): Boolean =
    x match {
      case x: GameObject => x.id == id
      case _             => false
    }

}

object GameObject {
  var lastId: Int = 0
  def getId: Int = {
    lastId += 1
    lastId - 1
  }
}
