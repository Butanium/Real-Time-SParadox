package rtsp
import engine2D.Game
import sfml.graphics.RenderWindow
import rtsp.battle.RTSPBattle
import rtsp.objects.RTSPWarrior
import rtsp.battle.Behavior
import rtsp.objects.Shop
import rtsp.Constants.ShopConstants.*

class RTSPShopGame(window: RenderWindow)
    extends Game(window, 60, sfml.graphics.Color.Black(), debug = false) {
  val engine = new RTSPGameEngine(1f / 60, window, debug = false)

  override def init() = {
    val joueur = new Player(0)
    val shop = new Shop(joueur, engine)
    shop.position = (
      window.size.x * (1 - SHOP_WIDTH_RATIO) / 2f + shop.thickness/2f,
      window.size.y * (1 - SHOP_HEIGHT_RATIO)
    )
    engine.spawn(shop)
  }
}
