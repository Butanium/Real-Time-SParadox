package engine2D.objects

/** The state of a GameObject's deletion. This is used to avoid concurrent
  * modification exceptions when deleting objects
  */
enum DeleteState:
  /** The GameObject is deleted. */
  case Deleted

  /** The GameObject should be deleted at the end of the frame. */
  case ToDelete

  /** The GameObject should not be deleted. */
  case Nope
