package engine2D.graphics

import sfml.graphics.RenderTarget
import sfml.graphics.RenderStates
import sfml.graphics.Drawable
import sfml.graphics.Transformable
import sfml.graphics.Transform

/** A Group is a Transformable that can contain other Drawable objects. It will
  * draw all of its children when it is drawn.
  * @note
  *   The children are drawn in the order they were added.
  * @note
  *   The children are drawn relative to the transform of this Group. i.e. if
  *   this Group is translated by (10, 10), the children will be translated by
  *   (10, 10) as well.
  */
class Group extends Transformable with Drawable:
  private var children: List[Drawable] = List()

  /** Adds a child to this Group.
    *
    * @param new_children
    *   The children to add.
    */
  def add(new_children: Drawable*): Unit =
    children = children.appendedAll(new_children)

  /** Removes a child from this Group.
    * @param child
    *   The child to remove.
    */
  def remove(child: Drawable): Unit =
    children = children.filterNot(_ == child)

    /** Draws all the children of this Group.
      *
      * @param target
      *   The RenderTarget to draw on.
      * @param states
      *   The RenderStates to use.
      */
  def draw(target: RenderTarget, states: RenderStates): Unit =
    children.foreach(_.draw(target, GrUtils.newState(states, transform)))
