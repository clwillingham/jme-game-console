package test;

import java.util.*;

import com.captiveimagination.game.hud.*;

public class SuperBean implements NamedValuesBean {

	private List<String> valueNames;
	
	private float speed = 0.5f;
	private float health = 0.7f;
	
	public List<String> getValueNames() {
		if (valueNames == null) {
			valueNames = new ArrayList<String>();
			valueNames.add("speed");
			valueNames.add("health");
		}
		return valueNames;
	}

	public float getNamedValue(String name) {
		if (name.equals("speed")){
			return getSpeed();
		} else if (name.equals("health")){
			return getHealth();
		} else {
			return 0;
		}
	}

	public float getHealth() {
		return health;
	}

	public void setHealth(float health) {
		this.health = health;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}
}
