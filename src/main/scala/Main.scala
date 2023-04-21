import sfml.graphics.*
import sfml.window.*
import engine2D.graphics.Group
import sfml.system.Vector2
import rtsp.RTSPShopGame

@main def main =
  val window = RenderWindow(VideoMode(800, 800), "RTSP")
  val demo = RTSPShopGame(window)
  demo.gameLoop()
