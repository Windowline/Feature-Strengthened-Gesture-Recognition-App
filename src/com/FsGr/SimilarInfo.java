package com.FsGr;

import Gesture.Gesture;

public class SimilarInfo {
	private Gesture gesture;
	private String partBit;
	
	public SimilarInfo(Gesture gesture, String partBit){
		this.gesture=gesture;
		this.partBit=partBit;	
	}
	
	
	public Gesture getGesture(){
		return this.gesture;
	}
	
	public String getPartBit(){
		return this.partBit;
	}
}
