package rtsp.objects
import rtsp.Constants.ShopConstants.*
import rtsp.Player
import rtsp.battle.RTSPBattle
import engine2D.*
import engine2D.objects.*
import scala.collection.mutable.ListBuffer
import sfml.system.Vector2
import sfml.graphics.RectangleShape
import sfml.graphics.Color

class WarriorShop(player: Player, bench: Bench, engine: GameEngine) extends Shop(player, bench, engine) {
   var actualiseButton = ActualiseButton(player, this, engine)
   addChildren(actualiseButton)
}
