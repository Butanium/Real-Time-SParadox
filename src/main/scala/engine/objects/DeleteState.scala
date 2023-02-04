package engine.objects

/** The state of a GameObject's deletion.
  * @param Deleted
  *   The GameObject should be deleted.
  * @param ToDelete
  *   The GameObject should be deleted at the end of the frame.
  * @param Nope
  *   The GameObject should not be deleted.
  */
enum DeleteState:
  case Deleted, ToDelete, Nope