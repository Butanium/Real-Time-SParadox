# Rapport de projet 1


## Le moteur de jeu
```
src/main/scala/engine2D/
├── Game.scala
├── GameEngine.scala
├── eventHandling
│   ├── EventManager.scala
│   ├── MouseEvent.scala
│   ├── MouseManager.scala
│   └── MouseState.scala
├── graphics
│   ├── FontManager.scala
│   ├── GameResourceManager.scala
│   ├── GrUtils.scala
│   ├── Group.scala
│   └── TextureManager.scala
└── objects
    ├── Boundable.scala
    ├── GameObject.scala
    ├── GameTransform.scala
    ├── GameUnit.scala
    ├── Grabbable.scala
    ├── GraphicObject.scala
    ├── MovingObject.scala
    ├── RectangleObject.scala
    ├── SpriteObject.scala
    └── TextObject.scala
```
### Boucle de jeu
Le moteur de jeu repose principalement sur la classe `GameEngine` qui gère l'ensemble des objets du jeu. Les objets du jeu héritent tous de la classe `GameObject` qui hérite de `Transform` et implémente le trait `Drawable`. Chaque gameObject peut avoir des enfants ce qui permet d'avoir des objets plus complexes. Le transform des enfants est relatif à celui de son parent. Tous les objets du jeu sont stockés dans une liste `GameEngine.gameObjects` qui est mise à jour à chaque frame.

La classe `Game` appel à chaque frame : `GameEngine.update()` et `GameEngine.draw()`. Ces deux méthodes appellent les méthodes `update()` et `draw()` de tous les objets du jeu. Notre implémentation est basée sur la librairie `sfml`.

### Les GameObjects
Les `GameObject` sont des objets qui peuvent être dessinés et qui peuvent être mis à jour. Ils héritent de la classe `GameTransform` qui permet de gérer la position, l'échelle et la rotation de l'objet. `GameTransform` hérite de `Transform` (une classe de la librairie `sfml` qui permet de gérer la position, l'échelle et la rotation d'un objet) et rajoute des méthodes utiles comme la méthode `distanceTo` qui permet de calculer la distance entre deux `GameTransform`.
Les `GameObject` peuvent aussi avoir des enfants. Les enfants sont stockés dans une liste `children` et sont mis à jour et dessinés à chaque frame. Le transform des enfants est relatif à celui de son parent. 
Plusieurs `GameObject` sont déjà implémentés :
- `Grabbable` : un trait qui permet de déplacer un objet avec la souris
- `Boundable` : un trait qui caractérise un objet qui a une hitbox
- `GraphicObject` : un `GameObject` qui peut être dessiné avec une texture
- `RectangleObject` : un `GraphicObject` qui peut être dessiné avec un rectangle
- `SpriteObject` : un `GraphicObject` qui peut être dessiné avec une texture (un sprite)
- `TextObject` : un `GraphicObject` qui peut être dessiné avec du texte
- `MovingObject` : un `GameObject` qui a une vitesse et une direction et qui peut donc se déplacer
- `GameUnit` : un `MovingObject` qui possède des points de vie et qui peut donc être détruit

### Gestion des évènements
La gestion des évènements est gérée par la classe `EventManager`. Pour l'instant notre jeu n'utilise pas les évènements clavier mais il est possible de les ajouter facilement (une branche est en stand-by pour ça). A chaque frame, l'`EventManager` distribue les évenements aux différents managers. Pour l'instant seul le `MouseManager` est implémenté. Les `gameObject` peuvent s'abonner à certains évènements avec la fonctions `listenToMouseEvent` qui prend en paramètre un `MouseEvent` et une fonction de callback. Lorsque l'évènement est déclenché, la fonction de callback est appelée.
Ainsi, pour chaque évènement distribué par l'`EventManager`, le `MouseManager` regarde si cela trigger un `MouseEvent`, si oui, il appelle la fonction de callback associée.

Le `MouseManager` gère aussi le `MouseState` qui est un objet qui contient les informations sur l'état actuel de la souris. Il contient notamment la position de la souris, le bouton enfoncé, etc. Il est mis à jour à chaque frame. Celui-ci est accessible par tous les `gameObject` via le `GameEngine`.

Ainsi pour avoir des interactions souris/objet, il suffit de s'abonner au bon évènement ou de regarder le `MouseState` dans la méthode `onUpdate()` de l'objet. Par exemple, pour print "hello" en continu lorsque la souris est sur l'objet :
```scala
// dans la méthode onUpdate()
if (this.globalBounds.contains(engine.mouseState.worldPos)) {
  println("hello")
}
```
ou encore :
```scala
// dans le constructeur de l'objet
listenToMouseEvent(MouseInBounds(this, true), () => println("hello"))
// le booléen indique si on veut que le callback soit appelé à chaque frame ou seulement à la première frame où la souris est dans la hitbox
```
Ces deux exemples sont équivalents mais supposent que l'objet est un `Boundable`, c'est à dire qu'il a une hitbox.

### Gestion des ressources
Pour éviter de charger plusieurs fois les mêmes ressources, le moteur de jeu utilise des `GameResourceManager` qui est une classe abstraite. Pour l'instancier, il suffit de lui donner un chemin vers là où se trouvent les ressources et une fonction qui permet de charger une ressource à partir d'un fichier.
Les objets `TextureManager` et `FontManager` héritent de cette classe.

### Limitations
Le moteur de jeu n'est pas totalement terminé. Il contient toute les fonctionnalités nécessaires pour faire ce premier rendu mais manque de fonctionnalités qui pourraient s'avérer utile dans le futur. Voici une liste non exhaustive de ces fonctionnalités :
- Gestion des évènements clavier. Elle est quasiment terminée mais nous n'en aurons peut-être pas besoin.
- Gestion du son. Le binding scala de la librairie `sfml` ne permet pas de gérer le son pour le moment.
- Gestion des collisions. Nous avons implémenté un système de hitbox mais nous n'avons pas implémenté de système de collision. Cela pourra être utile pour éviter que les unités se superposent.
- Gestion des animations. Nous avons implémenté un système de sprites mais nous n'avons pas implémenté de système d'animation. Cela pourra être utile pour faire bouger les unités.
- Gestion de la caméra. Pour l'instant nous n'utilisons pas de caméra. Cela pourra être utile pour faire des zooms et des déplacements sur la carte. Mais aussi pour avoir une GUI.
- Gestion de la profondeur. Pour l'instant les objets sont dessinés dans l'ordre où ils sont ajoutés à la liste des objets du jeu. Cela pourra être utile de les dessiner en fonction d'un attribut `zIndex` par exemple.




## Gameplay du prototype
### Combat
todo

### Shop
Le shop implémenté a pour objectif de permettre à un joueur d'acheter des combattants, pour pouvoir ensuite les placer sur le banc puis les placer pour combattre.
- Pour ce faire, le shop est défini comme appartenant à la classe `Shop`, qui prend en argument un joueur de la classe `Player` qui dispose d'un montant d'argent à dépenser dans le shop. Les combattants sont représentés dans le shop par un type `ShopWarrior` qui dispose du trait `Buyable` et qui permet l'affichage des combattants dans le shop. Pour permettre au joueur d'acheter des combattants le shop lui met à disposition un nombre constant (qui pourra peut-être évoluer plus tard) de champions qui sont tirés aléatoirement par un `Pool`.
- Le `Pool` permet de sélectionner aléatoirement un champion parmi tout les champions disponibles afin qu'il soit mis à l'achat dans le shop, il n'y a pour l'instant que deux types de champions différent, les barbares et les archers qui apparaissent avec la même probabilité mais il y aura plus de combattants plus tard avec éventuellemnt des coefficients d'apparition différents.
- Il y a aussi une possibilité pour le joueur de renouveler complètement le shop à sa disposition pour un certain montant d'argent, auquel cas chaque combattant du shop est remplacé par un champion du pool de la même manière que si ils étaient choisis un par un.
- Finalement, chaque champion acheté par le shop est envoyé sur le banc, duquel il pourra être envoyé au combat.

### Banc
todo