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

	public static final FunctionId F_NORTH = new FunctionId(GROUP, "North");
	public static final FunctionId F_SOUTH = new FunctionId(GROUP, "South");
	public static final FunctionId F_EAST = new FunctionId(GROUP, "East");
	public static final FunctionId F_WEST = new FunctionId(GROUP, "West");

	public static final FunctionId F_EXIT = new FunctionId(GROUP, "Exit");
	public static final FunctionId F_SCREENSHOT = new FunctionId("Screen Shot");

	/**
	 * Initializes a default set of input mappings for the ship functions. These
	 * can be changed later without impact... or multiple input controls can be
	 * mapped to the same function.
	 */
	public static void initializeDefaultMappings(InputMapper inputMapper) {
		// Default key mappings
		inputMapper.map(F_NORTH, KeyInput.KEY_W);
		inputMapper.map(F_SOUTH, KeyInput.KEY_S);
		inputMapper.map(F_EAST, KeyInput.KEY_D);
		inputMapper.map(F_WEST, KeyInput.KEY_A);
		inputMapper.map(F_EXIT, KeyInput.KEY_ESCAPE);
		inputMapper.map(F_SCREENSHOT, KeyInput.KEY_F2);
	}
}