package rtsp.objects
import util.Random
import rtsp.Player
import rtsp.battle.RTSPBattle
class Pool(type_prob_warrior: Array[Tuple2[(Shop) => ShopWarrior, Float]], shop : Shop) {
  private def coeff_sum(t: Array[Tuple2[(Shop) => ShopWarrior, Float]]): Float =
    var s: Float = 0
    for i <- 0 to ((t.size) - 1) do s += (t(i))._2
    s
  def get_random: ShopWarrior =
    var p: Float = Random.between(0f, coeff_sum(type_prob_warrior))
    var i: Int = 0
    while (p >= 0f) && (i < type_prob_warrior.size) do
      p -= type_prob_warrior(i)._2
      i += 1
    type_prob_warrior(i - 1)._1(shop)
}
