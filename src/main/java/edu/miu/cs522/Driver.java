package edu.miu.cs522;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class Driver extends Configured implements Tool {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();

        // Check first if the directory exists
        String location = args[1];
        Path path = new Path(location);
        FileSystem fs = path.getFileSystem(conf);

        if (fs.exists(path)) {
            fs.delete(path, true);
            System.out.println("Output directory deleted!");
        }

        int res = ToolRunner.run(conf, new Driver(), args);

        System.exit(res);
    }

    @Override
    public int run(String[] args) throws Exception {
        Job job = new Job(getConf(), "Word Count");
        job.setJarByClass(Driver.class);

        job.setMapperClass(WordCountMapper.class);
        job.setReducerClass(WordCountReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        return job.waitForCompletion(true) ? 0 : 1;
    }
}