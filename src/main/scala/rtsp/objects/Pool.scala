package rtsp.objects
import util.Random
import rtsp.Player
import rtsp.battle.RTSPBattle
class Pool(
    probObject: Array[Float],
    shop: Shop[?]
) {
  // tire aléatoirement un entier du tableau en fonction de la répartition aléatoire
  def get_random(): Int =
    val p = Random.between(0f, probObject.sum)
    probObject
      .scanLeft(0f)(_ + _)
      .indexWhere(_ > p) - 1

}
