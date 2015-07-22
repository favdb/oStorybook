#!/bin/sh
echo Création de la préparation pour la version 4.9.17
mkdir /home/favdb/ext/SB4/code/distrib/debian-package
echo Copie debian-package
cp -R /home/favdb/ext/SB4/code/deployment/linux/debian/DEBIAN /home/favdb/ext/SB4/code/distrib/debian-package
cp -R /home/favdb/ext/SB4/code/deployment/linux/debian/usr /home/favdb/ext/SB4/code/distrib/debian-package
sed -i -e "s/@version@/4.9.17/g" /home/favdb/ext/SB4/code/distrib/debian-package/DEBIAN/control
sed -i -e "s/@version@/4.9.17/g" /home/favdb/ext/SB4/code/distrib/debian-package/usr/share/applications/ostorybook.desktop
sed -i -e "s/@version@/4.9.17/g" /home/favdb/ext/SB4/code/distrib/debian-package/usr/share/doc/ostorybook/changelog
gzip -9 /home/favdb/ext/SB4/code/distrib/debian-package/usr/share/doc/ostorybook/changelog
echo Copie application
cp -R /home/favdb/ext/SB4/code/dist/* /home/favdb/ext/SB4/code/distrib/debian-package/usr/share/ostorybook
rm /home/favdb/ext/SB4/code/distrib/debian-package/usr/share/ostorybook/LICENSE.txt
echo Modification des droits
chmod -R 644 /home/favdb/ext/SB4/code/distrib/debian-package/usr
chmod -R 755 /home/favdb/ext/SB4/code/distrib/debian-package/DEBIAN
chmod -R 755 /home/favdb/ext/SB4/code/distrib/debian-package/usr/bin/ostorybook
chmod    755 /home/favdb/ext/SB4/code/distrib/debian-package/usr
chmod    755 /home/favdb/ext/SB4/code/distrib/debian-package/usr/bin
chmod    755 /home/favdb/ext/SB4/code/distrib/debian-package/usr/share
chmod    755 /home/favdb/ext/SB4/code/distrib/debian-package/usr/share/applications
chmod    755 /home/favdb/ext/SB4/code/distrib/debian-package/usr/share/doc
chmod    755 /home/favdb/ext/SB4/code/distrib/debian-package/usr/share/doc/ostorybook
chmod    755 /home/favdb/ext/SB4/code/distrib/debian-package/usr/share/icons
chmod    755 /home/favdb/ext/SB4/code/distrib/debian-package/usr/share/mime
chmod    755 /home/favdb/ext/SB4/code/distrib/debian-package/usr/share/mime/packages
chmod    755 /home/favdb/ext/SB4/code/distrib/debian-package/usr/share/ostorybook
chmod    755 /home/favdb/ext/SB4/code/distrib/debian-package/usr/share/ostorybook/dicts
chmod    755 /home/favdb/ext/SB4/code/distrib/debian-package/usr/share/ostorybook/lib
chmod    755 /home/favdb/ext/SB4/code/distrib/debian-package/usr/share/ostorybook/lib/languagetool
chmod    755 /home/favdb/ext/SB4/code/distrib/debian-package/usr/share/ostorybook/lib/languagetool/resource
chmod    755 /home/favdb/ext/SB4/code/distrib/debian-package/usr/share/ostorybook/lib/languagetool/resource/ast
chmod    755 /home/favdb/ext/SB4/code/distrib/debian-package/usr/share/ostorybook/lib/languagetool/resource/be
chmod    755 /home/favdb/ext/SB4/code/distrib/debian-package/usr/share/ostorybook/lib/languagetool/resource/de
chmod    755 /home/favdb/ext/SB4/code/distrib/debian-package/usr/share/ostorybook/lib/languagetool/resource/en
chmod    755 /home/favdb/ext/SB4/code/distrib/debian-package/usr/share/ostorybook/lib/languagetool/resource/fr
chmod    755 /home/favdb/ext/SB4/code/distrib/debian-package/usr/share/ostorybook/lib/languagetool/rules
chmod    755 /home/favdb/ext/SB4/code/distrib/debian-package/usr/share/ostorybook/lib/languagetool/rules/be
chmod    755 /home/favdb/ext/SB4/code/distrib/debian-package/usr/share/ostorybook/lib/languagetool/rules/de
chmod    755 /home/favdb/ext/SB4/code/distrib/debian-package/usr/share/ostorybook/lib/languagetool/rules/en
chmod    755 /home/favdb/ext/SB4/code/distrib/debian-package/usr/share/ostorybook/lib/languagetool/rules/fr
echo Construction du paquet DEBIAN
dpkg-deb --build /home/favdb/ext/SB4/code/distrib/debian-package /home/favdb/ext/SB4/code/distrib/oStorybook-4.9.17.deb
echo Nettoyage...
rm -r -f /home/favdb/ext/SB4/code/distrib/debian-package
echo Construction du paquet RPM
cd /home/favdb/ext/SB4/code/distrib
alien -r /home/favdb/ext/SB4/code/distrib/oStorybook-4.9.17.deb