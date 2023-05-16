import sfml.graphics.*
import sfml.window.*
import rtsp.RTSPShopGame
import rtsp.NodeGame

@main def main =
  val window = RenderWindow(VideoMode(800, 800), "RTSP")
  val demo = RTSPShopGame(window)
  // val demo = RTSPShopGame(window)
  demo.gameLoop()
