# Rapport de projet 2

## Ajout des bases
TODO clément

## Nouvelles classes, nouveaux bancs
### Ajout de buffs et d'un warrior
Nous avons introduit un système de buffs dans la classe `Effects` qui consiste en des potions augmentants les statistiques du warrior sur lequel on dépose la potion en question. On retrouve un buff d'attaque, de vitesse, et de tankiness. Les buffs sont stockés dans un banc qui est du même type que le banc pour warriors. D'autre part, un nouveau warrior a été ajouté: le warrior `Giant` qui est un tank melee infligeant peu de dégâts.
### Factorisation du code
Par rapport au premier rendu, nous avons cette fois-ci créé une classe `GeneralBench` qui permet de factoriser les bancs pour les warriors et les buffs. Les méthodes générales telles que `addBought` et `removeEntity` y sont présentes. Les classes `EffectBench` et `WarriorBench` extendent `GeneralBench` et définissent leurs fonctions spécifiques pour ajouter des entités dans leur intérieur.