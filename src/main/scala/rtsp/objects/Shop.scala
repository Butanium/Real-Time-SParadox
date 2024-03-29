package rtsp.objects
import rtsp.Constants.ShopConstants.*
import rtsp.Player
import rtsp.battle.RTSPBattle
import engine2D.*
import engine2D.objects.*
import scala.collection.mutable.ListBuffer
import sfml.system.Vector2
import sfml.graphics.RectangleShape
import sfml.graphics.Color

import util.Random
import rtsp.Constants
import rtsp.RTSPGameEngine
class Shop[T <: Buyable with GameObject](
    val player: Player,
    initNbBuyable: Int,
    maxNbBuyable: Int,
    poolRepartition: Array[Float],
    battle: RTSPBattle,
    idToBuyable: (Int, Player, RTSPBattle, RTSPGameEngine) => T,
    bench: GeneralBench[T],
    engine: RTSPGameEngine
) extends GameObject(engine) {
  // définit la taille du shop
  var height: Float = engine.window.size.y * SHOP_HEIGHT_RATIO
  var width: Float = engine.window.size.x * SHOP_WIDTH_RATIO
  var thickness: Float =
    Math.min(engine.window.size.x, engine.window.size.y) * SHOP_THICKNESS_RATIO

  // définit la taille du shop à donner en argument au sprite
  var realHeight: Float = height - 2f * thickness
  var realWidth: Float = width - 2f * thickness

  // donne le pool associé au shop
  var pool = new Pool(poolRepartition, this)

  // donne le nombre d'objets achetables dans le shop
  var nbBuyable: Int = initNbBuyable

  // calcul des tailles des objets du shop
  var max_height_buyable: Float = SPACE_FOR_BUYABLE * height
  var max_width_buyable: Float =
    SPACE_FOR_BUYABLE * width * 1f / (nbBuyable + 1)

  // calcul de la position des objets du shop en fonction de leur index
  def positionBuyable(index: Int): Vector2[Float] = (
    (realWidth / (nbBuyable + 1)) * (index + ((1f - SPACE_FOR_BUYABLE) / 2f)),
    realHeight * ((1f - SPACE_FOR_BUYABLE) / 4f)
  )

  // définition d'une limite de warriors que l'on peut placer sur le terrain et des fonctions associées
  def priceUpgrade(limit: Int): Int =
    (limit * limit * (limit + 1)) / 12
  def upgrade(limit: Int): Unit =
    if this.active && player.buy(priceUpgrade(limit)) then
      player.limitOfWarriors += 1
  
  // Création du bouton pour augmenter la limite
  val buttonUpgrade = ButtonObject(
    "Upgrade limit: " + priceUpgrade(player.limitOfWarriors).toString(),
    () => upgrade(player.limitOfWarriors),
    engine
  )
  buttonUpgrade.changeBackground(max_width_buyable, max_height_buyable / 2.1f)
  buttonUpgrade.position =
    positionBuyable(nbBuyable) + (0f, max_height_buyable / 1.9f)
  buttonUpgrade.background.fillColor = Color(165, 245, 73, 80)
  buttonUpgrade.background.outlineColor = Color(236, 151, 22)
  addChildren(buttonUpgrade)
  
  // Création de l'Array contenant les boutons
  val buttons = new Array[ShopButton[T]](maxNbBuyable)

  // fonction pour remplacer aléatoirement un objet du shop, à définir
  def replace(index: Int) =
    val random_id_object: Int = pool.get_random()
    buttons(index).changeBuyable(idToBuyable(random_id_object, player, battle, engine))

  // fonction pour initialiser un objet du shop à un certain index
  def init(index: Int) =
    val random_id_object: Int = pool.get_random()
    var newButton =
      ShopButton(
        index,
        idToBuyable(random_id_object, player, battle, engine),
        player,
        this,
        engine
      )
    buttons(index) = newButton
    newButton.zIndex = 1
    addChildren(newButton)
    newButton.position = positionBuyable(index)

  // fonction pour initialiser le shop
  def initShop() =
    for i <- 0 to (nbBuyable - 1) do init(i)

  // fonction pour remplacer tout le shop
  def change_shop() =
    for i <- 0 to (nbBuyable - 1) do replace(i)

  // création du rectangle qui sert de sprite au Shop
  val rectangle = RectangleObject(realWidth, realHeight, engine)
  rectangle.outlineColor = Color(107, 76, 50)
  rectangle.outlineThickness = thickness
  rectangle.fillColor = Color(93, 247, 150, 80)
  addChildren(rectangle)
  initShop()

  // définit les dimensions du rectangle où l'on affiche le nombre de money ainsi que sa position
  val realHeightMoney = height * RATIO_HEIGHT_MONEY - thickness
  val realWidthMoney = width * RATIO_WIDTH_MONEY - 2f * thickness
  val moneyRectangle =
    RectangleObject(realWidthMoney, realHeightMoney, engine)
  moneyRectangle.outlineColor = Color(107, 76, 30)
  moneyRectangle.outlineThickness = thickness
  moneyRectangle.fillColor = Color(93, 247, 150, 80)
  addChildren(moneyRectangle)
  moneyRectangle.position =
    (realWidth / 2f - realWidthMoney / 2f, -(thickness + realHeightMoney))
  var moneyText = TextObject(
    "Money:" + player.money
      .toString() + "  -  Warrior Limit" + player.limitOfWarriors.toString(),
    engine
  )
  moneyText.fillColor = (Color(236, 191, 42))
  addChildren(moneyText)
  moneyText.zIndex = 1
  moneyText.position = moneyRectangle.position

  // donne le bouton d'actualisation
  var actualiseButton = ActualiseButton(player, this, ACTUALISE_PRICE, engine)
  addChildren(actualiseButton)
  actualiseButton.zIndex = 1

  // fonction pour qu'un joueur achète un objet du shop
  def playerWantsToBuy(item: ShopButton[T]) =
    // définir une condition de banc plein
    if (bench.isNotFull && player.buy(item.buyable)) then
      // envoyer l'objet sur le banc où il doit aller
      bench.addBought(item.buyable)
      replace(item.index)

  // On modifie onUpdate pour que le montant de money soit actualisé
  override def onUpdate() = {
    moneyText.string =
      "   Money: " + player.money.toString + "  -  Warrior Limit: " + player.limitOfWarriors
        .toString()
    buttonUpgrade.changeText(
        "Upgrade limit: " + priceUpgrade(player.limitOfWarriors).toString(),
        adaptText = true
      )
    super.onUpdate()
  }
}
