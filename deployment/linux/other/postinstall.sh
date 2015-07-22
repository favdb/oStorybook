#!/bin/sh

#export SETUP_INSTALLPATH="!!INSTALLPATH!!"

#if [ -z "$SETUP_INSTALLPATH" ] ; then
#  echo "SETUP_INSTALLPATH not set"
#  exit -1
#fi

SETUP_INSTALLPATH=$1

DESKTOPFILE="$SETUP_INSTALLPATH/oStorybook.desktop"
MIMEFILE="$SETUP_INSTALLPATH/oStorybook-mimetypes.xml"
UNINSTALLFILE="$SETUP_INSTALLPATH/uninstall"
STARTFILE="$SETUP_INSTALLPATH/oStorybook"

cat >$DESKTOPFILE <<EOF
[Desktop Entry]
Encoding=UTF-8
Name=oStorybook
GenericName=Novel Writing Software
Comment=Open Source Novel Writing Software for Novelists, Authors and Creative Writers.
Exec=$SETUP_INSTALLPATH/oStorybook %f
Terminal=false
MultipleArgs=false
Type=Application
Icon=$SETUP_INSTALLPATH/oStorybook-icon.png
Categories=Office;Education;TextTools;Java
MimeType=application/vnd.oStorybook

EOF

# Gnome needs a executable Desktop File
chmod a+x $DESKTOPFILE

cat >$MIMEFILE <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<mime-info xmlns="http://www.freedesktop.org/standards/shared-mime-info">
  <mime-type type="application/vnd.oStorybook">
    <comment xml:lang="en">oStorybook File</comment>
    <glob pattern="*.h2.db"/>
  </mime-type>
</mime-info>

EOF

TMPPATH="$PATH"
PATH="$PATH:$SETUP_INSTALLPATH/xdg"
chmod a+rx $SETUP_INSTALLPATH/xdg/xdg-mime
chmod a+rx $SETUP_INSTALLPATH/xdg/xdg-desktop-menu
chmod a+rx $SETUP_INSTALLPATH/xdg/xdg-desktop-icon
echo "Installing mimetypes..."
xdg-mime install "$MIMEFILE"
echo "Installing desktop menu entries..."
xdg-desktop-menu install --novendor "$DESKTOPFILE"
echo "Installing desktop icon..."
xdg-desktop-icon install --novendor "$DESKTOPFILE"
PATH="$TMPPATH"


# start script
echo "Creating start script..."
cat >$STARTFILE <<EOF

#!/bin/sh

echo "Java VM version:"
java -version

echo "starting oStorybook ..."
cd $SETUP_INSTALLPATH
java -Dfile.encoding=UTF-8 -splash:splash.png -XX:MaxPermSize=256m -Xmx400m -jar oStorybook.jar \$*
echo "done."

EOF


# uninstall script
echo "Creating uninstall script..."

cat >$UNINSTALLFILE <<EOF
#!/bin/sh

if [ -f "$DESKTOPFILE" ]; then
    echo "Uninstalling desktop menu entries..."
    TMPPATH="$PATH"
    PATH="$PATH:$SETUP_INSTALLPATH/xdg"
    xdg-desktop-menu uninstall "$DESKTOPFILE"
    xdg-desktop-icon uninstall "$DESKTOPFILE"
    PATH="$TMPPATH"
    rm -f "$DESKTOPFILE"
fi

MIMEFILE="$MIMEFILE"
if [ -f "$MIMEFILE" ]; then
    echo "Uninstalling mimetypes..."
    TMPPATH="$PATH"
    PATH="$PATH:$SETUP_INSTALLPATH/xdg"
    xdg-mime uninstall "$MIMEFILE"
    PATH="$TMPPATH"
    rm -f "$MIMEFILE"
fi

echo "Uninstalling Storybook... "
rm -rf "$SETUP_INSTALLPATH"

EOF

chmod a+x $STARTFILE
chmod a+x $UNINSTALLFILE

exit 0
