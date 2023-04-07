package rtsp
import rtsp.objects.*
import rtsp.battle.*
object Constants {
  object ShopConstants {
    val SHOP_HEIGHT_RATIO: Float = 0.2f
    val SHOP_WIDTH_RATIO: Float = 1f
    val SHOP_THICKNESS_RATIO: Float =
      Math.min(SHOP_HEIGHT_RATIO, SHOP_WIDTH_RATIO) * 0.05f
    val SPACE_FOR_BUYABLE: Float = 0.8f
    val RATIO_WIDTH_MONEY: Float = 0.2f
    val RATIO_HEIGHT_MONEY: Float = 0.3f
    val INIT_NB_BUYABLE: Int = 3
    val MAX_NB_BUYABLE: Int = 3
    val PRICE_ARCHER: Int = 4
    val PRICE_BARBARIAN: Int = 2
    val ACTUALISE_PRICE: Int = 1
    val BASIC_POOL_REPARTITION: Array[Tuple2[(Shop) => ShopWarrior, Float]] =
      Array(
        (ShopWarrior.create_shop_Barbarian, 1f),
        (ShopWarrior.create_shop_Archer, 1f)
      )
  }
  object BattleC {
    // initial hp of the base
    val BASE_HP: Int = 10_000
    val BASE_RANGE: Int = 150
    val BASE_ATTACK_DAMAGE: Int = 200
    val BASE_ATTACK_DELAY: Float = 1f
    val TIMEOUT_DURATION: Float = 10f
    val ARENA_BOUNDS = sfml.graphics.Rect[Float](0f, 0f, 400f, 400f)
  }
  val STARTING_MONEY: Int = 6000
  val ID_BARBARIAN: Int = 0
  val ID_ARCHER: Int = 1
  val BENCH_SIZE: Int = 4
  val BENCH_WIDTH_RATIO: Float = 1f
  val BENCH_HEIGHT_RATIO: Float = 0.2f
}
