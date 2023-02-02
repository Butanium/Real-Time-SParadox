package engine.settings

import sfml.graphics.Drawable
import sfml.internal.window.Event

class Settings(val path: String = "src/main/settings/settings.json") {
  private val paramString = io.Source.fromFile(path).mkString
  private val paramJson = ujson.read(paramString)
  val keyBinding = KeyBinding(paramJson("keys"))
}
