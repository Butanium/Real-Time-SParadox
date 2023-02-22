package demo

import engine2D.objects.Grabbable
import engine2D.GameEngine
import sfml.window.Mouse
import sfml.system.Vector2
import engine2D.objects.GameObject
import engine2D.eventHandling.MouseEvent.*
import engine2D.objects.SpriteObject
import engine2D.graphics.TextureManager

class GrabableDemoUnit(engine: GameEngine, debug: Boolean = true)
    extends SpriteObject(TextureManager.getTexture("aircraft.png"), engine)
    with Grabbable(Mouse.Button.Left, engine, debug) {
  // this.baseRotation = 45
  setOriginToCenter(localBounds)
}
