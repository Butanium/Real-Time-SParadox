import sfml.graphics.*
import sfml.window.*
import engine2D.graphics.Group
import sfml.system.Vector2
import rtsp.RTSPShopGame
import rtsp.NodeGame

@main def main =
  val window = RenderWindow(VideoMode(800, 800), "RTSP")
  val demo = NodeGame(window)
  // val demo = RTSPShopGame(window)
  demo.gameLoop()
