import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;

public class Top5YearsReducer extends Reducer<IntWritable, DoubleWritable, Text, Text> {
    // TreeMap pour stocker top 5 (clé = moyenne, valeur = année)
    private TreeMap<Double, Integer> top5Map = new TreeMap<>();
    private DecimalFormat df = new DecimalFormat("0.00");

    @Override
    protected void reduce(IntWritable key, Iterable<DoubleWritable> values, Context context)
            throws IOException, InterruptedException {
        // Calcul de la moyenne pour l'année 'key'
        double sum = 0.0;
        long count = 0;
        for (DoubleWritable val : values) {
            sum += val.get();
            count++;
        }
        if (count == 0) {
            return;
        }
        double avg = sum / count;
        int year = key.get();
        // Insérer la moyenne et l'année dans le TreeMap
        top5Map.put(avg, year);
        if (top5Map.size() > 5) {
            // retirer la plus petite moyenne si plus de 5 éléments
            top5Map.remove(top5Map.firstKey());
        }
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        // Émettre les 5 années du TreeMap (du plus chaud au moins chaud)
        for (Map.Entry<Double, Integer> entry : top5Map.descendingMap().entrySet()) {
            String yearStr = entry.getValue().toString();
            String avgStr = df.format(entry.getKey());
            context.write(new Text(yearStr), new Text(avgStr));
        }
    }
}
