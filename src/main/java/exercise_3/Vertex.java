package exercise_3;

import java.util.List;
import java.util.ArrayList;
import scala.Tuple2;

public class Vertex extends Tuple2<Integer, List<String>> {

    public Vertex() { super(0, new ArrayList<String>()); }
    public Vertex(Integer num) { super(num, new ArrayList<String>()); }
    public Vertex(Integer num, List<String> str_list) { super(num, str_list); }

    boolean equals(Vertex vertex) { return this._1.equals(vertex._1) && this._2.equals(vertex._2); }
}
