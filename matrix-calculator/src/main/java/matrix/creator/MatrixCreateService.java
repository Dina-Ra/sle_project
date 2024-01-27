package matrix.creator;

import matrix.domain.Matrix;

public interface MatrixCreateService {
    Matrix getMatrixDetail(String strMatrix) throws MatrixDetailCreatorException;
}
