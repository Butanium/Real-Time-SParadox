package rtsp.battle
import rtsp.objects.RTSPWarrior
import scala.collection.mutable.ListBuffer

/*
    Il faut stocker les équipes dans une liste de liste de warriors: List[List[RTSPWarriors]]
*/

class RTSPBattle(private val team1 : List[RTSPWarrior], private val team2 : List[RTSPWarrior]) {
  // Lancer la bataille: faire bouger les warriors non morts
    private val teams = Array[List[RTSPWarrior]](team1, team2)
    def step() = {
        // On effectue une étape de combat, et on renvoie la liste de perdants à chaque étape (dès qu'elle n'est plus vide, le combat est terminé)
        val losers = ListBuffer[Int]()
        for (team <- teams) {
            var dead = true

            for (warrior <- team) {
                if (warrior.health > 0) {
                    dead = false
                }
            }
            if (dead) {
                losers += teams.indexOf(team)
            }
        }
        for (team <- teams) {
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
        teams(1-idTeam)
    }

    def getAllies(idTeam : Int) = {
        teams(idTeam)
    }
    
}