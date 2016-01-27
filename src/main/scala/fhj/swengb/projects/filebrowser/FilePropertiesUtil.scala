package fhj.swengb.projects.filebrowser

import java.io.File


object FilePropertiesUtil {
  def isTextFile(file: File) : Boolean = {
    file.getName.endsWith(".txt") | file.getName.endsWith(".log") |
      file.getName.endsWith(".cfg") | file.getName.endsWith(".config") |
      file.getName.endsWith(".scala") | file.getName.endsWith(".java") |
      file.getName.endsWith(".html") | file.getName.endsWith(".xhtml") |
      file.getName.endsWith(".xml") | file.getName.endsWith(".json") |
      file.getName.endsWith(".fxml") | file.getName.endsWith(".css") |
      file.getName.endsWith(".csv") | file.getName.endsWith(".md")
  }
  // TODO: create more file definitions
  def isPicture(file: File) : Boolean = {
    file.getName.endsWith(".jpg") | file.getName.endsWith(".png") |
      file.getName.endsWith(".ico") | file.getName.endsWith(".svg") |
      file.getName.endsWith(".bmp") | file.getName.endsWith(".gif")

  }
  def isExecFile(file: File) : Boolean = {
    file.getName.endsWith(".exe") | file.getName.endsWith(".bat") |
      file.getName.endsWith(".ps") | file.getName.endsWith(".cmd") |
      file.getName.endsWith(".msi") | file.getName.endsWith(".sh")

  }
  def isAudioFile(file: File) : Boolean = {
    file.getName.endsWith(".mp3") | file.getName.endsWith(".wav") |
      file.getName.endsWith(".m4a") | file.getName.endsWith(".aac")

  }
}
