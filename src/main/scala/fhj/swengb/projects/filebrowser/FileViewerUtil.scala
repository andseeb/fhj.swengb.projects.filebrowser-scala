package fhj.swengb.projects.filebrowser

import java.io.File
import javafx.scene.Node
import javafx.scene.control.{Label, TextField, TextArea}
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.{BorderPane, AnchorPane}


object FileViewerUtil {
  def createNode(file: File) : Node = file match {
    // TODO: create more cases for different file types (eg for images, videos, audio)
    // TODO: create case for folders
    case textFile if FilePropertiesUtil.isTextFile(file) =>
      val textArea = new TextArea()
      textArea.setText(scala.io.Source.fromFile(textFile).getLines.mkString("\n")) // TODO: verify if correct character encoding is chosen automatically
      textArea

    case picture if FilePropertiesUtil.isPicture(file) =>
      val y = new ImageView(file.toURI.toString)
      y.setFitWidth(300)
      y.setPreserveRatio(true)
      y.setSmooth(true)
      y.setCache(true)
      y

    case _ =>
      val defaultNode = new BorderPane()
      defaultNode.setCenter(new Label("No preview available"))
      //defaultNode.setStyle("-fx-background-color: gray;")
      defaultNode
  }
}
