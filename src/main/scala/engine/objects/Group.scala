package engine.objects

import sfml.graphics.RenderTarget
import sfml.graphics.RenderStates
import sfml.graphics.Drawable
import sfml.graphics.Transformable
import scala.annotation.newMain

class Group extends Transformable with Drawable:
  private var children: List[Drawable] = List()

  def add(new_children: Drawable*): Unit =
    children = children.appendedAll(new_children)

  def remove(child: Drawable): Unit =
    children = children.filterNot(_ == child)

  def draw(target: RenderTarget, states: RenderStates): Unit =
    states.transform *= transform
    children.foreach(_.draw(target, states))
