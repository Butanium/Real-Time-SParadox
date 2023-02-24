package rtsp.battle
import rtsp.objects.RTSPWarrior
import scala.collection.mutable.ListBuffer

/*
    Il faut stocker les équipes dans une liste de liste de warriors: List[List[RTSPWarriors]]
 */

class RTSPBattle(player: rtsp.Player, val debug: Boolean = false) {
  private var _active = false
  // Lancer la bataille: faire bouger les warriors non morts
  private val team0 = ListBuffer[RTSPWarrior]()
  private val team1 = ListBuffer[RTSPWarrior]()
  private val _teams = Array[ListBuffer[RTSPWarrior]](team0, team1)
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

  def reset(): Unit = {
    if debug then println("reset battle")

    battleWarriors.foreach(w => {
      w.reset(); w.isGrabbable = w.team == player.id
    })
    active = false
  }

  def step(): List[Int] = {
    // On effectue une étape de combat, et on renvoie la liste de perdants à chaque étape (dès qu'elle n'est plus vide, le combat est terminé)
    var losers = ListBuffer[Int]()
    if (active) then {
      for (i <- 0 to teams.size - 1) {
        var dead = true
        val team = teams(i)
        for (warrior <- team) {
          if (warrior.health > 0 && !warrior.benched) {
            dead = false
          }
        }
        if (dead) {
          losers += i
        }
      }
      for (team <- List(team0, team1)) {
        for (warrior <- team) {
          if (warrior.active && !warrior.benched) {
            // Le warrior agit
            warrior.behavior.evaluate(warrior)
          }
        }
      }
    }
    losers.toList

  }
  // fonction alliés / ennemis
  def getEnemies(idTeam: Int): List[RTSPWarrior] = {
    // if debug then
    teams(1 - idTeam).toList.filter(w => w.active && !w.benched)
  }

  def getAllies(idTeam: Int): List[RTSPWarrior] = {
    teams(idTeam).toList.filter(w => w.active && !w.benched)
  }

  def addWarriors(warriors: RTSPWarrior*): Unit = {
    warriors.foreach(w => teams(w.team) += w)
  }

}
