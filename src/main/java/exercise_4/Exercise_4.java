package exercise_4;

import com.clearspring.analytics.util.Lists;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.graphframes.GraphFrame;

import java.util.List;
import utils.Utils;

public class Exercise_4 {
	
	public static void wikipedia(JavaSparkContext ctx, SQLContext sqlCtx) {
		//https://spark.apache.org/docs/latest/api/java/org/apache/spark/graphx/GraphLoader.html#edgeListFile-org.apache.spark.SparkContext-java.lang.String-boolean-int-org.apache.spark.storage.StorageLevel-org.apache.spark.storage.StorageLevel-

		JavaRDD<String> vertex = ctx.textFile("src/main/resources/wiki-vertices.txt"); //getting the data for vertices from this file
		JavaRDD<String> edge = ctx.textFile("src/main/resources/wiki-edges.txt"); //getting the data for edges from this file

		List<StructField> ListVertex = Lists.newArrayList(); //StructField class is used to programmatically specify the schema to the DataFrame
		ListVertex.add(DataTypes.createStructField("id",DataTypes.LongType,false));
		ListVertex.add(DataTypes.createStructField("name",DataTypes.StringType,false));
		StructType forVertex = DataTypes.createStructType(ListVertex); //StructType is a collection of StructField

		List<StructField> ListEdge = Lists.newArrayList();
		ListEdge.add(DataTypes.createStructField("src",DataTypes.LongType,false));
		ListEdge.add(DataTypes.createStructField("dst",DataTypes.LongType,false));
		StructType forEdge = DataTypes.createStructType(ListEdge);

		Dataset<Row> v1 = sqlCtx.createDataFrame(vertex.map(v ->  //Dataset is a distributed collection of data
		RowFactory.create(Long.parseLong(v.split("\t")[0]),v.split("\t")[1])),forVertex); //A factory class used to construct Row objects
        Dataset<Row> e1 = sqlCtx.createDataFrame(edge.map(e ->
		RowFactory.create(Long.parseLong(e.split("\t")[0]),Long.parseLong(e.split("\t")[1]))),forEdge);
        GraphFrame graph = GraphFrame.apply(v1,e1);

		Utils.print(graph);

		graph.edges().show();
		graph.vertices().show();
		
        GraphFrame gf = graph.pageRank().resetProbability(0.15).maxIter(10).run();

		gf.edges().show();
		gf.vertices().show();

        // org.graphframes.lib.PageRank pgRank = graph.pageRank().resetProbability(0.15).maxIter(10);
        // GraphFrame pgRankGraph = pgRank.run(); //Run PageRank for a fixed number of iterations returning a graph with vertex attributes containing the PageRank and edge attributes the normalized edge weight.
        
		for (Row rname : gf.vertices().sort(org.apache.spark.sql.functions.desc("pagerank")).toJavaRDD().take(10)) {
		    Utils.print(rname.getString(1));
        }
	}
}
