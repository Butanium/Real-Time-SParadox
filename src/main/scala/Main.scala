import sfml.graphics.*
import sfml.window.*
import engine2D.graphics.Group
import sfml.system.Vector2
import demo.StaticGame
import rtsp.RTSPGame

/*
  Step by step, your program should look like:

  [Start]
  Initialize your context
  Create a sprite
  Start looping
    Clear the screen
    Collect inputs
    Move your sprite
    Draw your sprite
    Display your drawing on the window
  End looping
  [Exit]
 */

@main def main =
  val window = RenderWindow(VideoMode(800, 800), "Demo Game")
  val demo = RTSPGame(window)
  demo.gameLoop()
