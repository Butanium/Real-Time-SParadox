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
import engine2D.objects.RectangleObject
import sfml.window.Mouse

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
  val textType = new TextObject(type_string, engine, charSize = 16)
  textType.fillColor = (Color(236, 191, 42))
  addChildren(textType)
  textType.position =
    (sprite.globalBounds.width + 5, sprite.globalBounds.height * (3f / 4f))
  private val rectangle =
    RectangleObject(shop.max_width_buyable, shop.max_height_buyable, engine)
  rectangle.outlineColor = Color(236, 151, 22)
  rectangle.outlineThickness = 5
  rectangle.fillColor = Color(165, 245, 73, 20)
  addChildren(rectangle)
  var shopIndex: Int = (-1)
  def change_shopIndex_to(i: Int) =
    shopIndex = i
    position = shop.positionBuyable(i)
  def whenClicked() = shop.playerWantsToBuy(this)
  listenToBoundsClicked(Mouse.Button.Left, rectangle, true, whenClicked)
  def convertToWarrior(battle: RTSPBattle, behavior: Behavior = null) =
    RTSPWarrior(warrior_id, engine, battle, shop.player.id, behavior)
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
