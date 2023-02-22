package engine2D.graphics

/** A GameResourceManager is a singleton that manages the loading of resources.
  * Resources are loaded from the resources folder. If a resource is already
  * loaded, it will not be loaded again. Instead, the already loaded resource
  * will be returned.
  * @param createResource
  *   A function that creates a resource from a path.
  * @param subfolderPath
  *   The path to the subfolder where the resources are located, relative to the
  *   resources folder.
  * @note
  *   It is recommended to use the resource manager to load resources. It will
  *   prevent loading the same resource twice, which can be a performance issue.
  */
abstract class GameResourceManager[ResourceType](
    createResource: String => ResourceType,
    subfolderPath: String = ""
) {
  private val resources =
    scala.collection.mutable.Map[String, ResourceType]()

  /** Get a resource from the resources folder.
    * @param path
    *   The path to the resource, relative to the resources folder.
    * @note
    *   If the resource is already loaded, it will not be loaded again. Instead,
    *   the already loaded resource will be returned.
    */
  protected def getResource(path: String): ResourceType = {
    if (resources.contains(path)) {
      resources(path)
    } else {
      val resource = createResource(
        "src/main/resources/" + subfolderPath + path
      )
      resources(path) = resource
      resource
    }
  }
}
