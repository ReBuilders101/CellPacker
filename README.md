# CellPacker [Outdated]

**Dead Cells now has its own 
(basic) modding tools.** This project is no longer required and might not support newer versions of the game.

CellPacker can still be used as a resource viewer and editor (unless the res.pak format changes in the future), but can **not** be
used to create mods compatible with the Steam workshop

## [Old description below]

A modding utility for Dead Cells  

Version 2.1 of CellPacker: Information and Help
Please read this file before using the program.
The compiled file can be found [here](https://github.com/ReBuilders101/CellPacker/raw/master/release/CellPacker.jar).
  
--------------------------------------------------------------------------------------------------------
CellPacker is an application to view and modify the resources used by the game Dead Cells.  
**NOTE:** The format of the Dead Cells resource file can change at any time and there is no official
	documentation. This program was designed to be used with the Baguette Update, but will most
	likely also work on newer and older versions.  
  
--------------------------------------------------------------------------------------------------------
### Features:  

1. View all images/textures of the game that are stored in the resource file
2. Play all sounds stored in the resource files
3. Group connected resources into views, so they are easier to find
4. Export all resources as files (PNG for images, OGG for sounds)
5. Search for resources
6. Replace reources with custom ones. This way you can create your own textures or sounds for the game
7. Re-pack the modified file so it can be used by Dead Cells 
8. ~~A launcher that lets you run scripts to modify the resources, so you can choose if you want to play with modifications without opening the editor~~
9. Editing of the game data, either with a text editor or with the external program [CastleDB](http://castledb.org/) (recommended)

  
--------------------------------------------------------------------------------------------------------
### How to use:  
#### Download
[CellPacker Download](https://github.com/ReBuilders101/CellPacker/raw/master/release/CellPacker.jar)
##### Editor
1. CellPacker.jar can be run from any folder. Double-click to execute (Java 8 or newer must be installed)
2. Use File>Open to open the res.pak file in your Dead Cells folder (usually found at <Steam folder>/steamapps/common/Dead Cells/)
3. On the right, a tree with all found resources will appear. Click on an item to see the resource.
4. To replace a resource or to export it to a file, select Edit>Resource Options and choose your option.
**IMPORTANT:** When replacing images, make sure the replacement has the same size (pixles, not bytes) as the original.
5. You can always restore a modified resource to its original state with the Resource Options menu.
6. To create a version containing your changes that is useable by Dead Cells, simply use File>Save and overwrite the
existing res.pak file. Mare sure to back up the original file before.
  
##### ~~Scripting~~
The scripting feature was never fully implemented and was made obsolete when modding through the Steam Workshop was introduced

##### Uninstall
CellPacker does not create any permanent files. Simply delete the CellPacker.jar file to delete the program from your computer.

--------------------------------------------------------------------------------------------------------  
### Compiled files and code:
##### Compiled files
Use the CellPacker.jar in the release folder.
##### If you want to compile the code:
This repository contains a full eclipse project, you can just clone and import it. It will probably also work with any other IDE.
All jar files in the `lib` folder are dependencies.
  
--------------------------------------------------------------------------------------------------------
### License:  
Basic:
* Do whatever you want.
  
But: 
* Please don't claim that you made this. It's not nice.
* Feel free to take any code from this project and use it for your own project.

Also: 
* If Dead Cells breaks, it is not my fault.

##### Third Party Licenses and Credits:
This program uses the following third party librarys:
- CodecJOrbis by Paul Lamb (http://paulscode.com), because Java does not support the OGG sound format.
- Gson (under the Apache License 2.0, can be found at http://www.apache.org/licenses/LICENSE-2.0) to parse JSON.
