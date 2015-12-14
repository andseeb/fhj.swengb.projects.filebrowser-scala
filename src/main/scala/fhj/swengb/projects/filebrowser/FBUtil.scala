package fhj.swengb.projects.filebrowser

import java.io.File
import javafx.scene.control.{TreeCell, TreeView}
import javafx.util.Callback

/**
  * Created by Andreas on 14.12.2015.
  */
object FbUtil {


  type TreeCellFactory[T] = Callback[TreeView[T], TreeCell[T]]


  def mkTreeCellFactory[T](f: TreeView[T] => TreeCell[T]): TreeCellFactory[T] =
    new Callback[TreeView[T], TreeCell[T]] {
      override def call(param: TreeView[T]): TreeCell[T] = f(param)
    }



  def show[T](typeToString: T => String)(lv: TreeView[T]): TreeCell[T] = {

    class ATreeCell extends TreeCell[T] {
      override protected def updateItem(t: T, empty: Boolean): Unit = {
        super.updateItem(t, empty)
        if (t != null) {
          setText(typeToString(t))
        }
      }
    }
    new ATreeCell()
  }
}
