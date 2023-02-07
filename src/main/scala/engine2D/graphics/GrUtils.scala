package engine2D.graphics

import sfml.graphics.Transform
import sfml.graphics.RenderStates

/** A collection of utility functions for graphics.
  */
object GrUtils {

  /** Creates a new RenderStates with the given transform applied to the
    * transform of the given RenderStates.
    * @param states
    *   The RenderStates to use.
    * @param transform
    *   The Transform to apply.
    * @return
    *   A new RenderStates with the given transform applied to the transform of
    *   the given RenderStates.
    */
  def newState(states: RenderStates, transform: Transform): RenderStates =
    RenderStates(states.blendMode, states.transform * transform)

}
