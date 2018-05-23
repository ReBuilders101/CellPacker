# CellPacker
A modding utility for Dead Cells  

Version 2.0 of CellPacker: Information and Help
Please read this file before using the program.
The compiled file can be found [here](https://github.com/ReBuilders101/CellPacker/raw/master/release/CellPacker.jar).
  
--------------------------------------------------------------------------------------------------------
CellPacker is an application to view and modify the resources used by the game Dead Cells.  
**NOTE:** The format of the Dead Cells resource file can change at any time and there is no official
	documentation. This program was designed to be used with the Baguette Update, but will most
	likely also work on newer and older versions.  
**NOTE:** CellPacker will need Java/a JRE to run.
**IMPORTANT** If you are using scripts, see the How to use > Updates section.
  
--------------------------------------------------------------------------------------------------------
### Features:  

1. View all images/textures of the game that are stored in the resource file
2. Play all sounds stored in the resource files
3. Group connected resources into views, so they are easier to find
4. Export all resources as files (PNG for images, OGG for sounds)
5. Search for resourcesS
6. Replace reources with custom ones. This way you can create your own textures or sounds for the game
7. Re-pack the modified file so it can be used by Dead Cells 
8. A launcher that lets you run scripts to modify the resources, so you can choose if you want to play with modifications without opening the editor
9. Editing of the game data, either with a text editor or with the external program [CastleDB](http://castledb.org/) (recommended)

  
--------------------------------------------------------------------------------------------------------
### How to use:  
[CellPacker Download](https://github.com/ReBuilders101/CellPacker/raw/master/release/CellPacker.jar)
##### Launcher

1. Place the file CellPacker.jar in your Dead Cells folder (usually found at <Steam folder>/steamapps/common/Dead Cells/)
2. Double-click the jar file to execute it.
3. A window with three buttons will appear. The first start may take some time because CellPacker is creating a backup of your resource file.
4. CellPacker will create the folder `cpscripts` and the file `res.pak.cpbackup`. Do not delete these, they are important for CellPacker.
5. If you have any scripts (file extension `.patch`), you can drop them into the `cpscripts` folder. They should the show up as a checkbox at the top of the window after restarting CellPacker.
6. Start Dead Cells with the left button.

##### Editor  

1. Open the editor with the middle button (`Start CellPacker`). A new window should open.
2. Open a resource file by selecting File>Open and choosing the res.pak file.
3. On the right, a tree with all found resources will appear. Click on an item to see the resource.
4. To replace a resource or to export it to a file, select Edit>Resource Options and choose your option.
**IMPORTANT:** When replacing images, make sure the replacement has the same size (pixles, not bytes) as the original.
5. You can always restore a modified resource to its original state with the Resource Options menu.
6. to create a version containing your changes that is useable by Dead Cells, simply use File>Save.
  
##### Scripting

1. A script for CellPacker is just a JSON file that is placed in the `cpscripts` folder.
2. The root element can have three subelements: `add`, `remove` and `replace`.
Everything in the `add` tag will be added to or merged with the JSON data from the data.cdb resource.
**IMPORTANT:** If you want to merge a element inside an array, you have to set the array item index in the `CPINDEX` property.
Everything in the `remove` tag will be removed from the JSON data from the data.cdb resource.
The `replace` tag should be an array. Every item in this array should have the values "old" and "new". "old" will point to a resource in the resource file, e. g. `atlas/ancientTemple.png`, the "new" value should hold the name of a file (in the `cpscripts` folder) that replaces this resource.

##### Scripting example

The following example will spawn 12 scrolls in the start biome (instead of 2). Explainations below.
```json
{
	"add": {
		"sheets":[
			{
				"CPINDEX": 10,
				"lines": [
					{
						"CPINDEX": 0,
						"tripleUps": 12
					}
				]
			}
		]
	}
}
```

Explaination:
* This script only has an `add` section. The `add` section is also used to overwrite existing values.
* Inside the `add` tag is the `sheets` tag, the root element of the data.cdb file.
* Because `sheets` is an array and we want to modify an existing tag, the `CPINDEX` property is needed. In this case, the 10th sheet is the sheet called `levels`.
* Lines is an array too, so we need the `CPINDEX` property too. Element 1 is the start biome (id: `PrisonStart`).
* The value of the `tripleUps` property is then set to 12.

##### Updates
When Dead Cells updates, this might break CellPacker. The first thing you should try is to delete `res.pak.cpbackup` and start CellPacker again. If this doesn't help, you might want to uninstall CellPacker.

##### Uninstall
To remove CellPacker, launch the game once without any scripts enabled. Then simply launch the game normally from Steam. You might want to redownload the game files with Steam.
You can delete the `cpscripts` folder and the files `res.pak.cpbackup` and `CellPacker.jar`.

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
