package Gesture;

import java.io.Serializable;

public class SensorValue  implements Serializable{

	private double X, Y, Z;
	
	public SensorValue(){
		
	}
	

	
	
	
	
	public SensorValue(double valX, double valY, double valZ){ // constructor
		this.X = valX;
		this.Y = valY;
		this.Z = valZ;
	}
	

	
	public double getX(){
		return this.X;
	}
	
	public double getY(){
		return this.Y;
	}
	
	public double getZ(){
		return this.Z;
	}
	public void setX(double x){
		this.X = x;
	}
	public void setY(double y){
		this.Y = y;
	}
	public void setZ(double z){
		this.Z = z;
	}
	
}
