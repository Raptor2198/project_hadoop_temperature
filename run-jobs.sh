#!/bin/bash

# 1. Créer le dossier d'entrée HDFS
hdfs dfs -mkdir -p /input

# 2. Copier les fichiers CSV locaux vers HDFS
hdfs dfs -put -f /input_data/*.csv /input/

# 3. Supprimer les anciens résultats
hdfs dfs -rm -r -f /output

# 4. Lancer les jobs pour la moyenne par décennie
echo "Lancement des jobs moyenne par décennie..."
hadoop jar /job/TemperatureAnalysis.jar Driver decade /input/MENSQ_17_1868-1949.csv /output/file1_decade &
hadoop jar /job/TemperatureAnalysis.jar Driver decade /input/MENSQ_17_previous-1950-2023.csv /output/file2_decade &
hadoop jar /job/TemperatureAnalysis.jar Driver decade /input/MENSQ_17_latest-2024-2025.csv /output/file3_decade &

# 5. Lancer les jobs pour les températures par année
echo "Lancement des jobs températures par année..."
hadoop jar /job/TemperatureAnalysis.jar Driver yearly /input/MENSQ_17_1868-1949.csv /output/file1_yearly &
hadoop jar /job/TemperatureAnalysis.jar Driver yearly /input/MENSQ_17_previous-1950-2023.csv /output/file2_yearly &
hadoop jar /job/TemperatureAnalysis.jar Driver yearly /input/MENSQ_17_latest-2024-2025.csv /output/file3_yearly &

# 6. Attendre que tous les jobs soient terminés
wait

# 7. Fin
echo "✅ Tous les jobs sont terminés."
echo "📂 Résultats décennie : /output/file1_decade, file2_decade, file3_decade"
echo "📂 Résultats annuels : /output/file1_yearly, file2_yearly, file3_yearly"
