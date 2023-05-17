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
    val RATIO_WIDTH_MONEY: Float = 0.5f
    val RATIO_HEIGHT_MONEY: Float = 0.25f
    val INIT_NB_BUYABLE_SHOP: Int = 3
    val MAX_NB_BUYABLE_SHOP: Int = 3
    val PRICE_ARCHER: Int = 4
    val PRICE_BARBARIAN: Int = 2
    val ACTUALISE_PRICE: Int = 1
    val BASIC_POOL_REPARTITION: Array[Float] =
      Array(1f, 1f)
  }
  object BattleC {
    // initial hp of the base
    val BASE_HP: Int = 10_000
    val BASE_RANGE: Int = 150
    val BASE_ATTACK_DAMAGE: Int = 200
    val BASE_ATTACK_DELAY: Float = 1f
    val TIMEOUT_DURATION: Float = 10f
    val ARENA_BOUNDS = sfml.graphics.Rect[Float](0f, 0f, 800f, 550f)
  }
  object EditorC {
    val NODE_WIDTH: Float = 80f
    val NODE_HEIGHT: Float = 50f
    val NODE_CIRCLE_RADIUS: Float = 12f
    val LINE_THICKNESS: Float = 3f
    val MENU_PADDING: Float = 10f
  }
  val STARTING_MONEY: Int = 600
  val ID_BARBARIAN: Int = 0
  val ID_ARCHER: Int = 1
  val ID_GIANT: Int = 2
  val ID_MAGE: Int = 3
  val ID_HEALER: Int = 4
  val MAX_WARRIORS_IN_BATTLE: Int = 7
  val WARRIOR_DROP_RADIUS: Float = 350f
  val BENCH_SIZE: Int = 4
  val BENCH_WIDTH_RATIO: Float = 1f
  val BENCH_HEIGHT_RATIO: Float = 0.2f
  val NUMBER_OF_POTIONS: Int = 3
  val NUMBER_OF_WARRIORS: Int = 5
  val HEALT_BAR_WIDTH: Float = 30f
  val HEALT_BAR_HEIGHT: Float = 5f
  val INIT_LIMIT_OF_WARRIORS: Int = 3
  val BATTLE_DURATION: Int = 30
}
