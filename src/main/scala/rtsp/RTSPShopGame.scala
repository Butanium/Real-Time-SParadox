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
import engine2D.GameEngine
import sfml.system.Clock
import sfml.system.Time

class RTSPShopGame(window: RenderWindow)
    extends Game[RTSPGameEngine](
      window,
      60,
      sfml.graphics.Color.Black(),
      debug = false
    ) {
  val engineP0 = new RTSPGameEngine(3f / 60, window, debug = false)
  val engineP1 = new RTSPGameEngine(3f / 60, window, debug = false)
  val engineBattle = new RTSPGameEngine(3f / 60, window, debug = false)
  var engine: RTSPGameEngine = engineP0
  val player0 = Player(0, "Player 0")
  val player1 = Player(1, "Player 1")
  val battle = RTSPBattle(engineBattle, debug)
  val battleClock = Clock()

  def idToWarrior(
      id: Int,
      player: Player,
      battle: RTSPBattle,
      engine: RTSPGameEngine
  ) = id match {
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
    case 3 =>
      RTSPWarrior.createMage(
        engine,
        battle,
        player.id,
        Behavior.basicBehavior(battle),
        debug
      )
    case 4 =>
      RTSPWarrior.createHealer(
        engine,
        battle,
        player.id,
        Behavior.basicHealerBehavior(battle),
        debug
      )
    case _ => throw new Exception(s"Invalid warrior id $id")
  }

  def idToEffect(
      id: Int,
      player: Player,
      battle: RTSPBattle,
      engine: RTSPGameEngine
  ) = id match {
    case 0 => createAttackBuff(engine, player, debug)
    case 1 => createSpeedBuff(engine, player, debug)
    case 2 => createTankBuff(engine, player, debug)
    case _ => throw new Exception(s"Invalid effect id $id")
  }

  def initBenchesAndShops(engine: RTSPGameEngine, player: Player) =
    val background = engine2D.objects.SpriteObject("arena.png", engine)
    background.fillDimensions(
      window.size.x.toFloat,
      window.size.y.toFloat
    )
    background.zIndex = -1
    engine.spawn(background)
    val sellingBin = SellingBin(engine, player)
    val warriorBench = WarriorBench(engine, player, battle, BENCH_SIZE, sellingBin)
    val benchEffects = EffectBench(engine, player, battle, BENCH_SIZE, sellingBin)
    val shopWarrior = Shop(
      player,
      INIT_NB_BUYABLE_SHOP,
      MAX_NB_BUYABLE_SHOP,
      Array.tabulate(NUMBER_OF_WARRIORS)(_ => 1),
      battle,
      idToWarrior,
      warriorBench,
      engine
    )
    val shopEffects = Shop(
      player,
      INIT_NB_BUYABLE_SHOP,
      MAX_NB_BUYABLE_SHOP,
      Array.tabulate(NUMBER_OF_POTIONS)(_ => 1),
      battle,
      idToEffect,
      benchEffects,
      engine
    )
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
    benchEffects.position = (
      window.size.x * (1 - BENCH_WIDTH_RATIO) / 2f,
      window.size.y * (0.9f - BENCH_HEIGHT_RATIO) - 50
    )
    val switchButton = SwitchButton(shopWarrior, shopEffects, engine)
    engine.spawn(
      warriorBench,
      benchEffects,
      shopWarrior,
      shopEffects,
      switchButton
    )

  def switchPlayer() =
    if engine == engineP0 then engine = engineP1
    else engine = engineP0

  def startBattle() =
    battle.battleWarriors.foreach((warrior) => {
      warrior.engine = engineBattle
      engineBattle.spawn(warrior)
    })
    engine = engineBattle
    battle.active = true
    battleClock.restart()

  def createButtons(engine: GameEngine) =
    // Création d'un bouton Start
    val startButton = ButtonObject(
      "Start !",
      startBattle,
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
      switchPlayer,
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

  override def init() = {
    val basePlayer0 = RTSPBase(engineP0, battle, player0)
    engineP0.spawn(basePlayer0)
    engineP1.spawn(basePlayer0)
    engineBattle.spawn(basePlayer0)
    battle.addBase(
      basePlayer0,
      player0
    )
    val basePlayer1 = RTSPBase(engineP0, battle, player1)
    engineP0.spawn(basePlayer1)
    engineP1.spawn(basePlayer1)
    engineBattle.spawn(basePlayer1)
    battle.addBase(
      basePlayer1,
      player1
    )
    val background = engine2D.objects.SpriteObject("arena.png", engineBattle)
    background.fillDimensions(
      window.size.x.toFloat,
      window.size.y.toFloat
    )
    background.zIndex = -1
    engineBattle.spawn(background)
    initBenchesAndShops(engineP0, player0)
    initBenchesAndShops(engineP1, player1)
    createButtons(engineP0)
    createButtons(engineP1)
  }

  val timeLeft = ButtonObject(
    "30",
    () => (),
    engineBattle
  )
  timeLeft.position =
    (engine.window.size.x * 0.85f, engine.window.size.y * 0f)
  timeLeft.changeBackground(
    engineBattle.window.size.x * 0.15f,
    engineBattle.window.size.y * 0.1f
  )
  timeLeft.background.fillColor = Color(165, 245, 73, 80)
  timeLeft.background.outlineColor = Color(236, 151, 22)
  engineBattle.spawn(timeLeft)

  override def step() = {
    timeLeft.changeText(((Time.seconds(Constants.BATTLE_DURATION.toFloat) - battleClock.elapsedTime).asSeconds).round.toString())
    if engine == engineBattle then
      val ended = battle.step()
      if ended || battleClock.elapsedTime > Time.seconds(Constants.BATTLE_DURATION.toFloat) then {
        player0.earnMoney(
          2 * battle.enemies(player0.id).count(w => !w.active && !w.benched)
        )
        player1.earnMoney(
          2 * battle.enemies(player1.id).count(w => !w.active && !w.benched)
        )
        battle.reset()
        battle
          .getWarriors(0)
          .foreach((warrior) => {
            warrior.engine = engineP0
            engineBattle.removeGameObjects(warrior)
          })
        battle
          .getWarriors(1)
          .foreach((warrior) => {
            warrior.engine = engineP1
            engineBattle.removeGameObjects(warrior)
          })
        engine = engineP0
      }
    super.step()
  }
}
