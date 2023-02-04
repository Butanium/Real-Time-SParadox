package engine.objects

import engine.GameInfo
import sfml.graphics.Drawable
import sfml.graphics.RenderTarget
import sfml.graphics.RenderStates
import sfml.graphics.Transformable
import collection.mutable.ListBuffer
import engine.GameEngine

import DeleteState.*

/** Base class for all game objects. It's transformable, can have children, can
  * be drawn, and can be updated.
  *
  * @param engine
  *   The GameEngine that this GameObject belongs to.
  * @param parent
  *   The parent GameObject of this GameObject.
  * @param active
  *   Whether or not this GameObject is active. If it's not active, it won't be
  *   updated or drawn.
  */
abstract class GameObject(
    var engine: GameEngine,
    var parent: Option[GameObject] = None,
    var active: Boolean = true
) extends Transformable
    with Drawable {

  /** The unique id of this GameObject. Used for comparison.
    */
  val id: Int = GameObject.getNextId

  /** Whether or not this GameObject should be deleted.
    */
  var deleteState: DeleteState = Nope
  onCreation()
  val children: ListBuffer[GameObject] = ListBuffer.empty[GameObject]
  def draw(target: RenderTarget, states: RenderStates): Unit = ()
  def update(): Unit =
    children.foreach(_.update())
  def addChildren(newChildren: GameObject*): Unit = {
    children ++= newChildren
    newChildren.foreach(_.parent = Some(this))
  }
  def removeChildren(to_remove: GameObject*): Unit = {
    children --= to_remove
    to_remove.foreach(_.parent = None)
  }

  /** Called when this GameObject is deleted.
    */
  private def onDeletion(): Unit = ()

  /** Called when this GameObject is created. TODO : check if overriding it
    * works
    */
  private def onCreation(): Unit = ()

  /** Deletes this GameObject and all its children.
    */
  def delete(): Unit =
    deleteState = Deleted
    onDeletion()
    parent.foreach(
      _.removeChildren(this)
    ) // If parent exists, remove self from parent's children
    parent = None
    children.foreach(_.delete())
    children.clear()

  /** Deletes this GameObject if it's marked for deletion. Also deletes all
    * children that are marked for deletion.
    * @return
    *   Whether or not this GameObject was deleted.
    */
  def deleteIfNeeded(): Boolean =
    if deleteState == ToDelete then delete()
    children.filterInPlace(!_.deleteIfNeeded())
    deleteState == Deleted
  override def equals(x: Any): Boolean =
    x match {
      case x: GameObject => x.id == id
      case _             => false
    }

}

object GameObject {
  private var lastId: Int = 0
  private def getNextId: Int = {
    lastId += 1
    lastId - 1
  }
}
