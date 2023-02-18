package engine2D.objects

import sfml.graphics.Drawable
import sfml.graphics.RenderTarget
import sfml.graphics.RenderStates
import sfml.graphics.Transformable
import collection.mutable.ListBuffer
import engine2D.GameEngine

import engine2D.graphics.GrUtils
import sfml.graphics.Transform
import engine2D.eventHandling.MouseEvent

/** Base class for all game objects. It's transformable, can have children, can
  * be drawn, and can be updated.
  *
  * @param engine
  *   The GameEngine that this GameObject belongs to.
  * @param active
  *   Whether or not this GameObject is active. If it's not active, it won't be
  *   updated or drawn.
  */
abstract class GameObject(
    var engine: GameEngine
) extends GameTransform
    with Drawable {
  private var _active: Boolean = true

  def active: Boolean = _active
  def active_=(newValue: Boolean) =
    // TODO: Make sure it doesn't cause bug because of listeners
    if newValue != active then
      _active = newValue
      listeners.foreach(_.active = false)

  /** The parent of this GameObject. If it's None, this GameObject has no
    * parent.
    * @note
    *   The parent is drawn and updated before this GameObject. The transform of
    *   this GameObject is relative to the transform of the parent. i.e. if the
    *   parent is translated by (10, 10), this GameObject will be translated by
    *   (10, 10).
    */
  var parent: Option[GameObject] = None

  /** The events the GameObject listens to.
    */
  val listeners: ListBuffer[MouseEvent] = ListBuffer()

  /** The unique id of this GameObject. Used for comparison.
    */
  val id: Int = GameObject.nextId

  /** The state of a GameObject's deletion. This is used to avoid concurrent
    * modification exceptions when deleting objects
    */
  private enum DeleteState {

    /** The GameObject is deleted. */
    case Deleted

    /** The GameObject should be deleted at the end of the frame. */
    case ToDelete

    /** The GameObject should not be deleted. */
    case Nope
  }
  import DeleteState.*

  /** Whether or not this GameObject should be deleted.
    * @note
    *   This variable is used to delete the GameObject at the end of the current
    */
  private var deleteState: DeleteState = Nope

  /** The children of this GameObject.
    * @note
    *   The children are drawn and updated after this GameObject. The children
    *   are drawn and updated in the order they were added.
    * @note
    *   The children are deleted when this GameObject is deleted.
    * @note
    *   The transform of the children is relative to the transform of this
    *   GameObject. i.e. if this GameObject is translated by (10, 10), the
    *   children will be translated by (10, 10) as well.
    */
  val children: ListBuffer[GameObject] = ListBuffer.empty[GameObject]

  /** Is called when this GameObject is drawn. This method is called only if
    * this GameObject is active.
    * @param target
    *   The RenderTarget to draw on.
    * @param states
    *   The RenderStates to use.
    */
  protected def onDraw(target: RenderTarget, states: RenderStates): Unit =
    children.foreach(_.draw(target, GrUtils.newState(states, transform)))
  def draw(target: RenderTarget, states: RenderStates): Unit =
    if active then onDraw(target, states)

  /** Called when this GameObject is updated. This method is called only if this
    * GameObject is active.
    */
  protected def onUpdate(): Unit = children.foreach(_.update())

  /** Updates this GameObject and all its children if it's active.
    */
  def update(): Unit =
    if active then onUpdate()

  /** Adds children to this GameObject.
    * @param newChildren
    */
  def addChildren(newChildren: GameObject*): Unit = {
    children ++= newChildren
    newChildren.foreach(_.parent = Some(this))
  }

  /** Alias for addChildren
    * @param newChildren
    */
  def add(newChildren: GameObject*): Unit = addChildren(newChildren: _*)

  /** Removes children from this GameObject.
    * @param to_remove
    */
  def removeChildren(to_remove: GameObject*): Unit = {
    children --= to_remove
    to_remove.foreach(_.parent = None)
  }

  /** Called when this GameObject is deleted.
    */
  protected def onDeletion(): Unit =
    listeners.foreach(_.toRemove = true)
  listeners.clear()

  /** Deletes this GameObject and all its children. The parent of this
    * GameObject will remove it from its children list in deleteIfNeeded.
    */
  private def delete(): Unit =
    deleteState = Deleted
  onDeletion()
  parent = None
  children.foreach(_.delete())
  children.clear()

  /** Marks this GameObject for deletion. The GameObject will be deleted at the
    * end of the current frame.
    */
  def markForDeletion() =
    deleteState = ToDelete

  /** Deletes this GameObject if it's marked for deletion. Also deletes all
    * children that are marked for deletion.
    * @note
    *   THIS METHOD SHOULD ONLY BE USED BY THE GAME ENGINE. If you want to
    *   delete a GameObject, use markForDeletion instead.
    * @return
    *   Whether or not this GameObject was deleted.
    */
  def deleteIfNeeded(): Boolean =
    if deleteState == ToDelete then delete()
    children.filterInPlace(!_.deleteIfNeeded())
    deleteState == Deleted

  /** Tests if this GameObject is equal to another GameObject.
    * @param x
    *   The other GameObject.
    * @return
    *   Whether or not this GameObject is equal to the other GameObject.
    * @note
    *   Two GameObjects are equal if they have the same id.
    */
  override def equals(x: Any): Boolean =
    x match {
      case x: GameObject => x.id == id
      case _             => false
    }

  /** Returns the global transform of this GameObject.
    * @note
    *   The global transform is the transform of this GameObject relative to the
    *   global transform of the parent GameObject.
    */
  def globalTransform: Transform =
    parent match
      case None         => transform
      case Some(parent) => parent.globalTransform * transform
}

object GameObject {

  /** The last id that was given to a GameObject.
    */
  private var lastId: Int = 0

  /** Gets the next id for a GameObject.
    *
    * @return
    *   The next id.
    */
  private def nextId: Int = {
    lastId += 1
    lastId - 1
  }
}
