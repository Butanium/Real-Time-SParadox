package rtsp.objects
import engine2D.objects.GameObject
import engine2D.GameEngine
import rtsp.Player
import sfml.graphics.Texture
import rtsp.objects.RTSPWarrior
import engine2D.objects.SpriteObject
import rtsp.battle.*
import rtsp.Constants.ShopConstants.*
import rtsp.Constants.*
import engine2D.objects.TextObject
import sfml.graphics.Color
import sfml.graphics.RectangleShape
import engine2D.objects.GraphicObject

class ShopWarrior(
    val warrior_id: Int,
    val player: Player,
    val shop: Shop,
    val price: Int,
    val type_string: String,
    val spriteTexture: String,
    engine: GameEngine
) extends GameObject(engine)
    with Buyable {
  val sprite = SpriteObject(spriteTexture, engine)
  addChildren(sprite)
  sprite.boundDimensions(shop.max_width_buyable, shop.max_height_buyable)
  val text_price = new TextObject(price.toString(), engine, charSize = 48)
  text_price.fillColor = (Color(236, 191, 42))
  addChildren(text_price)
  text_price.position =
    (sprite.globalBounds.width + 40, sprite.globalBounds.height / 6)
  val text_type = new TextObject(type_string, engine, charSize = 20)
  text_type.fillColor = (Color(236, 191, 42))
  addChildren(text_type)
  text_type.position =
    (sprite.globalBounds.width + 5, sprite.globalBounds.height * (3f / 4f))
  val sprite_rectangle =
    RectangleShape(shop.max_width_buyable, shop.max_height_buyable)
  val rectangle = GraphicObject(sprite_rectangle, engine)
  sprite_rectangle.outlineColor = Color(236, 151, 22)
  sprite_rectangle.outlineThickness = 5
  sprite_rectangle.fillColor = Color(165, 245, 73, 20)
  addChildren(rectangle)
  var shop_position: Int = (-1)
  def change_shop_position_to(i: Int) =
    shop_position = i
    position = shop.positionBuyable(i)
  def when_clicked =
    // TODO: définir la condition banc plein pour ne pas acheter de warrior quand il l'est
    if affordable then
      print("Un warrior a été acheté!")
      player.money -= price
      shop.replace(shop_position)
      // TODO: définir une fonction qui envoie le RTSPWarrior sur le banc
}

object ShopWarrior {
  def create_shop_Archer(shop: Shop) =
    new ShopWarrior(
      ID_ARCHER,
      shop.player,
      shop,
      PRICE_ARCHER,
      "Archer",
      "warriors/archer.png",
      shop.engine
    )
  def create_shop_Barbarian(shop: Shop) =
    new ShopWarrior(
      ID_BARBARIAN,
      shop.player,
      shop,
      PRICE_BARBARIAN,
      "Barbarian",
      "warriors/warrior.png",
      shop.engine
    )
}
