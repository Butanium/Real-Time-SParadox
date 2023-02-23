package rtsp.battle
import rtsp.objects.RTSPWarrior
import scala.collection.mutable.ListBuffer

/*
    Il faut stocker les équipes dans une liste de liste de warriors: List[List[RTSPWarriors]]
 */

class RTSPBattle(val debug: Boolean = false) {
  private var _active = false
  // Lancer la bataille: faire bouger les warriors non morts
  private val team0 = ListBuffer[RTSPWarrior]()
  private val team1 = ListBuffer[RTSPWarrior]()
  private val teams = Array[ListBuffer[RTSPWarrior]](team0, team1)
  private def battleWarriors = team0.toList.appendedAll(team1.toList)
  def active = _active
  def active_=(newActive: Boolean): Unit =
    if (active != newActive) {
      if (newActive) {
        // On lance la bataille
        // todo: si on veut autoriser la réorganisation du banc pendant la bataille:
        // (gérer le cas où on les dépose dans la bataille: pas autorisé !)
        // battleWarriors.foreach(w => w.isGrabbable = w.benched)
        battleWarriors.foreach(w => {
          w.isGrabbable = false
          w.initialPosition = w.position
        })
      }
    }
  // var oui = false

  def resetBattle(): Unit = {
    battleWarriors.foreach(w => {
      w.health = w.maxHealth
      w.active = true
      w.benched = false
      w.position = w.initialPosition
      w.isGrabbable = true
    })
    active = false
  }

  def step(): Unit = {
    // if oui then throw new Exception("oui") else oui = true
    // On effectue une étape de combat, et on renvoie la liste de perdants à chaque étape (dès qu'elle n'est plus vide, le combat est terminé)
    if (active) then {
      var losers = ListBuffer[Int]()
      for (i <- 0 to teams.size - 1) {
        var dead = true
        val team = teams(i)
        for (warrior <- team) {
          if (warrior.health > 0) {
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
      losers
    }
  }
  // fonction alliés / ennemis
  def getEnemies(idTeam: Int): List[RTSPWarrior] = {
    if debug then
      println(s"nb enemies: ${teams(1 - idTeam).filter(_.active).size}")
    teams(1 - idTeam).toList.filter(w => w.active && !w.benched)
  }

  def getAllies(idTeam: Int): List[RTSPWarrior] = {
    teams(idTeam).toList.filter(w => w.active && !w.benched)
  }

  def addWarriors(warriors: RTSPWarrior*): Unit = {
    warriors.foreach(w => teams(w.team) += w)
  }

}
