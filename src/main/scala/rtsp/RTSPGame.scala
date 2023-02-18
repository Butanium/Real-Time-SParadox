package rtsp
import engine2D.Game
import sfml.graphics.RenderWindow
import rtsp.battle.RTSPBattle
import rtsp.objects.RTSPWarrior
import rtsp.battle.Behavior

class RTSPGame(window: RenderWindow)
    extends Game(window, 60, sfml.graphics.Color.Black(), debug = false) {
  val engine = new RTSPGameEngine(1f/60, window, debug = false)
  val battle = new RTSPBattle()
  val team1 = List(
    RTSPWarrior.createArcher(engine, battle, 0, Behavior.basicBehavior(battle)),
    RTSPWarrior.createArcher(engine, battle, 0, Behavior.basicBehavior(battle)),
    RTSPWarrior.createBarbarian(engine, battle, 0, Behavior.basicBehavior(battle)),
    RTSPWarrior.createBarbarian(engine, battle, 0, Behavior.basicBehavior(battle))
  )
  val team2 = List(
    RTSPWarrior.createArcher(engine, battle, 0, Behavior.basicBehavior(battle)),
    RTSPWarrior.createArcher(engine, battle, 0, Behavior.basicBehavior(battle)),
    RTSPWarrior.createArcher(engine, battle, 0, Behavior.basicBehavior(battle)),
    RTSPWarrior.createBarbarian(engine, battle, 0, Behavior.basicBehavior(battle))
  )
  override def step() = {battle.step(); super.step()}
}