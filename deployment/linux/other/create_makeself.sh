#!/bin/bash

SB_DIR=../..
TMP_DIR=storybook-installer
#RUN_FILE=storybook-3.1.0-linux-pro.bin
RUN_FILE=storybook-3.1.0-linux.bin
SETUP_FILE=./setup.sh

echo "update storybook.jar..."
SPWD=$(pwd)
cd $SB_DIR
ant jar
cd $SPWD

echo "copying files..."

rm -rf $TMP_DIR
mkdir $TMP_DIR

echo "    - setup files..."
cp $SETUP_FILE $TMP_DIR
cp postinstall.sh $TMP_DIR
svn -q export xdg $TMP_DIR/xdg

echo "    - Storybook files..."
cp $SB_DIR/configuration.xml $TMP_DIR
cp $SB_DIR/log4j.xml $TMP_DIR
cp $SB_DIR/log4j.dtd $TMP_DIR
cp $SB_DIR/LICENSE.txt $TMP_DIR
cp $SB_DIR/README.txt $TMP_DIR
cp $SB_DIR/storybook-icon.png $TMP_DIR
cp $SB_DIR/Demo.h2.db $TMP_DIR
cp $SB_DIR/splash.png $TMP_DIR
svn -q export $SB_DIR/lib $TMP_DIR/lib
cp $SB_DIR/lib/storybook.jar $TMP_DIR/lib
svn -q export $SB_DIR/dict $TMP_DIR/dict
svn -q export $SB_DIR/reports $TMP_DIR/reports
svn -q export $SB_DIR/resources $TMP_DIR/resources

#echo "cleaning..."
#find $TMP_DIR/scripts -name "*.pri" -exec rm  {} \;
#find $TMP_DIR/plugins -name ".svn" | xargs rm -Rf

echo "creating makeself file..."
rm $RUN_FILE 2>/dev/null
# makeself.sh [args] archive_dir file_name label startup_script [script_args]
# args: --notemp
./makeself.sh $TMP_DIR $RUN_FILE "Storybook" "$SETUP_FILE"

echo "done."
