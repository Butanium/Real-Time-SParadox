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

class WarriorBench(engine: GameEngine, player: Player, battle: RTSPBattle, size: Int)
        extends GeneralBench(engine, player, battle, size, new Array[RTSPWarrior](size), "warrior") {
    override def addBought(warrior: RTSPWarrior): Boolean = { // renvoie faux si le banc est plein (achat impossible)
        var i = 0
        while (benchArray(i) != null) do {
            i += 1
            if (i == size) then return false
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
                if (i == size) then return false
            }
            benchArray(i) = warrior
        }
        takenSlots += 1
        warrior.benched = true
        warrior.position = positionOfIndex(i)
        return true
    }
    def removeWarrior(warrior: RTSPWarrior): Unit = {
        var i = 0
        while (i < size) do {
        if (benchArray(i) == warrior) then
            benchArray(i) = null
            takenSlots -= 1
        i += 1
        }
    }
}
