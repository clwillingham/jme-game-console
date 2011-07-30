importPackage(Packages.com.jme.math);

var incr = 1.0;
var rotVec = new Vector3f(0.5, 0.5, 0.0);
var rotQuat = new Quaternion();

var angle = 0.0;
/**
 * @author <a href="mailto:Daniels.Douglas@gmail.com">Doug Daniels</a>
 * @version $Revision: 685 $
 * $Id: spinController.js 685 2007-04-03 23:46:57Z dougnukem $
 * $HeadURL: svn://captiveimagination.com/public/cigame/trunk/src/resources/scripts/js/spinController.js $
*/
function update(time) {
	if(console!=undefined)
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