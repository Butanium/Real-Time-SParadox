package rtsp.objects
import engine2D.objects.GameUnit
import rtsp.RTSPGameEngine
import rtsp.battle.RTSPBattle
import rtsp.battle.Behavior

/*
  Utiliser l'arbre de comportement
  Chaque warrior a une équipe: 0 = joueur, 1 = ennemi
*/

class RTSPWarrior (engine: RTSPGameEngine, battle: RTSPBattle, var team: Int, var range : Int, var damage : Int, var behavior : Behavior)
  extends GameUnit(100, 1f, engine, baseRotation = 0, active = true) {
    var target : Option[RTSPWarrior] = None
    def attack() : Unit = {
      
    }
}
