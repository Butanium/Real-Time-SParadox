package rtsp.objects
import rtsp.Player
trait Buyable {
  val price: Int
  def affordable(player : Player) = price <= player.money
  val name : String
  val spriteTexture : String
}
