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

abstract class GeneralBench[T <: GameObject](
    engine: GameEngine,
    player: Player,
    battle: RTSPBattle,
    size: Int,
    array: Array[T],
    benchType: String // todo remove
) extends GameObject(engine) {
  var x = 0
  var y = 0
  val benchArray = array
  /*
  if (benchType == "warrior") then // todo remove and do this in the RTSPShopGame code instead
    x = engine.window.size.x
    y = engine.window.size.y / 16
  else if (benchType == "effect") then
    var benchArray = new Array[Effect](size)
    x = engine.window.size.x
    y = engine.window.size.y / 19
  else throw new Exception("Invalid bench type, must be warrior or effect")
  */
  var takenSlots = 0

  def addBought(entity: T): Boolean =
    var i = benchArray.indexWhere(_ == null)
    if i == -1 then return false
    benchArray(i) = entity
    takenSlots += 1
    entity.position = positionOfIndex(i)
    engine.spawn(entity)
    return true

  def removeEntity(entity: T): Unit = {
    benchArray.mapInPlace(e =>
      if (e == entity) then { takenSlots -= 1; null.asInstanceOf[T] }
      else e
    )
  }
  def positionOfIndex(index: Int): Vector2[Float] = {
    val x = (index + 0.5f) * engine.window.size.x / BENCH_SIZE + position.x
    val y = position.y + 5
    return Vector2[Float](x, y)
  }
  def isNotFull: Boolean = takenSlots < BENCH_SIZE
  val rectangle = RectangleObject(x.toFloat, y.toFloat, engine)
  if (benchType == "warrior") then rectangle.fillColor = Color(165, 245, 73, 80)
  else if (benchType == "effect") then
    rectangle.fillColor = Color(165, 73, 245, 50)
  addChildren(rectangle)

  def addDropped(entity: T): Boolean = {
    // placer l'effect dans la bonne case de l'array quand il est déposé sur le banc
    // renvoie faux si le banc est plein (dépôt impossible) -> il faudra alors le replacer où il était sur le terrain
    // TODO: fix toujours drop tout à gauche (pour l'instant on utilise super.addBought) mais plus tard
    // il faudra choisir l'emplacement en fonction de entity.x
    addBought(entity)
  }
}
