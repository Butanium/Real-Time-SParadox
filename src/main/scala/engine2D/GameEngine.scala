package engine2D

import scala.collection.mutable.ListBuffer
import engine2D.objects.GameObject
import sfml.graphics.RenderWindow
import scala.collection.mutable.ListBuffer

/** The GameEngine is the main class of the game. It contains all game objects
  *
  * @param deltaTime
  *   The time between each step of the game engine.
  * @param gameObjectss
  *   The list of game objects.
  */
class GameEngine(
    val deltaTime: Float,
    val gameObjects: ListBuffer[GameObject] = ListBuffer.empty[GameObject],
    val debug: Boolean = false
) {
  var newGameObjects: ListBuffer[GameObject] = ListBuffer.empty[GameObject]
  val textureManager = new graphics.TextureManager()

  /** Performs a step of the game engine.
    */
  def step() =
    val size = gameObjects.size
    var time = 0f
    if debug then
      println(s"Step: $size game objects")
      time = System.nanoTime()
    gameObjects ++= newGameObjects
    newGameObjects.clear()
    gameObjects.foreach(_.update())
    gameObjects.filterInPlace(!_.deleteIfNeeded())
    if debug then
      println(s"Step: deleted ${size - gameObjects.size} game objects")
      println(s"Step: performed in ${(System.nanoTime() - time)* 1000 } ms")

  /** Renders all game objects.
    *
    * @param window
    */
  def render(window: RenderWindow) =
    var time = 0f
    if debug then 
      println(s"Render: ${gameObjects.size} game objects")
      time = System.nanoTime()
    gameObjects.foreach(window.draw(_))
    if debug then
      println(s"Render: performed in ${(System.nanoTime() - time) * 1000} ms")

  /** Adds GameObjects to the game engine. They'll be added at the end of the
    * current step.
    * @param gameObjects
    *   The GameObjects to add.
    */
  def addGameObjects(gameObjects: GameObject*): Unit =
    newGameObjects ++= gameObjects

  /** Spawns GameObjects. They'll be added at the end of the current step. It's
    * an alias for addGameObjects.
    *
    * @param gameObjects
    *   The GameObjects to spawn.
    */
  def spawn(gameObjects: GameObject*): Unit = addGameObjects(gameObjects: _*)
}
