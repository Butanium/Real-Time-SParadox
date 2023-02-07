package engine2D.graphics

/** A TextureManager is a singleton that manages the loading of textures.
  * Textures are loaded from the resources folder. If a texture is already
  * loaded, it will not be loaded again. Instead, the already loaded texture
  * will be returned.
  * @note
  *   It is recommended to use the texture manager to load textures. It will
  *   prevent loading the same texture twice, which can be a performance issue.
  */
object TextureManager {
  private val textures =
    scala.collection.mutable.Map[String, sfml.graphics.Texture]()
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
