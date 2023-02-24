package rtsp
import rtsp.Constants
class Player(val id: Int) {
  var money: Int = Constants.STARTING_MONEY
  def earnMoney(amount: Int): Unit = money += amount
}
