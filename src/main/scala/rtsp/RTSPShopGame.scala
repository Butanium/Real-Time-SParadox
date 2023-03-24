package rtsp
import engine2D.Game
import sfml.graphics.RenderWindow
import rtsp.battle.RTSPBattle
import rtsp.battle.Behavior
import rtsp.objects.*
import rtsp.Constants.ShopConstants.*
import rtsp.Constants.*
import sfml.window.Mouse

class RTSPShopGame(window: RenderWindow)
    extends Game(window, 60, sfml.graphics.Color.Black(), debug = false) {
  val engine = new RTSPGameEngine(1f / 60, window, debug = false)

  val player = Player(0, "You")
  val bot = Player(1, "Bot")
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
    val basePlayer = RTSPBase(engine, battle, player)
    engine.spawn(basePlayer)
    battle.addBase(
      basePlayer,
      player
    )
    val baseBot = RTSPBase(engine, battle, bot)
    engine.spawn(baseBot)
    battle.addBase(
      baseBot,
      bot
    )

    battle.addWarriors(team1*)
    engine.spawn(team1*)
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
