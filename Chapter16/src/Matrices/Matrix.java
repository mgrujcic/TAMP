package Matrices;

public class Matrix { // 2^n x 2^n only
    int dim;
    double[][] data;
    int rowDisplace, colDisplace;
    Matrix(int d){
        dim = d;
        rowDisplace = colDisplace = 0;
        data = new double[d][d];
    }
    Matrix(double [][] matrix, int x, int y, int d){
        data = matrix;
        rowDisplace = x;
        colDisplace = y;
        dim = d;
    }
    double get(int row, int col){
        return data[row+rowDisplace][col+colDisplace];
    }
    void set(int row, int col, double value){
        data[row+rowDisplace][col+colDisplace] = value;
    }
    int getDim(){
        return dim;
    }
    Matrix split(int i, int j){
        int newDim = dim / 2;
        return new Matrix(data,
                             rowDisplace + i*newDim, 
                             colDisplace + j*newDim, 
                             newDim);
    }

    static void add(Matrix lhs, Matrix rhs, Matrix sum){
        if( lhs.dim != rhs.dim 
         || lhs.rowDisplace != rhs.rowDisplace
         || lhs.colDisplace != rhs.colDisplace){

            System.exit(-1); //cba
        }
        for(int i = 0; i < lhs.dim; i++){
            for(int j = 0; j < lhs.dim; j++){
                sum.set(i, j, lhs.get(i, j) + rhs.get(i, j));
            }
        }
    }
    static void multiply(Matrix lhs, Matrix rhs, Matrix product){
        if( lhs.dim != rhs.dim 
         || lhs.rowDisplace != rhs.rowDisplace
         || lhs.colDisplace != rhs.colDisplace){

            System.exit(-1); //cba
        }
        for(int i = 0; i < lhs.dim; i++){
            for(int j = 0; j < lhs.dim; j++){
                double res = 0;
                for(int k = 0; k < lhs.dim; k++){
                    res += lhs.get(i, k) * rhs.get(k, j);
                }
                product.set(i, j, res);
            }
        }
        
    }
    @Override
    public String toString() {
        return "Matrix: [ " + this.rowDisplace + ", " + this.colDisplace + " ] , dim: " + this.dim;
    }
}
