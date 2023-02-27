package engine.interface

import sfml.window.Keyboard.Key

class KeyboardListener(val keyboardManager: KeyboardManager) {
  private var conditions: Map[Key, KeyState] = Map()
  def addCondition(key: Key, condition: KeyState): Unit =
    conditions += (key -> condition)
  def removeCondition(key: Key): Unit =
    conditions -= key

  def triggered: Boolean =
    conditions.forall((key, condition) => keyboardManager.check(key, condition))

}
