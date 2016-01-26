package fhj.swengb.projects.filebrowser

import java.io.File
import java.net.URL
import java.util.ResourceBundle
import javafx.application.Application
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.fxml.{FXML, FXMLLoader, Initializable}
import javafx.scene.control.TreeItem.TreeModificationEvent
import javafx.scene.control.{TextArea, ScrollPane, TreeItem, TreeView}
import javafx.scene.input.{MouseButton, MouseEvent}
import javafx.scene.layout.{AnchorPane, BorderPane}
import javafx.scene.{Node, Parent, Scene}
import javafx.stage.Stage


import scala.collection.JavaConversions._
import scala.util.control.NonFatal

/*
  Object erstellen um JavaFX GUI anzuzeigen

*/
object FileBrowserApp {
  def main(args: Array[String]) {
    Application.launch(classOf[FileBrowserApp], args: _*)
  }
}

class FileBrowserApp extends javafx.application.Application {
  val Fxml = "/fhj/swengb/projects/filebrowser/FileBrowserMain.fxml"
  // FXML Pfadvariable
  val Css = "/fhj/swengb/projects/filebrowser/FileBrowser.css"
  // FXML Pfadvariable
  val loader = new FXMLLoader(getClass.getResource(Fxml)) // FXML Loader Funktion mit Variable aufrufen
  override def start(stage: Stage): Unit = try {
    stage.setTitle("File Browser") // Titelleiste in Anzeigefenster beschriften
    loader.load[Parent]()
    val scene = new Scene(loader.getRoot[Parent]) // neue Scene aufrufen
    stage.setScene(scene)
    stage.getScene.getStylesheets.add(Css)
    stage.show()

  } catch { // Errorhandling für FXML gui
    case NonFatal(e) => {
      e.printStackTrace()
    }
  }

}


class FileBrowserAppController extends Initializable {
  // GUI set up
  @FXML var mainScrollPane: ScrollPane = _ // Elemnt aus FXML mit fx:id="mainScrollPane" herausholen
  @FXML var fileViewerPane: AnchorPane = _
  override def initialize(location: URL, resources: ResourceBundle): Unit = {


    // root Node
    val root: TreeItem[File] = new TreeItem[File](new File("/")) // root Node wird erstellt
    addChildFiles(root, mkObservableFiles("/")) // root wird befüllt
    root.setExpanded(true) // ausklappen von root node

    val treeView: TreeView[File] = new TreeView[File](root) // Tree wird erstellt
    treeView.setId("treeView") // ID setzen, auf die über CSS zugegriffen werden kann
    treeView.getSelectionModel.selectedItemProperty.addListener(FbUtil.onChange(selectFile)) // Select File aufrufen bei Auswahl/Änderung von TreeItem
    treeView.setCellFactory(FbUtil.mkTreeCellFactory(FbUtil.show { // Ruft mkTreeCellFactory Funktion aus FBUtil.scala file auf
      case f if f.isDirectory => f.getName // Wenn directory Name hinschreiben (möglichkeit ordner "anders" zu behandeln als files)
      case f => f.getName // wenn nicht Directory -> Name holen und hinschreiben
    }))

    root.addEventHandler(TreeItem.branchExpandedEvent[File](), expandedEventHandler) // wenn ausgeklappt -> expandedEventHandler aufrufen
    //treeView.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickedEventHandler) // bei klick auf tree item wird mouseClickEventHandler aufgerufen

    mainScrollPane.setContent(treeView) // Treeview in ScrollPane einfügen
  }

  // helper methods

  def mkObservableFiles(path: String) = FbUtil.mkObservableList(new File(path).listFiles().sortBy(_.getName)) // Ruft mkObservableList Funktion aus FBUtil.scala file auf


  def addChildFiles(treeItem: TreeItem[File], files: ObservableList[File]): Unit = {

    for (file <- files) {
      try {
        // für alle Files im Ordner
        if (file.isDirectory) {
          // wenn Directory
          var childTreeItem = new TreeItem[File](file) // neues TreeItem erstellen
          if (file.isDirectory  && file.listFiles.nonEmpty) { // wenn Unterodner vorhanden
            childTreeItem.getChildren.add(new TreeItem[File](new File(" - just in time loading - "))) // set a child so that the "expandable"-arrow icon schows up ( Platzhalter damit Ordner  mit "expandable" arrow angezeigt wird )
            childTreeItem.setExpanded(false) // nicht ausgeklappt
          }
          treeItem.getChildren.add(childTreeItem) // aktuelles Item als Child hinzufügen
        } else {
          // wenn nicht directory
          treeItem.getChildren.add(new TreeItem[File](file)) // neues TreeItem erstellen und als Child hinzufügen
        }

        }
        catch {
          case e: Exception => println("File loading error: " + e.getMessage)
        }
    }
  }



  // event handlers, etc..

  def selectFile(oldFile: TreeItem[File], newFile: TreeItem[File]): Unit = {
    println("selectedFile: " + newFile) // print selected File (not in use yet)
  }

  def expandedEventHandler[_ >:TreeModificationEvent[File]] = new EventHandler[TreeModificationEvent[File]]() { // bei klick auf expand Ordner in GUI
    def handle(event: TreeModificationEvent[File]) {
      event.getSource match {
        case treeItem: TreeItem[_] => {
          println("expandedEventHandler TreeItem: " + treeItem)
          println(treeItem.valueProperty().get().getAbsolutePath) // Print auswahl
          println(treeItem.getChildren)

          treeItem.getChildren.clear() // Platzhalter für arrow wird wieder gelöscht
          if (treeItem.getValue.isDirectory && treeItem.getValue.listFiles.nonEmpty) { // wenn Unterodner vorhanden
            treeItem.getValue.listFiles.toList.foreach(subfile => println("unterodner: " + subfile))
            addChildFiles(treeItem, mkObservableFiles(treeItem.getValue.getAbsolutePath)) // Alle Listeninhalte werden als neue ChildElemente hinzugefügt
            treeItem.setExpanded(true)
          }
        }
        case a => { // sollte nie zu verwendung kommen da nur auf TreeItems geklickt werden kann (bis jetzt)
          println("expandedEventHandler otherClass: " + a.getClass)
        }
      }
    }
  }


}