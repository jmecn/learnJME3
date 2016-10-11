package net.jmecn.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.jmecn.app.ModelFactory;
import net.jmecn.effects.DecayControl;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioData.DataType;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;

public class VisualAppState extends BaseAppState {

	private SimpleApplication simpleApp;
	private Camera cam;

	// 实体系统
	private EntityData ed;
	private EntitySet entities;

	// 场景图
	private final Node guiNode;
	private final Node rootNode;
	private final Node shootable;
	private AudioNode audio_bomb;
	
	private final Map<EntityId, Spatial> models;
	private ModelFactory modelFactory;

	/**
	 * 初始化
	 */
	public VisualAppState() {
		rootNode = new Node("VisualRootNode");
		guiNode = new Node("VisualGuiNode");
		shootable = new Node("Shootable");
		
		models = new HashMap<EntityId, Spatial>();
	}

	@Override
	protected void initialize(Application app) {
		this.simpleApp = (SimpleApplication) app;

		ed = getStateManager().getState(EntityDataState.class).getEntityData();
		entities = ed.getEntities(Position.class, Model.class);

		// 初始化场景
		simpleApp.getViewPort().setBackgroundColor(new ColorRGBA(0.75f, 0.875f, 1f, 1f));
		rootNode.detachAllChildren();
		guiNode.detachAllChildren();
		rootNode.attachChild(shootable);

		modelFactory = new ModelFactory(this.simpleApp.getAssetManager());

		// 初始化摄像机
		cam = app.getCamera();
		cam.setLocation(new Vector3f(200, 100, 80));
		cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);

		// 初始化光源
		DirectionalLight dl = new DirectionalLight();
		dl.setDirection(new Vector3f(-3, -5, -3).normalize());
		dl.setColor(new ColorRGBA(.6f, .6f, .6f, 0.8f));
		rootNode.addLight(dl);

		dl = new DirectionalLight();
		dl.setDirection(new Vector3f(3, -5, 3).normalize());
		dl.setColor(new ColorRGBA(.4f, .4f, .4f, 0.2f));
		rootNode.addLight(dl);
		
		
		// TODO ???
		audio_bomb = new AudioNode(simpleApp.getAssetManager(),
				"Sound/weapons/explode3.wav", DataType.Buffer);
		audio_bomb.setPositional(false);
		audio_bomb.setLooping(false);
		audio_bomb.setVolume(2);
		rootNode.attachChild(audio_bomb);
	}

	@Override
	protected void cleanup(Application app) {
		entities.release();
		entities = null;
	}
	

	@Override
	protected void onEnable() {
		simpleApp.getRootNode().attachChild(rootNode);
		simpleApp.getGuiNode().attachChild(guiNode);
	}

	@Override
	protected void onDisable() {
		// 退出游戏时要做清理工作
		simpleApp.getRootNode().detachChild(rootNode);
		simpleApp.getGuiNode().detachChild(guiNode);
	}

	@Override
	public void update(float tpf) {
		if (entities.applyChanges()) {
			removeModels(entities.getRemovedEntities());
			addModels(entities.getAddedEntities());
			updateModels(entities.getChangedEntities());
		}
	}

	private void removeModels(Set<Entity> entities) {
		for (Entity e : entities) {
			Spatial s = models.remove(e.getId());
			s.removeFromParent();
		}
	}

	private void addModels(Set<Entity> entities) {
		for (Entity e : entities) {
			Spatial s = createVisual(e);
			models.put(e.getId(), s);
			updateModelSpatial(e, s);

			String name = e.get(Model.class).getName();
			if (name.equals(Model.BOMB) || name.equals(Model.ICEWORLD)) {
				shootable.attachChild(s);
			} else {
				rootNode.attachChild(s);
			}
		}
	}

	private void updateModels(Set<Entity> entities) {
		for (Entity e : entities) {
			Spatial s = models.get(e.getId());
			updateModelSpatial(e, s);
		}
	}

	private void updateModelSpatial(Entity e, Spatial s) {
		Position p = e.get(Position.class);
		s.setLocalTranslation(p.getLocation());
		s.setLocalRotation(p.getRotation());
	}

	private Spatial createVisual(Entity e) {
		Model model = e.get(Model.class);
		
		String name = model.getName();
		Spatial s = modelFactory.create(name);
		
		// TODO ???
		if (name.equals(Model.BOMB)) {
			s.addControl(new DecayControl(audio_bomb, simpleApp));
		}
		
		// 将模型添加到场景图中
		if (name.equals(Model.BOMB) || name.equals(Model.ICEWORLD)) {
			shootable.attachChild(s);
		} else {
			rootNode.attachChild(s);
		}
		
		return s;
	}

	/**
	 * 获得可以被击中的物体。
	 * @return
	 */
	public Node getShootable() {
		return shootable;
	}
	
	public Spatial getModel(EntityId id) {
		return models.get(id);
	}

}