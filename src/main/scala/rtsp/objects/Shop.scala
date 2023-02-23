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
class Shop(val player: Player, engine: GameEngine) extends GameObject(engine) {
  var height: Float = engine.window.size.y * SHOP_HEIGHT_RATIO
  var width: Float = engine.window.size.x * SHOP_WIDTH_RATIO
  var thickness: Float =
    Math.min(engine.window.size.x, engine.window.size.y) * SHOP_THICKNESS_RATIO
  var real_height: Float = height - 2f * thickness
  var real_width: Float = width - 2f * thickness
  var pool = new Pool(BASIC_POOL_REPARTITION, this)
  val nb_buyable: Int = INIT_NB_BUYABLE
  var to_buy: Array[ShopWarrior] =
    new Array[ShopWarrior](MAX_NB_BUYABLE)
  var max_height_buyable: Float = SPACE_FOR_BUYABLE * height
  var max_width_buyable: Float = SPACE_FOR_BUYABLE * width * 1f / (nb_buyable + 1)
  def positionBuyable(index: Int): Vector2[Float] = (
    (real_width / (nb_buyable + 1)) * (index + ((1f - SPACE_FOR_BUYABLE) / 2f)),
    real_height * ((1f - SPACE_FOR_BUYABLE) / 4f)
  )
  var level: Int = 1
  def upgrade =
    level += 1
  def replace(index: Int) =
    val random_shop_warrior: ShopWarrior = pool.get_random()
    addChildren(random_shop_warrior)
    removeChildren(to_buy(index))
    random_shop_warrior.change_shop_index_to(index)
    to_buy(index) = random_shop_warrior
  def init(index: Int) =
    val random_shop_warrior: ShopWarrior = pool.get_random()
    addChildren(random_shop_warrior)
    random_shop_warrior.change_shop_index_to(index)
    to_buy(index) = random_shop_warrior
  def init_shop() =
    for i <- 0 to (nb_buyable - 1) do init(i)
  def change_shop() =
    for i <- 0 to (nb_buyable - 1) do replace(i)
  val rectangle = RectangleObject(real_width, real_height, engine)
  rectangle.outlineColor = Color(107, 76, 30)
  rectangle.outlineThickness = thickness
  rectangle.fillColor = Color(93, 247, 150, 50)
  addChildren(rectangle)
  init_shop()
  val real_height_money = height * RATIO_HEIGHT_MONEY - thickness
  val real_width_money = width * RATIO_WIDTH_MONEY - 2f * thickness
  val money_rectangle =
    RectangleObject(real_width_money, real_height_money, engine)
  money_rectangle.outlineColor = Color(107, 76, 30)
  money_rectangle.outlineThickness = thickness
  money_rectangle.fillColor = Color(93, 247, 150, 50)
  addChildren(money_rectangle)
  money_rectangle.position =
    (real_width / 2f - real_width_money / 2f, -(thickness + real_height_money))
  var money_text = TextObject("Money:" + player.money.toString(), engine)
  money_text.fillColor = (Color(236, 191, 42))
  addChildren(money_text)
  money_text.position = money_rectangle.position
  var actualiseButton = ActualiseButton(player, this, engine)
  addChildren (actualiseButton)
  override def onUpdate() = {
    money_text.string = "Money:" + player.money.toString
    super.onUpdate()
  }
}
