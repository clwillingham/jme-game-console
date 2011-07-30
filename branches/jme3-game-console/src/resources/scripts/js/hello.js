importPackage(com.jme.math);

console.log("hello console this is a script!");
console.log("Moving box back 32 units.");
var vec = new Vector3f(0,0,-32);
box.getLocalTranslation().z += vec.z;