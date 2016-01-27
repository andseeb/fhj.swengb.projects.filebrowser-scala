package fhj.swengb.projects.filebrowser


import java.io.File
import java.nio.file.{StandardCopyOption, Path, Files}
import javafx.application.Platform
import javafx.beans.value.{ObservableValue, ChangeListener}
import javafx.collections.{FXCollections, ObservableList}
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.{TreeItem, TreeCell, TreeView}
import javafx.scene.image.ImageView
import javafx.scene.input._
import javafx.scene.layout.AnchorPane
import javafx.util.Callback
import scala.collection.JavaConverters._

import scala.collection.JavaConversions

object FbUtil {
  def refreshTreeItemNode(treeItem: TreeItem[File]) : Unit = {
    treeItem.getChildren.clear() // Platzhalter für arrow wird wieder gelöscht
    if (treeItem.getValue.isDirectory && treeItem.getValue.listFiles.nonEmpty) { // wenn Unterodner vorhanden
      treeItem.getValue.listFiles.toList.foreach(subfile => println("unterodner: " + subfile))
      addChildFiles(treeItem, mkObservableFiles(treeItem.getValue.getAbsolutePath)) // Alle Listeninhalte werden als neue ChildElemente hinzugefügt
      treeItem.setExpanded(true)
    }
  }


  // helper methods

  def mkObservableFiles(path: String) = FbUtil.mkObservableList(new File(path).listFiles().sortBy(_.getName)) // Ruft mkObservableList Funktion aus FBUtil.scala file auf


  def addChildFiles(treeItem: TreeItem[File], files: ObservableList[File]): Unit = {
    for (file <- files.asScala) {
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



  type TreeCellFactory[T] = Callback[TreeView[T], TreeCell[T]]


  def mkTreeCellFactory[T](f: TreeView[T] => TreeCell[T]): TreeCellFactory[T] =
    new Callback[TreeView[T], TreeCell[T]] {
      override def call(param: TreeView[T]): TreeCell[T] = f(param)
    }

  def mkObservableList[T](collection: Iterable[T] = List()): ObservableList[T] = {
    import JavaConversions._
    val mutableList = new java.util.ArrayList[T]
    mutableList.addAll(collection)
    FXCollections.observableList(mutableList)
  }
  def onChange[T](f: (T, T) => Unit): ChangeListener[T] = {
    mkChangeListener[T](relisten(_, _, _)(f))
  }
  def mkChangeListener[T](onChangeAction: (ObservableValue[_ <: T], T, T) => Unit): ChangeListener[T] = {
    new ChangeListener[T]() {
      override def changed(observable: ObservableValue[_ <: T], oldValue: T, newValue: T) = {
        onChangeAction(observable, oldValue, newValue)
      }
    }
  }
  def relisten[T](observableValue: ObservableValue[_ <: T], oldT: T, newT: T)(f: (T, T) => Unit): Unit = {
    f(oldT, newT)
  }

  def show[T](typeToString: T => String)(lv: TreeView[T]): TreeCell[T] = {

    class ATreeCell extends TreeCell[T] {
      override protected def updateItem(t: T, empty: Boolean): Unit = {
        super.updateItem(t, empty)
        if (t != null) {
          setText(typeToString(t))
          val iconView = t.asInstanceOf[File] match {
            case folder if folder.isDirectory => new ImageView("/fhj/swengb/projects/filebrowser/Downloads-Black-Folder-icon.png")
            case textFile if FilePropertiesUtil.isTextFile(textFile) => new ImageView("/fhj/swengb/projects/filebrowser/txt.png")
            case picture if FilePropertiesUtil.isPicture(picture) => new ImageView("/fhj/swengb/projects/filebrowser/picture.png")
            case executable if FilePropertiesUtil.isExecFile(executable) => new ImageView("/fhj/swengb/projects/filebrowser/exe.png")
            case _ => new ImageView("/fhj/swengb/projects/filebrowser/file.png")
          }
          iconView.setFitWidth(24)
          iconView.setPreserveRatio(true)
          setGraphic(iconView)
          // https://docs.oracle.com/javase/8/javafx/events-tutorial/drag-drop.htm
          setOnDragDetected(new EventHandler[MouseEvent] {
            override def handle(event: MouseEvent): Unit = {
              // start dragn'n'drop gesture
              val db : Dragboard = startDragAndDrop(TransferMode.MOVE)
              val content : ClipboardContent = new ClipboardContent()
              content.putFiles(List(t.asInstanceOf[File]).asJava)
              db.setContent(content)
              event.consume()
            }
          })
          setOnDragOver(new EventHandler[DragEvent] {
            override def handle(event: DragEvent): Unit = {
              // disallow dragging on itself and on parent folder
              val src =  event.getGestureSource.asInstanceOf[TreeCell[File]].getItem
              val dest = getItem.asInstanceOf[File]
              val srcParentFolder = src.getParentFile
              if (dest != src && dest != srcParentFolder) {
                event.acceptTransferModes(TransferMode.MOVE)
                event.consume()
              }
            }
          })
          setOnDragDropped(new EventHandler[DragEvent]() { // on target
            def handle(event: DragEvent): Unit = {
              val db : Dragboard = event.getDragboard
              try {
                if (!db.hasFiles) throw new Exception()
                //setItem(db.getFiles.get(0).asInstanceOf[T])
                // http://codingjunkie.net/java-7-copy-move/
                val sourcePath = db.getFiles.get(0).toPath
                val basePath = new File("/").toPath
                val targetPath = getItem.asInstanceOf[File].toPath.resolve(sourcePath.getFileName)
                Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING)
                event.setDropCompleted(true)
                refreshTreeItemNode(getTreeItem.asInstanceOf[TreeItem[File]])
              } catch {
                case e: Exception =>
                  e.printStackTrace()
                  event.setDropCompleted(false)
              }
              event.consume()
            }
          })
          setOnDragDone(new EventHandler[DragEvent] { // on source
            override def handle(event: DragEvent): Unit = {
              if (event.getTransferMode == TransferMode.MOVE){
                println("setOnDragDone event.getTransferMode")
                getChildren.clear()
                setText(null)
                setGraphic(null)
                refreshTreeItemNode(getTreeItem.asInstanceOf[TreeItem[File]].getParent)

              }

              event.consume()
            }
          })
          setOnMouseClicked(mouseClickedEventHandler)
        }
        else {
          setText(null)
          setGraphic(null)
        }
      }
    }
    new ATreeCell()
  }

  def mouseClickedEventHandler[_ >:MouseEvent] = new EventHandler[MouseEvent]() { // // Bei Klick auf Cell
    def handle(event: MouseEvent) {
      event.getSource match {
        case treeCell: TreeCell[_] =>
          val item = treeCell.getTreeItem
          println("mouseClickedEventHandler TreeView: " + item) // Pfad wird geprintet
          event.getButton match {
            case MouseButton.PRIMARY =>
              if (item != null) {
                event.getClickCount match {
                  case 1 => // preview file within application
                    var fileViewerPane: AnchorPane = event.getSource.asInstanceOf[TreeCell[File]].getScene.lookup("#fileViewerPane").asInstanceOf[AnchorPane]
                    fileViewerPane.getChildren
                    fileViewerPane.getChildren.clear()
                    val viewNode = FileViewerUtil.createNode(item.getValue.asInstanceOf[File])
                    fileViewerPane.getChildren.add(viewNode)
                    AnchorPane.setTopAnchor(viewNode, 0.0)
                    AnchorPane.setBottomAnchor(viewNode, 0.0)
                    AnchorPane.setLeftAnchor(viewNode, 0.0)
                    AnchorPane.setRightAnchor(viewNode, 0.0)
                  case 2 =>
                    // TODO: open in external application
                    println("left-clicked 2 times")
                  case n =>
                    println("left-clicked " + n + " times")
                }
              }
            case MouseButton.SECONDARY => println("right-click (MouseButton.SECONDARY)")
            case _ => println("neither right- nor left-click")
          }
        case a =>
          println("mouseClickedEventHandler otherClass: " + a.getClass) // wird als "null" gematch, wenn auf "expand"-Arrow geklickt wird
      }
    }
  }


}
