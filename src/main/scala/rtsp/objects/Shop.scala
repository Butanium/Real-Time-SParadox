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
  var real_height: Float = height - 1f * thickness
  var real_width: Float = width - 1f * thickness
  var pool = new Pool(BASIC_POOL_REPARTITION, this)
  val nb_buyable: Int = INIT_NB_BUYABLE
  var to_buy: Array[ShopWarrior] =
    new Array[ShopWarrior](MAX_NB_BUYABLE)
  var max_height_buyable: Float = SPACE_FOR_BUYABLE * height
  var max_width_buyable: Float = SPACE_FOR_BUYABLE * width * 1f / nb_buyable
  def positionBuyable(position: Int): Vector2[Float] = (
    (width / nb_buyable) * (position + ((1f - SPACE_FOR_BUYABLE) / 2f)),
    height * ((1f - SPACE_FOR_BUYABLE) / 2f)
  )
  var level: Int = 1
  def upgrade =
    level += 1
  def replace(position: Int) =
    val random_shop_warrior: ShopWarrior = pool.get_random()
    addChildren(random_shop_warrior)
    removeChildren(to_buy(position))
    random_shop_warrior.change_shop_position_to(position)
    to_buy(position) = random_shop_warrior
  def init(position: Int) =
    val random_shop_warrior: ShopWarrior = pool.get_random()
    addChildren(random_shop_warrior)
    random_shop_warrior.change_shop_position_to(position)
    to_buy(position) = random_shop_warrior
  def init_shop() =
    for i <- 0 to (nb_buyable - 1) do init(i)
  def change_shop() =
    for i <- 0 to (nb_buyable - 1) do replace(i)
  init_shop()
  val rectangle = RectangleObject(real_width, real_height,engine)
  rectangle.outlineColor = Color(107, 76, 30)
  rectangle.outlineThickness = thickness
  rectangle.fillColor = Color(93, 247, 150, 50)
  addChildren(rectangle)

}
