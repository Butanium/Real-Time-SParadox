package rtsp.objects

import engine2D.GameEngine
import rtsp.Player
import rtsp.battle.RTSPBattle
import engine2D.objects.RectangleObject
import sfml.graphics.Color

class EffectBench(
    engine: GameEngine,
    player: Player,
    battle: RTSPBattle,
    size: Int,
    sellingBin: SellingBin
) extends GeneralBench[Effect](
      engine,
      player,
      battle,
      size,
      new Array[Effect](size),
      "effect"
    ) {
  var x = engine.window.size.x
  var y = engine.window.size.y * 0.19f
  val rectangle = RectangleObject(x.toFloat, y.toFloat, engine)
  rectangle.fillColor = Color(165, 73, 245, 50)
  addChildren(rectangle)
  override def addBought(effect: Effect): Boolean = {
    if !super.addBought(effect) then return false
    effect.setOnRelease(() => {
      removeEntity(effect)
      if (sellingBin.rectangle.contains(effect.position)) then
        sellingBin.sell(effect)
      else if (rectangle.contains(effect.position)) then addDropped(effect)
      else {
        battle
          .teams(player.id)
          .find(warrior => warrior.contains(effect.position)) match {
          case Some(warrior) => effect.apply(warrior)
          case None => effect.position = effect.grabLocation; addBought(effect)
        }
      }
    })
    return true
  }
}
