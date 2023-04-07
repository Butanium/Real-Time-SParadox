# Rapport de projet 2

## Ajout des bases
TODO clément

## Nouvelles classes, nouveaux bancs
### Ajout de buffs et d'un warrior
Nous avons introduit un système de buffs dans la classe `Effects` qui consiste en des potions augmentants les statistiques du warrior sur lequel on dépose la potion en question. On retrouve un buff d'attaque, de vitesse, et de tankiness. Les buffs sont stockés dans un banc qui est du même type que le banc pour warriors. D'autre part, un nouveau warrior a été ajouté: le warrior `Giant` qui est un tank melee infligeant peu de dégâts.
### Factorisation du code
Par rapport au premier rendu, nous avons cette fois-ci créé une classe `GeneralBench` qui permet de factoriser les bancs pour les warriors et les buffs. Les méthodes générales telles que `addBought` et `removeEntity` y sont présentes. Les classes `EffectBench` et `WarriorBench` extendent `GeneralBench` et définissent leurs fonctions spécifiques pour ajouter des entités dans leur intérieur. La fonction héritée `addBought` commune aux deux types de bancs est utiie pour l'achat dans les shops.

## Remaniement du shop
Nous avons beucoup travaillé sur le Shop afin que le code soit très modulable, de cette manière les deux shops différents, pour les warriors et les buffs, sont définis par une seule et même classe `shop`, ce qui nous a évité la duplication d'une bonne partie de notre code. Pour ce faire nous avons supprimé, ou du moins remplacé la classe `ShopWarrior` en utilisant une classe `ShopButton`, qui permet d'avois des boutons fixes pour chacun des Shops et dont seul le contenu change. Nous avons aussi apporté des améliorations à plusieurs autres parties du code, notamment à `Pool` et à `Shop` afin que la plupart des fonctions utilisent l'ID des objets (warriors et potions), pour améliorer la lisibilité du code.
Afin de pouvoir passer d'un Shop à l'autre, nous avons utiliser un `SwitchButton` situé en dehors du code qui permet de remplacer l'affichage d'un Shop par l'autre afin de ne pas perdre trop de place à afficher les deux Shops simultanément.

## Ajout de bases
Le but du jeu devient de détruire la base adverse qui peut se défendre. Pour cela, nous avons ajouté une classe `Base` qui est un `RTSPWarrior` qui ne peut pas bouger et qui inflige des dégâts aux warriors ennemis qui s'approchent. Les bases sont placées sur les côtés opposés du terrain de jeu. Les bases peuvent être attaquer par les warriors.

## Arbre de comportement
Après l'ajout de la base nous avons rajouté deux nouveaux noeuds à l'arbre de comportement: un pour se déplacer vers la base ennemie et un pour attaquer la base ennemie. Ces deux noeuds sont utilisés par les warriors pour attaquer la base ennemie.