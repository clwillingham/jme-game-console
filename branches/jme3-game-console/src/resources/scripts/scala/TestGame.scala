package resources.scripts.scala;

import com.jme.app.SimpleGame
import com.jme.bounding.BoundingSphere
import com.jme.math.Vector3f
import com.jme.scene.shape.Box

object TestGame {
  def main(args: Array[String]) :Unit = {

    object MyGame extends SimpleGame {

      override def simpleInitGame() {
        this.display.setTitle("A Simple Test - written in Scala!")
        def box = new Box("my box", new Vector3f(0, 0, 0), 2, 2, 2)
        box.setModelBound(new BoundingSphere())
        box.updateModelBound()
        this.rootNode.attachChild(box)
      }
      
      override def simpleUpdate() {
        
      }
    }
    MyGame.setDialogBehaviour(2)
    MyGame.start()
  }
}