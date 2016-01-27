package fhj.swengb.projects.filebrowser

import java.io.File
import javafx.scene.Node
import javafx.scene.control.{Label, TextField, TextArea}
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.{BorderPane, AnchorPane}
import javafx.scene.web.WebView


object FileViewerUtil {
  def createNode(file: File) : Node = {
    try {
      file match {
        // TODO: create more cases for different file types (eg for images, videos, audio)
        // TODO: create case for folders
        case textFile if FilePropertiesUtil.isTextFile(file) =>
          val textArea = new TextArea()
          textArea.setId("text")
          textArea.setText(scala.io.Source.fromFile(textFile).getLines.mkString("\n")) // TODO: verify if correct character encoding is chosen automatically
          textArea

        case picture if FilePropertiesUtil.isPicture(file) =>
          val y = new ImageView(file.toURI.toString)
          y.setFitWidth(300)
          y.setPreserveRatio(true)
          y.setSmooth(true)
          y.setCache(true)
          y

        case audioFile if FilePropertiesUtil.isAudioFile(file) =>
          val webview = new WebView
          webview.getEngine.loadContent(s"<style>body{background-color:#E6E6E6}</style><audio autoplay controls><source src='${file.toURI}' type='audio/mpeg'>No preview available.</audio>")
          webview

        case videoFile if FilePropertiesUtil.isVideoFile(file) =>
          val webview = new WebView
          val fileType = {
            if (FilePropertiesUtil.isMp4File(file)) "video/mp4"
            else if (FilePropertiesUtil.isOggFile(file)) "video/ogg"
            else throw new IllegalArgumentException
          }
          webview.getEngine.loadContent(s"<style>body{background-color:#E6E6E6}</style><video width='450' autoplay controls><source src='${file.toURI}' type='$fileType'>No preview available.</video>")
          webview
      }
    } catch {
      // catch if different filetype or exception occured while loading file
      case e: Exception =>
        val defaultNode = new BorderPane()
        defaultNode.setCenter(new Label("No preview available"))
        defaultNode.setId("noprev")
        defaultNode
    }
  }
}
