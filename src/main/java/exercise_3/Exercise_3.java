package exercise_3;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.graphx.*;
import org.apache.spark.storage.StorageLevel;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.collection.JavaConverters;
import scala.reflect.ClassTag$;
import scala.runtime.AbstractFunction1;
import scala.runtime.AbstractFunction2;
import scala.runtime.AbstractFunction3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import utils.Utils;

public class Exercise_3 {


    // Initial value for pregel execution
    static final Integer INITIAL_VALUE = Integer.MAX_VALUE;

    private static class VProg extends AbstractFunction3<Long,Integer,Integer,Integer> implements Serializable {
        @Override
        public Integer apply(Long vertexID, Integer vertexValue, Integer message) {
            Utils.print("[ VProg.apply ] vertexID: '" +  vertexID +  "' vertexValue: '" +  vertexValue + "' message: '" + message + "'" );
            // First superstep
            if (message.equals(INITIAL_VALUE)) {
                Utils.print("[ VProg.apply ] First superstep -> vertexID: '" +  vertexID +  "'");
                return vertexValue;
            } else {
            // Other supersteps
                Utils.print("[ VProg.apply ] vertexID: '" +  vertexID +  "' will send '" + Math.min(vertexValue, message) + "' value");
                return Math.min(vertexValue, message);
            }
        }
    }

    private static class sendMsg extends AbstractFunction1<EdgeTriplet<Integer,Integer>, Iterator<Tuple2<Object,Integer>>> implements Serializable {
        @Override
        public Iterator<Tuple2<Object, Integer>> apply(EdgeTriplet<Integer, Integer> triplet) {

            Long srcId = triplet.srcId();
            Long dstId = triplet.dstId();
            Integer weight = triplet.attr();
            Integer srcVertex = triplet.srcAttr();
            Integer descVertex = triplet.dstAttr();
            
            if ( srcVertex.equals(INITIAL_VALUE) ) {
                Utils.print("[ sendMsg.apply ] srcId: '" +  srcId +  " [" + srcVertex + "]' will send nothing to dstId: '" + dstId + " [" + descVertex + "]'");
                return JavaConverters.asScalaIteratorConverter(new ArrayList<Tuple2<Object,Integer>>().iterator()).asScala();
            } else {
                Integer value_to_send = srcVertex + weight;
                Utils.print("[ sendMsg.apply ] srcId: '" +  srcId +  " [" + srcVertex + "]' will send '" + value_to_send + "' to dstId: '" + dstId + " [" + descVertex + "]'");
                return JavaConverters.asScalaIteratorConverter(Arrays.asList(new Tuple2<Object,Integer>(triplet.dstId(), value_to_send)).iterator()).asScala();
            }
        }
    }

    private static class merge extends AbstractFunction2<Integer,Integer,Integer> implements Serializable {
        @Override
        public Integer apply(Integer msg1, Integer msg2) {
            Utils.print("[ merge.apply ] msg1: '" +  msg1 + "' msg2: '" + msg2 + "' -- do nothing");
            return null;
        }
    }

    public static void shortestPathsExt(JavaSparkContext ctx) {
        Utils.log("Create labels, vertices and edges");
        Map<Long, String> labels = ImmutableMap.<Long, String>builder()
                .put(1l, "A")
                .put(2l, "B")
                .put(3l, "C")
                .put(4l, "D")
                .put(5l, "E")
                .put(6l, "F")
                .build();

        List<Tuple2<Object,Integer>> vertices = Lists.newArrayList(
                new Tuple2<Object,Integer>(1l,0),
                new Tuple2<Object,Integer>(2l,Integer.MAX_VALUE),
                new Tuple2<Object,Integer>(3l,Integer.MAX_VALUE),
                new Tuple2<Object,Integer>(4l,Integer.MAX_VALUE),
                new Tuple2<Object,Integer>(5l,Integer.MAX_VALUE),
                new Tuple2<Object,Integer>(6l,Integer.MAX_VALUE)
        );
        List<Edge<Integer>> edges = Lists.newArrayList(
                new Edge<Integer>(1l,2l, 4), // A --> B (4)
                new Edge<Integer>(1l,3l, 2), // A --> C (2)
                new Edge<Integer>(2l,3l, 5), // B --> C (5)
                new Edge<Integer>(2l,4l, 10), // B --> D (10)
                new Edge<Integer>(3l,5l, 3), // C --> E (3)
                new Edge<Integer>(5l, 4l, 4), // E --> D (4)
                new Edge<Integer>(4l, 6l, 11) // D --> F (11)
        );

        Utils.log("Create RDD for vertices and edges");
        JavaRDD<Tuple2<Object,Integer>> verticesRDD = ctx.parallelize(vertices);
        JavaRDD<Edge<Integer>> edgesRDD = ctx.parallelize(edges);

        Utils.log("Create Graph from vertices and edges");
        Graph<Integer,Integer> G = Graph.apply(verticesRDD.rdd(),edgesRDD.rdd(),1, StorageLevel.MEMORY_ONLY(), StorageLevel.MEMORY_ONLY(),
                scala.reflect.ClassTag$.MODULE$.apply(Integer.class),scala.reflect.ClassTag$.MODULE$.apply(Integer.class));

        GraphOps ops = new GraphOps(G, scala.reflect.ClassTag$.MODULE$.apply(Integer.class),scala.reflect.ClassTag$.MODULE$.apply(Integer.class));

        String srcLabel = labels.get(1l);

        Utils.log("Run pregel over our graph with apply, scatter and gather functions");
        Utils.line_separator();
        
        JavaRDD<Tuple2<Object,Integer>> output_rdd = ops.pregel(
            INITIAL_VALUE,
            Integer.MAX_VALUE,
            EdgeDirection.Out(),
            new VProg(),
            new sendMsg(),
            new merge(),
            ClassTag$.MODULE$.apply(Integer.class))
            .vertices()
            .toJavaRDD();
            
        Utils.line_separator();
        
        output_rdd
            .sortBy(f -> ((Tuple2<Object, Integer>) f)._1, true, 0)
            .foreach(v -> {

                Tuple2<Object,Integer> vertex = (Tuple2<Object,Integer>) v;
                Long vertexId = (Long) vertex._1;
                Integer cost = (Integer) vertex._2;
                String descLabel = labels.get(vertexId);

                Utils.print("Minimum cost to get from '" + srcLabel + "' to '" + descLabel + "' is " + cost);
            });
    }
	
}
