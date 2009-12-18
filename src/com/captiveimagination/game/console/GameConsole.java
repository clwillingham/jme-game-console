/**
 * 
 */
package com.captiveimagination.game.console;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.*;

import com.captiveimagination.game.spatial.DialogBox;
import com.captiveimagination.game.util.TextureLoader;
import com.captiveimagination.game.console.command.*;
import com.captiveimagination.game.control.*;

import com.jme.image.*;
import com.jme.image.Texture.MagnificationFilter;
import com.jme.input.*;
import com.jme.math.Vector2f;
import com.jme.renderer.*;
import com.jme.scene.*;
import com.jme.scene.Spatial.TextureCombineMode;
import com.jme.scene.state.*;
import com.jme.scene.state.BlendState.DestinationFunction;
import com.jme.scene.state.BlendState.SourceFunction;
import com.jme.scene.state.BlendState.TestFunction;
import com.jme.system.*;
import com.jme.util.*;
import com.jmex.game.*;
import com.jmex.game.state.*;

/**
 * @author Matthew D. Hicks
 *
 */
public class GameConsole extends BasicGameState implements KeyInputListener {
    private static final long serialVersionUID = 1L;
    private static final String FONT_LOCATION = "/com/jme/app/defaultfont.tga";
    
    public static String errTag = "<Error> :";
    
    private BlendState as;
    private TextureState font;
    
    private int key;
    private Text[] rows;
    private Text entry;
    private Text cursor;
    private LinkedList<String> history;
    private LinkedList<String> commandHistory;
    private boolean echo;
    
    private Spatial backdrop;
    
    private int maxHistory;
    private int maxCommandHistory;
    
    private StringBuffer cursorBuffer;
    private StringBuffer current;
    
    private int cursorPosition;
    private int historyPosition;
    private int commandHistoryPosition;
    
    private float targetOnState;
    private FloatSpring spring;
    
    private String currentMode;
    private HashMap<String, CommandProcessor> modes;
    private List<GameConsoleListener> listeners;
    
    private float[] borders;
    private Vector2f onPosition;
    private Vector2f offPosition;
    private boolean on;
    private float width;

    public static int RIGHT     = 0;
    public static int TOP       = 1;
    public static int LEFT      = 2;
    public static int BOTTOM    = 3;
    
    private float yPromptPosition;
    private Text prompt;
    private String promptString;
    
    private boolean useSystemOut = false;
    PipedInputStream pin;
    PipedOutputStream pout;
    PrintStream out;
    BufferedReader in;
    
    public GameConsole(int key, int rows, boolean echo) {
        this(key, rows, echo, 
                new float[]{0, 20, 0, 0}, 
                new Vector2f(0, 0), 
                new Vector2f(0, calculateHeight(rows) + 5), 
                DisplaySystem.getDisplaySystem().getWidth());
    }
    
    public GameConsole(int key, int rows, boolean echo, 
            float[] borders, 
            Vector2f onPosition, Vector2f offPosition, 
            float width) {
        super("GameConsole");
        this.key = key;
        this.rows = new Text[rows];
        this.history = new LinkedList<String>();
        commandHistory = new LinkedList<String>();
        this.echo = echo;
        maxHistory = 250;
        maxCommandHistory = 250;
        commandHistoryPosition = -1;
        this.borders = borders;
        this.onPosition = onPosition;
        this.offPosition = offPosition;
        this.width = width;
        listeners = new ArrayList<GameConsoleListener>();
        init();
        prompt.setTextColor(ColorRGBA.red);
        entry.setTextColor(ColorRGBA.green);
        cursor.setTextColor(ColorRGBA.green);
        
    }
    
    private void init() {
        modes = new HashMap<String, CommandProcessor>();
        
        getRootNode().setRenderQueueMode(Renderer.QUEUE_ORTHO);
        
        cursorBuffer = new StringBuffer(40);
        cursorBuffer.append("_");
        current = new StringBuffer(40);
        
        //Create default backdrop
        backdrop = createDialogBackdrop();
        getRootNode().attachChild(backdrop);
        
        spring = new FloatSpring(400);
        
        //Start switched off, positioned off
        spring.setPosition(0);
        this.targetOnState = 0;
        this.on = false;
        
        // Create Text
        as = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
        as.setBlendEnabled(true);
        as.setSourceFunction(/*AlphaState.SB_SRC_ALPHA*/ BlendState.SourceFunction.SourceAlpha);
        as.setDestinationFunction(/*AlphaState.DB_ONE*/ BlendState.DestinationFunction.One);
        as.setTestEnabled(true);
        as.setTestFunction(/*AlphaState.TF_GREATER*/ TestFunction.GreaterThan);
        as.setEnabled(true);
        
        font = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
        font.setTexture(TextureManager.loadTexture(StandardGame.class.getResource(FONT_LOCATION), /*Texture.MM_LINEAR*/ Texture.MinificationFilter.BilinearNearestMipMap, /*Texture.FM_LINEAR*/ MagnificationFilter.Bilinear));
        font.setEnabled(true);
        
        float y = DisplaySystem.getDisplaySystem().getHeight() - 18.0f;
        for (int i = 0; i < rows.length; i++) {
            rows[i] = createText("Console Line" + i, "", 10.0f, y);
            y -= 14.0f;
        }
        entry = createText("Console Entry", "", 10.0f, y);
        cursor = createText("Cursor", cursorBuffer.toString(), 0.0f, y);
        yPromptPosition = y;
        //Initialize prompt
        prompt = createText("Prompt", "", 0.0f, yPromptPosition);
        prompt.getLocalTranslation().x = -2.0f;
        setPrompt(">");
        
//      getRootNode().setCullMode(Spatial.CULL_ALWAYS);
        getRootNode().updateRenderState();
        
        KeyInput.get().addListener(this);
        
        //getRootNode().getLocalTranslation().y = backdropHeight;
    }
    
    private void setupOutput()
    {
    	try {
    		pin = new PipedInputStream();
			pout = new PipedOutputStream(pin);
			out = new PrintStream(pout);
			in = new BufferedReader(new InputStreamReader(pin));
			System.setOut(out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public boolean isOpen() {
    	return on;
    }
    
    public static float calculateHeight(int rows) {
        return ((rows + 1) * 14.0f) + 10.0f;        
    }
    
    public void update(float time) {
        super.update(time);
        
        spring.update(targetOnState, time);
        
        float onNess = spring.getPosition();
        getRootNode().getLocalTranslation().x = onPosition.x * onNess + offPosition.x * (1-onNess);
        getRootNode().getLocalTranslation().y = onPosition.y * onNess + offPosition.y * (1-onNess);
        if(useSystemOut)
        {
        	updateOut();
        }
    }
    
    private void updateOut()
    {
    	try {
			if(in.ready())
			{
				this.log(in.readLine());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
    
    public void registerCommandProcessor(String name, CommandProcessor processor) {
        name = name.toLowerCase();
        if (currentMode == null) {
            currentMode = name;
            processor.onModeActivate(this);
        }
        modes.put(name, processor);
    }
    public void setPrompt(String promptStr) {
        this.promptString = promptStr;
        prompt.print(promptString);
        
        cursor.getLocalTranslation().x = prompt.getWidth();
        entry.getLocalTranslation().x = prompt.getWidth();
        //getRootNode().updateRenderState();
    }
    private Text createText(String name, String value, float xPosition, float yPosition) {
        Text text = new Text(name, value);
        text.setTextureCombineMode(/*TextureState.REPLACE*/ TextureCombineMode.Replace);
        text.setRenderState(as);
        text.setRenderState(font);
        text.updateGeometricState(0.0f, true);
        text.setLocalTranslation(xPosition, yPosition, 0.0f);
        getRootNode().attachChild(text);
        return text;
    }
    
    private Spatial createDialogBackdrop() {
        //Make default texture
    	float opacityAmount = 1.0f;
        Texture dialogTexture = TextureLoader.loadUncompressedTexture("resources/dialogArea.png");
        
        //Make the dialog box
        DialogBox box = new DialogBox("box", dialogTexture);
        box.setDimension(width + borders[LEFT] + borders[RIGHT], calculateHeight(rows.length) + borders[TOP] + borders[BOTTOM]);
        box.setLocalTranslation(-borders[LEFT], DisplaySystem.getDisplaySystem().getRenderer().getHeight() - calculateHeight(rows.length) - borders[BOTTOM], 0.0f);
        
        MaterialState materialState = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
        materialState.setAmbient(new ColorRGBA(0.0f, 0.0f, 0.0f, opacityAmount));
        materialState.setDiffuse(new ColorRGBA(0.1f, 0.5f, 0.8f, opacityAmount));
        materialState.setSpecular(new ColorRGBA(1.0f, 1.0f, 1.0f, opacityAmount));
        materialState.setShininess(128.0f);
        materialState.setEmissive(new ColorRGBA(0.0f, 0.0f, 0.0f, opacityAmount));
        materialState.setEnabled(true);
        
        materialState.setMaterialFace(MaterialState.MaterialFace.FrontAndBack);
        box.setRenderState(materialState);
        box.updateRenderState();
        
        //Create alpha state to blend using alpha
        BlendState bas = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
        bas.setBlendEnabled(true);
        
        //bas.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        bas.setSourceFunction(SourceFunction.SourceAlpha);
        //bas.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
        bas.setDestinationFunction(DestinationFunction.OneMinusSourceAlpha);
        bas.setTestFunction(TestFunction.GreaterThan);
        bas.setTestEnabled(true);
        bas.setEnabled(true);
        
        //set box render state
        box.setRenderState(bas);
        box.updateRenderState();
        box.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
        //return the box
        return box;
    }
    
    public void type(char character) {
        if (Character.isISOControl(character)) return;
        
        current.insert(cursorPosition++, character);
        cursorBuffer.insert(0, ' ');
        entry.print(current);
        cursor.print(cursorBuffer);
    }
    
    public void setText(String text) {
        current.delete(0, current.length());
        current.append(text);
        cursorBuffer.delete(0, cursorBuffer.length());
        cursorBuffer.append("_");
        for (int i = 0; i < text.length()-1; i++) {
            cursorBuffer.insert(0, ' ');
        }
        cursorPosition = text.length();
        entry.print(current);
        cursor.print(cursorBuffer);
    }
    
    public void backspace() {
        if ((current.length() > 0) && (cursorPosition > 0)) {
            current.deleteCharAt(--cursorPosition);
            cursorBuffer.deleteCharAt(0);
            entry.print(current);
            cursor.print(cursorBuffer);
        }
    }
    
    public void enter() {
        if (current.length() > 0) {
            String s = current.toString();
            if (echo) log(s);
            execute(s);
            if (commandHistory.size() >= maxCommandHistory) {
                commandHistory.poll();
            }
            commandHistory.add(s);
            current.delete(0, current.length());
            cursorBuffer.delete(0, cursorBuffer.length());
            cursorBuffer.append("_");
            cursorPosition = 0;
            commandHistoryPosition = -1;
            entry.print(current);
            cursor.print(cursorBuffer);
        }
    }
    
    public void execute(String command) {
        CommandProcessor processor = modes.get(currentMode);
        
        if (command.startsWith("mode")) {
            if (command.length() > "mode ".length()) {
                String mode = command.substring("mode ".length());
                mode = mode.toLowerCase();
                processor = modes.get(mode);
                if (processor != null) {
                    log("Mode Changed: " + mode);
                    currentMode = mode;
                    processor.onModeActivate(this);
                } else {
                    logErr("Unknown Mode: " + mode);
                }
            } else {
                StringBuffer buffer = new StringBuffer();
                for (String mode : modes.keySet()) {
                    if (buffer.length() > 0) buffer.append(", ");
                    buffer.append(mode);
                }
                log("Current Mode: " + currentMode + " (Available: " + buffer + ")");
            }
            return;
        }
        
        if (processor != null) {
            processor.execute(command, this);
        } else {
            logErr("No CommandProcessor set.");
        }
    }
    
    public void log(String message) {
        if (history.size() >= maxHistory) {
            history.poll();
        }
        history.add(message);
        if (historyPosition != 0) historyPosition++;
        
        updateHistory();
    }
    
    public void logErr(String message)
    {
    	if (history.size() >= maxHistory) {
            history.poll();
        }
        history.add(errTag + message);
        if (historyPosition != 0) historyPosition++;
        
        updateHistory();
    }
    
    public void updateHistory() {
        // Update history display
        int position = history.size() - rows.length - historyPosition;
        for (int i = 0; i < rows.length; i++) {
            if (position < 0) {
                rows[i].print("");
            } else {
            	
            	if(history.get(position).startsWith(errTag))
            	{
            		rows[i].setTextColor(ColorRGBA.red);
            		history.get(position).replaceAll(errTag, "");
            	}
            	else
            	{
            		rows[i].setTextColor(ColorRGBA.green);
            	}
                rows[i].print(history.get(position));
            }
            position++;
        }
    }
    
    public void saveLog(String location)
    {
    	PrintStream out;
    	try {
			out = new PrintStream(location);
			for(int i=0; i < history.size(); i++)
			{
				out.println(history.get(i));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			this.logErr("Could not save the Log, file not found");
		}
		
		
    }
    
    public void moveUpInHistory() {
        if (historyPosition < history.size() - rows.length) {
            historyPosition++;
            updateHistory();
        }
    }
    
    public void moveDownInHistory() {
        if (historyPosition > 0) {
            historyPosition--;
            updateHistory();
        }
    }
    
    public boolean moveCursorLeft() {
        if (cursorPosition > 0) {
            cursorPosition--;
            cursorBuffer.deleteCharAt(0);
            cursor.print(cursorBuffer);
            return true;
        }
        return false;
    }
    
    public void moveCursorHome() {
        while (moveCursorLeft());
    }
    
    public boolean moveCursorRight() {
        if (cursorPosition < current.length()) {
            cursorPosition++;
            cursorBuffer.insert(0, ' ');
            cursor.print(cursorBuffer);
            return true;
        }
        return false;
    }
    
    public void moveCursorEnd() {
        while (moveCursorRight());
    }
    
    public void moveUpInCommandHistory() {
        if (commandHistory.size() > 0) {
            if (commandHistoryPosition == 0) return;
            if (commandHistoryPosition == -1) {
                commandHistoryPosition = commandHistory.size();
            }
            commandHistoryPosition--;
            setText(commandHistory.get(commandHistoryPosition));
        }
    }
    
    public void moveDownInCommandHistory() {
        if (commandHistory.size() > 0) {
            if (commandHistoryPosition == commandHistory.size() - 1) {
                commandHistoryPosition = -1;
                setText("");
                return;
            }
            if (commandHistoryPosition == -1) return;
            commandHistoryPosition++;
            setText(commandHistory.get(commandHistoryPosition));
        }
    }
    
    public void onKey(char character, int keyCode, boolean pressed) {
        if (pressed) {
            if (keyCode == key) {
                on = !on;
                targetOnState = on ? 1 : 0;
                for (GameConsoleListener listener : listeners) {
	                if (on) {
	                	listener.consoleActivated();
	                } else {
	                	listener.consoleDeactivated();
	                }
                }
            } else if (!on) {
                // We're disabled, we're not going to listen
                return;
            } else if (keyCode == KeyInput.KEY_BACK) {
                backspace();
            } else if (keyCode == KeyInput.KEY_RETURN) {
                enter();
            } else if (keyCode == KeyInput.KEY_PGUP) {
                moveUpInHistory();
            } else if (keyCode == KeyInput.KEY_PGDN) {
                moveDownInHistory();
            } else if (keyCode == KeyInput.KEY_LEFT) {
                moveCursorLeft();
            } else if (keyCode == KeyInput.KEY_RIGHT) {
                moveCursorRight();
            } else if (keyCode == KeyInput.KEY_UP) {
                moveUpInCommandHistory();
            } else if (keyCode == KeyInput.KEY_DOWN) {
                moveDownInCommandHistory();
            } else if (keyCode == KeyInput.KEY_HOME) {
                moveCursorHome();
            } else if (keyCode == KeyInput.KEY_END) {
                moveCursorEnd();
            } else {
                type(character);
            }
        }
    }

    public boolean addListener(GameConsoleListener listener) {
    	return listeners.add(listener);
    }
    
    public boolean removeListener(GameConsoleListener listener) {
    	return listeners.remove(listener);
    }

	public void setUseSystemOut(boolean useSystemOut) {
		this.useSystemOut = useSystemOut;
		if(useSystemOut)
		{
			setupOutput();
		}
	}

	public boolean usesSystemOut() {
		return useSystemOut;
	}
}