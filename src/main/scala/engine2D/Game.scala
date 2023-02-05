package engine2D

abstract class Game(val targetFps: Int = 60, backgroundColor: sfml.graphics.Color = sfml.graphics.Color.Black()) {
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
      step(window)
      // Handle FPS
      val frameTime = 1.0 / targetFps
      val currentTime = System.nanoTime()
      val delta = (currentTime - lastTime) / 1000000000.0
      if delta < frameTime then
        val sleepTime = (frameTime - delta) * 1000
        Thread.sleep(sleepTime.toLong)

  }
}
