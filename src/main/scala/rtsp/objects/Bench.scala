package rtsp.objects
import rtsp.objects.RTSPWarrior
import rtsp.Constants.*
import engine2D.objects.GameObject
import engine2D.GameEngine
import engine2D.objects.RectangleObject
import sfml.graphics.Color
// Faire un banc qui recevra les warriors achetés. Ils pourront ensuite être placés sur le terrain.
// addBoughtWarrior pour ajouter un warrior acheté au banc
// addDroppedWarrior pour ajouter un warrior qu'on dépose sur le banc depuis le terrain

// todo soon: Si je drop dans un endroit déjà pris ou un endroit illégal, remettre à sa place
//      => Dans RTSPWarrior, stocker la position où il est grabbed => utiliser setOnGrab

class Bench(engine: GameEngine) extends GameObject(engine) {
    var benchArray = new Array[RTSPWarrior](BENCH_SIZE)
    var takenSlots = 0
    def addBoughtWarrior(warrior: RTSPWarrior) : Boolean = { // renvoie faux si le banc est plein (achat impossible)
        var i = 0
        while (benchArray(i) != null) do {
            i += 1
            if (i == BENCH_SIZE) then return false
        }
        benchArray(i) = warrior
        warrior.setOnRelease(() => if (rectangle.contains(warrior.position)) then addDroppedWarrior(warrior, warrior.position.x))
        return true
    }
    def addDroppedWarrior(warrior: RTSPWarrior, x: Float) : Boolean = { // placer le warrior dans la bonne case de l'array quand il est déposé sur le banc
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
        return true
    }
    def removeWarrior(position : Int) : Unit = { // Il faudra calculer la position du warrior dans l'array à partir de sa position sur le banc
        benchArray(position) = null
    }

    val rectangle = RectangleObject(engine.window.size.x, engine.window.size.y / 4, engine)
    rectangle.fillColor = Color(165, 245, 73, 20)
    addChildren(rectangle)
}
