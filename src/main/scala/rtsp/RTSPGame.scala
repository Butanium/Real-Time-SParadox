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
    RTSPWarrior.createArcher(engine, battle, 1, Behavior.basicBehavior(battle), debug = true),
    RTSPWarrior.createArcher(engine, battle, 1, Behavior.basicBehavior(battle), debug = false),
    RTSPWarrior.createBarbarian(engine, battle, 1, Behavior.basicBehavior(battle), debug = false),
    RTSPWarrior.createBarbarian(engine, battle, 1, Behavior.basicBehavior(battle), debug = false)
  )
  val team2 = List(
    RTSPWarrior.createArcher(engine, battle, 2, Behavior.basicBehavior(battle), debug = false),
    RTSPWarrior.createArcher(engine, battle, 2, Behavior.basicBehavior(battle), debug = false),
    RTSPWarrior.createArcher(engine, battle, 2, Behavior.basicBehavior(battle), debug = false),
    RTSPWarrior.createBarbarian(engine, battle, 2, Behavior.basicBehavior(battle), debug = true)
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
  // Print teams size
  println("Size team 1:" + team1.size + " Size team 2:" + team2.size)

  println("Size enemies: " + battle.getEnemies(1).size + " Size allies: " + battle.getAllies(1).size)
  
  override def step() = {battle.step(); super.step()}
  override def init() = {
    engine.spawn(team1:_*)
    engine.spawn(team2:_*)
    super.init()
  }
}