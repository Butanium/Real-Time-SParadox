package rtsp.editor

import rtsp.battle.Behavior
import rtsp.objects.RTSPWarrior
import engine2D.objects.ButtonObject
import engine2D.objects.GameObject

class BehaviorEditor(engine: rtsp.RTSPGameEngine) extends GameObject(engine) {
  val menu = Menu(None, engine)
  engine.nodeCreationMenu.nodeTypeMenu.uiParent = Some(menu)
  add(menu)
  val saveButton = ButtonObject("Save", saveBehavior, engine)
  menu.addChildren(saveButton)
  saveButton.position = (engine.window.size.x - saveButton.width, 0f)
  saveButton.zIndex = 10
  var currentWarrior: RTSPWarrior = null
  var root: NodeObject = null

  def open(warrior: RTSPWarrior) = {
    currentWarrior = warrior
    active = true
    menu.open()
    root = NodeObject.fromBehavior(currentWarrior.behavior.tree, this, engine, isRoot = true)
    root.nodeType = NodeType.Root
  }
  def saveBehavior() = {
    println("Saving Behavior")
    try currentWarrior.behavior.tree = root.toBehaviorTree
    // catch only Cyclic Dependency Exception
    catch
      case e: CyclicDependencyException =>
        println("Could not save behavior: Cyclic Dependency")
      case e: Exception => throw e
  }
  menu.onClose = () => {
    destroyChildren(NodeObject.nodeList.toList: _*)
    NodeObject.nodeList.clear()
    active = false
  }
}