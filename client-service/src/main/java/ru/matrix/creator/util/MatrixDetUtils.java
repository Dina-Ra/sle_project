package ru.matrix.creator.util;

public class MatrixDetUtils {
    private MatrixDetUtils() {
    }

    public static double getMatrixDet(double[][] u, int count_reverse) {
        double det = Math.pow(-1, count_reverse);
        for (int i = 0; i < u.length; i++) {
            det = det * u[i][i];
        }
        return det;
    }
}
