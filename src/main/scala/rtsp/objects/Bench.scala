package rtsp.objects
import rtsp.objects.RTSPWarrior
import rtsp.Constants.*
import engine2D.objects.GameObject
import engine2D.GameEngine
import engine2D.objects.RectangleObject
import sfml.graphics.Color
import sfml.system.Vector2
import rtsp.Player
import rtsp.battle.RTSPBattle
// Faire un banc qui recevra les warriors achetés. Ils pourront ensuite être placés sur le terrain.
// addBoughtWarrior pour ajouter un warrior acheté au banc
// addDroppedWarrior pour ajouter un warrior qu'on dépose sur le banc depuis le terrain

// todo soon: Si je drop dans un endroit déjà pris ou un endroit illégal, remettre à sa place
//      => Dans RTSPWarrior, stocker la position où il est grabbed => utiliser setOnGrab

// todo fix: il faut pouvoir mettre un warrior où on veut sur le banc, pas forcément le plus à gauche ( c'est ce qui se passe automatiquement atm)

class Bench(engine: GameEngine, player: Player, battle: RTSPBattle)
    extends GameObject(engine) {
  var benchArray = new Array[RTSPWarrior](BENCH_SIZE)
  var takenSlots = 0
  def positionOfIndex(index: Int): Vector2[Float] = {
    val x = (index + 0.5f) * engine.window.size.x / BENCH_SIZE
    val y = position.y + 5
    return Vector2[Float](x, y)
  }
  def isNotFull: Boolean = takenSlots < BENCH_SIZE
  def addBoughtWarrior(shopWarrior: ShopWarrior): Boolean = { // renvoie faux si le banc est plein (achat impossible)
    var i = 0
    val warrior = shopWarrior.convertToWarrior(battle)
    while (benchArray(i) != null) do {
      i += 1
      if (i == BENCH_SIZE) then return false
    }
    benchArray(i) = warrior
    warrior.benched = true
    battle.addWarriors(warrior)
    warrior.setOnRelease(() => {
      removeWarrior(warrior); warrior.benched = false;

      if (rectangle.contains(warrior.position)) then
        addDroppedWarrior(warrior, warrior.position.x)
    })
    engine.spawn(warrior)
    warrior.position = positionOfIndex(i)
    takenSlots += 1
    return true
  }
  def addDroppedWarrior(warrior: RTSPWarrior, x: Float): Boolean = { // placer le warrior dans la bonne case de l'array quand il est déposé sur le banc
    // renvoie faux si le banc est plein (dépôt impossible) -> il faudra alors le replacer où il était sur le terrain
    var i = (x / engine.window.size.x).floor.toInt
    if (benchArray(i) == null) then benchArray(i) = warrior
    else {
      while (benchArray(i) != null) do {
        i += 1
        if (i == BENCH_SIZE) then return false
      }
      benchArray(i) = warrior
    }
    takenSlots += 1
    warrior.benched = true
    warrior.position = positionOfIndex(i)
    return true
  }
  def removeWarrior(position: Int): Unit = {
    benchArray(position) = null
    takenSlots -= 1
  }
  def removeWarrior(warrior: RTSPWarrior): Unit = {
    var i = 0
    while (i < BENCH_SIZE) do {
      if (benchArray(i) == warrior) then
        benchArray(i) = null
        takenSlots -= 1
      i += 1
    }
  }
  val rectangle =
    RectangleObject(
      engine.window.size.x.toFloat,
      engine.window.size.y.toFloat / 16f,
      engine
    )
  rectangle.fillColor = Color(165, 245, 73, 50)
  addChildren(rectangle)
}
