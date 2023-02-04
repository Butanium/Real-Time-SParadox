package engine

import scala.collection.mutable.ListBuffer
import engine.objects.GameObject
import sfml.graphics.RenderWindow

class GameEngine() {
  val gameInfo: GameInfo = new GameInfo
  val gameObjects: ListBuffer[GameObject] = ListBuffer.empty[GameObject]
  def step(delta: Float) =
    gameObjects.foreach(_.update())
    gameObjects.filter(x =>
      if !x.active then { x.delete(); false }
      else true
    )

  def render(window: RenderWindow) =
    gameObjects.foreach(window.draw(_))
}
