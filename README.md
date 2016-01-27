# fhj.swengb.projects.filebrowser-scala

#File Browser

The program mimics the behavior of the windows file explorer. There is a TreeView to show all folders, subfolders and files.
A Preview for text files and pictures is implemented. Moving files around by drag´n drop is implemented.
The visual appearance is achieved by the use of a CSS-file.


### __Functions:__

* __Root with Subfolder__

![Ohne CSS](ohne_css.png)

* __CSS for Style__

![Startansicht](Startansicht.png)

* __Expand Tree__ 

* __Preview of files:__

    * Text files (.txt , .css , .scala ,...)
    Left MouseButton on the file to open the preview

![Text Ansicht](TXT_Preview.png)
    
    * Image files (.png , .jpg , .bmp ,...)
    Left MouseButton on the file to open the preview

![Bilder Ansicht](Picture_Preview.png)

    * Audio Player(.mp3 , .wav , .aac ,...)
    Left MouseButton on the file to open the player in the preview-window

![Audio Player] (Audio_Player.png)

    * Video Player(.mp3 , .wav , .aac ,...)
    Left MouseButton on the file to open the player in the preview-window

![Video Player] (Video_Player.png)

    * No Preview

![Bilder Ansicht](No_preview.png)

* __Appropriate file icon:__

    * Text
    
    * Picture
    
    * Executable file
    
    * Other

* __Drag and Drop__
    * Move a file or folder by dragging and dropping it

    Left MouseButton on the file/folder then drag it to the desired position
    __Careful! Already existing files will be overwritten!__


* __Open with external program__

    * Double Click with left MouseButton to open the file with the assigned program
    * Note: There is a bug in certain JVM versions that causes the application to freeze





