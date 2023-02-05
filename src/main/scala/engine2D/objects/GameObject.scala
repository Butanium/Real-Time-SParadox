package engine2D.objects

import engine2D.GameInfo
import sfml.graphics.Drawable
import sfml.graphics.RenderTarget
import sfml.graphics.RenderStates
import sfml.graphics.Transformable
import collection.mutable.ListBuffer
import engine2D.GameEngine

import DeleteState.*
import engine2D.graphics.GrUtils

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
    var active: Boolean = true
) extends Transformable
    with Drawable {
  var parent: Option[GameObject] = None

  /** The unique id of this GameObject. Used for comparison.
    */
  val id: Int = GameObject.getNextId

  /** Whether or not this GameObject should be deleted.
    */
  var deleteState: DeleteState = Nope
  val children: ListBuffer[GameObject] = ListBuffer.empty[GameObject]
  protected def onDraw(target: RenderTarget, states: RenderStates): Unit =
    children.foreach(_.draw(target, GrUtils.newState(states, transform)))
  def draw(target: RenderTarget, states: RenderStates): Unit =
    if active then onDraw(target, states)

  /** Called when this GameObject is updated. This method is called only if this
    * GameObject is active.
    */
  protected def onUpdate(): Unit = children.foreach(_.update())

  /** Updates this GameObject and all its children.
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
  protected def onDeletion(): Unit = ()

  /** Called when this GameObject is created.
    */
  protected def onCreation(): Unit = ()

  /** Deletes this GameObject and all its children. The parent of this
    * GameObject will remove it from its children list in deleteIfNeeded.
    */
  def delete(): Unit =
    deleteState = Deleted
    onDeletion()
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

  /* Call the onCreation method. Note that this is the last line of the
   * constructor, so the object is fully initialized when this method is called.
   */
  onCreation()
}

object GameObject {
  private var lastId: Int = 0
  private def getNextId: Int = {
    lastId += 1
    lastId - 1
  }
}
