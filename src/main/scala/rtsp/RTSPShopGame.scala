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
  val engine = new RTSPGameEngine(3f / 60, window, debug = false)
  val background = engine2D.objects.SpriteObject("arena.png", engine)
  background.fillDimensions(
    window.size.x.toFloat,
    window.size.y.toFloat
  )
  engine.spawn(background)
  val player = Player(0, "You")
  val bot = Player(1, "Bot")
  val battle = RTSPBattle(player, debug)
  val warriorBench = WarriorBench(engine, player, battle, BENCH_SIZE)
  val benchEffects = EffectBench(engine, player, battle, BENCH_SIZE)

  def idToWarrior(id: Int) = id match {
    case 0 =>
      RTSPWarrior.createBarbarian(
        engine,
        battle,
        0,
        Behavior.advancedBehavior(battle),
        debug
      )
    case 1 =>
      RTSPWarrior.createArcher(
        engine,
        battle,
        0,
        Behavior.advancedBehavior(battle),
        debug
      )
    case 2 =>
      RTSPWarrior.createGiant(
        engine,
        battle,
        0,
        Behavior.basicBehavior(battle),
        debug
      )
    case _ => throw new Exception(s"Invalid warrior id $id")
  }

  def idToEffect(id: Int) = id match {
    case 0 => createAttackBuff(engine, player, battle, debug)
    case 1 => createSpeedBuff(engine, player, battle, debug)
    case 2 => createTankBuff(engine, player, battle, debug)
    case _ => throw new Exception(s"Invalid effect id $id")
  }

  val shopWarrior = Shop(
    player,
    INIT_NB_BUYABLE_SHOP,
    MAX_NB_BUYABLE_SHOP,
    Array.tabulate(NUMBER_OF_WARRIORS)(_ => 1),
    idToWarrior,
    warriorBench,
    engine
  )
  val shopEffects = Shop(
    player,
    INIT_NB_BUYABLE_SHOP,
    MAX_NB_BUYABLE_SHOP,
    Array.tabulate(NUMBER_OF_POTIONS)(_ => 1),
    idToEffect,
    benchEffects,
    engine
  )
  shopEffects.active = false
  val switchButton = SwitchButton(shopWarrior, shopEffects, engine)
  engine.spawn(switchButton)
  override def init() = {
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
    )
    shopEffects.position = (
      window.size.x * (1 - SHOP_WIDTH_RATIO) / 2f + shopEffects.thickness,
      window.size.y * (1 - SHOP_HEIGHT_RATIO) + shopEffects.thickness
    )
    warriorBench.position = (
      window.size.x * (1 - BENCH_WIDTH_RATIO) / 2f,
      window.size.y * (0.9f - BENCH_HEIGHT_RATIO)
    )
    engine.spawn(warriorBench)
    benchEffects.position = (
      window.size.x * (1 - BENCH_WIDTH_RATIO) / 2f,
      window.size.y * (0.9f - BENCH_HEIGHT_RATIO) - 50
    )
    benchEffects.addBought(potionTest)

    engine.spawn(benchEffects)
    engine.spawn(shopEffects, shopWarrior)
  }
  override def step() = {
    val ended = battle.step()
    if ended then {
      player.earnMoney(
        2 * battle.enemies(player.id).count(w => !w.active && !w.benched)
      )

      battle.reset()

    }
    super.step()
  }

}
