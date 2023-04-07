package rtsp.objects

import engine2D.GameEngine
import rtsp.Player
import rtsp.battle.RTSPBattle

class EffectBench(engine: GameEngine, player: Player, battle: RTSPBattle, size: Int)
        extends GeneralBench[Effect](engine, player, battle, size, new Array[Effect](size), "effect") {
    def addEffect(effect: Effect): Boolean = {
        var i = 0
        while (benchArray(i) != null) do {
            i += 1
            if (i == size) then return false
        }
        benchArray(i) = effect
        effect.setOnRelease(() => {
            removeEffect(effect)
            if (rectangle.contains(effect.position)) then
                addDroppedEffect(effect, effect.position.x)
            else {
                battle.teams(player.id).find(warrior => effect.distanceTo(warrior) <= 5) match {
                    case Some(warrior) => effect.apply(warrior)
                    case None => effect.position = effect.grabLocation; addEffect(effect)
                }
            }
        }
        )
        engine.spawn(effect)
        takenSlots += 1
        effect.position = positionOfIndex(i)
        return true    
    }

    def removeEffect(effect: Effect): Unit = {
        var i = 0
        while (i < size) do {
            if (benchArray(i) == effect) then
                benchArray(i) = null
                takenSlots -= 1
            i += 1
        }
    }


    def addDroppedEffect(effect: Effect, x: Float): Boolean = { // placer l'effect dans la bonne case de l'array quand il est déposé sur le banc
        // renvoie faux si le banc est plein (dépôt impossible) -> il faudra alors le replacer où il était sur le terrain
        var i = (x / engine.window.size.x).floor.toInt
        if (benchArray(i) == null) then benchArray(i) = effect
        else {
            while (benchArray(i) != null) do {
                i += 1
                if (i == size) then return false
            }
            benchArray(i) = effect
        }
        takenSlots += 1
        effect.position = positionOfIndex(i)
        return true
    }
}