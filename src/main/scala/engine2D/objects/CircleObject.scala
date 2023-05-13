package engine2D.objects

class CircleObject(
    radius: Float,
    engine: engine2D.GameEngine
) extends ShapeObject(
      sfml.graphics.CircleShape(radius),
      engine
    )
    with Boundable {
  val circle = drawable.asInstanceOf[sfml.graphics.CircleShape]
  def contains(point: sfml.system.Vector2[Float]): Boolean =
    if scale.x == scale.y then
      // Use the distance formula to check if the point is within the circle.
      val dx = point.x - globalPosition.x
      val dy = point.y - globalPosition.y
      print(Math.sqrt(dx * dx + dy * dy).round, radius, scale.x, "\n")
      Math.sqrt(dx * dx + dy * dy) <= radius * scale.x
    else
      // If the circle is scaled differently in the x and y directions, then
      // we use the bounding box of the circle to check if the point is within
      // the circle.
      globalTransform
        .transformRect(
          sfml.graphics.Rect[Float](0, 0, radius, radius)
        )
        .contains(point)
}
