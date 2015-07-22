#!/bin/sh

SETUP_INSTALLPATH=~/opt/oStorybook

echo "Storybook Setup..."

echo "making dir $SETUP_INSTALLPATH..."
mkdir -v -p "$SETUP_INSTALLPATH"

echo "copying files..."
#cp ./configuration.xml $SETUP_INSTALLPATH
#cp ./log4j.xml $SETUP_INSTALLPATH
#cp ./log4j.dtd $SETUP_INSTALLPATH
#cp ./LICENSE.txt $SETUP_INSTALLPATH
#cp ./README.txt $SETUP_INSTALLPATH
#cp ./oStorybook-icon.png $SETUP_INSTALLPATH
#cp ./Demo.h2.db $SETUP_INSTALLPATH
#cp ./splash.png $SETUP_INSTALLPATH
#cp -r lib $SETUP_INSTALLPATH
#cp -r dicts $SETUP_INSTALLPATH
#cp -r reports $SETUP_INSTALLPATH
#cp -r resources $SETUP_INSTALLPATH
#cp -r xdg $SETUP_INSTALLPATH
cp -r . $SETUP_INSTALLPATH

echo "invoking postinstall.sh..."
./postinstall.sh $SETUP_INSTALLPATH

echo "Storybook Setup finished."
