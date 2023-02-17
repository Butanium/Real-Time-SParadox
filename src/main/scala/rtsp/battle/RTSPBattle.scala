package rtsp.battle
import rtsp.objects.RTSPWarrior
import scala.collection.mutable.ListBuffer

/*
    Il faut stocker les équipes dans une liste de liste de warriors: List[List[RTSPWarriors]]
*/

class RTSPBattle {
  val teams = ListBuffer[List[RTSPWarrior]]()
  // Lancer la bataille: faire bouger les warriors non morts
    def step() = {
        /*
        // Boucle de tour
        // Check si une des teams est totalement morte
        val losers = ListBuffer[Int]()
        while (losers.length == 0) {
            // Boucle de tour
            for (team <- teams) {
                for (warrior <- team) {
                    if (warrior.health > 0) {
                        // Warrior doit bouger
                        // Warrior doit attaquer
                    }
                }
            }
            // Check si une des teams est totalement morte
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
        }*/
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
                    // Warrior doit bouger
                    // Warrior doit attaquer
                }
            }
        }
    }
}