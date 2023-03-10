package rtsp.objects
import rtsp.objects.RTSPWarrior
// Classe contenant différentes fonctions de buffs pour un warrior
class Buffs {
    def attackBuff(warrior: RTSPWarrior): Unit = {
        warrior.attackDamage += 10
    }
    def speedBuff(warrior: RTSPWarrior): Unit = {
        warrior.speed += 10
    }
    def tankBuff(warrior: RTSPWarrior): Unit = {
        warrior.maxHP += 500
    }
    def applyBuff(warrior: RTSPWarrior, buffFunction: RTSPWarrior => Unit): Unit = {
        buffFunction(warrior)
    }
}
