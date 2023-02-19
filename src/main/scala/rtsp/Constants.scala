package rtsp
import rtsp.objects.*
import rtsp.battle.*
object Constants {
  val INIT_NB_BUYABLE : Int = 3
  val MAX_NB_BUYABLE : Int = 3
  val STARTING_MONEY : Int = 20
  val PRICE_ARCHER : Int = 3
  val PRICE_BARBARIAN : Int = 4
  val ID_BARBARIAN : Int = 1
  val ID_ARCHER : Int = 2
  val BASIC_POOL_REPARTITION : Array[Tuple2[(Shop) => ShopWarrior, Float]] = Array((ShopWarrior.create_shop_Barbarian,1f),(ShopWarrior.create_shop_Archer,1f))
}
