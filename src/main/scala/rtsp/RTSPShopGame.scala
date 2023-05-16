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
import engine2D.objects.ButtonObject
import sfml.graphics.Color

class RTSPShopGame(window: RenderWindow)
    extends Game(window, 60, sfml.graphics.Color.Black(), debug = false) {
  var engine = new RTSPGameEngine(3f / 60, window, debug = false)
  val engineP0 = new RTSPGameEngine(3f / 60, window, debug = false)
  val engineP1 = new RTSPGameEngine(3f / 60, window, debug = false)
  val engineBattle = new RTSPGameEngine(3f / 60, window, debug = false)

  /*
  val background = engine2D.objects.SpriteObject("arena.png", engine)
  background.fillDimensions(
    window.size.x.toFloat,
    window.size.y.toFloat
  )
  engine.spawn(background)
  */
  val player0 = Player(0, "Player 0")
  val player1 = Player(1, "Player 1")
  val battleP0 = RTSPBattle(player0, debug)
  val battleP1 = RTSPBattle(player1, debug)
  val battleReal = RTSPBattle(player0, debug)
  val warriorBench0 = WarriorBench(engineP0, player0, battleP0, BENCH_SIZE)
  val benchEffects0 = EffectBench(engineP0, player0, battleP0, BENCH_SIZE)
  val warriorBench1 = WarriorBench(engineP1, player1, battleP1, BENCH_SIZE)
  val benchEffects1 = EffectBench(engineP1, player1, battleP1, BENCH_SIZE)

  def idToWarrior(id: Int, player: Player, battle: RTSPBattle) = id match {
    case 0 =>
      RTSPWarrior.createBarbarian(
        engine,
        battle,
        player.id,
        Behavior.advancedBehavior(battle),
        debug
      )
    case 1 =>
      RTSPWarrior.createArcher(
        engine,
        battle,
        player.id,
        Behavior.advancedBehavior(battle),
        debug
      )
    case 2 =>
      RTSPWarrior.createGiant(
        engine,
        battle,
        player.id,
        Behavior.basicBehavior(battle),
        debug
      )
    case _ => throw new Exception(s"Invalid warrior id $id")
  }

  def idToEffect(id: Int, player: Player, battle: RTSPBattle) = id match {
    case 0 => createAttackBuff(engine, player, debug)
    case 1 => createSpeedBuff(engine, player, debug)
    case 2 => createTankBuff(engine, player, debug)
    case _ => throw new Exception(s"Invalid effect id $id")
  }
  val shopWarrior0 = Shop(
    player0,
    INIT_NB_BUYABLE_SHOP,
    MAX_NB_BUYABLE_SHOP,
    Array.tabulate(NUMBER_OF_WARRIORS)(_ => 1),
    battleP0,
    idToWarrior,
    warriorBench0,
    engineP0
  )
  val shopEffects0 = Shop(
    player0,
    INIT_NB_BUYABLE_SHOP,
    MAX_NB_BUYABLE_SHOP,
    Array.tabulate(NUMBER_OF_POTIONS)(_ => 1),
    battleP0,
    idToEffect,
    benchEffects0,
    engineP0
  )
  val shopWarrior1 = Shop(
    player1,
    INIT_NB_BUYABLE_SHOP,
    MAX_NB_BUYABLE_SHOP,
    Array.tabulate(NUMBER_OF_WARRIORS)(_ => 1),
    battleP1,
    idToWarrior,
    warriorBench1,
    engineP1
  )
  val shopEffects1 = Shop(
    player1,
    INIT_NB_BUYABLE_SHOP,
    MAX_NB_BUYABLE_SHOP,
    Array.tabulate(NUMBER_OF_POTIONS)(_ => 1),
    battleP1,
    idToEffect,
    benchEffects1,
    engineP1
  )

  val switchButton = SwitchButton(shopWarrior0, shopEffects0, engineP0)
  engineP0.spawn(switchButton)
  val switchButton1 = SwitchButton(shopWarrior1, shopEffects1, engineP1)
  engineP1.spawn(switchButton1)
  override def init() = {
    val basePlayer0 = RTSPBase(engineP0, battleReal, player0)
    engineP0.spawn(basePlayer0)
    engineP1.spawn(basePlayer0)
    engineBattle.spawn(basePlayer0)
    battleReal.addBase(
      basePlayer0,
      player0
    )
    /*battle.addBase(
      basePlayer0,
      player0
    )*/
    val basePlayer1 = RTSPBase(engineP0, battleReal, player1)
    engineP0.spawn(basePlayer1)
    engineP1.spawn(basePlayer1)
    engineBattle.spawn(basePlayer1)
    battleReal.addBase(
      basePlayer1,
      player1
    )
    /*battle.addBase(
      basePlayer1,
      player1
    )*/
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
    engine.spawn(team1*)

    // Création d'un bouton Start
    val startButton = ButtonObject(
      "Start !",
      () => {
        battle.active = !battle.active;
      },
      engine
    )
    startButton.position =
      (engine.window.size.x * 0.75f, engine.window.size.y * 0.08f)
    startButton.changeBackground(
      engine.window.size.x * 0.25f,
      engine.window.size.y * 0.08f
    )
    startButton.background.fillColor = Color(165, 245, 73, 80)
    startButton.background.outlineColor = Color(236, 151, 22)
    engine.spawn(startButton)

    // Création d'un bouton Switch Player
    val switchPlayerButton = ButtonObject(
      "Switch Player",
      () => (),
      engine
    )
    switchPlayerButton.position =
      (engine.window.size.x * 0.75f, engine.window.size.y * 0.16f)
    switchPlayerButton.changeBackground(
      engine.window.size.x * 0.25f,
      engine.window.size.y * 0.08f
    )
    switchPlayerButton.background.fillColor = Color(165, 245, 73, 80)
    switchPlayerButton.background.outlineColor = Color(236, 151, 22)
    engine.spawn(switchPlayerButton)

    shopWarrior.position = (
      window.size.x * (1 - SHOP_WIDTH_RATIO) / 2f + shopWarrior.thickness,
      window.size.y * (1 - SHOP_HEIGHT_RATIO) + shopWarrior.thickness
    engine.spawn(team1*)*/
    val potionTest = createAttackBuff(engineP0, player0, debug)

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
    engineP0.spawn(warriorBench0)
    benchEffects0.position = (
      window.size.x * (1 - BENCH_WIDTH_RATIO) / 2f,
      window.size.y * (0.9f - BENCH_HEIGHT_RATIO) - 50
    )
    benchEffects0.addBought(potionTest)
    engineP0.spawn(benchEffects0)
    engineP0.spawn(shopEffects0, shopWarrior0)

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
  engineP1.spawn(warriorBench1)
  engineP1.spawn(benchEffects1)
  engineP1.spawn(shopEffects1, shopWarrior1)

  engine = engineP0

  var stateCounter = 0
  engine.mouseManager.registerMouseEvent(
    engine2D.eventHandling.MouseEvent
      .ButtonPressed(
        Mouse.Button.Right,
        true
      ),
    () => {
      stateCounter = (stateCounter + 1) % 3
      stateCounter match {
        case 0 =>
          engine = engineP0
          shopWarrior0.active = true
          shopEffects0.active = false
          shopWarrior1.active = false
          shopEffects1.active = false
        case 1 =>
          battleReal.teams(0) = battleP0.teams(0)
          engine = engineP1
          shopWarrior0.active = false
          shopEffects0.active = false
          shopWarrior1.active = true
          shopEffects1.active = false
        case 2 =>
          battleReal.teams(1) = battleP1.teams(1)
          engine = engineBattle
          shopWarrior0.active = false
          shopEffects0.active = false
          shopWarrior1.active = false
          shopEffects1.active = false
          battleReal.active = true
      }
    }
  )

  override def step() = {
    val ended = battleReal.step()
    if ended then {
      player0.earnMoney(
        2 * battleReal.enemies(player0.id).count(w => !w.active && !w.benched)
      )
      player1.earnMoney(
        2 * battleReal.enemies(player1.id).count(w => !w.active && !w.benched)
      )

      battleReal.reset()

    }
    super.step()
  }

}
