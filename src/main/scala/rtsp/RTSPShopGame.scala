package rtsp
import engine2D.Game
import sfml.graphics.RenderWindow
import rtsp.battle.RTSPBattle
import rtsp.objects.RTSPWarrior
import rtsp.battle.Behavior
import rtsp.objects.Shop
import rtsp.Constants.ShopConstants.*
import rtsp.Constants.*
import rtsp.objects.Bench
import sfml.window.Mouse

class RTSPShopGame(window: RenderWindow)
    extends Game(window, 60, sfml.graphics.Color.Black(), debug = false) {
  val engine = new RTSPGameEngine(1f / 60, window, debug = false)

  val player = Player(0)
  val battle = RTSPBattle(player, debug)
  val bench = Bench(engine, player, battle)
  val shop = Shop(player, bench, engine)
  override def init() = {

    val team1 = List(
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
      ),
      RTSPWarrior.createBarbarian(
        engine,
        battle,
        1,
        Behavior.basicBehavior(battle),
        debug
      )
    )

    team1(0).position = (100, 100)
    team1(1).position = (200, 100)
    team1(2).position = (100, 200)
    team1(3).position = (200, 200)

    battle.addWarriors(team1*)

    engine.spawn(team1: _*)
    engine.mouseManager.registerMouseEvent(
      engine2D.eventHandling.MouseEvent
        .ButtonPressed(
          Mouse.Button.Right,
          true
        ),
      () => {
        battle.active = !battle.active;
      }
    )
    shop.position = (
      window.size.x * (1 - SHOP_WIDTH_RATIO) / 2f + shop.thickness,
      window.size.y * (1 - SHOP_HEIGHT_RATIO) + shop.thickness
    )
    bench.position = (
      window.size.x * (1 - BENCH_WIDTH_RATIO) / 2f,
      window.size.y * (0.9f - BENCH_HEIGHT_RATIO)
    )
    engine.spawn(bench)
    engine.spawn(shop)
    // bench.addBoughtWarrior(
    //   RTSPWarrior.createBarbarian(
    //     engine,
    //     battle,
    //     0,
    //     Behavior.basicBehavior(battle),
    //     debug
    //   )
    // )
    // bench.addBoughtWarrior(
    //   RTSPWarrior.createArcher(
    //     engine,
    //     battle,
    //     0,
    //     Behavior.basicBehavior(battle),
    //     debug
    //   )
    // )
  }
  override def step() = {
    val losers = battle.step()
    if losers.nonEmpty then {
      player.earnMoney(
        2 * battle.enemies(player.id).count(w => !w.active && !w.benched)
          + 10 * (if ((!losers.contains(player.id))) then 1 else 0)
      )
      battle.reset()

    }
    super.step()
  }

}
