import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;
import java.text.DecimalFormat;

public class AverageByDecadeReducer extends Reducer<Text, DoubleWritable, Text, Text> {
    private DecimalFormat df = new DecimalFormat("0.00");

    @Override
    protected void reduce(Text key, Iterable<DoubleWritable> values, Context context)
            throws IOException, InterruptedException {
        double sum = 0.0;
        long count = 0;
        for (DoubleWritable val : values) {
            sum += val.get();
            count++;
        }
        if (count > 0) {
            double avg = sum / count;
            Text avgText = new Text(df.format(avg));
            context.write(key, avgText);
        }
    }
}
