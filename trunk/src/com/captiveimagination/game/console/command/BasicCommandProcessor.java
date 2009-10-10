/**
 * 
 */
package com.captiveimagination.game.console.command;

import java.lang.reflect.*;
import java.util.*;

import com.captiveimagination.game.console.*;

/**
 * @author Matthew D. Hicks
 *
 */
public class BasicCommandProcessor implements CommandProcessor {
	private static final int PARSE_NORMAL = 1;
	private static final int PARSE_SEPARATOR = 2;
	private static final int PARSE_QUOTE = 3;
	
	private StringBuffer commandBuffer;
	private LinkedList<String> parser;
	
	private HashMap<String, Object> commands;
	private HashMap<String, Method> methods;
	
	public BasicCommandProcessor() {
		commandBuffer = new StringBuffer();
		parser = new LinkedList<String>();
		
		commands = new HashMap<String, Object>();
		methods = new HashMap<String, Method>();
	}
	
	public void registerCommand(Object object) {
		Method[] ms = object.getClass().getMethods();
		for (int i = 0; i < ms.length; i++) {
			if ("hashCode, getClass, wait, equals, toString, notify, notifyAll".indexOf(ms[i].getName()) != -1) continue;
			System.out.println("Registering Command: " + ms[i].getName());
			commands.put(ms[i].getName(), object);
			methods.put(ms[i].getName(), ms[i]);
		}
	}
	
	public void execute(String command, GameConsole console) {
		int mode = 1;
		for (int i = 0; i < command.length(); i++) {
			if (PARSE_NORMAL == mode) {
				if (command.charAt(i) == ' ') {
					if (commandBuffer.length() == 0) continue;
					parser.add(commandBuffer.toString());
					commandBuffer.delete(0, commandBuffer.length());
					mode = PARSE_SEPARATOR;
				} else {
					commandBuffer.append(command.charAt(i));
				}
			} else if (PARSE_SEPARATOR == mode) {
				if (command.charAt(i) == '"') {
					mode = PARSE_QUOTE;
				} else if (command.charAt(i) == ' ') {
					// ignore
				} else {
					mode = PARSE_NORMAL;
					commandBuffer.append(command.charAt(i));
				}
			} else if (PARSE_QUOTE == mode) {
				if (command.charAt(i) == '"') {
					if (commandBuffer.length() == 0) continue;
					parser.add(commandBuffer.toString());
					commandBuffer.delete(0, commandBuffer.length());
					mode = PARSE_SEPARATOR;
				} else {
					commandBuffer.append(command.charAt(i));
				}
			}
		}
		if (commandBuffer.length() > 0) {
			parser.add(commandBuffer.toString());
			commandBuffer.delete(0, commandBuffer.length());
		}
		if (parser.size() > 0) {
			String c = parser.get(0);
			String[] params = new String[parser.size() - 1];
			for (int i = 0; i < params.length; i++) {
				params[i] = parser.get(i + 1);
			}
			parser.clear();
			
			Object obj = commands.get(c);
			if (obj != null) {
				Method method = methods.get(c);
				if (method.getParameterTypes().length != params.length) {
					console.log("Wrong number of arguments, received " + params.length + ", needed " + method.getParameterTypes().length);
					return;
				}
				
				Object[] args = new Object[params.length];
				for (int i = 0; i < params.length; i++) {
					args[i] = convert(params[i], method.getParameterTypes()[i]);
				}
				try {
					Object response = method.invoke(obj, args);
					if (response != null) {
						String message = toString(response);
						console.log(message);
					}
				} catch(Exception exc) {
					exc.printStackTrace();
					console.log("Error: " + exc.getMessage());
				}
			} else {
				console.log("Unknown Command: " + c);
			}
		}
	}
	
	@SuppressWarnings("all")
	public static Object convert(String s, Class type) {
		if (s == null) return null;
		if (type == String.class) {
			return s;
		} else if ((type == int.class) || (type == Integer.class)) {
			return new Integer(Integer.parseInt(s));
		} else if ((type == long.class) || (type == Integer.class)) {
			return new Long(Long.parseLong(s));
		} else if ((type == byte.class) || (type == Byte.class)) {
			return new Byte(Byte.parseByte(s));
		} else if ((type == boolean.class) || (type == Boolean.class)) {
			return new Boolean(Boolean.parseBoolean(s));
		} else if ((type == float.class) || (type == Float.class)) {
			return new Float(Float.parseFloat(s));
		} else if ((type == double.class) || (type == Double.class)) {
			return new Double(Double.parseDouble(s));
		} else if (type.isAssignableFrom(Calendar.class)) {
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTimeInMillis(Long.parseLong(s));
			return calendar;
		} else if (type == String[].class) {
			if (s.length() == 0) {
				return new String[0];
			}
			return s.split(",");
		}
		System.err.println("Unknown type for conversion: " + type.getSimpleName());
		return null;
	}
	
	public static String toString(Object o) {
		if (o == null) return "null";
		if (o.getClass().isArray()) {
			if (((Object[])o).length == 1) {
				return toString(((Object[])o)[0]);
			}
			StringBuffer buffer = new StringBuffer();
			for (Object child : (Object[])o) {
				if (buffer.length() > 0) {
					buffer.append (';');
				}
				buffer.append(toString(child));
			}
			return buffer.toString();
		} else if (o.getClass().isEnum()) {
			return ((Enum)o).name().toLowerCase();
		}
		return o.toString();
	}

    /* (non-Javadoc)
     * @see com.captiveimagination.game.console.command.CommandProcessor#onModeActivate(com.captiveimagination.game.console.GameConsole)
     */
    public void onModeActivate(GameConsole console) {
        console.setPrompt("cmd>");
        
    }
}
