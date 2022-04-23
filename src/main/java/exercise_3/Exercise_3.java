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

    }
	
}
