import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;

public class IdentityReducer extends Reducer<IntWritable, DoubleWritable, IntWritable, DoubleWritable> {
    @Override
    protected void reduce(IntWritable key, Iterable<DoubleWritable> values, Context context)
            throws IOException, InterruptedException {
        for (DoubleWritable val : values) {
            context.write(key, val); // pas de regroupement ici
        }
    }
}
