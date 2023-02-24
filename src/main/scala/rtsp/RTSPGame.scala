/* package rtsp
import engine2D.Game
import sfml.graphics.RenderWindow
import rtsp.battle.RTSPBattle
import rtsp.objects.RTSPWarrior
import rtsp.battle.Behavior

class RTSPGame(window: RenderWindow)
    extends Game(window, 60, sfml.graphics.Color.Black(), debug = false) {
  val engine = new RTSPGameEngine(1f / 60, window, debug = false)
  val battle = new RTSPBattle()
  val team1 = List(
    RTSPWarrior
      .createArcher(engine, battle, 0, Behavior.basicBehavior(battle), debug),
    RTSPWarrior
      .createArcher(engine, battle, 0, Behavior.basicBehavior(battle), debug),
    RTSPWarrior.createBarbarian(
      engine,
      battle,
      0,
      Behavior.basicBehavior(battle),
      debug
    ),
    RTSPWarrior.createBarbarian(
      engine,
      battle,
      0,
      Behavior.basicBehavior(battle),
      debug
    )
  )
  val team2 = List(
    RTSPWarrior
      .createArcher(engine, battle, 1, Behavior.basicBehavior(battle), debug),
    RTSPWarrior
      .createArcher(engine, battle, 1, Behavior.basicBehavior(battle), debug),
    RTSPWarrior
      .createArcher(engine, battle, 1, Behavior.basicBehavior(battle), debug),
    RTSPWarrior.createBarbarian(
      engine,
      battle,
      1,
      Behavior.basicBehavior(battle),
      debug
    )
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
  battle.addWarriors(team1*)
  battle.addWarriors(team2*)
  // Print teams size

  override def step() = { battle.step(); super.step() }
  override def init() = {
    engine.spawn(team1: _*)
    engine.spawn(team2: _*)
    super.init()
  }
}
 */