package fhj.swengb.projects.filebrowser


import java.io.File
import javafx.beans.value.{ObservableValue, ChangeListener}
import javafx.collections.{FXCollections, ObservableList}
import javafx.scene.control.{TreeCell, TreeView}
import javafx.scene.image.ImageView
import javafx.util.Callback

import scala.collection.JavaConversions

object FbUtil {


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
            case _ => new ImageView()
          }
          setGraphic(iconView)
        }
        else {
          setText(null)
          setGraphic(null)
        }
      }
    }
    new ATreeCell()
  }
}
