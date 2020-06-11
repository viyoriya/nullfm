###  Username fm

Experimental Javafx based file manager for Linux systems with nord/gruv box theme.


#### Build

   1. Download / clone this repo.
   2. change permission to build script

        `chmod +x *.sh`
      
   3. run the build.sh and enter provide theme .css file name [nord.css or gruvbox.css]
   
        `./build.sh`
   
   2. File manager will be listed in application menu

#### Uninstall
   1. run the uninstall.sh file
   
       `./uninstall.sh`
       
#### Theme mod 
    
   1. Duplicate a theme css file in themes directory, update the colorscheme and uninstall and rebuild.      

#### i18n support

   1. Before building the source code duplicate a file from nullfm/resources/i18n/ .
   2. Save as fm_Laungauge_Country.properties and add/update the content and build.

      example : fm_fr_FR.properties
    
#### Cons
  1. Memory hungry (uses ~150MB of RAM)
  2. Too many java process will be seen in htop
  3. Only list view available
  3. No drag and drop support
  4. No automatic USB connection (Avoided running the thread)
  5. Only tested in ext4 filesystem 

#### Pro
   1. Basic file manager with colorful theme
   2. True Open Source (No license, Use/mod/redistribute the code)
   3. No need to buy a coffee for me :)   

#### Note
   1. Connect the USB/External drive and click devices button on top bar.

#### Why ?
   
###### I haven't seen any java based good looking file browser so created one. 
 
#### Screen shot
 
![Nord](https://github.com/viyoriya/nullfm/blob/master/screenshot/2020-06-11-17-43-13.png)

![Gruv Box](https://github.com/viyoriya/nullfm/blob/master/screenshot/2020-06-11-17-38-10.png)
  