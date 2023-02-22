package rtsp.objects
import rtsp.objects.RTSPWarrior
import rtsp.Constants.*
// Faire un banc qui recevra les warriors achetés. Ils pourront ensuite être placés sur le terrain.
object Bench {
    var benchList = new Array[RTSPWarrior](BENCH_SIZE)
    def addWarrior(warrior: RTSPWarrior) : Boolean = { // renvoie faux si le banc est plein
        var i = 0
        while (benchList(i) != null) do {
            i += 1
            if (i == BENCH_SIZE) then return false
        }
        benchList(i) = warrior
        return true
    }
    
}
