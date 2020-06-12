#! /bin/bash

export JAVA_HOME=/usr/lib64/openjdk-8
export PATH=$PATH:$JAVA_HOME/bin:
shopt -s globstar

function makeMenuFile(){
touch config/$1.desktop && > config/$1.desktop
echo "[Desktop Entry]" >> config/$1.desktop 
echo "Name=$1"  >> config/$1.desktop 
echo "Exec=/usr/lib64/openjdk-8/bin/java -XX:+UseG1GC -Xms16M -Xmx50M -jar /opt/$1/$1.jar"  >> config/$1.desktop 
echo "icon=/opt/$1/icon.png"  >> config/$1.desktop 
echo "Type=Application"  >> config/$1.desktop 
echo "Terminal=false"  >> config/$1.desktop 
}

function makeSHFile(){
touch config/$1.sh && > config/$1.sh
echo "#! /bin/bash" >> config/$1.sh
echo "export JAVA_HOME=/usr/lib64/openjdk-8" >> config/$1.sh
echo "export PATH=$PATH:$JAVA_HOME/bin:" >> config/$1.sh
echo " " >> config/$1.sh
echo "java -XX:+UseG1GC -Xms16M -Xmx50M -jar /opt/$1/$1.jar" >> config/$1.sh
chmod +x config/$1.sh
}

function makeUninstallFile(){
touch uninstall.sh && > uninstall.sh
echo "#! /bin/bash" >> uninstall.sh
echo " " >> uninstall.sh
echo "sudo rm /usr/local/bin/$1" >> uninstall.sh
echo "rm -r /opt/$1" >> uninstall.sh
echo "rm ~/.local/share/applications/$1.desktop" >> uninstall.sh
echo "update-desktop-database ~/.local/share/applications/" >> uninstall.sh
echo "echo " >> uninstall.sh
echo 'echo "====    File manager uninstalled    ===="' >> uninstall.sh
chmod +x uninstall.sh
}

echo
echo "====     Please enter theme file name with file extention          ===="
echo "====     nord.css and gruvbox.css available in themes directory    ===="

read themeFile 

if [ ! -f themes/$themeFile ]; then
    echo "Provided theme file not found in themes directory" ;
    exit 1 ;
else 
    cssFile="$themeFile"
fi

# chage it for other distro
#sudo eopkg it openjfx-8 font-awesome-ttf
# change it for other distro

fmname="$USER""fm"

mkdir build 
javac -cp .:nullfm/lib/* -d build -s nullfm/src **/*.java
cp themes/$cssFile nullfm/resources/css/fm.css
cp -r nullfm/resources/* build/
cd build
jar xf ../nullfm/lib/commons-io-2.6.jar
jar xf ../nullfm/lib/log4j-api-2.13.3.jar
jar xf ../nullfm/lib/log4j-core-2.13.3.jar
jar cfm $fmname.jar ../config/MANIFEST.txt *
sudo mkdir -p /opt/$fmname && sudo mv $fmname.jar /opt/$fmname/
cd .. && rm -r build
sudo cp config/icon.png /opt/$fmname/        
makeMenuFile $fmname && makeSHFile $fmname && makeUninstallFile $fmname
cp config/$fmname.desktop ~/.local/share/applications/
sudo cp config/$fmname.sh /usr/local/bin/$fmname
update-desktop-database ~/.local/share/applications/
echo 
echo "====   $fmname File manager installed :::: Please check application menu       ===="
