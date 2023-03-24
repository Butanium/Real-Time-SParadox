package rtsp.battle
import rtsp.objects.RTSPWarrior
import scala.collection.mutable.ListBuffer
import rtsp.objects.RTSPBase
import rtsp.Constants
import sfml.system.Vector2

/*
    Il faut stocker les équipes dans une liste de liste de warriors: List[List[RTSPWarriors]]
 */

class RTSPBattle(player: rtsp.Player, val debug: Boolean = false) {
  private var _active = false
  // Lancer la bataille: faire bouger les warriors non morts
  private val team0 = ListBuffer[RTSPWarrior]()
  private val team1 = ListBuffer[RTSPWarrior]()
  private val _teams = Array[ListBuffer[RTSPWarrior]](team0, team1)
  private def warriors = team0.toList ++ team1.toList
  val bases = Array[RTSPBase](null, null)
  def addBase(base: RTSPBase, player: Int): Unit = {
    bases(player) = base
    _teams(player) += base
    val bounds = Constants.BattleC.ARENA_BOUNDS
    base.position = Vector2(bounds.width, bounds.height) * (1 - player).toFloat

  }
  def teams = _teams
  def enemies(team: Int) = _teams(1 - team)
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
        battleWarriors.foreach(w => w.action = WarriorAction.Idle)
      }
    }
    _active = newActive

  private val losers = ListBuffer[Int]()

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
      w.reset(); w.isGrabbable = w.team == player.id
    })
    active = false
    losers.clear()
    timeOut = -1
  }

  def step(): List[Int] = {
    // On effectue une étape de combat, et on renvoie la liste de perdants à chaque étape (dès qu'elle n'est plus vide, le combat est terminé)
    var allDeadTeams = ListBuffer[Int]()
    if (active) then {
      allDeadTeams = teams.indices
        .filter(i => teams(i).forall(w => w.health <= 0 || w.benched))
        .to(ListBuffer)
      warriors
        .filter(w => w.active && !w.benched)
        .foreach(w => w.behavior.evaluate(w))
    }
    if allDeadTeams.length == teams.length then {
      allDeadTeams
    } else if allDeadTeams.nonEmpty then {
      timeOut
    }
    if allDeadTeams.length == teams.length then {
      allDeadTeams
    } else if allDeadTeams.nonEmpty then {
      timeOut
    }
    losers.toList

  }
  // fonction alliés / ennemis
  def getEnemies(idTeam: Int): List[RTSPWarrior] = {
    teams(1 - idTeam).toList.filter(w => w.active && !w.benched)
  }

  def getAllies(idTeam: Int): List[RTSPWarrior] = {
    teams(idTeam).toList.filter(w => w.active && !w.benched)
  }

  def addWarriors(warriors: RTSPWarrior*): Unit = {
    warriors.foreach(w => teams(w.team) += w)
  }

}
