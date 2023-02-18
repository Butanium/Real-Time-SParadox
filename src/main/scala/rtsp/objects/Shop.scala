package rtsp.objects
import rtsp.Constants
import engine2D.*
import engine2D.objects.*
import scala.collection.mutable.ListBuffer
class Shop(pool: Pool, engine: GameEngine) extends GameObject(engine) {
  val nb_buyable: Int = Constants.INIT_NB_BUYABLE
  var to_buy: Array [ShopWarrior] = new Array[ShopWarrior](Constants.MAX_NB_BUYABLE)
  var level : Int = 1
  def upgrade =
    level +=1
  def replace (position: Int) = 
    val random_shop_warrior : ShopWarrior = pool.get_random
    addChildren(random_shop_warrior)
    removeChildren(to_buy(position))
    to_buy(position) = random_shop_warrior
  def change_shop = 
    for i <- 0 to nb_buyable do
      val random_shop_warrior : ShopWarrior = pool.get_random
      addChildren(random_shop_warrior)
      removeChildren(to_buy(i))
      to_buy(i) = random_shop_warrior
}
