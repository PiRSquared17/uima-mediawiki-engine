# Introduction #

Lorsque l'on commence à travailler sur du traitement automatique du langage, et notamment dans le cadre de la fouille de texte, il apparaît rapidement quelques mots barbares mais essentiels à connaître.
C'est bien évidemment tout aussi vrai pour toute la programmation intervenant sur ces problématiques.


# Lexique #

  * **Corpus**: un ensemble de documents (texte, image, vidéo,...) regroupés dans une optique précise. <br /> "D'un point de vue méthodologique, ils apportent une objectivité nécessaire à la validation scientifique en traitement automatique du langage naturel. L'information n'est plus empirique, elle est vérifiée par le corpus. Il est donc possible de s'appuyer sur des corpus (à condition qu'ils soient bien formés) pour formuler et vérifier des hypothèses scientifiques." [Wikipedia Corpus -- le corpus dans la science ](http://fr.wikipedia.org/wiki/Corpus\#Le_corpus_dans_la_science).

  * **dump**: "est un mot anglais signifiant l'action de laisser choir en tas ou en masse. Utilisé en informatique (...) pour définir une « action de vidage mémoire » vers un périphérique de sortie" [Wikipedia : Dump ](http://fr.wikipedia.org/wiki/Dump). <br />Dans notre cas un dump sera un document brute issue d'une source contenant des données à traiter, en particulier un dump wikipedia est un enregistrement des articles, news ou citations de l'encyclopédie qui sont librement accessible en téléchargement [Wikipedia : dumps des différentes base de donnés de l'encyclopédie libre ](http://dumps.wikimedia.org/backup-index.html).

  * **XML**: (ou Extensible Markup Language) "est un langage informatique de balisage générique. Il sert essentiellement à stocker/transférer des données de type texte Unicode structurées en champs arborescents" [Wikipedia : XML ](http://fr.wikipedia.org/wiki/Extensible_Markup_Language). Le XML est un langage permettant de structurer un texte, à l'aide de balise entourée de crochets exemple: `<italique>`_texte en italique_`</italique>`, largement utilisé.

  * **UIMA**: (ou Unstructured Information Management) est une librairie utilisable en java permettant l'analyse de documents non structurés chargés dans des CAS.

  * **CAS**: (ou Common Analysis Structure) est une structure, une manière de mettre en forme les données afin qu'elles soient utilisables, notamment par UIMA. C'est l'élément de base de l'analyse de document avec UIMA. <br /> Un CAS est composé de deux objets:
    * **SOFA**: (ou Subject of Analysis) le contenu du document, soit les données brutes. Dans la linguistique on appel cette partie le "corpus".
    * **anotations**: (liste(s) de) les anntotations issues du traitements du corpus, ces anntoations sont le fruit de l'analyse. En linguistique on parle ici de "status".

  * **Collection Reader**: un outil codé en Java transformant un document en un CAS utilisable dans UIMA. Dans notre cas cet outil est codé en java et le document source est un dump de wikipedia en format XML.

  * **Analysis engine**: un outil codé en Java traitant les CAS issue d'un collection reader et les enrichi d'annotations, ce sont ces différents "moteurs d'analyse" qui fournissent les informations recherchées par l'utilisateur.

  * **Annotator**: un descripteur d'analysis engine.

  * **CAS Consumer**: outil codé en Java qui exporte le contenu d'un CAS vers un format approprié, cette exportation se fait en générale après l'analyse. Dans notre cas le format d'export le plus courant est le XML.

  * **Parser**: (ou analyseur syntaxique) met en evidence la structure d'un texte. Le programme découpe un texte selon un grammaire fournie et transforme ainsi le document en un flux de "lexèmes"; un lexéme est un groupe de sens soit un ensemble d'entité aillant le m\^eme sens. <br /> Dans notre cas les parsers sont codés en Java et permettent de débiter les documents XMLs en fonction des balises (\'a chaque rencontre de balise on coupe) et les dumps Wikipedia en fonction de la syntaxe de mise en forme Wikipedia.