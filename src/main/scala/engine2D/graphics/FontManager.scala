package engine2D.graphics

/** A FontManager is a singleton that manages the loading of fonts. Fonts are
  * loaded from the resources folder. If a font is already loaded, it will not
  * be loaded again. Instead, the already loaded font will be returned.
  * @note
  *   It is recommended to use the font manager to load fonts. It will prevent
  *   loading the same font twice, which can be a performance issue.
  */
object FontManager
    extends GameResourceManager[sfml.graphics.Font](
      createResource = path => {
        val font = sfml.graphics.Font()
        font.loadFromFile(path)
        font
      },
      subfolderPath = "fonts/"
    ) {

  /** Get a font from the resources folder.
    * @param path
    *   The path to the font, relative to the resources/fonts folder.
    * @note
    *   If the font is already loaded, it will not be loaded again. Instead, the
    *   already loaded font will be returned.
    */
  def getFont(path: String): sfml.graphics.Font = getResource(path)
}
