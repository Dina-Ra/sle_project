package ru.matrix.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.annotation.PersistenceCreator;
import reactor.util.annotation.NonNull;

@Table("matrix")
public class Matrix {

    @Id
    private final Long id;
    @NonNull
    private final String strMatrix;
    @NonNull
    private final String det;
    @NonNull
    private final String reverse;
    @NonNull
    private final String transpose;
    private final String decomposition;

    @PersistenceCreator
    public Matrix(Long id, @NonNull String strMatrix, @NonNull String det, @NonNull String reverse, @NonNull String transpose, String decomposition) {
        this.id = id;
        this.strMatrix = strMatrix;
        this.det = det;
        this.reverse = reverse;
        this.transpose = transpose;
        this.decomposition = decomposition;
    }

    public Matrix(@NonNull String strMatrix, @NonNull String det, @NonNull String reverse, @NonNull String transpose, String decomposition) {
        this(null, strMatrix, det, reverse, transpose, decomposition);
    }

    public Long getId() {
        return id;
    }

    @NonNull
    public String getStrMatrix() {
        return strMatrix;
    }

    @NonNull
    public String getDet() {
        return det;
    }

    @NonNull
    public String getReverse() {
        return reverse;
    }

    @NonNull
    public String getTranspose() {
        return transpose;
    }

    public String getDecomposition() {
        return decomposition;
    }

    @Override
    public String toString() {
        return "Matrix{" +
                "id=" + id +
                ", strMatrix='" + strMatrix + '\'' +
                ", det='" + det + '\'' +
                ", reverse='" + reverse + '\'' +
                ", transpose='" + transpose + '\'' +
                ", decomposition='" + decomposition + '\'' +
                '}';
    }
}
