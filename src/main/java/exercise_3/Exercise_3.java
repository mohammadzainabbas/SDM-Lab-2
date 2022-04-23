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
import shapeless.Tuple;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import utils.Utils;
import exercise_3.Vertex;

public class Exercise_3 {
    
    // Initial value for pregel execution
    // static final Tuple2<Integer, List<String>> INITIAL_VALUE = new Tuple2<Integer, List<String>>(Integer.MAX_VALUE, new ArrayList<String>());
    static final Vertex INITIAL_VALUE = new Vertex(Integer.MAX_VALUE);
    
    // Nodes' Labels 
    static final Map<Long, String> labels = ImmutableMap.<Long, String>builder()
            .put(1l, "A")
            .put(2l, "B")
            .put(3l, "C")
            .put(4l, "D")
            .put(5l, "E")
            .put(6l, "F")
            .build();

    private static class VProg extends AbstractFunction3<Long,Vertex,Vertex,Vertex> implements Serializable {
        @Override
        public Vertex apply(Long vertexID, Vertex vertexValue, Vertex message) {

            String node = labels.get(vertexID);
            Integer nodeCost = vertexValue._1;
            Integer incomingCost = message._1;

            Utils.print("[ VProg.apply ] node: '" + node + "' vertexID: '" +  vertexID +  "' vertexValue: '" +  nodeCost + "' message: '" + incomingCost + "'" );
            // First superstep
            if (message.equals(INITIAL_VALUE)) {
                Utils.print("[ VProg.apply ] First superstep -> vertexID: '" +  vertexID +  "'");
                return vertexValue;
            } else {
            // Other supersteps
                Utils.print("[ VProg.apply ] vertexID: '" +  vertexID +  "' will send '" + Math.min(nodeCost, incomingCost) + "' value");
                
                if (nodeCost >= incomingCost) {
                    return message;
                } else {
                    return vertexValue;
                }
            }
        }
    }

    private static class sendMsg extends AbstractFunction1<EdgeTriplet<Vertex,Integer>, Iterator<Tuple2<Object,Vertex>>> implements Serializable {
        @Override
        public Iterator<Tuple2<Object, Vertex>> apply(EdgeTriplet<Vertex, Integer> triplet) {

            Long srcId = triplet.srcId();
            Long dstId = triplet.dstId();
            Integer weight = triplet.attr();

            String srcNode = labels.get(srcId);
            // String dstNode = labels.get(dstId);

            Vertex srcVertex = triplet.srcAttr();
            Vertex descVertex = triplet.dstAttr();
            
            if ( srcVertex.equals(INITIAL_VALUE) ) {
                Utils.print("[ sendMsg.apply ] srcId: '" +  srcId +  " [" + srcVertex + "]' will send nothing to dstId: '" + dstId + " [" + descVertex + "]'");
                return JavaConverters.asScalaIteratorConverter(new ArrayList<Tuple2<Object,Vertex>>().iterator()).asScala();
            } else {
                List<String> srcList = new ArrayList<String>(srcVertex._2);
                srcList.add(srcNode);
                // srcList.add(dstNode);
                Vertex value_to_send = new Vertex(srcVertex._1 + weight, srcList);
                // Vertex value_to_send = srcVertex + weight;
                Utils.print("[ sendMsg.apply ] srcId: '" +  srcId +  " [" + srcVertex + "]' will send '" + value_to_send._1 + "' to dstId: '" + dstId + " [" + descVertex + "]'");
                return JavaConverters.asScalaIteratorConverter(Arrays.asList(new Tuple2<Object,Vertex>(triplet.dstId(), value_to_send)).iterator()).asScala();
            }
        }
    }

    private static class merge extends AbstractFunction2<Vertex,Vertex,Vertex> implements Serializable {
        @Override
        public Vertex apply(Vertex msg1, Vertex msg2) {
            Utils.print("[ merge.apply ] msg1: '" +  msg1 + "' msg2: '" + msg2 + "' -- do nothing");
            return null;
        }
    }

    public static void shortestPathsExt(JavaSparkContext ctx) {
        Utils.log("Create labels, vertices and edges");

        List<Tuple2<Object, Vertex>> vertices = Lists.newArrayList(
            new Tuple2<Object, Vertex>(1l, new Vertex(0)),
            new Tuple2<Object, Vertex>(2l, new Vertex(Integer.MAX_VALUE)),
            new Tuple2<Object, Vertex>(3l, new Vertex(Integer.MAX_VALUE)),
            new Tuple2<Object, Vertex>(4l, new Vertex(Integer.MAX_VALUE)),
            new Tuple2<Object, Vertex>(5l, new Vertex(Integer.MAX_VALUE)),
            new Tuple2<Object, Vertex>(6l, new Vertex(Integer.MAX_VALUE))
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
        JavaRDD<Tuple2<Object, Vertex>> verticesRDD = ctx.parallelize(vertices);
        JavaRDD<Edge<Integer>> edgesRDD = ctx.parallelize(edges);

        Utils.log("Create Graph from vertices and edges");
        Graph<Vertex, Integer> G = Graph.apply(verticesRDD.rdd(),edgesRDD.rdd(), new Vertex(), StorageLevel.MEMORY_ONLY(), StorageLevel.MEMORY_ONLY(),
                scala.reflect.ClassTag$.MODULE$.apply(Vertex.class),scala.reflect.ClassTag$.MODULE$.apply(Integer.class));

        GraphOps ops = new GraphOps(G, scala.reflect.ClassTag$.MODULE$.apply(Vertex.class),scala.reflect.ClassTag$.MODULE$.apply(Integer.class));

        String srcLabel = labels.get(1l);

        Utils.log("Run pregel over our graph with apply, scatter and gather functions");
        Utils.line_separator();
        
        JavaRDD<Tuple2<Object, Vertex>> output_rdd = ops.pregel(
            INITIAL_VALUE,
            Integer.MAX_VALUE,
            EdgeDirection.Out(),
            new VProg(),
            new sendMsg(),
            new merge(),
            ClassTag$.MODULE$.apply(Vertex.class))
            .vertices()
            .toJavaRDD();
            
        Utils.line_separator();
        
        output_rdd
            .sortBy(f -> ((Tuple2<Object, Vertex>) f)._1, true, 0)
            .foreach(v -> {

                Tuple2<Object, Vertex> vertex = (Tuple2<Object, Vertex>) v;
                Long vertexId = (Long) vertex._1;
                Vertex value = (Vertex) vertex._2;
                Integer cost = value._1;
                List<String> path = value._2;
                String descLabel = labels.get(vertexId);
                path.add(descLabel);

                Utils.print("Shortest path to get from '" + srcLabel + "' to '" + descLabel + "' is " + path + " with cost " + cost);
            });
    }
	
}
