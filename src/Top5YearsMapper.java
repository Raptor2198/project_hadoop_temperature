import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;

public class Top5YearsMapper extends Mapper<LongWritable, Text, IntWritable, DoubleWritable> {
    private IntWritable yearKey = new IntWritable();
    private DoubleWritable tempValue = new DoubleWritable();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        if (line.length() == 0 || !Character.isDigit(line.charAt(0))) {
            return; // ignore header or invalid lines
        }
        String[] fields = line.split(";", -1);
        if (fields.length > 66 && !fields[66].isEmpty()) {
            // extraire l'année et la température moyenne mensuelle
            String yearStr = fields[5].substring(0, 4);
            int year = Integer.parseInt(yearStr);
            double temperature = Double.parseDouble(fields[66]);
            yearKey.set(year);
            tempValue.set(temperature);
            context.write(yearKey, tempValue);
        }
    }
}
