package exercise_1;

import com.google.common.collect.Lists;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.graphx.*;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.storage.StorageLevel;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.collection.JavaConverters;
import scala.runtime.AbstractFunction1;
import scala.runtime.AbstractFunction2;
import scala.runtime.AbstractFunction3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import utils.Utils;

public class Exercise_1 {

    private static class VProg extends AbstractFunction3<Long,Integer,Integer,Integer> implements Serializable {
        @Override
        public Integer apply(Long vertexID, Integer vertexValue, Integer message) {
            System.out.println(" VertecID : "+vertexID+" vertexValue : "+vertexValue+" recieved message  :"+message+ " In Vprog");
            if (message == Integer.MAX_VALUE) {             // superstep 0
                System.out.println(vertexValue + " start - max value");
                return vertexValue;
            } else { 
                System.out.println (" maximum value which it will send : "+Math.max(vertexValue,message));  // superstep > 0
                return Math.max(vertexValue,message);
            }
        }
    }

    private static class sendMsg extends AbstractFunction1<EdgeTriplet<Integer,Integer>, Iterator<Tuple2<Object,Integer>>> implements Serializable {
        @Override
        public Iterator<Tuple2<Object, Integer>> apply(EdgeTriplet<Integer, Integer> triplet) {
            
            Tuple2<Object,Integer> sourceVertex = triplet.toTuple()._1();
            Tuple2<Object,Integer> dstVertex = triplet.toTuple()._2();
            System.out.println(" source : "+sourceVertex+" dst vertex : "+ dstVertex +"  In sendmsg");
            if (sourceVertex._2 <= dstVertex._2) {   // source vertex value is smaller than dst vertex?
                // do nothing
                System.out.println(" source value :"+ sourceVertex+ " dest : " +dstVertex + " doing nothing source value is smaller than dst");
                return JavaConverters.asScalaIteratorConverter(new ArrayList<Tuple2<Object,Integer>>().iterator()).asScala();
            } else {
                // propagate source vertex value
                System.out.println(" source value : "+ sourceVertex+ " dest : " +dstVertex + " propagating value ");
                return JavaConverters.asScalaIteratorConverter(Arrays.asList(new Tuple2<Object,Integer>(triplet.dstId(),sourceVertex._2)).iterator()).asScala();
            }
        }
    }

    private static class merge extends AbstractFunction2<Integer,Integer,Integer> implements Serializable {
        
        @Override
        public Integer apply(Integer o, Integer o2) {
            System.out.println(" in merge doing nothing ");
            return null;
        }
    }

    public static void maxValue(JavaSparkContext ctx) {
        // https://github.com/google/guava/wiki/CollectionUtilitiesExplained#static-constructors
        // doc for Lists.newArrayList -> https://guava.dev/releases/19.0/api/docs/com/google/common/collect/Lists.html
        // doc for Tuple2 -> https://www.scala-lang.org/api/2.12.2/scala/Tuple2.html
        List<Tuple2<Object,Integer>> vertices = Lists.newArrayList(
            new Tuple2<Object,Integer>(1l,9),
            new Tuple2<Object,Integer>(2l,1),
            new Tuple2<Object,Integer>(3l,6),
            new Tuple2<Object,Integer>(4l,8)
            );
        // doc for Edge -> https://spark.apache.org/docs/latest/api/java/
        List<Edge<Integer>> edges = Lists.newArrayList(
            new Edge<Integer>(1l,2l, 1),
            new Edge<Integer>(2l,3l, 1),
            new Edge<Integer>(2l,4l, 1),
            new Edge<Integer>(3l,4l, 1),
            new Edge<Integer>(3l,1l, 1)
        );

        //Distribute a local Scala collection to form an RDD.
        JavaRDD<Tuple2<Object,Integer>> verticesRDD = ctx.parallelize(vertices);
        JavaRDD<Edge<Integer>> edgesRDD = ctx.parallelize(edges);

        //https://spark.apache.org/docs/latest/api/java/org/apache/spark/graphx/Graph.html#apply-org.apache.spark.rdd.RDD-org.apache.spark.rdd.RDD-VD-org.apache.spark.storage.StorageLevel-org.apache.spark.storage.StorageLevel-scala.reflect.ClassTag-scala.reflect.ClassTag-
        //https://spark.apache.org/docs/latest/api/java/org/apache/spark/storage/StorageLevel.html
        //Construct a graph from a collection of vertices and edges with attributes. Duplicate vertices are picked arbitrarily and vertices found in the edge collection but not in the input vertices are assigned the default attribute.
        Graph<Integer,Integer> G = Graph.apply(verticesRDD.rdd(),edgesRDD.rdd(),1, StorageLevel.MEMORY_ONLY(), StorageLevel.MEMORY_ONLY(),
                scala.reflect.ClassTag$.MODULE$.apply(Integer.class),scala.reflect.ClassTag$.MODULE$.apply(Integer.class));

        //https://spark.apache.org/docs/latest/api/java/org/apache/spark/graphx/GraphOps.html#GraphOps-org.apache.spark.graphx.Graph-scala.reflect.ClassTag-scala.reflect.ClassTag-
        GraphOps ops = new GraphOps(G, scala.reflect.ClassTag$.MODULE$.apply(Integer.class),scala.reflect.ClassTag$.MODULE$.apply(Integer.class));

        //https://spark.apache.org/docs/latest/api/java/org/apache/spark/graphx/GraphOps.html#pregel-A-int-org.apache.spark.graphx.EdgeDirection-scala.Function3-scala.Function1-scala.Function2-scala.reflect.ClassTag-
        // https://spark.apache.org/docs/latest/api/java/org/apache/spark/graphx/EdgeDirection.html
        Graph<Integer, Integer> output_graph = ops.pregel(Integer.MIN_VALUE, Integer.MAX_VALUE, EdgeDirection.Out(), new VProg(), new sendMsg(), new merge(), scala.reflect.ClassTag$.MODULE$.apply(Integer.class));
        
        //https://spark.apache.org/docs/latest/api/java/org/apache/spark/graphx/Graph.html#vertices--
        VertexRDD<Integer> output_vertices = output_graph.vertices();
        Utils.print("Output graph has '" + output_vertices.count() + "' vertices");
        
        //https://spark.apache.org/docs/latest/api/java/org/apache/spark/rdd/RDD.html#toJavaRDD--
        JavaRDD<Tuple2<Object,Integer>> output_rdd = output_vertices.toJavaRDD();
        
        //https://spark.apache.org/docs/latest/api/java/org/apache/spark/api/java/JavaRDDLike.html#first--
        Tuple2<Object,Integer> max_value = output_rdd.first();

        Utils.print("Vertex '" + max_value._1 + "' has the maximum value in the graph '" + max_value._2 + "'");


        // Tuple2<Long,Integer> max = (Tuple2<Long,Integer>)ops.pregel(
        //         Integer.MAX_VALUE,
        //         Integer.MAX_VALUE,      // Run until convergence
        //         EdgeDirection.Out(),
        //         new VProg(),
        //         new sendMsg(),
        //         new merge(),
        //         scala.reflect.ClassTag$.MODULE$.apply(Integer.class))
        // .vertices().toJavaRDD().first();

        // System.out.println(max._2 + " is the maximum value in the graph");
	}
	
}
