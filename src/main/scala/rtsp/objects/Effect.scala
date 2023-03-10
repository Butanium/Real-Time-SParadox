package rtsp.objects
import rtsp.objects.RTSPWarrior

abstract class Effect {
  def apply(warrior: RTSPWarrior): Unit
}
class AttackBuff(val amount : Int) extends Effect {
override def apply(warrior: RTSPWarrior): Unit = {
    warrior.attackDamage += 10
}
}
class SpeedBuff(val amount : Int) extends Effect {
override def apply(warrior: RTSPWarrior): Unit = {
    warrior.speed += 10
}
}
class TankBuff(val amount : Int) extends Effect  {
override def apply(warrior: RTSPWarrior): Unit = {
    warrior.maxHP += 500
}
}

