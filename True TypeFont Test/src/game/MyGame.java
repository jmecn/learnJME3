package game;

import truetypefont.TrueTypeFont;
import truetypefont.TrueTypeKey;
import truetypefont.TrueTypeLoader;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;

public class MyGame extends SimpleApplication {

	@Override
	public void simpleInitApp() {
		// 注册ttf字体资源加载器
		assetManager.registerLoader(TrueTypeLoader.class, "ttf");

		// 创建字体 (例如：楷书)
		TrueTypeKey ttk = new TrueTypeKey("Interface/Fonts/SIMKAI.TTF",// 字体
				java.awt.Font.PLAIN,// 字形：普通、斜体、粗体
				64);// 字号
		
		TrueTypeFont font = (TrueTypeFont)assetManager.loadAsset(ttk);

		String[] poem = {"空山新雨后", "天气晚来秋", "明月松间照", "清泉石上流"};
		
		for(int i=0; i<poem.length; i++) {
			// 创建文字
			Geometry text = font.getBitmapGeom(poem[i], 0, ColorRGBA.White);
			text.scale(0.5f);
			text.setLocalTranslation(450, 450 - i*32, 0);
			guiNode.attachChild(text);
		}
	}

	public static void main(String[] args) {
		MyGame game = new MyGame();
		game.start();
	}

}
