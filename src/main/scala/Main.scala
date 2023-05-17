import sfml.graphics.*
import sfml.window.*
import rtsp.RTSPShopGame
import rtsp.NodeGame

@main def main =
  val window = RenderWindow(VideoMode(900, 900), "RTSP")
  // val demo = NodeGame(window)
  val demo = RTSPShopGame(window)
  demo.gameLoop()
