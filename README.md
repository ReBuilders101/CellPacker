# CellPacker
A modding utility for Dead Cells  

Version 0.8 of CellPacker: Information and license  
Please read this file completely before using the program.
  
--------------------------------------------------------------------------------------------------------
CellPacker is an application to view and modify the resources of the game
Dead Cells.  
**NOTE:** The format of the Dead Cells resource file can change at any time and there is no official
	documentation. This program was designed to be used with the Brutal Update, but will most
	likely also work on newer and older versions.  
**NOTE:** CellPacker will need Java/a JRE to run.
  
--------------------------------------------------------------------------------------------------------
### Features:  
1) View all images/textures of the game that are stored in the resource file
2) Play all sounds stored in the resource files
3) Assingn filter files (which are responsibe for the 3D appearance of objects) and
	atlas files (which identify different sprites in an image file) to the main image resource
4) Export all resources as files (PNG for images, OGG for sounds)
5) Search and sort found resources
6) Replace reources with custom ones. WARNING: The replacements are completely unchecked, so make
	sure that images have the same width and height, and the sprites are at the correct position.
7) Export a modified version of the resource file that can be used by Dead Cells
8) Import and export patch files, which store changes made to the original resources and can be applied
	to any version of the resource file

The file data.exported.cdb contains the game data and can be edited with the program 'CastleDB'.
  
--------------------------------------------------------------------------------------------------------
### How to use:  
NOTE: Almost every element has a tooltip that describes what it does.

1) Execute the file CellPacker.jar. You will need the Java JRE for that.
2) Find the path to your Dead Cells resource file. It is called 'res.pak'.
	Either enter the path into the text field labeled with 'Path to res.pak:' or click the '...' button
	next to it to open a file selection dialog. If the text filed is empty, the reading path defaults
	to 'C:/Program Files (x86)/Steam/steamapps/common/Dead Cells/res.pak'.
3) Click the 'Read' button. The progress bar should be active for a moment. A list of resources should appear
	at the top rigth afterwards. This are the resources found in the 'res.pak' file.  
  
Exporting the files:
1) Select an output folder by clicking on the '...' button next to the 'Write' button.
2) Click 'Write' WARNING: This will create ~1000 files in this folder, so don't choose your Desktop!
  
Replacing a file:  
1) Select the file you want to replace.  
2) Hit 'Replace current resource' and select your replacement.
	WARNING: If the file is an image, it must have the same width and height as the original, otherwise
	the game will crash.  
  
Exporting Patches:  
1) Hit 'Export patch file'. This will only work if there are modified resources.  
2) Choose a name for the created patch file.  
3) From the upcoming list, select all resources you want to inclue in your patch and hit 'Ok'.  
  
--------------------------------------------------------------------------------------------------------  
### How to build:
This repository contains a full eclipse project, you can just clone and import it. It will probably also work with any other IDE, but you will have to set the .jar files in the lib folder as dependencies. 
  
--------------------------------------------------------------------------------------------------------
### License:  
You may use and distibute this program as long as the following conditions are met:
1) It's completely open source, you can take parts of the source code and use them for your own project. You don't have to give me credit if you use parts of this code in your own project
2) You are not allowed to sell this program or commercially use it.
3) If you distribute this program, you must include this README.md file unmodified.
4) You are not allowed to claim to be the author of this program or copyright it.

Third Party Licenses and Credits:
This program uses the following third party librarys:
- CodecJOrbis by Paul Lamb (http://paulscode.com), because Java does not support the OGG sound format (really, Java?)
