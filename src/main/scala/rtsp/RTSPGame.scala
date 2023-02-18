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
  // Initialize units position
  team1(0).position = (100, 100)
  team1(1).position = (200, 100)
  team1(2).position = (100, 200)
  team1(3).position = (200, 200)
  team2(0).position = (700, 100)
  team2(1).position = (600, 100)
  team2(2).position = (700, 200)
  team2(3).position = (600, 200)
  battle.team1 = team1
  battle.team2 = team2
  override def step() = {battle.step(); super.step()}
}