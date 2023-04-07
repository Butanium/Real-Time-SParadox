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

abstract class GeneralBench[T](engine: GameEngine, player: Player, battle: RTSPBattle, size: Int, array: Array[T], benchType: String)
        extends GameObject(engine) {
    var x = 0
    var y = 0
    val benchArray = array
    if (benchType == "warrior") then
        x = engine.window.size.x
        y = engine.window.size.y/16
    else if (benchType == "effect") then
        var benchArray = new Array[Effect](size)
        x = engine.window.size.x
        y = engine.window.size.y/19
    else
        throw new Exception("Invalid bench type, must be warrior or effect")
    var takenSlots = 0
    def addBought(entity: T): Boolean
    def removeEntity(entity: T): Unit = {
        var i = 0
        while (i < size) do {
        if (benchArray(i) == entity) then
            benchArray(i) = null.asInstanceOf[T]
            takenSlots -= 1
        i += 1
        }
    }
    def positionOfIndex(index: Int): Vector2[Float] = {
        val x = (index + 0.5f) * engine.window.size.x / BENCH_SIZE + position.x
        val y = position.y + 5
        return Vector2[Float](x, y)
    }
    def isNotFull: Boolean = takenSlots < BENCH_SIZE
    val rectangle = RectangleObject(x, y, engine)
    if (benchType == "warrior") then
        rectangle.fillColor = Color(165, 245, 73, 50)
    else if (benchType == "effect") then
        rectangle.fillColor = Color(165, 73, 245, 50)
    addChildren(rectangle)
}
