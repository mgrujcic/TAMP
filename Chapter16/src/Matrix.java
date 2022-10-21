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

    void add(Matrix lhs, Matrix rhs, Matrix sum){
        if( lhs.dim != rhs.dim 
         || lhs.rowDisplace != rhs.rowDisplace
         || lhs.colDisplace != rhs.colDisplace){

            System.exit(-1); //cba
        }
        for(int i = lhs.rowDisplace; i < lhs.rowDisplace+lhs.dim; i++){
            for(int j = lhs.colDisplace; i < lhs.colDisplace+lhs.dim; j++){
                sum.set(i, j, lhs.get(i, j) + rhs.get(i, j));
            }
        }
    }
}
