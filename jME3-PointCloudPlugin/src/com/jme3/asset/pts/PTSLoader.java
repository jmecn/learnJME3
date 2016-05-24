package com.jme3.asset.pts;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Mesh.Mode;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

/**
 * PTS 点云文件导入
 * @author yanmaoyuan
 *
 */
public class PTSLoader implements AssetLoader {

	@Override
	public Object load(AssetInfo assetInfo) throws IOException {

		String objName = assetInfo.getKey().getName();

		InputStream in = assetInfo.openStream();
		Scanner scanner = new Scanner(in);

		// 顶点数量
		int vCount = scanner.nextInt();
		scanner.nextLine();

		// 顶点缓冲
		float[] vertex = new float[vCount * 3];

		// size
		float pointSize = 2f;
		float[] sizes = new float[vCount * 1];

		// 颜色缓冲
		float[] colors = new float[vCount * 4];

		int i = 0;
		while (scanner.hasNext()) {
			String line = scanner.nextLine().trim();
			String[] split = line.split("\\s+");

			// 顶点坐标
			vertex[i * 3] = Float.parseFloat(split[0].trim());
			vertex[i * 3 + 1] = Float.parseFloat(split[1].trim());
			vertex[i * 3 + 2] = Float.parseFloat(split[2].trim());

			// 顶点颜色
			if (split.length == 4) {
				float gray = Integer.parseInt(split[3].trim()) / 255f;
				colors[i * 4] = gray;
				colors[i * 4 + 1] = gray;
				colors[i * 4 + 2] = gray;
				colors[i * 4 + 3] = 1;
			} else if (split.length == 7) {
				colors[i * 4] = Integer.parseInt(split[4].trim()) / 255f;
				colors[i * 4 + 1] = Integer.parseInt(split[5].trim()) / 255f;
				colors[i * 4 + 2] = Integer.parseInt(split[6].trim()) / 255f;
				colors[i * 4 + 3] = 1;
			} else {
				colors[i * 4] = 1;
				colors[i * 4 + 1] = 1;
				colors[i * 4 + 2] = 1;
				colors[i * 4 + 3] = 1;
			}
			// 顶点大小
			sizes[i] = pointSize;
			
			i++;
		}
		scanner.close();

		assert vCount == i;

		return build(objName, assetInfo.getManager(), vertex, sizes, colors);
	}

	private Spatial build(String name, AssetManager assetManager, float[] vertex,
			float[] sizes, float[] colors) {
		// 粒子材质
		Material mat = new Material(assetManager,
				"Common/MatDefs/Misc/Particle.j3md");
		mat.getAdditionalRenderState().setPointSprite(true);
		mat.getAdditionalRenderState().setBlendMode(BlendMode.Off);
		mat.setBoolean("PointSprite", true);
		mat.setFloat("Quadratic", 0.25f);

		// 创建一个点状网格
		Mesh m = new Mesh();
		m.setMode(Mode.Points);
		m.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertex));
		m.setBuffer(Type.Color, 4, BufferUtils.createFloatBuffer(colors));
		m.setBuffer(Type.Size, 1, BufferUtils.createFloatBuffer(sizes));
		m.setStatic();
		m.updateBound();

		// 创建一个几何体
		Geometry g = new Geometry(name, m);
		g.setShadowMode(ShadowMode.Off);
		g.setQueueBucket(Bucket.Opaque);
		g.setMaterial(mat);
		g.updateModelBound();

		return g;
	}

}
