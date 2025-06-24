import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;

public class AverageByDecadeMapper extends Mapper<LongWritable, Text, Text, DoubleWritable> {
    private Text decadeKey = new Text();
    private DoubleWritable tempValue = new DoubleWritable();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        // Ignorer l'en-tête ou toute ligne non valide
        if (line.length() == 0 || !Character.isDigit(line.charAt(0))) {
            return;
        }
        // Découper la ligne CSV par ';'
        String[] fields = line.split(";", -1);
        if (fields.length > 66 && !fields[66].isEmpty()) {
            String yearStr = fields[5].substring(0, 4);    // champs AAAAMM -> année
            int year = Integer.parseInt(yearStr);
            int decade = (year / 10) * 10;
            double temperature = Double.parseDouble(fields[66]);
            decadeKey.set(String.valueOf(decade));
            tempValue.set(temperature);
            context.write(decadeKey, tempValue);
        }
    }
}
