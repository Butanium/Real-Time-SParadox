package rtsp.objects
import engine2D.objects.GameObject
import engine2D.GameEngine
import rtsp.Player
import sfml.graphics.Texture
import rtsp.objects.RTSPWarrior
import engine2D.objects.SpriteObject
import rtsp.battle.*
import rtsp.Constants
class ShopWarrior(
    val warrior_id: Int,
    val player: Player,
    val shop : Shop,
    val price: Int,
    val spriteTexture: Texture,
    engine: GameEngine
) extends GameObject(engine)
    with Buyable {
        val sprite = SpriteObject(spriteTexture, engine)
        addChildren(sprite)
        var shop_position: Int = (-1)
        def change_shop_position_to(i: Int) = 
            shop_position = i
        def when_clicked =
            // TODO: définir la condition banc plein pour ne pas acheter de warrior quand il l'est 
            if affordable then
                print("Un warrior a été acheté!")
                player.money -= price
                shop.replace(shop_position)
                //TODO: définir une fonction qui envoie le RTSPWarrior sur le banc
    }

object ShopWarrior{
    def create_shop_Archer(shop : Shop) = 
        new ShopWarrior(
            2,
            shop.player,
            shop,
            Constants.PRICE_ARCHER,
            warrior.sprite.texture,
            shop.engine
        )
    def create_shop_Barbarian(shop : Shop) = 
        new ShopWarrior(
            1,
            shop.player,
            shop,
            Constants.PRICE_BARBARIAN,
            warrior.sprite.texture,
            shop.engine
        )
}
