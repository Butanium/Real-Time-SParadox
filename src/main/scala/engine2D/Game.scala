package engine2D

import sfml.graphics.RenderWindow

abstract class Game(
    val window: RenderWindow,
    val targetFps: Int = 60,
    val backgroundColor: sfml.graphics.Color = sfml.graphics.Color.Black(),
    val debug: Boolean = false
) {
  val engine: GameEngine
  def init() = {
    window.framerateLimit = targetFps
  }
  def step() = {
    window.clear(backgroundColor)
    engine.step()
    engine.render(window)
    window.display()
  }
  def gameLoop(): Unit = {
    init()
    var lastTime = System.nanoTime()
    while window.isOpen() do
      for event <- window.pollEvent() do
        event match {
          case _: sfml.window.Event.Closed => window.closeWindow()
          case _                           => ()
        }
      if debug then println()
      step()

      if debug then
        // print sps
        val sps: Int =
          (1.0 / ((System.nanoTime() - lastTime) / 1000000000.0)).toInt
        println(s"Step per seconds: $sps")

      val currentTime = System.nanoTime()
      if debug then
        // print fps
        val fps: Int = (1.0 / (currentTime - lastTime) * 1000000000).toInt
        println(s"Frame per seconds: $fps")
      lastTime = currentTime

  }
}
