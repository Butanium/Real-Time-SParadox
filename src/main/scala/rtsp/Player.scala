package rtsp
import rtsp.Constants
import scala.compiletime.ops.boolean
class Player(val id: Int) {
  var money: Int = Constants.STARTING_MONEY
  def buy(price: Int) :Boolean =
    if money >= price then
      money -= price
      true
    else
      false
  def earnMoney(amount: Int): Unit = money += amount
}
