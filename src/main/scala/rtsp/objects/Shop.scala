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
class Shop[T <: Buyable](
    val player: Player,
    initNbBuyable: Int,
    maxNbBuyable: Int,
    poolRepartition: Array [Float],
    idToBuyable: Int => T,
    engine: GameEngine

) extends GameObject(engine) {
  // définit la taille du shop
  var height: Float = engine.window.size.y * SHOP_HEIGHT_RATIO
  var width: Float = engine.window.size.x * SHOP_WIDTH_RATIO
  var thickness: Float =
    Math.min(engine.window.size.x, engine.window.size.y) * SHOP_THICKNESS_RATIO

  // définit la taille du shop à donner en argument au sprite
  var realHeight: Float = height - 2f * thickness
  var real_width: Float = width - 2f * thickness

  // donne le pool associé au shop
  var pool = new Pool(BASIC_POOL_REPARTITION, this)

  // donne le nombre d'objets achetables dans le shop
  var nb_buyable: Int = initNbBuyable

  // calcul des tailles des objets du shop
  var max_height_buyable: Float = SPACE_FOR_BUYABLE * height
  var max_width_buyable: Float =
    SPACE_FOR_BUYABLE * width * 1f / (nb_buyable + 1)

  // calcul de la position des objets du shop en fonction de leur index
  def positionBuyable(index: Int): Vector2[Float] = (
    (real_width / (nb_buyable + 1)) * (index + ((1f - SPACE_FOR_BUYABLE) / 2f)),
    realHeight * ((1f - SPACE_FOR_BUYABLE) / 4f)
  )

  // définition d'un niveau de shop (inutile pour le moment)
  var level: Int = 1
  def upgrade =
    level += 1

  // Création de l'Array contanant les boutons
  val buttons = new Array[ShopButton[T]](maxNbBuyable)
  
  // fonction pour remplacer aléatoirement un objet du shop, à définir
  def replace(index :Int) =
    val random_id_object: Int = pool.get_random()
    buttons(index).changeBuyable(idToBuyable(random_id_object))

  // fonction pour initialiser un objet du shop à un certain index
  def init(index: Int) = 
    val random_id_object: Int = pool.get_random()
    var newButton =
      ShopButton(index, idToBuyable(random_id_object), player, this, engine)
    buttons(index) = newButton
    addChildren(newButton)
    newButton.position = positionBuyable(index)

  // fonction pour initialiser le shop
  def init_shop() =
    for i <- 0 to (nb_buyable - 1) do init(i)

  // fonction pour remplacer tout le shop
  def change_shop() =
    for i <- 0 to (nb_buyable - 1) do replace(i)

  // création du rectangle qui sert de sprite au Shop
  val rectangle = RectangleObject(real_width, realHeight, engine)
  rectangle.outlineColor = Color(107, 76, 30)
  rectangle.outlineThickness = thickness
  rectangle.fillColor = Color(93, 247, 150, 50)
  addChildren(rectangle)
  init_shop()

  // définit les dimensions du rectangle où l'on affiche le nombre de money ainsi que sa position
  val realHeightMoney = height * RATIO_HEIGHT_MONEY - thickness
  val real_width_money = width * RATIO_WIDTH_MONEY - 2f * thickness
  val moneyRectangle =
    RectangleObject(real_width_money, realHeightMoney, engine)
  moneyRectangle.outlineColor = Color(107, 76, 30)
  moneyRectangle.outlineThickness = thickness
  moneyRectangle.fillColor = Color(93, 247, 150, 50)
  addChildren(moneyRectangle)
  moneyRectangle.position =
    (real_width / 2f - real_width_money / 2f, -(thickness + realHeightMoney))
  var moneyText = TextObject("Money:" + player.money.toString(), engine)
  moneyText.fillColor = (Color(236, 191, 42))
  addChildren(moneyText)
  moneyText.position = moneyRectangle.position

  // donne le bouton d'actualisation
  var actualiseButton = ActualiseButton(player, this, ACTUALISE_PRICE, engine)
  addChildren(actualiseButton)
  
  // fonction pour qu'un joueur achète un objet du shop (TODO)
  def playerWantsToBuy(item: ShopButton[T]) =
    // définir une condition de banc plein
    if player.buy(item.buyable) then
      // envoyer l'objet sur le banc où il doit aller
      // TODO REPLACE WITH BENCH
      val w = item.buyable.asInstanceOf[GameObject]
      engine.spawn(w)
      w.position = (Random.between(10, 30),Random.between(10, 30))
      replace(item.index)

  // On modifie onUpdate pour que le montant de money soit actualisé
  override def onUpdate() = {
    moneyText.string = "Money:" + player.money.toString
    super.onUpdate()
  }
}
