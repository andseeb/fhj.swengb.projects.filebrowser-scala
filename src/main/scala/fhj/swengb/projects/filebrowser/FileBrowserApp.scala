package fhj.swengb.projects.filebrowser

import java.net.URL
import java.util.ResourceBundle
import javafx.application.Application
import javafx.event.{EventType, Event, EventHandler}
import javafx.fxml.{FXML, FXMLLoader, Initializable}
import javafx.scene.control.TreeItem.TreeModificationEvent
import javafx.scene.control.{TreeItem, TreeView}
import javafx.scene.effect.DropShadow
import javafx.scene.input.MouseEvent
import javafx.scene.layout.{AnchorPane, BorderPane}
import javafx.scene.shape.{Line, StrokeType}
import javafx.scene.{Parent, Scene, Node}
import javafx.stage.Stage
import java.io.File


import scala.collection.JavaConversions._
import scala.util.control.NonFatal

/**
  * A simple JavaFX GUI for the tree assignment.
  */
object FileBrowserApp {
  def main(args: Array[String]) {
    Application.launch(classOf[FileBrowserApp], args: _*)
  }
}

class FileBrowserApp extends javafx.application.Application {

  val Fxml = "/fhj/swengb/projects/remotefilebrowser/Gui.fxml"

  val loader = new FXMLLoader(getClass.getResource(Fxml))

  override def start(stage: Stage): Unit = try {
    stage.setTitle("Remote File Browser")
    loader.load[Parent]() // side effect
    val scene = new Scene(loader.getRoot[Parent])
    stage.setScene(scene)
    stage.show()

  } catch {
    case NonFatal(e) => {
      e.printStackTrace()
    }
  }

}


class FileBrowserAppController extends Initializable {


  @FXML var borderPane: BorderPane = _
  override def initialize(location: URL, resources: ResourceBundle): Unit = {


    //val root : FileTreeItem[File] = createNode(new File("/"));


    val root2: TreeItem[String] = new TreeItem[String]("Root Node")
    root2.setExpanded(false)


    root2.addEventHandler(TreeItem.branchExpandedEvent[String](), expandedEventHandler)





    val item1 = new TreeItem[String]("Item 1")
    //item1.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandler)
    val item2 = new TreeItem[String]("Item 2")
    //item2.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandler)
    val item3 = new TreeItem[String]("Item 3")
    //item3.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandler)

    // https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/TreeItem.html


    root2.getChildren().addAll(item1, item2, item3)
    val treeView: TreeView[String] = new TreeView[String](root2)

    borderPane.setCenter(treeView)


  }

def expandedEventHandler[_ >:TreeModificationEvent[String]] = new EventHandler[TreeModificationEvent[String]]() {
    def handle(event: TreeModificationEvent[String]) {
      event.getSource match {
        case ti: TreeItem[_] => println("expandedEventHandler")
        case a => println(a.getClass)
      }
    }
  }



}
