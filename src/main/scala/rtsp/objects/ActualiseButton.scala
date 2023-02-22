package rtsp.objects
import rtsp.Constants.ShopConstants.*
import rtsp.*
import engine2D.*
import engine2D.objects.*
import rtsp.objects.*
import sfml.graphics.Color
class ActualiseButton(val player: Player, shop: Shop,val price: Int = ACTUALISE_PRICE, engine: GameEngine) extends GameObject(engine) with Buyable{
  position = shop.positionBuyable(shop.nb_buyable + 1)
  val rectangle =
    RectangleObject(shop.max_width_buyable, shop.max_height_buyable,engine)
  rectangle.outlineColor = Color(236, 151, 22)
  rectangle.outlineThickness = 5
  rectangle.fillColor = Color(165, 245, 73, 20)
  addChildren(rectangle)
  val text = new TextObject("Actualise: ", engine, charSize = 28)
  text.fillColor = (Color(236, 191, 42))
  addChildren(text)
  text.position =
    ()
}
