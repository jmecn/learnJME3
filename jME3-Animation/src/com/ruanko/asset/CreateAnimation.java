package com.ruanko.asset;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.jme3.animation.Bone;
import com.jme3.animation.BoneTrack;
import com.jme3.animation.Skeleton;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.Box;
import com.jme3.util.BufferUtils;

public class CreateAnimation {

	AssetManager assetManager;

	// relative vertex coordinates
	Vector3f bone1_local = new Vector3f(0, 0, 1);
	Vector3f bone2_local = new Vector3f(0.74f, 0.74f, 0);
	Vector3f bone3_local = new Vector3f(0, 1, 0);

	// absolute vertex coordinates
	Vector3f box1_piovt = bone1_local.clone();
	Vector3f box2_piovt = box1_piovt.add(bone2_local);
	Vector3f box3_piovt = box2_piovt.add(bone3_local);
	
	public CreateAnimation(AssetManager assetManager) {
		this.assetManager = assetManager;
	}
	
	public AnimControl createAnimControl() {
		
		Skeleton ske = buildSkeleton();
		
		AnimControl animControl = new AnimControl(ske);
		
		float length = 9f;
		
		Animation anim = new Animation("Anim", length);
		
		anim.addTrack(track0());
		anim.addTrack(track1());
		anim.addTrack(track2());
		
		animControl.addAnim(anim);
		
		return animControl;
	}
	
	public Skeleton buildSkeleton() {
		
		// create bones
		Bone bip01 = new Bone("Bip01");
		bip01.setBindTransforms(bone1_local, new Quaternion(0, 0, 0, 1), new Vector3f(1, 1, 1));

		Bone bip02 = new Bone("Bip02");
		bip02.setBindTransforms(bone2_local, new Quaternion(0, 0, 0, 1), new Vector3f(1, 1, 1));

		Bone bip03 = new Bone("Bip03");
		bip03.setBindTransforms(bone3_local, new Quaternion(0, 0, 0, 1), new Vector3f(1, 1, 1));
		
		bip01.addChild(bip02);
		bip02.addChild(bip03);
		
		// create skeleton
		Bone[] bones = new Bone[] {bip01, bip02, bip03};
		Skeleton ske = new Skeleton(bones);

		return ske;
	}
	
	private BoneTrack track0() {
		int size = 10;// numbers of keyframes
		
		float[] times = new float[size];
		Vector3f[] translations = new Vector3f[size];
		Quaternion[] rotations = new Quaternion[size];
		Vector3f[] scales = new Vector3f[size];
		
		// initBindTransform
		times[0] = 0;
		translations[0] = new Vector3f();
		rotations[0] = new Quaternion();
		scales[0] = new Vector3f(1, 1, 1);
		
		Quaternion q = new Quaternion().fromAngleAxis(FastMath.PI/3, new Vector3f(0, 1, 0));
		for(int i=1; i<size; i++) {
			times[i] = i;
			translations[i] = translations[i-1];
			rotations[i] = rotations[i-1].mult(q);
			scales[i] = scales[i-1];
		}
		
		BoneTrack track = new BoneTrack(0, times, translations, rotations, scales);
		
		return track;
	}
	
	private BoneTrack track1() {
		int size = 10;// numbers of keyframes
		
		float[] times = new float[size];
		Vector3f[] translations = new Vector3f[size];
		Quaternion[] rotations = new Quaternion[size];
		Vector3f[] scales = new Vector3f[size];
		
		// initBindTransform
		times[0] = 0;
		translations[0] = new Vector3f();
		rotations[0] = new Quaternion();
		scales[0] = new Vector3f(1, 1, 1);
		
		for(int i=1; i<size; i++) {
			times[i] = i;
			translations[i] = translations[i-1].add(0, 0.2f, 0);
			rotations[i] = rotations[i-1];
			scales[i] = scales[i-1];
		}
		
		BoneTrack track = new BoneTrack(1, times, translations, rotations, scales);
		
		return track;
	}
	
	private BoneTrack track2() {
		int size = 10;// numbers of keyframes
		
		float[] times = new float[size];
		Vector3f[] translations = new Vector3f[size];
		Quaternion[] rotations = new Quaternion[size];
		Vector3f[] scales = new Vector3f[size];
		
		// initBindTransform
		times[0] = 0;
		translations[0] = new Vector3f();
		rotations[0] = new Quaternion();
		scales[0] = new Vector3f(1, 1, 1);
		
		for(int i=1; i<size; i++) {
			times[i] = i;
			translations[i] = translations[i-1].add(new Vector3f(0.4f, 0, 0f));
			rotations[i] = rotations[i-1];
			scales[i] = scales[i-1].mult(1.1f);
		}
		
		BoneTrack track = new BoneTrack(2, times, translations, rotations, scales);
		
		return track;
	}
	
	public Node createModel() {
		Node model = new Node("model");
		
		Box box = new Box(0.1f, 0.1f, 0.1f);
		
		reCalculateVertex(box, box1_piovt);
		
		box.setBuffer(Type.BoneIndex, 4, createBoneIndex((byte)0));
		box.setBuffer(Type.BoneWeight, 4, createBoneWeight());
		box.setMaxNumWeights(4);
		box.generateBindPose(true);
		
		Geometry box01 = new Geometry("box01", box);
		Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat1.setColor("Color", ColorRGBA.Green);
		box01.setMaterial(mat1);
		
		Box box2 = new Box(0.1f, 0.1f, 0.1f);
		
		reCalculateVertex(box2, box2_piovt);
		
		box2.setBuffer(Type.BoneIndex, 4, createBoneIndex((byte)1));
		box2.setBuffer(Type.BoneWeight, 4, createBoneWeight());
		box2.setMaxNumWeights(4);
		box2.generateBindPose(true);
		
		Geometry box02 = new Geometry("box02", box2);
		Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat2.setColor("Color", ColorRGBA.Blue);
		box02.setMaterial(mat2);
		
		Box box3 = new Box(0.1f, 0.1f, 0.1f);
		
		reCalculateVertex(box3, box3_piovt);
		
		box3.setBuffer(Type.BoneIndex, 4, createBoneIndex((byte)2));
		box3.setBuffer(Type.BoneWeight, 4, createBoneWeight());
		box3.setMaxNumWeights(4);
		box3.generateBindPose(true);
		
		Geometry box03 = new Geometry("box03", box3);
		Material mat3 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat3.setColor("Color", ColorRGBA.Red);
		box03.setMaterial(mat3);
		
		model.attachChild(box01);
		model.attachChild(box02);
		model.attachChild(box03);
		
		return model;
	}
	
	private ByteBuffer createBoneIndex(byte n) {
		byte[] boneIndices = new byte[24*4];
		for(int i=0; i<24*4; i++) {
			boneIndices[i] = n;
		}
		return BufferUtils.createByteBuffer(boneIndices);
	}
	
	private FloatBuffer createBoneWeight() {
		float[] boneWeight = new float[24*4];
		
		//for(int i=0; i<24*4; i++) {
		//	boneWeight[i] = 1;
		//}
		
		// fix: the total of 4 weights must be 1.
		for(int i=0; i<24*4; i++) {
			boneWeight[i] = 1f/4;
		}
		
		// as I have only one bone index for each box, below works just fine.
		//for(int i=0; i<24*4; i+=4) {
		//	boneWeight[i] = 0.4f;
		//	boneWeight[i+1] = 0.3f;
		//	boneWeight[i+2] = 0.2f;
		//	boneWeight[i+3] = 0.1f;
		//}
		
	    //for(int i=0; i<24*4; i+=4) {
	    //	boneWeight[i] = 1;
	    //	boneWeight[i+1] = 0;
	    //	boneWeight[i+2] = 0;
	    //	boneWeight[i+3] = 0;
	    //}
		
		return BufferUtils.createFloatBuffer(boneWeight);
	}
	
	/**
	 * recalculate the vertex coordinate
	 * @param mesh
	 * @param translation
	 */
	private void reCalculateVertex(Mesh mesh, Vector3f translation) {

		// get vertex data
		VertexBuffer vb = mesh.getBuffer(Type.Position);
		FloatBuffer fb = (FloatBuffer)vb.getData();
		
		// ready for read
		fb.flip();
		int length = fb.limit();
		for(int i=0; i<length; i+=3) {
			float x = fb.get() + translation.x;
			float y = fb.get() + translation.y;
			float z = fb.get() + translation.z;
			
			fb.put(i, x);
			fb.put(i+1, y);
			fb.put(i+2, z);
		}
	}
}
