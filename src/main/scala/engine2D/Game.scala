package engine2D

import sfml.graphics.RenderWindow

/** A game is a class that contains a game engine and a window. It is
  * responsible for the game loop.
  * @param window
  *   the window to render the game on
  * @param targetFps
  *   the target frame per seconds
  * @param backgroundColor
  *   the background color of the window
  * @param debug
  *   if true, print debug information
  */
abstract class Game(
    val window: RenderWindow,
    val targetFps: Int = 60,
    val backgroundColor: sfml.graphics.Color = sfml.graphics.Color.Black(),
    val debug: Boolean = false
) {

  /** The game engine
    */
  var engine: GameEngine

  /** Initialize the game, called once before starting the game loop
    */
  def init() = {
    window.framerateLimit = targetFps
  }
  var lastTime: Long = 0

  /** Perform a step of the game.
    * @note
    *   It will
    *   - Make the engine perform a step
    *   - Render the engine on the window
    */
  def step() = {
    window.clear(backgroundColor)
    engine.step()
    if debug then
      // print sps
      val sps: Int =
        (1.0 / ((System.nanoTime() - lastTime) / 1000000000.0)).toInt
      println(s"Engine Step per seconds: $sps")
    engine.render()
    window.display()
  }

  /** Start the game loop
    * @note
    *   The game loop is a loop that will
    *   - Clear the screen
    *   - Collect inputs
    *   - Perform a step
    *   - Display the result on the window
    *   - Repeat
    */
  def gameLoop(): Unit = {
    init()
    lastTime = System.nanoTime()
    while window.isOpen() do
      if debug then println()
      step()

      if debug then
        val currentTime = System.nanoTime()
        // print fps
        val fps: Int = (1.0 / (currentTime - lastTime) * 1000000000).toInt
        println(s"Frame per seconds: $fps")
        lastTime = currentTime

  }
}
