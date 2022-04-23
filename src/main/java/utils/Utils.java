package utils;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.lang.String;

public class Utils {

	static {
		Logger.getLogger("org.apache.spark").setLevel(Level.WARN);
		Logger.getLogger("org.apache.spark.storage.BlockManager").setLevel(Level.ERROR);
	}

    public static void print(String text) {
		System.out.println(text);
	}
    public static void log(String text) {
		print("[ log ] " + text);
	}

}
