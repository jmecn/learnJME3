package yan.mazegame;

import java.util.ArrayList;
import java.util.List;

import yan.mazegame.states.AxisState;
import yan.mazegame.states.GameState;
import yan.mazegame.states.TestPlayerState;
import yan.mazegame.states.TestMazeState;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

/**
 * ³ÌÐòÈë¿Ú
 * 
 * @author yanmaoyuan
 * 
 */
public class Game extends SimpleApplication {

	public static void main(String[] args) {
		SimpleApplication app = new Game();

		AppSettings settings = new AppSettings(true);
		settings.setTitle("Maze Game");
		settings.setResolution(1024, 768);

		app.setSettings(settings);
		app.setShowSettings(false);
		app.setPauseOnLostFocus(false);

		app.start();
	}

	public Game() {
		super(//new FlyCamAppState(),
				new GameState(),
				//new TestMazeState(),
				//new TestPlayerState(),
				//new AxisState(), 
				new ScreenshotAppState(""));
	}

	@Override
	public void simpleInitApp() {
		//flyCam.setMoveSpeed(50);
	}
}
