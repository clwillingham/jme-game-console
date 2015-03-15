This code is an attempt to add generic scripting to the jME project, through hooks into the GameConsole class, as well as creating Spatial controllers that are implemented through scripts.

# Using the ScriptCommandProcessor and GameConsole in jME #

By default the ScriptCommandProcessor will evaluate whatever text you enter in the scripting language set (By default Pnuts). You can register objects from your application to be available to the scripts by calling the register(string, obj). IE:

```
GameConsole console = new GameConsole(KeyInput.KEY_GRAVE, rows, true);
ScriptCommandProcessor scriptProcessor = new ScriptCommandProcessor(console);
scriptProcessor.register("console", console);
scriptProcessor.register("game", game);
scriptProcessor.register("state", debug);
scriptProcessor.register("box", box);
```

The ScriptCommandProcessor provides the following highlevel commands:

  * Enter the script mode by typing “mode script” from the console

  * lang `<scriptLanguage>` - Let's the user select a scripting language (IE: lang  javascript - will change to javascript interpreter)

  * removeController `<spatial>` - Removes any ScriptController's from a spatial that was registered to the ScriptCommandProcessor.register()

  * addController `<spatial> <scriptFile.ext>` - Adds a ScriptController to the spatial that was registered to the ScriptCommandProcessor.register()

  * file `<scriptFile.ext>` - Evaluates the script file in your current working directory using the extension to determine the script language (IE: **.js,**.py, **.pnut,**.bsh)

For example in the TestScriptConsole.java we register the box to the ScriptCommandProcessor:

```
scriptProcessor.register("box", box);
```

Then on the console we type:

```
addController box spinController.js
```
This will add a ScriptController implemented by the spinController.js file (It implements it by defining a “function update(time)” function):

```
importPackage(Packages.com.jme.math);
 
var incr = 1.0;
var rotVec = new Vector3f(0.5, 0.5, 0.0);
var rotQuat = new Quaternion();
 
var angle = 0.0;
/**
 * @author <a href="mailto:Daniels.Douglas@gmail.com">Doug Daniels</a>
 * @version $Revision: 678 $
 * $Id: spinController.js 678 2007-03-31 23:41:40Z dougnukem $
 * $HeadURL: https://captiveimagination.com/svn/public/cigame/trunk/src/resources/scripts/js/spinController.js $
*/
function update(time) {
	if(console!=null)
		console.log("time="+time);
	else
		System.out.println("time="+time);
	//update the angle
    angle = angle + incr;
    if (angle > 360) {
        angle = 0;
    }
    rotQuat.fromAngleNormalAxis(angle *  FastMath.DEG_TO_RAD, rotVec);
	//controlled is special binding referencing the Spatial object being controlled.
    controlled.setLocalRotation(rotQuat);	
    //print('spinController update: ' + angle);
 
}
```

Then to remove it the user can type in the console:
```
removeController box
```


![http://ddaniels.net/eventhorizongames/wiki/lib/exe/fetch.php?cache=&media=articles:spincontroller.jpg](http://ddaniels.net/eventhorizongames/wiki/lib/exe/fetch.php?cache=&media=articles:spincontroller.jpg)