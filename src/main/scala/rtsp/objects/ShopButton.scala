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

class ShopButton[T <: Buyable with GameObject](
    val index: Int,
    var buyable: T,
    val player: Player,
    val shop: Shop[T],
    engine: GameEngine
) extends GameObject(engine) {
  // prend le sprite et ajuste ses dimensions
  var sprite = SpriteObject(buyable.spriteTexture, engine)
  addChildren(sprite)
  sprite.zIndex = 1
  sprite.boundDimensions(shop.max_width_buyable, shop.max_height_buyable)

  // ajoute le prix pour pouvoir l'afficher
  val textPrice =
    new TextObject(buyable.price.toString(), engine, charSize = 48)
  textPrice.fillColor = (Color(236, 191, 42))
  textPrice.zIndex = 2
  addChildren(textPrice)
  textPrice.position =
    (sprite.globalBounds.width + 40, sprite.globalBounds.height / 6)

  // affiche le nom de l'objet mis en vente
  val textType = new TextObject(buyable.name, engine, charSize = 16)
  textType.fillColor = (Color(236, 191, 42))
  textType.zIndex = 2
  addChildren(textType)
  textType.position =
    (sprite.globalBounds.width + 5, sprite.globalBounds.height * (3f / 4f))

  // définit le rectangle qui encadre l'objet en vente
  private val rectangle =
    RectangleObject(shop.max_width_buyable, shop.max_height_buyable, engine)
  rectangle.outlineColor = Color(236, 151, 22)
  rectangle.outlineThickness = 5
  rectangle.fillColor = Color(165, 245, 73, 80)
  addChildren(rectangle)

  // définit le comportement du bouton quand il est cliqué
  def whenClicked() = if shop.active then shop.playerWantsToBuy(this)
  listenToBoundsClicked(Mouse.Button.Left, rectangle, true, whenClicked)

  // Appelé quand le joueur veut acheter l'objet
  def changeBuyable(buyable: T) =
    removeChildren(this.sprite)
    this.sprite = SpriteObject(buyable.spriteTexture, engine)
    this.buyable = buyable
    textType.text.string = buyable.name
    textPrice.text.string = buyable.price.toString()
    addChildren(sprite)
    sprite.boundDimensions(shop.max_width_buyable, shop.max_height_buyable)
    textPrice.position =
      (sprite.globalBounds.width + 40, sprite.globalBounds.height / 6)
    textType.position =
      (sprite.globalBounds.width + 5, sprite.globalBounds.height * (3f / 4f))

}
