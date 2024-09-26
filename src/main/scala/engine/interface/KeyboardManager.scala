package engine.interface

import sfml.window.Keyboard.Key
import KeyState._
import sfml.window.Event
import collection.mutable.Map

class KeyboardManager(window: sfml.window.Window) {
  val keys: Map[Key, KeyState] = Map()
  def get_state(key: Key): KeyState =
    keys.getOrElse(key, Released)

  def check(key: Key, condition: KeyState): Boolean =
    condition == get_state(key)

        
  def update(keyEvents: Seq[Event]): Unit =
    keyEvents.foreach {
      case Event.KeyPressed (key, alt, ctrl, shift, sys) => 
        if keys.getOrElse(key, Released) != Released then
          keys(key) = Pressed
        else
          keys(key) = Down
        
      case Event.KeyReleased(key, alt, ctrl, shift, sys) =>
      case _ =>
    }

}
