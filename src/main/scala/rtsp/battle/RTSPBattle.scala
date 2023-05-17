package rtsp.battle
import rtsp.objects.RTSPWarrior
import scala.collection.mutable.SortedSet
import rtsp.objects.RTSPBase
import rtsp.Constants
import sfml.system.Vector2
import scala.collection.mutable.ListBuffer
import rtsp.RTSPGameEngine
import rtsp.objects.Arrow

class RTSPBattle(val engine: RTSPGameEngine, val debug: Boolean = false) {
  private var _active = false
  // Lancer la bataille: faire bouger les warriors non morts
  private val team0 = SortedSet.empty[RTSPWarrior]
  private val team1 = SortedSet.empty[RTSPWarrior]
  val _teams = Array[SortedSet[RTSPWarrior]](team0, team1)
  private def warriorsAndBases = team0 ++ team1 ++ bases
  val bases = Array[RTSPBase](null, null)
  def addBase(base: RTSPBase, player: Int): Unit = {
    bases(player) = base
  }
  def teams = _teams
  def enemies(team: Int) = _teams(1 - team)
  def warriorsInBattle(team: Int) = _teams(team).filter(w => !w.benched).size
  def battleWarriors = team0.toList ++ (team1.toList)
  def active = _active
  def active_=(newActive: Boolean): Unit =
    if (active != newActive) {
      if (newActive) {
        // On lance la bataille
        // todo: si on veut autoriser la réorganisation du banc pendant la bataille:
        // (gérer le cas où on les dépose dans la bataille: pas autorisé !)
        // battleWarriors.foreach(w => w.isGrabbable = w.benched)
        if debug then println("battle started")
        battleWarriors.foreach(w => {
          w.isGrabbable = false
          w.initialPosition = w.position
        })
      } else {
        warriorsAndBases.foreach(w => {
          w.currentAction = WarriorAction.Idle
          w.nextAction = WarriorAction.Idle
        })
        Arrow.disableAll()
      }
    }
    _active = newActive

  val losers = SortedSet[Int]()

  /** Ajoute un perdant à la liste des perdants
    * @param id
    *   l'id du joueur qui a perdu
    * @note
    *   Cette liste sera prise en compte par la fonction step. Cette fonction
    *   est appelée par les bases des équipes si elles sont détruites.
    */
  def addLoser(id: Int): Unit = losers += id
  private var timeOut = -1
  def reset(): Unit = {
    if debug then println("reset battle")
    battleWarriors.foreach(w => {
      w.reset(); w.isGrabbable = true
    })
    active = false
    losers.clear()
    timeOut = -1
  }

  def step(): Boolean = {
    // On effectue une étape de combat, et on renvoie la liste de perdants à chaque étape (dès qu'elle n'est plus vide, le combat est terminé)
    if (active) then {
      warriorsAndBases
        .filter(w => w.active && !w.benched)
        .foreach(w => w.behavior.evaluate(w))
    }
    // On vérifie si plus personne n'a de warriors actifs
    teams.forall(team => team.forall(w => w.isDead || w.benched)) || losers.nonEmpty
  }

  /** Renvoie la liste des warriors actifs d'une équipe
    * @param idTeam
    *   l'id de l'équipe
    * @return
    *   la liste des warriors actifs de l'équipe
    */
  def getWarriors(idTeam: Int): List[RTSPWarrior] = {
    teams(idTeam).toList.filter(w => w.active && !w.benched)
  }

  def addWarriors(warriors: RTSPWarrior*): Unit = {
    warriors.foreach(w => teams(w.team) += w)
  }

}
