package engine2D.graphics

import sfml.graphics.RenderTarget
import sfml.graphics.RenderStates
import sfml.graphics.Drawable
import sfml.graphics.Transformable
import sfml.graphics.Transform

class Group extends Transformable with Drawable:
  private var children: List[Drawable] = List()

  def add(new_children: Drawable*): Unit =
    children = children.appendedAll(new_children)

  def remove(child: Drawable): Unit =
    children = children.filterNot(_ == child)

  def draw(target: RenderTarget, states: RenderStates): Unit =
    children.foreach(_.draw(target, GrUtils.newState(states, transform)))
