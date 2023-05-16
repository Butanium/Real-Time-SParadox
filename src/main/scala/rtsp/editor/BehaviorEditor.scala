package rtsp.editor

import rtsp.battle.Behavior
import rtsp.objects.RTSPWarrior
import engine2D.objects.ButtonObject
import org.w3c.dom.Node

class BehaviorEditor(engine: rtsp.RTSPGameEngine) extends Menu(None, engine)
{
  val saveButton = ButtonObject("Save", saveBehavior, engine)
  add(saveButton)
  saveButton.position = (engine.window.size.x - saveButton.width, 0f)
  var currentWarrior : RTSPWarrior = null
  var root : NodeObject = null
  def open(warrior : RTSPWarrior) = {
    currentWarrior = warrior
    active = true
    root = NodeObject.fromBehavior(currentWarrior.behavior.tree, this, engine)
    root.nodeType = NodeType.Root
  }
  def saveBehavior() = {
  
  }
  onClose = () => { 

  }
}
