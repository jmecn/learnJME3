package net.jmecn.app;

import com.jme3.input.KeyInput;
import com.simsilica.lemur.input.FunctionId;
import com.simsilica.lemur.input.InputMapper;

/**
 * Defines a set of standard input function IDs and their default control
 * mappings. The PlayerControlState uses these function IDs as triggers to the
 * player control.
 * 
 * @author Paul Speed
 */
public class PlayerFunctions {

	/**
	 * The group to which these functions are assigned for easy grouping and
	 * also for easy activation and deactivation of the entire group.
	 */
	public static final String GROUP = "Player Controls";

	public static final FunctionId F_SLOT_1 = new FunctionId(GROUP, "slot1");// 主武器
	public static final FunctionId F_SLOT_2 = new FunctionId(GROUP, "slot2");// 手枪
	public static final FunctionId F_SLOT_3 = new FunctionId(GROUP, "slot3");// 匕首
	public static final FunctionId F_SLOT_4 = new FunctionId(GROUP, "slot4");// 手雷
	public static final FunctionId F_SLOT_5 = new FunctionId(GROUP, "slot5");// C4
	
	public static final FunctionId F_FORWARD = new FunctionId(GROUP, "forward");
	public static final FunctionId F_BACKWARD = new FunctionId(GROUP, "backward");
	public static final FunctionId F_RIGHT = new FunctionId(GROUP, "right");
	public static final FunctionId F_LEFT = new FunctionId(GROUP, "left");
	public static final FunctionId F_DUCK = new FunctionId(GROUP, "duck");// 蹲
	public static final FunctionId F_JUMP = new FunctionId(GROUP, "jump");// 跳
	
	public static final FunctionId F_BUY = new FunctionId(GROUP, "buy");// 买道具
	public static final FunctionId F_QUIKE_BUY = new FunctionId(GROUP, "quike_buy");// 买道具
	public static final FunctionId F_RELOAD = new FunctionId(GROUP, "reload");// 换弹夹
	public static final FunctionId F_USE = new FunctionId(GROUP, "use");// 使用物品：救人质、开门、使用监控器等
	public static final FunctionId F_LIGHT = new FunctionId(GROUP, "light");// 战术手电筒
	public static final FunctionId F_GIVE = new FunctionId(GROUP, "give");// 扔武器
	
	public static final FunctionId F_MENU = new FunctionId(GROUP, "menu");// 选择阵营的菜单
	public static final FunctionId F_TEAM = new FunctionId(GROUP, "team");// 查看队伍情况
	
	public static final FunctionId F_LATTACK = new FunctionId(GROUP, "lAttack");// 左键攻击
	public static final FunctionId F_RATTACK = new FunctionId(GROUP, "rAttack");// 右键攻击

	public static final FunctionId F_EXIT = new FunctionId(GROUP, "Exit");// 退出
	
	public static final FunctionId F_SCREENSHOT = new FunctionId("Screen Shot");

	/**
	 * Initializes a default set of input mappings for the ship functions. These
	 * can be changed later without impact... or multiple input controls can be
	 * mapped to the same function.
	 */
	public static void initializeDefaultMappings(InputMapper inputMapper) {
		// Default key mappings
		inputMapper.map(F_SLOT_1, KeyInput.KEY_1);
		inputMapper.map(F_SLOT_2, KeyInput.KEY_2);
		inputMapper.map(F_SLOT_3, KeyInput.KEY_3);
		inputMapper.map(F_SLOT_4, KeyInput.KEY_4);
		inputMapper.map(F_SLOT_5, KeyInput.KEY_5);
		
		inputMapper.map(F_FORWARD, KeyInput.KEY_W);
		inputMapper.map(F_BACKWARD, KeyInput.KEY_S);
		inputMapper.map(F_RIGHT, KeyInput.KEY_D);
		inputMapper.map(F_LEFT, KeyInput.KEY_A);
		
		inputMapper.map(F_DUCK, KeyInput.KEY_LCONTROL);
		inputMapper.map(F_JUMP, KeyInput.KEY_SPACE);
		
		inputMapper.map(F_BUY, KeyInput.KEY_B);
		inputMapper.map(F_QUIKE_BUY, KeyInput.KEY_O);
		inputMapper.map(F_RELOAD, KeyInput.KEY_R);
		inputMapper.map(F_USE, KeyInput.KEY_E);
		inputMapper.map(F_LIGHT, KeyInput.KEY_F);
		inputMapper.map(F_GIVE, KeyInput.KEY_G);
		
		inputMapper.map(F_MENU, KeyInput.KEY_M);
		inputMapper.map(F_TEAM, KeyInput.KEY_TAB);
		
		inputMapper.map(F_EXIT, KeyInput.KEY_ESCAPE);
		inputMapper.map(F_SCREENSHOT, KeyInput.KEY_F2);
	}
}