package engine2D.graphics

/** A TextureManager is a singleton that manages the loading of textures.
  * Textures are loaded from the resources folder. If a texture is already
  * loaded, it will not be loaded again. Instead, the already loaded texture
  * will be returned.
  * @note
  *   It is recommended to use the texture manager to load textures. It will
  *   prevent loading the same texture twice, which can be a performance issue.
  */
object TextureManager
    extends GameResourceManager[sfml.graphics.Texture](
      createResource = path => {
        val texture = sfml.graphics.Texture()
        texture.loadFromFile(path)
        texture
      }
    ) {

  /** Get a texture from the resources folder.
    * @param path
    *   The path to the texture, relative to the resources folder.
    * @note
    *   If the texture is already loaded, it will not be loaded again. Instead,
    *   the already loaded texture will be returned.
    */
  def getTexture(path: String): sfml.graphics.Texture = getResource(path)
}
