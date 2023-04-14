import sfml.graphics.*
import sfml.window.*
import rtsp.RTSPShopGame

@main def main =
  val window = RenderWindow(VideoMode(900, 900), "Demo Game")
  val demo = RTSPShopGame(window)
  demo.gameLoop()
