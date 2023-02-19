package rtsp.battle
import rtsp.objects.RTSPWarrior
import scala.collection.mutable.ListBuffer

/*
    Il faut stocker les équipes dans une liste de liste de warriors: List[List[RTSPWarriors]]
 */

class RTSPBattle(val debug: Boolean = false) {
  // Lancer la bataille: faire bouger les warriors non morts
  var team0 = List[RTSPWarrior]()
  var team1 = List[RTSPWarrior]()
  private def teams = Array[List[RTSPWarrior]](team0, team1)
  // var oui = false

  def step(): Unit = {
    // if oui then throw new Exception("oui") else oui = true
    // On effectue une étape de combat, et on renvoie la liste de perdants à chaque étape (dès qu'elle n'est plus vide, le combat est terminé)
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
        if (warrior.active) {
          // Le warrior agit
          warrior.behavior.evaluate(warrior)
        }
      }
    }
    losers
  }
  // fonction alliés / ennemis
  def getEnemies(idTeam: Int): List[RTSPWarrior] = {
    if debug then 
        println(s"nb enemies: ${teams(1 - idTeam).filter(_.active).size}")
    teams(1 - idTeam).filter(_.active)
  }

  def getAllies(idTeam: Int): List[RTSPWarrior] = {
    teams(idTeam).filter(_.active)
  }

}
