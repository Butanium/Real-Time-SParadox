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

class RTSPShopGame(window: RenderWindow)
    extends Game(window, 60, sfml.graphics.Color.Black(), debug = false) {
  val engine = new RTSPGameEngine(3f / 60, window, debug = false)


  val player0 = Player(0, "Player 0")
  val player1 = Player(1, "Player 1")
  val battle = RTSPBattle(player0, debug)
  val warriorBench0 = WarriorBench(engine, player0, battle, BENCH_SIZE)
  val benchEffects0 = EffectBench(engine, player0, battle, BENCH_SIZE)
  val warriorBench1 = WarriorBench(engine, player1, battle, BENCH_SIZE)
  val benchEffects1 = EffectBench(engine, player1, battle, BENCH_SIZE)

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
 
  
  val shopWarrior0 = Shop(player0, INIT_NB_BUYABLE_SHOP, MAX_NB_BUYABLE_SHOP, Array.tabulate(NUMBER_OF_WARRIORS)(_=>1), idToWarrior, warriorBench0, engine)
  val shopEffects0 = Shop(player0, INIT_NB_BUYABLE_SHOP, MAX_NB_BUYABLE_SHOP, Array.tabulate(NUMBER_OF_POTIONS)(_=>1), idToEffect, benchEffects0, engine)
  val shopWarrior1 = Shop(player1, INIT_NB_BUYABLE_SHOP, MAX_NB_BUYABLE_SHOP, Array.tabulate(NUMBER_OF_WARRIORS)(_=>1), idToWarrior, warriorBench1, engine)
  val shopEffects1 = Shop(player1, INIT_NB_BUYABLE_SHOP, MAX_NB_BUYABLE_SHOP, Array.tabulate(NUMBER_OF_POTIONS)(_=>1), idToEffect, benchEffects1, engine)
  shopEffects0.active = false
  shopWarrior1.active = false
  shopEffects1.active = false

  val switchButton = SwitchButton(shopWarrior0, shopEffects0, engine)
  engine.spawn(switchButton)
  override def init() = {
    val basePlayer0 = RTSPBase(engine, battle, player0)
    engine.spawn(basePlayer0)
    battle.addBase(
      basePlayer0,
      player0
    )
    val basePlayer1 = RTSPBase(engine, battle, player1)
    engine.spawn(basePlayer1)
    battle.addBase(
      basePlayer1,
      player1
    )
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
    val potionTest = createAttackBuff(engine, player0, battle, debug)
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
    shopWarrior0.position = (
      window.size.x * (1 - SHOP_WIDTH_RATIO) / 2f + shopWarrior0.thickness,
      window.size.y * (1 - SHOP_HEIGHT_RATIO) + shopWarrior0.thickness
    )
    shopEffects0.position = (
      window.size.x * (1 - SHOP_WIDTH_RATIO) / 2f + shopEffects0.thickness,
      window.size.y * (1 - SHOP_HEIGHT_RATIO) + shopEffects0.thickness
    )
    warriorBench0.position = (
      window.size.x * (1 - BENCH_WIDTH_RATIO) / 2f,
      window.size.y * (0.9f - BENCH_HEIGHT_RATIO)
    )
    engine.spawn(warriorBench0)
    benchEffects0.position = (
      window.size.x * (1 - BENCH_WIDTH_RATIO) / 2f,
      window.size.y * (0.9f - BENCH_HEIGHT_RATIO) - 50
    )
    benchEffects0.addBought(potionTest)

    engine.spawn(benchEffects0)
    engine.spawn(shopEffects0, shopWarrior0)

    shopWarrior1.position = (
      window.size.x * (1 - SHOP_WIDTH_RATIO) / 2f + shopWarrior1.thickness,
      window.size.y * (1 - SHOP_HEIGHT_RATIO) + shopWarrior1.thickness
    )
    shopEffects1.position = (
      window.size.x * (1 - SHOP_WIDTH_RATIO) / 2f + shopEffects1.thickness,
      window.size.y * (1 - SHOP_HEIGHT_RATIO) + shopEffects1.thickness
    )
    warriorBench1.position = (
      window.size.x * (1 - BENCH_WIDTH_RATIO) / 2f,
      window.size.y * (0.9f - BENCH_HEIGHT_RATIO)
    )
    benchEffects1.position = (
      window.size.x * (1 - BENCH_WIDTH_RATIO) / 2f,
      window.size.y * (0.9f - BENCH_HEIGHT_RATIO) - 50
    )
  }
  override def step() = {
    val ended = battle.step()
    if ended then {
      player0.earnMoney(
        2 * battle.enemies(player0.id).count(w => !w.active && !w.benched)
      )
      player1.earnMoney(
        2 * battle.enemies(player1.id).count(w => !w.active && !w.benched)
      )

      battle.reset()

    }
    super.step()
  }

}
