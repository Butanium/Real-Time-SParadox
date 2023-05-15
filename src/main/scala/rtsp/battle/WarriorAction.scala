package rtsp.battle
import rtsp.objects.RTSPWarrior
import engine2D.objects.GameTransform

enum WarriorAction {
  case Attack(val target: RTSPWarrior)
  case Move(val target: RTSPWarrior)
  case Flee(val target: RTSPWarrior)
  case Idle
}