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
import rtsp.editor.NodeObject
import engine2D.GameEngine
import rtsp.editor.NodeType
import rtsp.battle.BehaviorTree
import engine2D.objects.ButtonObject

class NodeGame(window: RenderWindow)
    extends Game(window, 60, sfml.graphics.Color.Black(), debug = false) {
  val engine: RTSPGameEngine = RTSPGameEngine(3f / 60, window, debug = false)
  override def init(): Unit =
    val firstNode =
      NodeObject(NodeType.Root, BehaviorTree.Node(List.empty), engine)
    firstNode.position = (window.size.x / 2f, window.size.y / 2f)
    engine.spawn(firstNode)
}
