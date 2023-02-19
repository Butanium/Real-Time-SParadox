package rtsp.objects
import rtsp.Constants.*
import rtsp.Player
import rtsp.battle.RTSPBattle
import engine2D.*
import engine2D.objects.*
import scala.collection.mutable.ListBuffer
class Shop(val player: Player, engine: GameEngine) extends GameObject(engine) {
  var pool = new Pool (BASIC_POOL_REPARTITION,this)
  val nb_buyable: Int = INIT_NB_BUYABLE
  var to_buy: Array[ShopWarrior] =
    new Array[ShopWarrior](MAX_NB_BUYABLE)
  var level: Int = 1
  def upgrade =
    level += 1
  def replace(position: Int) =
    val random_shop_warrior: ShopWarrior = pool.get_random
    addChildren(random_shop_warrior)
    removeChildren(to_buy(position))
    random_shop_warrior.change_shop_position_to(position)
    to_buy(position) = random_shop_warrior
  def change_shop =
    for i <- 0 to nb_buyable do
      replace(i)

}
