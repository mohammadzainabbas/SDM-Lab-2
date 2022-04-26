package exercise_4;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.MetadataBuilder;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.rdd.RDD;
import org.graphframes.GraphFrame;
import java.util.stream.IntStream;
import java.util.ArrayList;
import java.util.List;
import utils.Utils;

public class Exercise_4 {
	
	public static void wikipedia(JavaSparkContext ctx, SQLContext sqlCtx) {

		JavaRDD<String> vertex = ctx.textFile("src/main/resources/wiki-vertices.txt");
		JavaRDD<String> edge = ctx.textFile("src/main/resources/wiki-edges.txt");
		
		StructType verticesSchema = new StructType(new StructField[] {
			new StructField("id", DataTypes.LongType, false, new MetadataBuilder().build()),
			new StructField("name", DataTypes.StringType, false, new MetadataBuilder().build())
		});

		StructType edgesSchema = new StructType(new StructField[] {
			new StructField("src", DataTypes.LongType, false, new MetadataBuilder().build()),
			new StructField("dst", DataTypes.LongType, false, new MetadataBuilder().build())
		});
		
		RDD<Row> vertexRDD = vertex.map(r -> RowFactory.create(Long.parseLong(r.split("\t")[0]), r.split("\t")[1])).rdd();
		RDD<Row> edgeRDD = edge.map(r -> RowFactory.create(Long.parseLong(r.split("\t")[0]), Long.parseLong(r.split("\t")[1]))).rdd();
		
		Dataset<Row> vertices = sqlCtx.createDataFrame(vertexRDD, verticesSchema);
		Dataset<Row> edges = sqlCtx.createDataFrame(edgeRDD, edgesSchema);
		
		GraphFrame graphFrame = GraphFrame.apply(vertices, edges);
		
		Utils.line_separator();
		
		// For dumping factor
		List<Row> timeList = new ArrayList<Row>();
		StructType outputSchema = new StructType(new StructField[] {
			new StructField("dumping_factor", DataTypes.DoubleType, false, new MetadataBuilder().build()),
			new StructField("maxIter", DataTypes.IntegerType, false, new MetadataBuilder().build()),
			new StructField("time", DataTypes.LongType, false, new MetadataBuilder().build())
		});
		IntStream.range(1, 20).forEach(i -> {
			// For max iterations
			IntStream.range(1, 5).forEach(j -> {
				Double dumpFactor = i * 0.05;
				Integer maxIteration = j * 5;

				Long startTime = System.currentTimeMillis();
				GraphFrame gf = graphFrame.pageRank().resetProbability(dumpFactor).maxIter(maxIteration).run();
				Long endTime = System.currentTimeMillis();
				
				Long timeTaken = endTime - startTime;
				timeList.add(RowFactory.create(dumpFactor, maxIteration, timeTaken));

				Dataset<Row> topVertices = gf.vertices().sort(org.apache.spark.sql.functions.desc("pagerank"));
				
				String log = "dumping factor: '" + dumpFactor + "' maxIter: '" + maxIteration + "' time: '" + timeTaken + "' msec\n\n";
				Utils.print(log);
				topVertices.show(10);
				Utils.line_separator();
			});
		});
		
		Utils.line_separator();
		Dataset<Row> output = sqlCtx.createDataFrame(ctx.parallelize(timeList), outputSchema);
		Long count = output.count();
		output.show(count.intValue());
		Utils.line_separator();
	}
}
