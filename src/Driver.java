import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Driver {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: Driver [decade|top5|yearly] <input_path> <output_path>");
            System.exit(1);
        }

        Configuration conf = new Configuration();
        Job job;
        String jobType;
        String inputPath;
        String outputPath;

        if (args.length == 2) {
            jobType = "decade";
            inputPath = args[0];
            outputPath = args[1];
        } else {
            jobType = args[0].toLowerCase();
            inputPath = args[1];
            outputPath = args[2];
        }

        if (jobType.equals("top5")) {
            job = Job.getInstance(conf, "Top5Years");
            job.setJarByClass(Driver.class);
            job.setMapperClass(Top5YearsMapper.class);
            job.setReducerClass(Top5YearsReducer.class);
            job.setMapOutputKeyClass(IntWritable.class);
            job.setMapOutputValueClass(DoubleWritable.class);
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(Text.class);
            job.setNumReduceTasks(1);  // Un seul reducer pour le top 5
        } else if (jobType.equals("yearly")) {
            job = Job.getInstance(conf, "TemperatureByYear");
            job.setJarByClass(Driver.class);
            job.setMapperClass(TemperatureByYearMapper.class);
            job.setReducerClass(IdentityReducer.class);  // ← juste passer les paires
            job.setMapOutputKeyClass(IntWritable.class);
            job.setMapOutputValueClass(DoubleWritable.class);
            job.setOutputKeyClass(IntWritable.class);
            job.setOutputValueClass(DoubleWritable.class);
            job.setNumReduceTasks(1);  // facultatif, mais pratique ici
        } else {
            job = Job.getInstance(conf, "AverageByDecade");
            job.setJarByClass(Driver.class);
            job.setMapperClass(AverageByDecadeMapper.class);
            job.setReducerClass(AverageByDecadeReducer.class);
            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(DoubleWritable.class);
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(Text.class);
        }

        FileInputFormat.setInputPaths(job, new Path(inputPath));
        FileOutputFormat.setOutputPath(job, new Path(outputPath));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
