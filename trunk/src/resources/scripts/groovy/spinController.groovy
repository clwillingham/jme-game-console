import com.jme.math.Vector3f;
import com.jme.math.Quaternion;

def incr = 1.0f;
def rotVec = new Vector3f(0.0f, 1.0f, 0.0f);
def rotQuat = new Quaternion();
def angle = 0.0f;

def update(time, mesh) {
	//update the angle
    angle = angle + incr;
    if (angle > 360) {
        angle = 0;
    }
    rotQuat.fromAngleNormalAxis(angle * FastMath.DEG_TO_RAD, rotVec);
    mesh.setLocalRotation(rotQuat);	
}