package engine

import scala.collection.mutable.ListBuffer
import engine.objects.GameObject
import sfml.graphics.RenderWindow

/** The GameEngine is the main class of the game. It contains all game objects
  *
  * @param simulationSpeed
  *   The speed at which the game is simulated. It'll influence the speed of all
  *   game objects.
  * @param gameObjects
  *   The list of game objects.
  */
class GameEngine(
    val simulationSpeed: Float,
    val gameObjects: ListBuffer[GameObject] = ListBuffer.empty[GameObject]
) {
  val gameInfo: GameInfo = new GameInfo

  /** Performs a step of the game engine.
    */
  def step() =
    gameObjects.foreach(_.update())
    gameObjects.filterInPlace(!_.deleteIfNeeded())

  /** Renders all game objects.
    *
    * @param window
    */
  def render(window: RenderWindow) =
    gameObjects.foreach(window.draw(_))
}
