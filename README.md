# Temperature Analysis with Hadoop MapReduce

Ce projet analyse les données de température historiques en utilisant Hadoop MapReduce pour identifier les tendances climatiques et générer des visualisations.

## 📊 Aperçu du Projet

Le projet traite des données de température mensuelle pour :
- Calculer les moyennes de température par décennie
- Générer des graphiques de visualisation des tendances

## 🏗️ Architecture

### MapReduce Jobs
- **TemperatureByYearMapper/IdentityReducer** : Agrégation des températures par année
- **AverageByDecadeMapper/AverageByDecadeReducer** : Calcul des moyennes par décennie

### Génération de Graphiques
- **ChartGenerator** : Création de graphiques à partir des résultats MapReduce
- Utilise JFreeChart pour les visualisations

## 📁 Structure du Projet

```
temperature_project/
├── src/                    # Code source Java
├── input/                  # Données d'entrée (fichiers CSV)
├── build/                  # Fichiers compilés
├── output_graphs/          # Graphiques générés
├── lib/                    # Bibliothèques Hadoop (non versionnées)
├── docker-compose.yml      # Configuration Docker
└── run-jobs.sh            # Script d'exécution
```

## 🔧 Prérequis

- Java 8+
- Hadoop 3.2.1
- Docker et Docker Compose (pour l'environnement conteneurisé)

## 📦 Installation

Télécharge les .jar nécessaires :

https://repo1.maven.org/maven2/org/jfree/jfreechart/1.5.6/

jfreechart-x.y.z.jar

https://repo1.maven.org/maven2/org/jfree/jcommon/1.0.24/

jcommon-x.y.z.jar

Créer un sous-dossier nommé "external" dans le dossier "lib" et place les 2 fichiers jar téléchargé dans le sous-dossier "external"


Clone le projet depuis GitHub et entre dans le dossier.
```bash
git clone git@github.com:Raptor2198/project_hadoop_temperature.git
cd project_hadoop_temperature
```

Compile le code Java en version Java 8, en incluant toutes les dépendances Hadoop + JFreeChart.
Les fichiers .class sont générés dans le dossier build/.
```bash
javac --release 8 -classpath "lib/external/*;lib/common/*;lib/hdfs/*;lib/mapreduce/*;lib/yarn/*" -d build src/*.java
```

Crée un JAR exécutable (TemperatureAnalysis.jar) contenant tous les fichiers compilés pour MapReduce.
```bash
jar -cvf build/TemperatureAnalysis.jar -C build/ .
```

Lance l’environnement Hadoop complet via Docker en arrière-plan (namenode + 3 datanodes).
```bash
docker compose up -d
```

Copie les bibliothèques Hadoop du conteneur vers ton dossier lib/ local pour la compilation côté hôte.
```bash
docker cp namenode:/opt/hadoop-3.2.1/share/hadoop ./lib
```

Ouvre une session shell interactive dans le conteneur namenode, pour lancer les jobs MapReduce.
```bash
docker exec -it namenode bash 
```

Rend le script exécutable puis lance les jobs MapReduce pour chaque CSV.
Ce script :
Copie les données vers HDFS
Supprime d’anciennes sorties
Lance 3 jobs (décennie et année)
```bash
chmod +x /run-jobs.sh && /run-jobs.sh
```

 Affiche les résultats Hadoop pour file1_decade. À répéter pour les autres fichiers (file2, file3, etc.).
Au niveau du bash du conteneur: 
```bash
hdfs dfs -cat /output/file1_decade/part-r-00000
```

Exécute ChartGenerator depuis le conteneur, avec les bibliothèques nécessaires.
Résultat : deux graphiques chart_decade.png et chart_yearly.png enregistrés dans /output_graphs/.
```bash
java -classpath /job:/external_libs/* ChartGenerator
```

## 📈 Résultats

Le projet génère :
- **Moyennes par décennie** : Tendances climatiques à long terme
- **Top 5 des années les plus chaudes** : Identification des pics de température
- **Graphiques de visualisation** : Représentations visuelles des données

Les graphiques sont sauvegardés dans le dossier `output_graphs/`

## 📊 Données d'Entrée

Le projet traite des fichiers CSV avec les colonnes :
- Date/Année
- Température mensuelle
- Métadonnées climatiques

Format des fichiers supportés :
- `MENSQ_17_1868-1949.csv` : Données historiques anciennes
- `MENSQ_17_1950-2023.csv` : Données récentes
- `MENSQ_17_latest-2024-2025.csv` : Données les plus récentes

## 🛠️ Technologies Utilisées

- **Apache Hadoop 3.2.1** : Framework de traitement distribué
- **MapReduce** : Paradigme de traitement parallèle
- **JFreeChart** : Génération de graphiques
- **Java 8+** : Langage de programmation
- **Docker** : Conteneurisation