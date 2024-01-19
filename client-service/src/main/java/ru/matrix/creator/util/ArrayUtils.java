package ru.matrix.creator.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArrayUtils {

    private static final Logger logger = LoggerFactory.getLogger(ArrayUtils.class);

    private ArrayUtils() {
    }

    public static double[][] getArray(String[] lineArray) {
        double[][] a = null;
        try {
            a = new double[lineArray.length - 1][lineArray.length - 1];
            for (int j = 0; j < lineArray.length - 1; j++) {
                String[] colArray = lineArray[j].split(",");
                double[] col = new double[colArray.length];
                for (int i = 0; i < colArray.length; i++) {
                    col[i] = Double.parseDouble(colArray[i]);
                }
                a[j] = col;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return a;
    }

    public static double[] getVector(String[] lineArray) {
        double[] b = null;
        try {
            b = new double[lineArray.length - 1];
            String[] lastColArray = lineArray[lineArray.length - 1].split(",");
            for (int i = 0; i < lastColArray.length; i++) {
                b[i] = Double.parseDouble(lastColArray[i]);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return b;
    }
}
