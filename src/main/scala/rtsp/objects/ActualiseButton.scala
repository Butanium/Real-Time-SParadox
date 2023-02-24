package rtsp.objects
import rtsp.Constants.ShopConstants.*
import rtsp.*
import engine2D.*
import engine2D.objects.*
import rtsp.objects.*
import sfml.graphics.Color
import sfml.window.Mouse
class ActualiseButton(val player: Player, shop: Shop, engine: GameEngine,val price: Int = ACTUALISE_PRICE) extends GameObject(engine) with Buyable{
  position = shop.positionBuyable(shop.nb_buyable)
  val rectangle =
    RectangleObject(shop.max_width_buyable, shop.max_height_buyable,engine)
  rectangle.outlineColor = Color(236, 151, 22)
  rectangle.outlineThickness = 5
  rectangle.fillColor = Color(165, 245, 73, 20)
  addChildren(rectangle)
  val text = new TextObject("Actualise: " + price.toString(), engine)
  text.fillColor = (Color(236, 191, 42))
  addChildren(text)
  text.position =
    (shop.max_width_buyable / 10, shop.max_height_buyable/2)
  def when_clicked() =
    if player.buy(price) then
      shop.change_shop()
  listenToBoundsClicked(Mouse.Button.Left, rectangle, true, when_clicked)
}
