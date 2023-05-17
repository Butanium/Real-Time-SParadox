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

class WarriorBench(
    engine: GameEngine,
    player: Player,
    battle: RTSPBattle,
    size: Int,
    sellingBin: SellingBin,
    base : RTSPBase
) extends GeneralBench(
      engine,
      player,
      battle,
      size,
      new Array[RTSPWarrior](size)
    ) {
  var width = engine.window.size.x
  var height = engine.window.size.y * 0.16f
  val rectangle = RectangleObject(width.toFloat, height.toFloat, engine)
  rectangle.fillColor = Color(165, 245, 73, 80)
  addChildren(rectangle)
  override def addBought(warrior: RTSPWarrior): Boolean = { // renvoie faux si le banc est plein (achat impossible)
    if !super.addBought(warrior) then return false
    warrior.benched = true
    battle.addWarriors(warrior)
    warrior.setOnRelease(() => {
      removeEntity(warrior); warrior.benched = false; // first, assume that the warrior is dropped on the battlefield
      if (sellingBin.rectangle.contains(warrior.position)) then // sell case
        sellingBin.sell(
          warrior
        )
      else if (
        rectangle.contains(warrior.position) || battle.warriorsInBattle(
          warrior.team
        ) > MAX_WARRIORS_IN_BATTLE || warrior.distanceTo(base) > WARRIOR_DROP_RADIUS
      ) // drop on bench / too many warriors in battle / too far from base cases
      then addDropped(warrior)
    })
    return true
  }

  override def addDropped(warrior: RTSPWarrior): Boolean = { // placer le warrior dans la bonne case de l'array quand il est déposé sur le banc
    // renvoie faux si le banc est plein (dépôt impossible) -> il faudra alors le replacer où il était sur le terrain
    warrior.benched = true
    super.addDropped(warrior)
  }
}
