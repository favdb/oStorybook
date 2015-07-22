#!/bin/sh

echo "Java VM version:"
java -version

echo "starting oStorybook ..."
cd /home/favdb/opt/oStorybook
java -Dfile.encoding=UTF-8 -splash:splash.png -XX:MaxPermSize=256m -Xmx400m -jar oStorybook.jar $*
echo "done."

