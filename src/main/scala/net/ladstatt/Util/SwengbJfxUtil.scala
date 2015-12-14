package net.ladstatt.Util

import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.{FXCollections, ObservableList}
import javafx.scene.control.{ListCell, ListView}
import javafx.util.Callback

import scala.collection.JavaConversions

/**
  * Utility class for common javafx related tasks
  */
object SwengbJfxUtil {

  /**
    * Given a scala collection, this function converts it into a JavaFX compatible
    * data structure called ObservableList (see http://docs.oracle.com/javafx/2/api/javafx/collections/ObservableList.html)
    *
    * The observable list allows to track changes in the list when they occur.
    *
    * @param collection a scala collection
    * @tparam T th type contained in the collection
    * @return
    */
  def mkObservableList[T](collection: Iterable[T] = List()): ObservableList[T] = {
    import JavaConversions._
    val mutableList = new java.util.ArrayList[T]
    mutableList.addAll(collection)
    FXCollections.observableList(mutableList)
  }

  /**
    * This function serves as syntactic sugar for creating a changelistener by providing a
    * suitable function.
    *
    * @param onChangeAction the action to be performed when a change happened.
    * @tparam T type which is observed.
    * @return
    */
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

  /**
    * Most often you are only interested in the new value, sometimes also in the old
    * value. This function makes it easier to create changelistener which exactly do this
    * by providing a suitable function as parameter.
    *
    * @param f function to be executed when a change happenes
    * @tparam T the underlying type which is observed
    * @return
    */
  def onChange[T](f: (T, T) => Unit): ChangeListener[T] = {
    mkChangeListener[T](relisten(_, _, _)(f))
  }

  type ListCellFactory[T] = Callback[ListView[T], ListCell[T]]

  /**
    * Creates a callback which is suitable for using it in a ListView.setCellFactory(...)
    *
    * @param f the function to be performed if the callback is called
    * @tparam T the underlying type
    * @return
    */
  def mkListCellFactory[T](f: ListView[T] => ListCell[T]): ListCellFactory[T] =
    new Callback[ListView[T], ListCell[T]] {
      override def call(param: ListView[T]): ListCell[T] = f(param)
    }

  /**
    * Helpe method which can be used to display custom text for a given type
    * in a Listview.
    *
    * @param typeToString function transforms a T into a String
    * @param lv           listView
    * @tparam T underlying type
    * @return a listCell which can be used in a listview
    */
  def show[T](typeToString: T => String)(lv: ListView[T]): ListCell[T] = {

    class AListCell extends ListCell[T] {
      override protected def updateItem(t: T, empty: Boolean): Unit = {
        super.updateItem(t, empty)
        if (t != null) {
          setText(typeToString(t))
        }
      }
    }
    new AListCell()
  }


}
