package rtsp.objects
import util.Random
import rtsp.Player
import rtsp.battle.RTSPBattle
class Pool[T <: Buyable](
  probObject: Array [Float],
    shop: Shop[T]
) {
  // calcule la somme flottante des coefficients de l'Array
  private def coeff_sum(t: Array[Float]): Float =
    var s: Float = 0
    for i <- 0 to ((t.size) - 1) do s += t(i)
    s
  // tire aléatoirement un entier du tableau en fonction de la répartition aléatoire
  def get_random(): Int =
    var p: Float = Random.between(0f, coeff_sum(probObject))
    var i: Int = 0
    while (p >= 0f) && (i < probObject.size) do
      p -= probObject(i)
      i += 1
    i - 1
}
