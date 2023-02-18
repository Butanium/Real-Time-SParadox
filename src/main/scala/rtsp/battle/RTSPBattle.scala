package rtsp.battle
import rtsp.objects.RTSPWarrior
import scala.collection.mutable.ListBuffer

/*
    Il faut stocker les équipes dans une liste de liste de warriors: List[List[RTSPWarriors]]
*/

class RTSPBattle() {
  // Lancer la bataille: faire bouger les warriors non morts
    var team1 = List[RTSPWarrior]()
    var team2 = List[RTSPWarrior]()
    //private val teams = Array[List[RTSPWarrior]](team1, team2)
    def step() = {
        // On effectue une étape de combat, et on renvoie la liste de perdants à chaque étape (dès qu'elle n'est plus vide, le combat est terminé)
        var losers = ListBuffer[Int]()
        var idTeam = 1
        for (team <- List(team1, team2)) {
            var dead = true
            for (warrior <- team) {
                if (warrior.health > 0) {
                    dead = false
                }
            }
            if (dead) {
                losers += idTeam
            }
            if (idTeam == 1) {
                idTeam = 2
            } else {
                idTeam = 1
            }
        }
        for (team <- List(team1, team2)) {
            for (warrior <- team) {
                if (warrior.health > 0) {
                    // Le warrior agit
                    warrior.behavior.evaluate(warrior)
                }
            }
        }
        losers
    }
    // fonction alliés / ennemis
    def getEnemies(idTeam : Int) = {
        if (idTeam == 1) {
            team2
        } else {
            team1
        }
    }

    def getAllies(idTeam : Int) = {
        if (idTeam == 1) {
            team1
        } else {
            team2
        }
    }
    
}