package terrain;

import interfaces.PlacePropertiesManagerI;

public 
class T_Place{

    int total;
    int limit;
    int row,col;
    float x[], y[];
    int id[];
    PlaceProperties placeProperties;
    PlacePropertiesManagerI placePropertiesManager;

    T_Place( int col_ , int fila_,PlacePropertiesManagerI placePropertiesManager ){

        row = fila_;
        col = col_;
        total = 0;
        limit = 100;
        x = new float[limit];
        y = new float[limit];
        id = new int[limit];
        placeProperties = placePropertiesManager.startProperties();
        

    }

    void agregar( float x_ , float y_ , int id_ ){

        if( total < limit-1 ){

            x[ total ] = x_;
            y[ total ] = y_;
            id[ total ] = id_;
            total ++;

        }

    }

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public float[] getX() {
		return x;
	}

	public void setX(float[] x) {
		this.x = x;
	}

	public float[] getY() {
		return y;
	}

	public void setY(float[] y) {
		this.y = y;
	}

	public int[] getId() {
		return id;
	}

	public void setId(int[] id) {
		this.id = id;
	}
	
	public void printProperties(){
		System.out.println("----------------- "+row+" "+col);
		for(String s :placeProperties.getPropertyNames()){
			System.out.println(s+" "+placeProperties.getDoubleProperty(s));
		}
		System.out.println("-----------------");
		
	}

}  
