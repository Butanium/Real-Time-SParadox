package rtsp.objects
import rtsp.Player
trait Buyable {
  val price: Int
  val player: Player
  def affordable = price <= player.money
}
