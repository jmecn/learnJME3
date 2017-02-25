package game;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.system.AppSettings;

/**
 * Timer that will close the game after 60 seconds
 * @author yanmaoyuan
 *
 */
public class EndIn60S extends SimpleApplication {
	final static String FORMAT = "End in %.1fs.";
	BitmapText uiText;
	
	final static float TOTAL_SECOND = 60;
	float timeInSecond;
	
	public EndIn60S() {
		super(new FlyCamAppState());
	}
	
	@Override
	public void simpleInitApp() {
		// show mouse
		flyCam.setDragToRotate(true);
		
		// Time
		timeInSecond = 0;
		
		// GUI
		BitmapFont fnt = assetManager.loadFont("Interface/Fonts/Default.fnt");
		uiText = new BitmapText(fnt, false);
		uiText.setText(String.format(FORMAT, TOTAL_SECOND-timeInSecond));
		uiText.setLocalTranslation(0, 20, 0);
		
		guiNode.attachChild(uiText);
		

	}

	@Override
	public void simpleUpdate(float tpf) {
		timeInSecond += tpf;
		
		// update gui
		uiText.setText(String.format(FORMAT, TOTAL_SECOND-timeInSecond));
		
		if (timeInSecond >= TOTAL_SECOND) {
			stop();
		}
	}

	public static void main(String[] args) {
		AppSettings settings = new AppSettings(true);
		settings.setTitle(String.format(FORMAT, TOTAL_SECOND));
		
		EndIn60S app = new EndIn60S();
		app.setSettings(settings);
		app.start();
	}

}
