package exercise_4;

import com.google.common.collect.Lists;
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
import org.graphframes.GraphFrame;
import org.apache.spark.rdd.RDD;

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
		
		graphFrame.edges().show();
		graphFrame.vertices().show();
		
        GraphFrame gf = graphFrame.pageRank().resetProbability(0.15).maxIter(10).run();
		
		Utils.line_separator();
		
		gf.edges().show();
		gf.vertices().show();

        // org.graphframes.lib.PageRank pgRank = graph.pageRank().resetProbability(0.15).maxIter(10);
        // GraphFrame pgRankGraph = pgRank.run(); //Run PageRank for a fixed number of iterations returning a graph with vertex attributes containing the PageRank and edge attributes the normalized edge weight.
        
		for (Row rname : gf.vertices().sort(org.apache.spark.sql.functions.desc("pagerank")).toJavaRDD().take(10)) {
		    // Utils.print(rname.getString(1));
		    Utils.print(rname);
        }
	}
}
