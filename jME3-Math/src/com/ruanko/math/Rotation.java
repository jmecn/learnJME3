package com.ruanko.math;

import com.jme3.math.Matrix3f;
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class Rotation {
	
	public static void main(String[] args) {
		
		System.out.println("===================Differences between Quaternion and Matrix3f:");
		// X = [A B C]
		Vector3f xAxis = new Vector3f(1, 2, 3);
		// Y = [D E F]
		Vector3f yAxis = new Vector3f(4, 5, 6);
		// Z = [G H I]
		Vector3f zAxis = new Vector3f(7, 8, 9);
		// O = [J K L]
		Vector3f translation = new Vector3f(1.4f, 2.3f, 13f);
		
		Quaternion rotation = new Quaternion();
		rotation.fromAxes(xAxis, yAxis, zAxis);
		System.out.println(rotation);
		
		// Matrix3f rotMatrix = [X Y Z]
		Matrix3f rotMat1 = new Matrix3f(
				xAxis.x, yAxis.x, zAxis.x,
				xAxis.y, yAxis.y, zAxis.y,
				xAxis.z, yAxis.z, zAxis.z
				);
		rotation.fromRotationMatrix(rotMat1);
		System.out.println(rotation);
		
		Matrix3f rotMat2 = rotation.toRotationMatrix();
		System.out.println(rotMat1);
		System.out.println(rotMat2);
		
		// Transform
		System.out.println("===================Transfrom a vector3f:");
		Vector3f origin = new Vector3f(1, 4, 7);
		Matrix4f rotMat3 = new Matrix4f(
				xAxis.x, yAxis.x, zAxis.x, translation.x,
				xAxis.y, yAxis.y, zAxis.y, translation.y,
				xAxis.z, yAxis.z, zAxis.z, translation.z,
				0, 0, 0, 1
				);
		
		
		Vector3f rVal1 = rotation.mult(origin);
		rVal1.addLocal(translation);
		
		Vector3f rVal2 = rotMat1.mult(origin);
		rVal2.addLocal(translation);
		
		Vector3f rVal3 = rotMat2.mult(origin);
		
		Vector3f rVal4 = rotMat3.mult(origin);
		
		System.out.println(origin + " " + rVal1);
		System.out.println(origin + " " + rVal2);
		System.out.println(origin + " " + rVal3);
		System.out.println(origin + " " + rVal4);
		
		// Inverse
		rotMat3.invert().mult(rVal2, rVal2);
		System.out.println(origin + " " + rVal4);
	}

}
