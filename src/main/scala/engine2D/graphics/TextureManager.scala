package engine2D.graphics

class TextureManager {
  private val textures = scala.collection.mutable.Map[String, sfml.graphics.Texture]()
  def getTexture(path: String): sfml.graphics.Texture = {
    if (textures.contains(path)) {
      textures(path)
    } else {
      val texture = sfml.graphics.Texture()
      texture.loadFromFile("src/main/resources/" + path)
      textures(path) = texture
      texture
    }
  }
}
