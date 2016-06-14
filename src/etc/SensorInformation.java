package etc;

import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;

public class SensorInformation {
	private static final String				TAG_NAME									= "SensorInformation";
	private static final int 				HANDLER_WHAT_SEND_TO_CLIENT					= 1100;
	private static final int 				DATA_X										= 0;
	private static final int 				DATA_Y										= 1;
	private static final int 				DATA_Z										= 2;
	private static final int 				DATA_W										= 3;
	
	private static final float				ALPHA_LH									= 0.9f;
	private static final float				ALPHA_L										= 0.1f;
	
	private static final int				SENSITIVE_GRADE_HIGHEST						= 5;
	private static final int				SENSITIVE_GRADE_HIGH						= 4;
	private static final int				SENSITIVE_GRADE_NORMAL						= 3;
	private static final int				SENSITIVE_GRADE_LOW							= 2;
	private static final int				SENSITIVE_GRADE_LOWEST						= 1;
	
	private static final int				KALMAN_GYRO									= 100;
	private static final int				KALMAN_ACCELEROMETER						= 200;
	private static final int				KALMAN_MAGNETIC								= 300;
	
	private static final int				LOW_GYR										= 110;
	private static final int				LOW_ACC										= 120;
	
	private Handler							mainHandler									= null;
	
	
	// Cunstructor
	public SensorInformation(){
		//default
	}
	
	public SensorInformation(int[] deviceSize) {
		
	}
	
	public SensorInformation(Handler mainHandler) {
		this.mainHandler = mainHandler;
	}
	
	public void setHandler(Handler handler){
		this.mainHandler = handler;
	}
	
	/*
	 * Set magnetic array.
	 */
	private float[] 						arrMagnetic 								= new float[3];
	private boolean							isMagnetic									= false;
	public void setGeoMagnetic(float[] values){
		arrMagLowPassFilter[DATA_X] = ALPHA_L * arrMagLowPassFilter[DATA_X] + (1 - ALPHA_L) * values[DATA_X];
		arrMagLowPassFilter[DATA_Y] = ALPHA_L * arrMagLowPassFilter[DATA_Y] + (1 - ALPHA_L) * values[DATA_Y];
		arrMagLowPassFilter[DATA_Z] = ALPHA_L * arrMagLowPassFilter[DATA_Z] + (1 - ALPHA_L) * values[DATA_Z];
		System.arraycopy(arrMagLowPassFilter, 0, arrMagnetic, 0, arrMagLowPassFilter.length);
	}
	
	/*
	 * Set accelerometer array.
	 */
	private float[] 						arrAccelerometer							= new float[3];
	private boolean							isAcc										= false;
	public void setAccelerometer(float[] values) {
		arrAccFirstLowPassFilter[DATA_X] = ALPHA_L * arrAccFirstLowPassFilter[DATA_X] + (1 - ALPHA_L) * values[DATA_X];
		arrAccFirstLowPassFilter[DATA_Y] = ALPHA_L * arrAccFirstLowPassFilter[DATA_Y] + (1 - ALPHA_L) * values[DATA_Y];
		arrAccFirstLowPassFilter[DATA_Z] = ALPHA_L * arrAccFirstLowPassFilter[DATA_Z] + (1 - ALPHA_L) * values[DATA_Z];
		System.arraycopy(values, 0, arrAccelerometer, 0, values.length);
		arrAccelerometer = setLowAndHighPassFilter(values, LOW_ACC);
		arrAccelerometer[0] = values[0] - arrAccelerometer[0];
		arrAccelerometer[1] = values[1] - arrAccelerometer[1];
		arrAccelerometer[2] = values[2] - arrAccelerometer[2];
	}
	
	
	
	/*
	 * Low and High Pass Filter. 
	 * The description of this method is in the Android Developer Guide.
	 */
	private float[] 						arrAccFirstLowPassFilter 					= new float[3];
	private float[] 						arrAccLastLowPassFilter 					= new float[3];
	private float[] 						arrMagLowPassFilter 						= new float[3];
	private float[] 						arrGyrLowPassFilter 						= new float[3];
	private float[] 						arrAccTemp									= new float[3];
	private float[] 						arrGyrTemp									= new float[3];
	private float[] setLowAndHighPassFilter(float[] values, int lFlag){
		switch(lFlag){
		case LOW_ACC:
			arrAccLastLowPassFilter[DATA_X] = ALPHA_LH * arrAccLastLowPassFilter[DATA_X] + (1.0f - ALPHA_LH) * values[DATA_X];
			arrAccLastLowPassFilter[DATA_Y] = ALPHA_LH * arrAccLastLowPassFilter[DATA_Y] + (1.0f - ALPHA_LH) * values[DATA_Y];
			arrAccLastLowPassFilter[DATA_Z] = ALPHA_LH * arrAccLastLowPassFilter[DATA_Z] + (1.0f - ALPHA_LH) * values[DATA_Z];
			arrAccTemp[DATA_X] = values[DATA_X] - arrAccLastLowPassFilter[DATA_X];
			arrAccTemp[DATA_Y] = values[DATA_Y] - arrAccLastLowPassFilter[DATA_Y];
			arrAccTemp[DATA_Z] = values[DATA_Z] - arrAccLastLowPassFilter[DATA_Z];
			return arrAccTemp;
		case LOW_GYR:
			arrGyrLowPassFilter[DATA_X] = ALPHA_LH * arrGyrLowPassFilter[DATA_X] + (1.0f - ALPHA_LH) * values[DATA_X];
			arrGyrLowPassFilter[DATA_Y] = ALPHA_LH * arrGyrLowPassFilter[DATA_Y] + (1.0f - ALPHA_LH) * values[DATA_Y];
			arrGyrLowPassFilter[DATA_Z] = ALPHA_LH * arrGyrLowPassFilter[DATA_Z] + (1.0f - ALPHA_LH) * values[DATA_Z];
			arrGyrTemp[DATA_X] = values[DATA_X] - arrGyrLowPassFilter[DATA_X];
			arrGyrTemp[DATA_Y] = values[DATA_Y] - arrGyrLowPassFilter[DATA_Y];
			arrGyrTemp[DATA_Z] = values[DATA_Z] - arrGyrLowPassFilter[DATA_Z];
			return arrGyrTemp;
		}
		
	    return null;
	}
	
	/*
	 * This method can make the Orientation values by accelerometer and magnetic sensor.
	 * More details is in the Android Developer Guide.
	 */
	private float[]							arrOrientation								= new float[3];
	private float[]							arrTemporary								= new float[3];
	private float[]							arrR										= new float[9];
	private float[]							arrI										= new float[9];
	private boolean							isSuccessGettingRotaionMatrix				= false;
	private Message							msgOrientation								= null;
	public void setTempOrientation(boolean reverseLeft, boolean reverseUp){
		isSuccessGettingRotaionMatrix =SensorManager.getRotationMatrix(arrR, arrI, arrAccelerometer, arrMagnetic);
		if(isSuccessGettingRotaionMatrix){
			SensorManager.getOrientation(arrR, arrTemporary);
			arrTemporary[DATA_X] = (float) Math.toDegrees(arrTemporary[DATA_X]);
			arrTemporary[DATA_Y] = (float) Math.toDegrees(arrTemporary[DATA_Y]);
			arrTemporary[DATA_Z] = (float) Math.toDegrees(arrTemporary[DATA_Z]);
			if(reverseLeft){
				arrTemporary[DATA_X] = -arrTemporary[DATA_X];
			}
			if(arrTemporary[DATA_X] < 0){
				arrTemporary[DATA_X] += 360;
			}
			/*
			 * arrTemporary[DATA_Y]'s value is between 90 and -90.
			 */
			if(reverseUp){
				arrTemporary[DATA_Y] = - arrTemporary[DATA_Y];
			}
			msgOrientation = new Message();
			msgOrientation.what = HANDLER_WHAT_SEND_TO_CLIENT;
			mainHandler.sendMessage(msgOrientation);
			msgOrientation = null;
		}
			
	}
	
	private float[]							arrDegree									= new float[2];
	private float[]							arrGyroscope								= new float[3];
	/*
    private float[]							arrPrevDegree								= new float[2];
    private double[]						arrQuaternion								= new double[4];
    private double[]						arrPrevQuaternion							= new double[4];
    
    private double							product										= 0;
    private double							normPrev									= 0;
    private double							normCurr									= 0;
    private double							theta										= 0;*/
	
    private double							rateOfSensitiveX							= 3;
    private double							rateOfSensitiveY							= 2;
    private Message							msgGyroscope								= null;
    
    /*
     * 자이로 스코프 센서를 이용하여 쿼터니언 데이터를 생성하는 메소드 입니다. 
     * 쿼터니언과 관련된 자료는 레퍼런스를 참조하시기 바랍니다.
     */
	public void setGyroscopeQuaternion(float[]	values, boolean reverseLeft, boolean reverseUp){
		// making a Quaternion with Gyroscope raw data.
		arrGyroscope = setLowAndHighPassFilter(values, LOW_GYR);
		arrGyroscope[DATA_X] = values[DATA_X] - arrGyroscope[DATA_X];
		arrGyroscope[DATA_Y] = values[DATA_Y] - arrGyroscope[DATA_Y];
		arrGyroscope[DATA_Z] = values[DATA_Z] - arrGyroscope[DATA_Z];
		arrGyroscope[DATA_X] = (float) (Math.cos(arrGyroscope[DATA_Z] / 2) * Math.cos(arrGyroscope[DATA_Y] / 2) * Math.sin(arrGyroscope[DATA_X] / 2) 
				+ Math.sin(arrGyroscope[DATA_Z] / 2) * Math.sin(arrGyroscope[DATA_Y] / 2) * Math.cos(arrGyroscope[DATA_X] / 2));
		arrGyroscope[DATA_Y] = (float) (Math.cos(arrGyroscope[DATA_Z] / 2) * Math.sin(arrGyroscope[DATA_Y] / 2) * Math.cos(arrGyroscope[DATA_X] / 2) 
				- Math.sin(arrGyroscope[DATA_Z] / 2) * Math.cos(arrGyroscope[DATA_Y] / 2) * Math.sin(arrGyroscope[DATA_X] / 2));
		arrGyroscope[DATA_Z] = (float) (Math.sin(arrGyroscope[DATA_Z] / 2) * Math.cos(arrGyroscope[DATA_Y] / 2) * Math.cos(arrGyroscope[DATA_X] / 2) 
				+ Math.cos(arrGyroscope[DATA_Z] / 2) * Math.sin(arrGyroscope[DATA_Y] / 2) * Math.sin(arrGyroscope[DATA_X] / 2));
		/*arrQuaternion[DATA_W] = Math.cos(arrGyroscope[DATA_Z] / 2) * Math.cos(arrGyroscope[DATA_Y] / 2) * Math.cos(arrGyroscope[DATA_X] / 2) 
				- Math.sin(arrGyroscope[DATA_Z] / 2) * Math.sin(arrGyroscope[DATA_Y] / 2) * Math.sin(arrGyroscope[DATA_X] / 2);*/
		
		/*
		//inner product of the quaternion.
		product =  arrQuaternion[DATA_X] * arrPrevQuaternion[DATA_X]
						+ arrQuaternion[DATA_Y] * arrPrevQuaternion[DATA_Y]
						+ arrQuaternion[DATA_Z] * arrPrevQuaternion[DATA_Z]
						+ arrQuaternion[DATA_W] * arrPrevQuaternion[DATA_W];
		
		// norm of the previous quaternion 
		normPrev = Math.sqrt(  Math.pow(arrPrevQuaternion[DATA_X], 2)
									+ Math.pow(arrPrevQuaternion[DATA_Y], 2)
									+ Math.pow(arrPrevQuaternion[DATA_Z], 2)
									+ Math.pow(arrPrevQuaternion[DATA_W], 2));
		// norm of the current quaternion
		normCurr = Math.sqrt(  Math.pow(arrQuaternion[DATA_X], 2)
									+ Math.pow(arrQuaternion[DATA_Y], 2)
									+ Math.pow(arrQuaternion[DATA_Z], 2)
									+ Math.pow(arrQuaternion[DATA_W], 2));
		// theta of between previous and current quaternion.
		theta = Math.toDegrees( Math.acos( product / (normPrev * normCurr) ) );
		//printLog("theta = "+theta);
		arrPrevQuaternion[DATA_X] = arrQuaternion[DATA_X];
		arrPrevQuaternion[DATA_Y] = arrQuaternion[DATA_Y];
		arrPrevQuaternion[DATA_Z] = arrQuaternion[DATA_Z];
		arrPrevQuaternion[DATA_W] = arrQuaternion[DATA_W];
		if(Double.isNaN(theta)){
			return;
		}*/
		arrDegree[DATA_X] = (float) ( Math.toDegrees(arrGyroscope[DATA_Z]) / rateOfSensitiveX);
		arrDegree[DATA_Y] = (float) ( Math.toDegrees(arrGyroscope[DATA_X]) / rateOfSensitiveY);
		if(arrDegree[DATA_X] < 1 && arrDegree[DATA_X] > -1){
			arrDegree[DATA_X] = 0;
		}
		if(arrDegree[DATA_Y] < 1 && arrDegree[DATA_Y] > -1){
			arrDegree[DATA_Y] = 0;
		}
		
		if(reverseLeft){
			arrDegree[DATA_X] = -arrDegree[DATA_X];
		}
		
		if(reverseUp){
			arrDegree[DATA_Y] = -arrDegree[DATA_Y];
		}
		msgGyroscope = new Message();
		msgGyroscope.what = HANDLER_WHAT_SEND_TO_CLIENT;
		mainHandler.sendMessage(msgGyroscope);
		msgGyroscope = null;
	}
	
	// Orientation sensor value's setter 
	public void setOrientation(){
		this.arrOrientation = arrTemporary;
		/*Log.d(TAG_NAME, 
				"arrOrientation[DATA_X] = "+arrOrientation[DATA_X]+
				", arrOrientation[DATA_Y] = "+arrOrientation[DATA_Y]+
				", arrOrientation[DATA_Z] = "+arrOrientation[DATA_Z]);*/
	}
	// Orientation sensor value's getter
	public float[] getOrientation(){
		return arrOrientation;
	}
	
	public void setIsAccelerometer(boolean temp){
		this.isAcc = temp;
	}
	
	public void setIsMagnetic(boolean temp){
		this.isMagnetic = temp;
	}
	
	public boolean getIsAccelerometer() {
		return this.isAcc;
	}
	
	public boolean getIsMagnetic() {
		return this.isMagnetic;
	}
	
	/*
	 * Get magnetic array.
	 */
	public float[] getGeoMagnetic(){
		return arrMagnetic;
	}
	/*
	 * Get accelerometer array.
	 */
	public float[] getAccelerometer() {
		return arrAccelerometer;
	}
	
	public float getAccelerometer_X() {
		return arrAccelerometer[0];
	}
	public float getAccelerometer_Y() {
		return arrAccelerometer[1];
	}
	public float getAccelerometer_Z() {
		return arrAccelerometer[2];
	}
	
	
	
	
	
	public float[] getGyroscope(){
		return arrGyroscope;
	}
	
	public float[] getDegree(){
		return this.arrDegree;
	}
	public void setSensitive(int grade){
		switch(grade){
		case SENSITIVE_GRADE_HIGHEST:
			rateOfSensitiveX = 1;
			rateOfSensitiveY = 1;
			break;
		case SENSITIVE_GRADE_HIGH:
			rateOfSensitiveX = 2;
			rateOfSensitiveY = 1.5;
			break;
		case SENSITIVE_GRADE_NORMAL:
			rateOfSensitiveX = 3;
			rateOfSensitiveY = 2;
			break;
		case SENSITIVE_GRADE_LOW:
			rateOfSensitiveX = 4;
			rateOfSensitiveY = 2.5;
			break;
		case SENSITIVE_GRADE_LOWEST:
			rateOfSensitiveX = 5;
			rateOfSensitiveY = 3;
			break;
		}
	}
}