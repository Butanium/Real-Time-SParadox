package rtsp
import rtsp.Constants
import rtsp.objects.Buyable
class Player(val id: Int) {
  var money: Int = Constants.STARTING_MONEY
  def buy(price: Int): Boolean =
    if money >= price then
      money -= price
      true
    else false
  def buy(buyable: Buyable): Boolean = buy(buyable.price)

  def earnMoney(amount: Int): Unit = money += amount
}
