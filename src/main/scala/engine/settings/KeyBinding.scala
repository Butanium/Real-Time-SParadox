package engine.settings
import scala.collection.mutable.LinkedHashMap
import sfml.window.Keyboard.Key
class KeyBinding(val keyBinding: ujson.Value) {
  val moveUp = KeyBinding.stringToKey(keyBinding("move")("up").str)
  val moveDown = KeyBinding.stringToKey(keyBinding("move")("down").str)
  val moveLeft = KeyBinding.stringToKey(keyBinding("move")("left").str)
  val moveRight = KeyBinding.stringToKey(keyBinding("move")("right").str)
}

private object KeyBinding {
  val default = new KeyBinding(ujson.read("""{
        "move": {
            "up": "z",
            "down": "s",
            "left": "q",
            "right": "d"
        }
    }"""))

  val stringToKeyMap = Map(
    "up" -> Key.KeyUp,
    "down" -> Key.KeyDown,
    "left" -> Key.KeyLeft,
    "right" -> Key.KeyRight,
    "a" -> Key.KeyA,
    "b" -> Key.KeyB,
    "c" -> Key.KeyC,
    "d" -> Key.KeyD,
    "e" -> Key.KeyE,
    "f" -> Key.KeyF,
    "g" -> Key.KeyG,
    "h" -> Key.KeyH,
    "i" -> Key.KeyI,
    "j" -> Key.KeyJ,
    "k" -> Key.KeyK,
    "l" -> Key.KeyL,
    "m" -> Key.KeyM,
    "n" -> Key.KeyN,
    "o" -> Key.KeyO,
    "p" -> Key.KeyP,
    "q" -> Key.KeyQ,
    "r" -> Key.KeyR,
    "s" -> Key.KeyS,
    "t" -> Key.KeyT,
    "u" -> Key.KeyU,
    "v" -> Key.KeyV,
    "w" -> Key.KeyW,
    "x" -> Key.KeyX,
    "y" -> Key.KeyY,
    "z" -> Key.KeyZ,
    "num0" -> Key.KeyNum0,
    "num1" -> Key.KeyNum1,
    "num2" -> Key.KeyNum2,
    "num3" -> Key.KeyNum3,
    "num4" -> Key.KeyNum4,
    "num5" -> Key.KeyNum5,
    "num6" -> Key.KeyNum6,
    "num7" -> Key.KeyNum7,
    "num8" -> Key.KeyNum8,
    "num9" -> Key.KeyNum9,
    "escape" -> Key.KeyEscape,
    "lcontrol" -> Key.KeyLControl,
    "lshift" -> Key.KeyLShift,
    "lalt" -> Key.KeyLAlt,
    "lsystem" -> Key.KeyLSystem,
    "rcontrol" -> Key.KeyRControl,
    "rshift" -> Key.KeyRShift,
    "ralt" -> Key.KeyRAlt,
    "rsystem" -> Key.KeyRSystem,
    "menu" -> Key.KeyMenu,
    "lbracket" -> Key.KeyLBracket,
    "rbracket" -> Key.KeyRBracket,
    "semicolon" -> Key.KeySemicolon,
    "comma" -> Key.KeyComma,
    "period" -> Key.KeyPeriod,
    "quote" -> Key.KeyQuote,
    "slash" -> Key.KeySlash,
    "backslash" -> Key.KeyBackslash,
    "tilde" -> Key.KeyTilde,
    "equal" -> Key.KeyEqual,
    "space" -> Key.KeySpace,
    "enter" -> Key.KeyEnter,
    "delete" -> Key.KeyDelete,
    "tab" -> Key.KeyTab,
    "pageup" -> Key.KeyPageUp,
    "pagedown" -> Key.KeyPageDown,
    "end" -> Key.KeyEnd,
    "home" -> Key.KeyHome,
    "insert" -> Key.KeyInsert,
    "delete" -> Key.KeyDelete,
    "add" -> Key.KeyAdd,
    "subtract" -> Key.KeySubtract,
    "multiply" -> Key.KeyMultiply,
    "divide" -> Key.KeyDivide,
    "left" -> Key.KeyLeft,
    "right" -> Key.KeyRight,
    "up" -> Key.KeyUp,
    "down" -> Key.KeyDown,
    "numpad0" -> Key.KeyNumpad0,
    "numpad1" -> Key.KeyNumpad1,
    "numpad2" -> Key.KeyNumpad2,
    "numpad3" -> Key.KeyNumpad3,
    "numpad4" -> Key.KeyNumpad4,
    "numpad5" -> Key.KeyNumpad5,
    "numpad6" -> Key.KeyNumpad6,
    "numpad7" -> Key.KeyNumpad7,
    "numpad8" -> Key.KeyNumpad8,
    "numpad9" -> Key.KeyNumpad9,
    "f1" -> Key.KeyF1,
    "f2" -> Key.KeyF2,
    "f3" -> Key.KeyF3,
    "f4" -> Key.KeyF4,
    "f5" -> Key.KeyF5,
    "f6" -> Key.KeyF6,
    "f7" -> Key.KeyF7,
    "f8" -> Key.KeyF8,
    "f9" -> Key.KeyF9,
    "f10" -> Key.KeyF10,
    "f11" -> Key.KeyF11,
    "f12" -> Key.KeyF12,
    "f13" -> Key.KeyF13,
    "f14" -> Key.KeyF14,
    "f15" -> Key.KeyF15,
    "pause" -> Key.KeyPause
  )
  def stringToKey(string: String): Key = stringToKeyMap(string.toLowerCase)

  val keyToStringMap = stringToKeyMap.map(_.swap)

  def keyToString(key: Key): String = keyToStringMap(key)

}
