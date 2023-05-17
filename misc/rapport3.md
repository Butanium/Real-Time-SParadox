# TLDR (aide de jeu)
## Shop et multijoueur
- Mettre un warrior/potion dans `Drop Here to sell` permet de le vendre à moitié prix
- `Switch player` permet d'alterner entre les deux joueurs
- `Start!` lance la bataille
- Les warriors doivent être déposés dans la zone du joueur correspondant
- Pas plus de `Warrior limit` warriors par joueur sur le terrain. Le bouton `Upgrade limit` permet d'augmenter cette limite
- `Switch shop` permet d'alterner entre le shop pour les warriors et le shop pour les potions

## Arbre de comportement
Lors de l'évaluation d'un arbre on effectue un parcours en profondeur.

Il y a 4 types de noeuds dans l'arbre de comportement : `Condition`, `Action`, `Node` et `Filter`. Ils s'executent comme suit :
- `Condition` : Si la condition de ce noeud est vraie, alors on évalue les noeuds enfants. Les conditions sont de la forme `IF (not) <target> <operator> <value>`.
- `Action` : Si une cible est disponible pour l'action, alors on l'effectue. Les actions sont de la forme `<action> <target> <selector>`. Le *selector* permet de choisir la cible de l'action parmi les cibles disponibles. Par exemple, la plus proche, la plus faible, la plus forte, etc. **Tous les enfants d'un noeud action sont des `Filter`**. En effet rien n'est exécuté après l'évaluation d'un noeud action.
- `Node` : On évalue les noeuds enfants. **Ainsi un `Node` n'a pas d'enfant `Filter`**.
- `Filter` : S'applique au noeud parent. Plusieurs `Filters` peuvent être appliqués à un même noeud parent. Ils permettent de filtrer les cibles disponibles pour l'évaluation de l'action / condition. Par exemple on peut ne considérer que les warriors ennemis **qui sont en train d'attaquer** et **qui ont moins de 50% de leurs PV**. Un `Filter` n'a donc pas d'enfant. Voir le rapport / explorer les menus pour plus de détails.

Les enfants d'un noeuds sont évalués **dans l'ordre croissant des abscisses** de leurs positions. L'enfant le plus à gauche est donc le premier évalué.

## Editeur de comportement
- `Click droit` sur un warrior permet de modifier son arbre de comportement
- `Close` ferme l'éditeur de comportement sans sauvegarder
- `Save` sauvegarde l'arbre de comportement du warrior. A noter que :
  - Si l'arbre de comportement contient un cycle, alors il ne sera pas sauvegardé
  - Tous les noeuds "illégaux" (par exemple une `Condition` enfant d'une `Action`) ou non reliés à la racine ne seront pas sauvegardés.
- Le carré jaune sur un lien permet de le supprimer avec un `click gauche`
- Il est possible de supprimer un noeud avec un `click droit` dessus
- Le **carré rouge** en dessous d'un noeud permet de lui ajouter un enfant en maintenant le `click gauche` et en le déplaçant sur un autre noeud pour le lier ou sur un espace vide pour créer un nouveau noeud
### Créer un nouveau noeud
Afin de créer un nouveau noeud, il suffit de sélectionner les options voulues en parcourant les différents sous menus. A noter que :
- Les menus sauvegardent les options sélectionnées d'une création de noeud à l'autre et d'une ouverture de l'éditeur à l'autre. Ainsi si vous avez déjà sélectionner les `Warrior Enemy` comme `target` de l'action `Attack`, pour créer un nouveau noeud `Move` il suffit de sélectionner `Move` puis de valider avec `Done`.
- Les boutons alternant entre `If` et `If not` permettent d'activer ou non la négation pour la condition / le filtre.
 
## Les différents warriors
Voici un petit récapitulatif des différents warriors disponibles dans le jeu :
| Nom        | PV max | Portée | Dégâts | Délai d'attaque | Vitesse | Prix |
| ---------- | ------ | ------ | ------ | --------------- | ------- | ---- |
| Barbare    | 1800   | 10     | 20     | 0.5             | 15      | 3    |
| Archer     | 1000   | 100    | 15     | 1               | 10      | 4    |
| Géant      | 3000   | 10     | 10     | 1               | 7       | 3    |
| Mage       | 1800   | 70     | 25     | 1               | 15      | 5    |
| Guérisseur | 1900   | 80     | -30    | 1.5             | 10      | 5    |


# Rapport 3, vers l'infini et l'au-delà
Lors de cette troisième partie nous nous sommes concentrés sur 3 points :
- L'amélioration du GamePlay et implémentation du multijoueur
- Augmenter grandement l'expressivité des comportements des warriors
- Implémenter un éditeur de comportement

## Ajouts pour le système de combat et les warriors
Désormais, les warriors ne peuvent pas dépasser les limites du champ de bataille, qui ont été fixées dans les constantes. 

De plus, de nouveaux warriors ont été ajoutés: les mages et les healers. Les mages font de puissants dégâts à distance, et les healers soigent leurs alliés avec des boules d'énergie vitale. Attention, il faut que les healeurs ciblent *leur* équipe, sinon, ils soigneront l'équipe adverse ! 

D'autre part, les warriors qui lancent des capacités à distance sont maintenant classés comme des `RangeWarrior` (surclasse de `RTSPWarrior`). Cela permet de traiter leur cas d'attaque différemment et de créer une animation avec l'affichage d'un projectile adapté allant du warrior à sa cible. Pour ne pas consommer trop de mémoire à la longue, nous avons également mis en place une optimisation consistant, pour chaque type de projectile, à réutiliser les projectiles déjà créés. On en produit des nouveaux seulement lorsqu'il n'y a plus de projectiles réutilisables de disponibles.

En outre, il est désormais possible de vendre ses biens, que ce soient des warriors ou des potions à effet. Le joueur est remboursé à hauteur de la moitié du prix du bien. Enfin, une limite de zone de drop des warriors sur la map a été fixée pour que les warriors soient déposés suffisamment proches de leur base, et empêcher de les déposer en face de la base adverse.

Il y a maintenant une limite au nombre de warriors déployable au combat. Cette limite peut être augmentée de 1 pour le coût de (limite actuelle) * (limite actuelle) * (limite actuelle + 1) /12 et étant initialisé à 3.
Cette limite rajoute donc une utilité à l'argent et permet aux potions d'avoir un réel intérêt dans le jeu (si on a moins de warriors, il faut qu'ils soient plus forts). De plus améliorer cette limite est au début peu coûteux afin d'augmenter les possibilités de stratégie au niveau de la gestion de l'argent, mais ce coût augmente rapidement afin qu'il soit très difficile (et même non rentable) de dépasser la dizaine de warriors sur le terrain.

## Ajouts pour le système de comportement
### **Les actions**
Les actions sont les noeuds qui permettent d'effectuer une action. Elles sont de la forme `<action> <target> <filters> <selector>`. Les actions disponibles sont les suivantes :
- `Attack` : Attaque la cible
- `Move` : Déplace le warrior vers la cible
- `Flee` : Fuit la cible
- `Idle` : Ne fait rien

Les `Selector` permettent de choisir la cible de l'action parmi les cibles disponibles. Pour cela on choisit le warrior qui minimise / maximise une `Metric`.

Les `Filters` permettent de filtrer les cibles disponibles.

### Target
On compte 3 types de `Target` :
- `Warrior <team>` : Un warrior de l'équipe `<team>`, `team` étant `Ally` ou `Enemy`
- `Base <team>` : La base de l'équipe `<team>`
- `Self` : Le warrior qui exécute l'action

### Metric
On compte 3 types de `Metric` :
- `DistanceFromClosest<target>` : La distance entre le warrior et la `<target>` la plus proche
- `Health` : La vie du warrior
- `HealthPercentage` : La vie du warrior en pourcentage

### Selector
On compte 2 types de `Selector` :
- `Lowest<metric>` : Le warrior qui minimise la `<metric>`
- `Highest<metric>` : Le warrior qui maximise la `<metric>`

### Filter
On compte 3 sous types de `Filter` :
- Les `Filter` s'appliquant sur une `Metric` :
  - `LessThan <metric>  <value>` : sélectionne les cibles dont la `<metric>` est inférieure à `<value>`
  - `GreaterThan <metric>  <value>` : sélectionne les cibles dont la `<metric>` est supérieure à `<value>`
  - `Equals <metric>  <value>` : sélectionne les cibles dont la `<metric>` est égale à `<value>`
- Les `Filter` s'appliquant sur l'action de la cible :
  - `Attacking<target>` : sélectionne les cibles qui attaquent une `<target>`
  - `MovingTo<target>` : sélectionne les cibles qui se déplacent vers une `<target>`
  - `FleeingFrom<target>` : sélectionne les cibles qui fuient une `<target>`
  - `Idling` : sélectionne les cibles qui ne font rien
  - `CanAttack<target>` : sélectionne les cibles qui peuvent attaquer une `<target>`
- Les `Filter` s'appliquant filtrant sur les actions des autres sur la cible :
  - `AttackedBy<target>` : sélectionne les cibles qui sont attaquées par une `<target>`
  - `ApproachedBy<target>` : sélectionne les cibles qui sont approchées par une `<target>`
  - `FledBy<target>` : sélectionne les cibles qui sont fuies par une `<target>`
  - `CanBeAttackedBy<target>` : sélectionne les cibles qui peuvent être attaquées par une `<target>`
On peut accessoirement ajouter un `Not` devant un `Filter` pour sélectionner les cibles qui ne vérifient pas le `Filter`.

### **Les conditions**
Les conditions sont les noeuds qui permettent de prendre une décision. Elles sont de la forme `<condition> <target> <filters> <countCondition>`.
Une condition compte le nombre de cibles qui vérifient les `filters` et qui sont de type `target`. Si ce nombre vérifie la `countCondition`, alors la condition est vraie et on execute les noeuds enfants jusqu'à rencontrer une action possible.

### CountCondition
On compte 3 types de `CountCondition` :
- `LessThan <value>` : La condition est vraie si le nombre de cibles vérifiant les `filters` et qui sont de type `target` est inférieur à `<value>`
- `GreaterThan <value>` : La condition est vraie si le nombre de cibles vérifiant les `filters` et qui sont de type `target` est supérieur à `<value>`
- `Equals <value>` : La condition est vraie si le nombre de cibles vérifiant les `filters` et qui sont de type `target` est égal à `<value>`


## Ajout de l'éditeur de comportement
Afin de pouvoir donner à chaque unité un comportement complexe pour permettre au joueur de varier les stratégies, nous avons décidé d'ajouter un éditeur de comportement.

Tout d'abord, pour ouvrir l'éditeur de comportement, il faut faire click droit sur l'une de nos unités et l'on a alors un arbre qui va définir le comportement de l'unité. Les noeuds de cet arbre sont représentés par des rectangles blancs dont le type est écrit en rouge, ces rectangles disposent aussi de petits carrés rouges d'où l'on peut relier les différents noeuds de l'arbre entre eux par des lignes grises. Ces lignes permettent aussi d'instancier de nouveaux noeuds (il suffit de cliquer sur un carré rouge et de relâcher là où il n'y a pas de noeud). 

Afin de pouvoir moduler cet arbre à notre guise, il est possible de supprimer n'importe quel noeud ou ligne. Il suffit de clic droit sur un noeud pour le supprimer et de clic gauche sur le carré jaune au milieu d'une ligne pour la supprimer.

Une fois que l'on a modifié le comportement à notre convenance, le comportement d'une unité, on peut alors sortir de l'éditeur en appuyant sur "save". 

L'unité a alors enregistré le comportement qu'elle devait avoir lors d'une battle, et en réitérant le procédé sur nos autres unités, on peut ainsi obtenir une quasi-infinité de stratégies possibles, de quoi s'adapter aux unités et à la stratégie adverse.

Afin de rendre la visualisation de l'arbre plus facile, mettre sa souris sur un noeud permet d'afficher les informations de ce noeud en bas à gauche de l'écran.