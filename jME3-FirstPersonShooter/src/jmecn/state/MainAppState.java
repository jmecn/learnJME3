package jmecn.state;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Node;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.style.BaseStyles;

/**
 * Ö÷½çÃæ
 * @author yanmaoyuan
 *
 */
public class MainAppState extends AbstractAppState {

	private SimpleApplication simpleApp;
	
	public final static String START_GAME = "Start Game";
	
	private Node guiNode = new Node("mainGui");
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		
		simpleApp = (SimpleApplication)app;
		
		simpleApp.getGuiNode().attachChild(guiNode);
		
	    // Initialize the globals access so that the default
	    // components can find what they need.
		GuiGlobals.initialize(app);
		
		initGUI();
	}
	
	@SuppressWarnings("unchecked")
	private void initGUI() {
		// Load the 'glass' style
		BaseStyles.loadGlassStyle();
		
		// Set 'glass' as the default style when not specified
		GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");
		
		// Create a simple container for our elements
		Container myWindow = new Container();
		guiNode.attachChild(myWindow);

		// Put it somewhere that we will see it.
		// Note: Lemur GUI elements grow down from the upper left corner.
		myWindow.setLocalTranslation(300, 300, 0);

		// Add some elements
		myWindow.addChild(new Label("Main Menu"));
		Button clickMe = myWindow.addChild(new Button(START_GAME));
		clickMe.addClickCommands(new Command<Button>() {
		        @Override
		        public void execute(Button source) {
		        	simpleApp.getStateManager().detach(MainAppState.this);
					simpleApp.getStateManager().attach(new InGameAppState());
		        }
		    });
	}

	@Override
	public void cleanup() {
		super.cleanup();
	}

	@Override
	public void stateDetached(AppStateManager stateManager) {
		System.out.println("detached");
		guiNode.detachAllChildren();
		simpleApp.getGuiNode().detachChild(guiNode);
	}

}
