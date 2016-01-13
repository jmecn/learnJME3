package com.ruanko.asset;

import java.nio.FloatBuffer;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.jme3.animation.Bone;
import com.jme3.animation.BoneTrack;
import com.jme3.animation.Skeleton;
import com.jme3.animation.SkeletonControl;
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
import com.jme3.scene.shape.Sphere;

/**
 * Create both software and hardware skinning animation
 * 
 * @author yanmaoyuan
 *
 */
public class CreateAnimationHW {

	AssetManager assetManager;

	// local vertex coordinates
	Vector3f bone1_local = new Vector3f(0, 0, 1);
	Vector3f bone2_local = new Vector3f(0.74f, 0.74f, 0);
	Vector3f bone3_local = new Vector3f(0, 1, 0);

	// world vertex coordinates
	Vector3f piovt1 = bone1_local.clone();
	Vector3f piovt2 = piovt1.add(bone2_local);
	Vector3f piovt3 = piovt2.add(bone3_local);
	
	// Bone name
	
	public CreateAnimationHW(AssetManager assetManager) {
		this.assetManager = assetManager;
	}
	
	String[] boneNames = {"Bip01", "Bip02", "Bip03"};
	public Node createModel() {
		/** create a model */
		Node model = new Node("model");
		
		Geometry geom1 = createBox("box1", piovt1, ColorRGBA.Green);
		Geometry geom2 = createBox("box2", piovt2, ColorRGBA.Blue);
		Geometry geom3 = createSphere("sphere1", piovt3, ColorRGBA.Red);
		
		model.attachChild(geom1);
		model.attachChild(geom2);
		model.attachChild(geom3);
		
		/** add controls to the model*/
		AnimControl ac = createAnimControl();
		model.addControl(ac);
		
		Skeleton ske = ac.getSkeleton();
		SkeletonControl sc = new SkeletonControl(ske);
		model.addControl(sc);
		
		// try to use hardware skinning animation
		sc.setHardwareSkinningPreferred(true);
		
		/** skinning the model */
		skinning(geom1.getMesh(), (byte)ske.getBoneIndex(boneNames[0]));
		skinning(geom2.getMesh(), (byte)ske.getBoneIndex(boneNames[1]));
		skinning(geom3.getMesh(), (byte)ske.getBoneIndex(boneNames[2]));
		
		return model;
	}
	
	private Geometry createBox(String name, Vector3f piovt, ColorRGBA color) {
		Box mesh = new Box(0.1f, 0.1f, 0.1f);
		reCalculateVertex(mesh, piovt);
		
		Geometry geom = new Geometry(name, mesh);
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", color);
		geom.setMaterial(mat);
		
		return geom;
	}
	
	private Geometry createSphere(String name, Vector3f piovt, ColorRGBA color) {
		Sphere mesh = new Sphere(50, 50, 0.1f);
		reCalculateVertex(mesh, piovt);
		
		Geometry geom = new Geometry(name, mesh);
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", color);
		geom.setMaterial(mat);
		
		return geom;
	}
	
	/**
	 * Skinning the mesh
	 * @param mesh
	 * @param targetBoneIndex
	 */
	private void skinning(Mesh mesh, byte targetBoneIndex) {
		if (targetBoneIndex == -1) return;
		
		// Calculate vertex count
		int limit = mesh.getBuffer(Type.Position).getData().limit();
		// Notice: i should call mesh.getMode() to decide how many 
		// floats is used for each vertex. Default mode is Mode.Triangles
		int vertexCount = limit/3;// by default
		
		int boneIndexCount = vertexCount * 4;
		byte[] boneIndex = new byte[boneIndexCount];
		float[] boneWeight = new float[boneIndexCount];

		// calculate bone indices and bone weights;
		for(int i=0; i<boneIndexCount; i+=4) {
			boneIndex[i] = targetBoneIndex;
			// I don't need the other 3 indices so I discard them
			boneIndex[i+1] = 0;
			boneIndex[i+2] = 0;
			boneIndex[i+3] = 0;
			
			boneWeight[i] = 1;
			// I don't need the other 3 indices so I discard them
			boneWeight[i+1] = 0;
			boneWeight[i+2] = 0;
			boneWeight[i+3] = 0;
		}
		mesh.setMaxNumWeights(1);
		
		// apply software skinning
		mesh.setBuffer(Type.BoneIndex, 4, boneIndex);
		mesh.setBuffer(Type.BoneWeight, 4, boneWeight);
		// apply hardware skinning
		mesh.setBuffer(Type.HWBoneIndex, 4, boneIndex);
		mesh.setBuffer(Type.HWBoneWeight, 4, boneWeight);

		mesh.generateBindPose(true);
	}
	
	/**
	 * Create an AnimControl, it contains an Animation with 3 BoneTracks.
	 * @return
	 */
	private AnimControl createAnimControl() {
		
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
	
	/**
	 * Build a skeleton with 3 Bones.
	 * @return
	 */
	private Skeleton buildSkeleton() {
		
		// create bones
		Bone bip01 = new Bone(boneNames[0]);
		bip01.setBindTransforms(bone1_local, new Quaternion(0, 0, 0, 1), new Vector3f(1, 1, 1));

		Bone bip02 = new Bone(boneNames[1]);
		bip02.setBindTransforms(bone2_local, new Quaternion(0, 0, 0, 1), new Vector3f(1, 1, 1));

		Bone bip03 = new Bone(boneNames[2]);
		bip03.setBindTransforms(bone3_local, new Quaternion(0, 0, 0, 1), new Vector3f(1, 1, 1));
		
		bip01.addChild(bip02);
		bip02.addChild(bip03);
		
		// create skeleton
		Bone[] bones = new Bone[] {bip01, bip02, bip03};
		Skeleton ske = new Skeleton(bones);

		return ske;
	}
	
	/**
	 * This bone rotate it self
	 * @return
	 */
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
	
	/**
	 * This bone rises it self.
	 * @return
	 */
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
	
	/**
	 * This bone grows bigger and moves away from parent
	 * @return
	 */
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
