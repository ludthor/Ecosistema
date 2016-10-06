package terrain;

import java.util.Arrays;

public class Territory {

    private float width;
    private float height;
    private int row;
    private int col;
    private int modH,modV;
    private T_Place places[][];

    public Territory( float screenW , float screenH, int row, int col){
    	//inicializa el objeto definiendo la matriz de celdas
    	String [] propertyNames={"test1","test2","test3"};

    	this.width = screenW;
    	this.height = screenH;
    	this.row = row;
    	this.col = col;
    	this.modH = (int) this.width /this.col;
    	this.modV = (int) this.height/this.row;
    	this.places = new T_Place[ col ][ row ];
        for(int i=0;i < col;i++){

            for(int j=0;j<row;j++){

                places[i][j] = new T_Place(i,j, new RandomNumericProperties(Arrays.asList(propertyNames)));

            }

        }

    }
    
    public void printProperties(double mouseX,double mouseY){
    	
    	int modH = (int) (mouseX /(this.width/row));
    	int modV = (int) (mouseY/(this.height/col));
    	this.places[modH][modV].printProperties();
    }
    public void locate( float x , float y , int id ){
    //este comportamiento ubica a cada objeto en su celda

        if( x>0 && x< this.width && y>0 && y<this.height){

            int whichX = (int) x/this.modH;
            //define el lugar horizontal en el que //cae el objeto
            int whichY = (int) y/this.modV;
            //define el lugar vertical en el que cae //el objeto
            whichX = (whichX >= this.col ? this.col-1 : whichX);
            whichY = (whichY >= this.row ? this.row-1 : whichY);
            //agrega el objeto en la celda elegida
            this.places[ whichX ][ whichY ].agregar( x , y , id );

        }
    }
	public float getWidth() {
		return width;
	}
	public void setWidth(float width) {
		this.width = width;
	}
	public float getHeight() {
		return height;
	}
	public void setHeight(float height) {
		this.height = height;
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
	public int getModH() {
		return modH;
	}
	public void setModH(int modH) {
		this.modH = modH;
	}
	public int getModV() {
		return modV;
	}
	public void setModV(int modV) {
		this.modV = modV;
	}
	public T_Place[][] getPlaces() {
		return places;
	}
	public void setPlaces(T_Place[][] places) {
		this.places = places;
	}
}

   	