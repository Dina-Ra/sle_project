package matrix.creator;

import matrix.creator.util.*;
import matrix.domain.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class MatrixCreateServiceImpl implements MatrixCreateService {

    private static final Logger logger = LoggerFactory.getLogger(MatrixCreateServiceImpl.class);

    private static final String DET = "det";
    private static final String REVERSE = "reverse";
    private static final String TRANSPOSE = "transpose";
    private static final String DECOMPOSITION = "decomposition";
    private static final String EMPTY_STRING = "";

    public MatrixCreateServiceImpl() {
    }

    @Override
    public Matrix getMatrixDetail(String strMatrix) throws MatrixDetailCreatorException {
        Map<String, Object> objectMap = new HashMap<>();
        getMapMatrixDetail(strMatrix, objectMap);

        String reverse = Arrays.stream((double[][]) objectMap.get(REVERSE))
                .map(Arrays::toString)
                .collect(Collectors.joining());

        String transpose = Arrays.stream((double[][]) objectMap.get(TRANSPOSE))
                .map(Arrays::toString)
                .collect(Collectors.joining());


        String decomposition = EMPTY_STRING;
        if (Objects.nonNull(objectMap.get(DECOMPOSITION))) {
            StringJoiner stringJoiner = new StringJoiner(",");
            double[] decompositionArray = (double[]) objectMap.get(DECOMPOSITION);
            for (int i = 0; i < decompositionArray.length; i++) {
                stringJoiner.add("x" + (i + 1) + " = " + decompositionArray[i]);
            }
            decomposition = stringJoiner.toString();
        }

        return new Matrix(strMatrix, String.valueOf((double) objectMap.get(DET)), reverse, transpose, decomposition);
    }

    private void getMapMatrixDetail(String strMatrix, Map<String, Object> objectMap) throws MatrixDetailCreatorException {
        String[] lineArray = strMatrix.split("\\|");

        double[][] a = ArrayUtils.getArray(lineArray);
        double[] b = ArrayUtils.getVector(lineArray);

        if (Objects.isNull(a) || Objects.isNull(b))
            throw new MatrixDetailCreatorException("Array A OR Vector B cannot be null");

        logger.info("matrix A: {}", Arrays.stream(a).map(Arrays::toString).collect(Collectors.joining()));
        logger.info("vector b: {}", Arrays.toString(b));

        decompositionLU(a, b, objectMap);
        transposeMatrix(a, objectMap);
    }

    private void decompositionLU(double[][] a, double[] b, Map<String, Object> objectMap) throws MatrixDetailCreatorException {
        /*
            Решение СЛАУ LU-разложение матрицы
            LUx = b; Ux = z; Lz = b;
        */
        MatrixLU matrixLU = MatrixLUUtils.getMatrixLU(a, b);
        if (matrixLU.reverse_b() != null) {
            b = Arrays.copyOf(matrixLU.reverse_b(), matrixLU.reverse_b().length);
        }

        detMatrix(a, b, matrixLU.u(), matrixLU.l(), matrixLU.reverse_count(), objectMap);
    }

    private void detMatrix(double[][] a, double[] b, double[][] u, double[][] l, int count_reverse, Map<String, Object> objectMap) throws MatrixDetailCreatorException {
        /*
            вычисление определителя матрицы
         */
        double det = MatrixDetUtils.getMatrixDet(u, count_reverse);
        logger.info("determinant matrix A = {}", det);
        objectMap.put(DET, det);
        resultSLE(a, b, u, l, det, objectMap);
    }

    private void resultSLE(double[][] a, double[] b, double[][] u, double[][] l, double det, Map<String, Object> objectMap) throws MatrixDetailCreatorException {
        /*
            решение СЛАУ
         */
        double[] x = null;
        if (det != 0.0) {
            x = MatrixDecompositionUtils.getDecomposition(l, u, b);
            logger.info("solution SLE: {}", Arrays.toString(x));
        }
        objectMap.put(DECOMPOSITION, x);
        reverseMatrix(a, u, l, objectMap);
    }

    private void reverseMatrix(double[][] a, double[][] u, double[][] l, Map<String, Object> objectMap) throws MatrixDetailCreatorException {
        /*
            Обратная матрица a_
         */
        double[][] a_ = MatrixReverseUtil.getMatrixReverse(u, l);
        logger.info("reverse matrix A: {}", Arrays.stream(a_).map(Arrays::toString).collect(Collectors.joining()));
        objectMap.put(REVERSE, a_);

        /*
            проверка результата
            a * a_ = e;
         */
        boolean checkResult = checkResult(a, a_);
        logger.info("result is valid: {}", checkResult);
    }

    private boolean checkResult(double[][] a, double[][] a_) throws MatrixDetailCreatorException {
        try {
            double[][] multiplicationAandA_ = new double[a.length][a.length];
            for (int k = 0; k < a.length; k++) {
                for (int i = 0; i < a.length; i++) {
                    double element = 0.0;
                    for (int j = 0; j < a[i].length; j++) {
                        element = element + a[k][j] * a_[j][i];
                    }
                    multiplicationAandA_[k][i] = (double) Math.round(element * 10) / 10; // производим округление вещественного числа
                }
            }

            // единичная матрица
            double[][] e = new double[a.length][a.length];
            for (int i = 0; i < e.length; i++) {
                for (int j = 0; j < e.length; j++) {
                    if (i == j) {
                        e[i][j] = 1;
                    } else {
                        e[i][j] = 0;
                    }
                }
            }

            logger.info("A * A_: {}",
                    Arrays.stream(multiplicationAandA_).map(Arrays::toString).collect(Collectors.joining()));

            return Arrays.deepEquals(e, multiplicationAandA_);
        } catch (Exception ex) {
            throw new MatrixDetailCreatorException(ex.getMessage());
        }
    }

    private void transposeMatrix(double[][] a, Map<String, Object> objectMap) throws MatrixDetailCreatorException {
        // транспонирование матрицы A
        double[][] a_t = MatrixTransposeUtils.getMatrixTranspose(a);
        logger.info("transponder matrix A: {}", Arrays.stream(a_t).map(Arrays::toString).collect(Collectors.joining()));
        objectMap.put(TRANSPOSE, a_t);
    }
}
