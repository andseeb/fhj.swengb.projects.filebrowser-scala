package fhj.swengb.projects.filebrowser

import java.io.File


object FilePropertiesUtil {
  def isTextFile(file: File) : Boolean = {
    file.getName.endsWith(".txt") | file.getName.endsWith(".log") |
      file.getName.endsWith(".cfg") | file.getName.endsWith(".config") |
      file.getName.endsWith(".scala") | file.getName.endsWith(".java") |
      file.getName.endsWith(".html") | file.getName.endsWith(".xhtml") |
      file.getName.endsWith(".xml") | file.getName.endsWith(".json") | file.getName.endsWith(".csv")
  }
  // TODO: create more file definitions
}
