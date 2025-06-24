import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;
import org.apache.hadoop.io.IntWritable;


public class TemperatureByYearMapper extends Mapper<LongWritable, Text, IntWritable, DoubleWritable> {

    private IntWritable yearKey = new IntWritable();
    private DoubleWritable tempValue = new DoubleWritable();

    @Override
    protected void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {

        String line = value.toString();
        int index = 0;

        if (line.length() == 0 || !Character.isDigit(line.charAt(index))) {
            return; // ignorer ligne vide ou non valide
        }

        String[] fields = line.split(";", -1);
        if (fields.length > 66 && !fields[66].isEmpty()) {
            String yearStr = fields[0].substring(0, 4); // AAAAMM → AAAA
            int year = Integer.parseInt(yearStr);
            double temp = Double.parseDouble(fields[66]);

            yearKey.set(year);
            tempValue.set(temp);
            context.write(yearKey, tempValue);
        }
    }
}
