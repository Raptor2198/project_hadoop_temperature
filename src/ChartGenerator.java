import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.*;

public class ChartGenerator {

    // Utilitaire pour lire les données HDFS
    public static DefaultCategoryDataset readDatasetFromHDFS(String[] files, String label) throws IOException, InterruptedException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (String file : files) {
            ProcessBuilder pb = new ProcessBuilder("hdfs", "dfs", "-cat", file);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length == 2) {
                    String yearOrDecade = parts[0];
                    double temperature = Double.parseDouble(parts[1]);
                    dataset.addValue(temperature, label, yearOrDecade);
                }
            }

            process.waitFor();
        }

        return dataset;
    }

    public static void main(String[] args) throws Exception {

        // ---------- 1. Graphique par décennie ----------
        String[] decadeFiles = {
            "/output/file1_decade/part-r-00000",
            "/output/file2_decade/part-r-00000",
            "/output/file3_decade/part-r-00000"
        };

        DefaultCategoryDataset decadeDataset = readDatasetFromHDFS(decadeFiles, "Décennie");
        JFreeChart decadeChart = ChartFactory.createLineChart(
            "Température moyenne par décennie",
            "Décennie",
            "Température (°C)",
            decadeDataset
        );
        ChartUtils.saveChartAsPNG(new File("/charts_output/chart_decade.png"), decadeChart, 800, 600);

        // ---------- 2. Graphique par année ----------
        String[] yearlyFiles = {
            "/output/file1_yearly/part-r-00000",
            "/output/file2_yearly/part-r-00000",
            "/output/file3_yearly/part-r-00000"
        };

        DefaultCategoryDataset yearlyDataset = readDatasetFromHDFS(yearlyFiles, "Année");
        JFreeChart yearlyChart = ChartFactory.createLineChart(
            "Température par année",
            "Année",
            "Température (°C)",
            yearlyDataset
        );
        ChartUtils.saveChartAsPNG(new File("/charts_output/chart_yearly.png"), yearlyChart, 1000, 600);

        System.out.println("✅ Graphiques générés : /charts_output/chart_decade.png et /charts_output/chart_yearly.png");
    }
}
