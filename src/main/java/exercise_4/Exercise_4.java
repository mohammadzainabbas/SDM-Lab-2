package exercise_4;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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
		
		// graphFrame.edges().show();
		// graphFrame.vertices().show();

		// @todo: Benchmark with multiple dumping factor and numIterations
		// --- Ideas ---
		// 1. Define some dumping factors and numIterations and run & time pagerank algo. and select the best
		// For dumping factor
		List<String> time = new ArrayList<String>(Arrays.asList(new String[120]));
		IntStream.range(1, 20).forEach(i -> {
			// For max iterations
			IntStream.range(1, 5).forEach(j -> {
				Double dumpFactor = i * 0.05;
				Integer maxIteration = j * 5;

				Long startTime = System.currentTimeMillis();
				GraphFrame gf = graphFrame.pageRank().resetProbability(dumpFactor).maxIter(maxIteration).run();
				Long endTime = System.currentTimeMillis();
				
				Dataset<Row> topVertices = gf.vertices().sort(org.apache.spark.sql.functions.desc("pagerank"));
				
				String log = "dumping factor: '" + dumpFactor + "' maxIter: '" + maxIteration + "' time: '" + (endTime - startTime) + "' msec\n\n";
				Utils.print(log);
				topVertices.show(10);
				Utils.line_separator();
				time.add(log);
			});
		});
		
		// try {
		// 	Files.write(Paths.get("/root/SDM-Lab-2/src/main/java/exercise_4/output.txt"), time);
		// } catch (IOException e) {
		// 	Utils.print("Unable to save file" + e);
		// }
		
		// GraphFrame gf = graphFrame.pageRank().tol(0.01).resetProbability(0.15).run();
		// GraphFrame gf = graphFrame.pageRank().resetProbability(0.15).maxIter(10).run();
		
		// Utils.line_separator();
		
		// gf.edges().show();
		// gf.vertices().show();
		
		// Utils.line_separator();
		// Dataset<Row> topVertices = gf.vertices().sort(org.apache.spark.sql.functions.desc("pagerank"));
		// topVertices.show(10);

	}
}
