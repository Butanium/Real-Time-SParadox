package engine.interface

/** Represents the state of a key
  */
enum KeyState:
  /** Fire once when key is pressed
    */
  case Down

  /** Keep firing while key is pressed
    */
  case Pressed

  /** Fire once when key is released
    */
  case Up

  /** Keep firing while key is released
    */
  case Released

