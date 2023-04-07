package rtsp
import engine2D.Game
import sfml.graphics.RenderWindow
import rtsp.battle.RTSPBattle
import rtsp.objects.RTSPWarrior
import rtsp.battle.Behavior
import rtsp.objects.Shop
import rtsp.Constants.ShopConstants.*
import rtsp.Constants.*
import sfml.window.Mouse
import rtsp.objects.WarriorBench
import rtsp.objects.EffectBench
import rtsp.objects.Effect.*

class RTSPShopGame(window: RenderWindow)
    extends Game(window, 60, sfml.graphics.Color.Black(), debug = false) {
  val engine = new RTSPGameEngine(1f / 60, window, debug = false)

  val player = Player(0, "uwu")
  val battle = RTSPBattle(player, debug)
  val bench = WarriorBench(engine, player, battle, BENCH_SIZE)
  val benchEffects = EffectBench(engine, player, battle, BENCH_SIZE)

  def idToWarrior(id : Int) = id match {
    case 0 => RTSPWarrior.createBarbarian(engine, battle, 0, Behavior.basicBehavior(battle), debug)
    case 1 => RTSPWarrior.createArcher(engine, battle, 0, Behavior.basicBehavior(battle), debug)
    case 2 => RTSPWarrior.createGiant(engine, battle, 0, Behavior.basicBehavior(battle), debug)
    case _ => throw new Exception(s"Invalid warrior id $id")
  }

  def idToEffect(id : Int) = id match {
    case 0 => createAttackBuff(engine, player, battle, debug)
    case 1 => createSpeedBuff(engine, player, battle, debug)
    case 2 => createTankBuff(engine, player, battle, debug)
    case _ => throw new Exception(s"Invalid effect id $id")
  }
  // val shopWarriors = Shop(player, INIT_NB_BUYABLE_SHOP, MAX_NB_BUYABLE_SHOP, BASIC_POOL_REPARTITION, idToWarrior, bench, engine)
  val shopEffects = Shop(player, INIT_NB_BUYABLE_SHOP, MAX_NB_BUYABLE_SHOP, Array.tabulate(NUMBER_OF_POTIONS)(x=>1), idToEffect, benchEffects, engine)
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
    val potionTest = createAttackBuff(engine, player, battle, debug)


    engine.spawn(team1: _*)
    engine.mouseManager.registerMouseEvent(
      engine2D.eventHandling.MouseEvent
        .ButtonPressed(
          Mouse.Button.Right,
          true
        ),
      () => {
        battle.active = !battle.active;
        //shopWarriors.active = !shopWarriors.active;
      }
    )
    // shopWarriors.position = (
    //   window.size.x * (1 - SHOP_WIDTH_RATIO) / 2f + shopWarriors.thickness,
    //   window.size.y * (1 - SHOP_HEIGHT_RATIO) + shopWarriors.thickness
    // )
    shopEffects.position = (
      window.size.x * (1 - SHOP_WIDTH_RATIO) / 2f + shopEffects.thickness,
      window.size.y * (1 - SHOP_HEIGHT_RATIO) + shopEffects.thickness
    )
    bench.position = (
      window.size.x * (1 - BENCH_WIDTH_RATIO) / 2f,
      window.size.y * (0.9f - BENCH_HEIGHT_RATIO)
    )
    engine.spawn(bench)
    benchEffects.position = (
      window.size.x * (1 - BENCH_WIDTH_RATIO) / 2f,
      window.size.y * (0.9f - BENCH_HEIGHT_RATIO) - 50
    )
    benchEffects.addBought(potionTest)

    engine.spawn(benchEffects)
    engine.spawn(shopEffects)

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
