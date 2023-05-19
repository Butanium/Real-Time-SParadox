## Ajout d'un multijoueur local
On a implémenté dans le jeu la possibilité d'y jouer à deux en local afin d'avoir un réel intérêt dans le jeu.
Pour jouer de cette manière on a implémenté un bouton switch player qui permet de passer d'un joueur à l'autre, puis un bouton start qui permet de lancer une battle entre les deux joueurs, évidemment on admet que chacun des deux joueurs ne va pas essayer de tricher en regardant la stratégie adverse ou en lançant le combat avnt que son sdversaire soit prêt.
Le jeu consiste en une suite de combat qui s'arrête au moment ou l'un des deux joueurs réussit à détruire la base adverse, il est alors déclaré vainqueur. Tout les warriors tués pendant un combat font rapporter deux d'argent à leur adversaire. Tout les warriors blessés ou morts pendant une battle reviennent à la vie avec tout leurs PVs à la fin du combat mais les bases ne regagnent pas de vie.
Pour ce faire nous avons créé un engine par joueur et un engine pour les battles, où chaque entité n'est affiché que dans l'engine correspondant, et les boutons sont essentiellement là pour changer d'engine en déplaçant éventuellement certain gameObjects d'un engine à un autre.
On pourrait ainsi implémenter un multijoueur avec plus de joueurs, de préférence en ligne plutôt qu'en local pour des questions de jouabilité, où le nombre de joueurs serait préférablement pair et où chaque joueur tomberait sur un adversaire aléatoire encore en vie à chaque battle, un joueur étant éliminé dès lors que sa base n'a plus de vie, le dernier joueur ayant encore des PVs sur sa base est alors déclaré vainqueur, et l'on pourrait aussi classer les joueurs par ordre d'élimination.
## Limitation du nombre de warriors sur le terrain
On a rajouté une limite au nombre de warriors que l'on pouvait avoir au combat en même temps, cettte limite pouvant être augmentée de 1 pour le coût de (limite actuelle) * (limite actuelle) * (limite actuelle + 1) /12 et étant initialisé à 3.
Cette limite rajoute donc une utilité à l'argent et permet aux potions d'avoir un réel intérêt dans le jeu (si on a moins de warriors, il faut qu'ils soient plus forts). De plus améliorer cette limite est au début peu coûteux afin d'augmenter les possibilités de stratégie au niveau de la gestion de l'argent, mais ce coût augmente rapidement afin qu'il soit très difficile (et même non rentable) de dépasser la dizaine de warriors sur le terrain.
## Implémentation de l'éditeur d'arbres de comportement
Pour pouvoir donner à chaque unité un comportement complexe afin d'affiner une certaine stratégie pour un joueur il nous a fallu avoir un bon éditeur de comportement.
Tout d'abord, pour ouvrir l'éditeur de comportement, il faut faire click droit sur l'une de nos unités et l'on a alors un arbre qui va définir le comportemnt de l'unité. Les noeuds de cet arbre sont représentés par des rectangles blancs dont le type est écrit en rouge dessus, ces rectangles disposent aussi de petits carrés rouges d'où l'on peut relier les différents noeuds de l'arbre entre eux par des lignes grises, ce qui permet aussi d'instancier de nouveaux noeuds (il suffit de cliquer sur un carré rouge et de relacher là où il n'y a pas de noeud). Afin de pouvoir moduler cet arbre à notre guise il est possible de supprimer n'importe quel noeud ou ligne, en faisant click droit sur un noeud pour le supprimer et en appuyant sur le carré jaune au milieu d'une ligne pour la supprimer. Une fois que l'on a modifié à notre convenance le comportement d'une unité on peut alors sortir de l'éditeur en appuyant sur "save". 
L'unité a alors enregistré le comportement qu'elle devait avoir lors d'une battle, et en réitérant le procédé sur nos autres unités on peut ainsi obtenir une quasi-infinité de stratégies possibles, de quoi s'adapter aux unités et à la stratégie adverses.
## Problèmes rencontrés
Les deux principaux problèmes ont été le temps de compilation et le fait que sbt se fasse très souvent killed avant que l'on ait pu run le code. La combinaison de ces deux problèmes a en plus le défaut d'être relativement puissante ce qui nous a conduit à des périodes où l'on passait plus de temps à attendre de pouvoir run notre code que à réellement coder.
Sinon on a rencontré plusieurs bugs mineurs que l'on a corrigés tels que la supression des warriors à certain moment (typiquement quand on les vendait), gérer les différentes entités avec les différents engines, par exemple a un moment tout les bancs avaient disparus et les guerriers achetés se mettaient en haut à gauche ou encore le jeu qui crashait dans l'éditeur de comportement car certaines lignes restaient attachées à des noeuds qui avaient été supprimés.