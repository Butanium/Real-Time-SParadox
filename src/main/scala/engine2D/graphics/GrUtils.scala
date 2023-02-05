package engine2D.graphics

import sfml.graphics.Transform
import sfml.graphics.RenderStates

object GrUtils {
  def newState(states: RenderStates, transform: Transform): RenderStates =
    RenderStates(states.blendMode, states.transform * transform)

}
