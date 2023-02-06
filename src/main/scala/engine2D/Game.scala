package engine2D

abstract class Game(
    val targetFps: Int = 60,
    val backgroundColor: sfml.graphics.Color = sfml.graphics.Color.Black(),
    val debug: Boolean = false
) {
  val engine: GameEngine
  def init(): Unit
  def step(window: sfml.graphics.RenderWindow) = {
    window.clear(backgroundColor)
    engine.step()
    engine.render(window)
    window.display()
  }
  def gameLoop(window: sfml.graphics.RenderWindow): Unit = {
    init()
    var lastTime = System.nanoTime()
    while window.isOpen() do
      for event <- window.pollEvent() do
        event match {
          case _: sfml.window.Event.Closed => window.closeWindow()
          case _                           => ()
        }
      if debug then
        println()
      step(window)

      // Handle FPS
      val frameTime = 1.0 / targetFps
      val currentTime = System.nanoTime()
      val delta = (currentTime - lastTime) / 1000000000.0
      // print sps
      if debug then
        val sps: Int = (1.0 / delta).toInt
        println(s"Step per seconds: $sps")
      // sleep if needed
      if delta < frameTime then
        val sleepTime = (frameTime - delta)
        Thread.sleep((sleepTime * 1000).toLong)
      // print fps
      if debug then
        val fps: Int = (1.0 / (System.nanoTime() - lastTime) * 1000000000).toInt
        println(s"Frame per seconds: $fps")
      lastTime = currentTime

  }
}
