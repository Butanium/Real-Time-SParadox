package rtsp
import engine2D.Game
import sfml.graphics.RenderWindow
import rtsp.battle.RTSPBattle
import rtsp.battle.Behavior
import rtsp.objects.*
import rtsp.Constants.ShopConstants.*
import rtsp.Constants.*
import sfml.window.Mouse
import rtsp.objects.WarriorBench
import rtsp.objects.EffectBench
import rtsp.objects.Effect.*
import objects.SwitchButton
import rtsp.editor.Node
import engine2D.GameEngine

class NodeGame(window: RenderWindow)
    extends Game(window, 60, sfml.graphics.Color.Black(), debug = false) {
  var engine = GameEngine(3f / 60, window, debug = false) 
  override def init(): Unit = 
    val firstNode = Node(engine)
    firstNode.position = (window.size.x / 2f, window.size.y / 2f)
    engine.spawn(firstNode)

}
