package rtsp.objects
import engine2D.objects.GameObject
import engine2D.GameEngine
import rtsp.Player
import sfml.graphics.Texture
import engine2D.objects.SpriteObject
class ShopWarrior(
    val unit: RTSPWarrior,
    val player: Player,
    val shop : Shop,
    val price: Int,
    val shop_position: Int,
    val spriteTexture: Texture,
    engine: GameEngine
) extends GameObject(engine)
    with Buyable {
        val sprite = SpriteObject(spriteTexture, engine)
        addChildren(sprite)
        def when_clicked = 
            // TODO: définir la condition banc plein pour ne pas acheter de warrior quand il l'est 
            if affordable then
                player.money -= price
                shop.replace(shop_position)
                //TODO: définir une fonction qui envoie le RTSPWarrior sur le banc
    }
