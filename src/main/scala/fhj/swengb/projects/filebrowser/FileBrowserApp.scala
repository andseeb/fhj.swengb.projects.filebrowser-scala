package fhj.swengb.projects.filebrowser

import java.io.File
import java.net.URL
import java.util.ResourceBundle
import javafx.application.Application
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.fxml.{FXML, FXMLLoader, Initializable}
import javafx.scene.control.TreeItem.TreeModificationEvent
import javafx.scene.control.{TreeItem, TreeView}
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.{Parent, Scene}
import javafx.stage.Stage

import net.ladstatt.Util.SwengbJfxUtil

import scala.collection.JavaConversions._
import scala.util.control.NonFatal


object FileBrowserApp {
  def main(args: Array[String]) {
    Application.launch(classOf[FileBrowserApp], args: _*)
  }
}

class FileBrowserApp extends javafx.application.Application {
  val Fxml = "/fhj/swengb/projects/filebrowser/Gui.fxml"
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
    val root: TreeItem[File] = new TreeItem[File](new File("."))
    addChildFiles(root, mkObservableFiles("."))
    root.setExpanded(true)

    val treeView: TreeView[File] = new TreeView[File](root)
    treeView.setEditable(true)
    treeView.getSelectionModel.selectedItemProperty.addListener(SwengbJfxUtil.onChange(selectFile))
    treeView.setCellFactory(FbUtil.mkTreeCellFactory(FbUtil.show {
      //case f if f.isDirectory => "[DIR] " + f.getName
      case f if f.isDirectory => f.getName
      case f => f.getName
    }))

    root.addEventHandler(TreeItem.branchExpandedEvent[File](), expandedEventHandler)
    treeView.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickedEventHandler)

    borderPane.setCenter(treeView)
  }

  // helper methods

  def mkObservableFiles(path: String) = SwengbJfxUtil.mkObservableList(new File(path).listFiles())

  def addChildFiles(treeItem: TreeItem[File], files: ObservableList[File]): Unit = {
    for (file <- files) {
      if (file.isDirectory) {
        var childTreeItem = new TreeItem[File](file)
        childTreeItem.getChildren.add(new TreeItem[File](new File(" - just in time loading - "))) // set a child so that the "expandable"-arrow icon schows up
        childTreeItem.setExpanded(false)
        treeItem.getChildren().add(childTreeItem)
      } else {
        treeItem.getChildren().add(new TreeItem[File](file))
      }
    }
  }



  // event handlers, etc..

  def selectFile(oldFile: TreeItem[File], newFile: TreeItem[File]): Unit = {
    println("selectedFile: " + newFile)
  }

  def expandedEventHandler[_ >:TreeModificationEvent[File]] = new EventHandler[TreeModificationEvent[File]]() {
      def handle(event: TreeModificationEvent[File]) {
        event.getSource match {
          case treeItem: TreeItem[_] => {
            println("expandedEventHandler TreeItem: " + treeItem)
            println(treeItem.valueProperty().get().getAbsolutePath)
            println(treeItem.getChildren)

            treeItem.getChildren.clear()
            addChildFiles(treeItem, mkObservableFiles(treeItem.getValue.getAbsolutePath))
            treeItem.setExpanded(true)
          }
          case a => {
            println("expandedEventHandler otherClass: " + a.getClass)
          }
        }
      }
    }


  def mouseClickedEventHandler[_ >:MouseEvent] = new EventHandler[MouseEvent]() {
    def handle(event: MouseEvent) {
      event.getSource match {
        case treeView: TreeView[_] => {
          val item = treeView.getSelectionModel.getSelectedItem
          println("mouseClickedEventHandler TreeView: " + item)
          item
        }
        case a => {
          println("mouseClickedEventHandler otherClass: " + a.getClass)
        }
      }
    }
  }

}
