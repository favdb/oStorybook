Name "Storybook Pro"

# Defines
!define REGKEY "SOFTWARE\$(^Name)"
!define VERSION 3.1.0
!define COMPANY Intertec
!define URL www.novelist.ch
!define PROGRAM_NAME "Storybook Pro"

# MUI defines
!define MUI_ICON "${NSISDIR}\Contrib\Graphics\Icons\modern-install.ico"
!define MUI_STARTMENUPAGE_REGISTRY_ROOT HKLM
!define MUI_STARTMENUPAGE_NODISABLE
!define MUI_STARTMENUPAGE_REGISTRY_KEY ${REGKEY}
!define MUI_STARTMENUPAGE_REGISTRY_VALUENAME StartMenuGroup
!define MUI_STARTMENUPAGE_DEFAULTFOLDER "Storybook Pro"
!define MUI_UNICON "${NSISDIR}\Contrib\Graphics\Icons\modern-uninstall.ico"
!define MUI_UNFINISHPAGE_NOAUTOCLOSE

# finish page
!define MUI_FINISHPAGE_NOAUTOCLOSE
!define MUI_FINISHPAGE_RUN
!define MUI_FINISHPAGE_RUN_TEXT "Start Storybook Pro"
!define MUI_FINISHPAGE_RUN_FUNCTION "LaunchLink"
!define MUI_FINISHPAGE_SHOWREADME $INSTDIR\README.txt

# Included files
!include "FileAssociation.nsh"
!include Sections.nsh
!include MUI.nsh

# Reserved Files
ReserveFile "${NSISDIR}\Plugins\AdvSplash.dll"

# Variables
Var StartMenuGroup

# Installer pages
!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_LICENSE LICENSE.txt
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_STARTMENU Application $StartMenuGroup
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

# Installer languages
!insertmacro MUI_LANGUAGE English

# Installer attributes
OutFile "storybook-3.1.0-win32-pro.exe"
InstallDir "$PROGRAMFILES\Storybook Pro"
CRCCheck on
XPStyle on
ShowInstDetails show
VIProductVersion 3.1.0.0
VIAddVersionKey ProductName "Storybook Pro"
VIAddVersionKey ProductVersion "${VERSION}"
VIAddVersionKey CompanyName "${COMPANY}"
VIAddVersionKey CompanyWebsite "${URL}"
VIAddVersionKey FileVersion "${VERSION}"
VIAddVersionKey FileDescription ""
VIAddVersionKey LegalCopyright ""
InstallDirRegKey HKLM "${REGKEY}" Path
ShowUninstDetails show

# Installer sections
Section -Main SEC0000
    SetOutPath $INSTDIR
    SetOverwrite on
    File storybook.bat
    File README.txt
    File LICENSE.txt
    File INSTALL.txt
    File storybook-icon.ico
    File configuration.xml
    File log4j.xml
    File log4j.dtd
    
	# read the value from the registry into the $0 register
    readRegStr $0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" CurrentVersion
	
    # create short cuts
	Call GetJRE
	Pop $R0
	CreateShortcut "$INSTDIR\Storybook Pro.lnk" $R0 "-Xmx256m -jar lib/storybook.jar" $INSTDIR\storybook-icon.ico
    CreateDirectory "$SMPROGRAMS\$StartMenuGroup"
	CreateShortcut "$SMPROGRAMS\$StartMenuGroup\${PROGRAM_NAME}.lnk" $R0 "-Xmx256m -jar lib/storybook.jar" $INSTDIR\storybook-icon.ico
    CreateShortcut "$SMPROGRAMS\$StartMenuGroup\${PROGRAM_NAME} Homepage.lnk" http://www.novelist.ch $INSTDIR\storybook-icon.ico

    # create desktop short cut
	CreateShortCut "$DESKTOP\${PROGRAM_NAME}.lnk" $R0 "-Xmx256m -jar lib/storybook.jar" $INSTDIR\storybook-icon.ico

	# register file extension
	#${registerExtension} "storybook.bat" ".db" "Storybook File"

    SetOutPath $INSTDIR\lib
    File /r lib\*.jar
    
    SetOutPath $INSTDIR\dict
    File /r dict\*.ortho

    SetOutPath $INSTDIR\reports
    File /r reports\*
    
    SetOutPath $INSTDIR\resources
    File /r resources\*
    
    WriteRegStr HKLM "${REGKEY}\Components" Main 1
SectionEnd

Section -post SEC0001
    WriteRegStr HKLM "${REGKEY}" Path $INSTDIR
    SetOutPath $INSTDIR
    WriteUninstaller $INSTDIR\uninstall.exe
    !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
    SetOutPath $SMPROGRAMS\$StartMenuGroup
    CreateShortcut "$SMPROGRAMS\$StartMenuGroup\Uninstall $(^Name).lnk" $INSTDIR\uninstall.exe
    !insertmacro MUI_STARTMENU_WRITE_END
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayName "$(^Name)"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayVersion "${VERSION}"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" Publisher "${COMPANY}"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" URLInfoAbout "${URL}"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayIcon $INSTDIR\uninstall.exe
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" UninstallString $INSTDIR\uninstall.exe
    WriteRegDWORD HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" NoModify 1
    WriteRegDWORD HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" NoRepair 1
SectionEnd

# Macro for selecting uninstaller sections
!macro SELECT_UNSECTION SECTION_NAME UNSECTION_ID
    Push $R0
    ReadRegStr $R0 HKLM "${REGKEY}\Components" "${SECTION_NAME}"
    StrCmp $R0 1 0 next${UNSECTION_ID}
    !insertmacro SelectSection "${UNSECTION_ID}"
    GoTo done${UNSECTION_ID}
next${UNSECTION_ID}:
    !insertmacro UnselectSection "${UNSECTION_ID}"
done${UNSECTION_ID}:
    Pop $R0
!macroend

# Uninstaller sections
Section /o -un.Main UNSEC0000
    RmDir /r /REBOOTOK $INSTDIR\lib
    RmDir /r /REBOOTOK $INSTDIR\reports
    RmDir /r /REBOOTOK $INSTDIR\dict
    RmDir /r /REBOOTOK $INSTDIR\resources
    Delete /REBOOTOK $INSTDIR\LICENSE.txt
    Delete /REBOOTOK $INSTDIR\README.txt
    Delete /REBOOTOK $INSTDIR\INSTALL.txt
    Delete /REBOOTOK $INSTDIR\storybook.bat
    Delete /REBOOTOK "$INSTDIR\Storybook Pro.lnk"
    Delete /REBOOTOK $INSTDIR\storybook-icon.ico
    Delete /REBOOTOK $INSTDIR\configuration.xml
    Delete /REBOOTOK $INSTDIR\log4j.dtd
    Delete /REBOOTOK $INSTDIR\log4j.xml
    Delete /REBOOTOK "$SMPROGRAMS\$StartMenuGroup\Storybook Pro.lnk"
    Delete /REBOOTOK "$SMPROGRAMS\$StartMenuGroup\Storybook Homepage.lnk"
    RmDir /r /REBOOTOK $SMPROGRAMS\$StartMenuGroup
    Delete /REBOOTOK "$DESKTOP\${PROGRAM_NAME}.lnk"
    
    #${unregisterExtension} ".db" "Storybook File"
    
    DeleteRegValue HKLM "${REGKEY}\Components" Main
SectionEnd

Section -un.post UNSEC0001
    DeleteRegKey HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)"
    Delete /REBOOTOK "$SMPROGRAMS\$StartMenuGroup\Uninstall $(^Name).lnk"
    Delete /REBOOTOK $INSTDIR\uninstall.exe
    DeleteRegValue HKLM "${REGKEY}" StartMenuGroup
    DeleteRegValue HKLM "${REGKEY}" Path
    DeleteRegKey /IfEmpty HKLM "${REGKEY}\Components"
    DeleteRegKey /IfEmpty HKLM "${REGKEY}"
    RmDir /REBOOTOK $SMPROGRAMS\$StartMenuGroup
    RmDir /REBOOTOK $INSTDIR
SectionEnd

# Installer functions
Function .onInit

  ReadRegStr $R0 HKLM \
  "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PROGRAM_NAME}" \
  "UninstallString"
  StrCmp $R0 "" done
 
  MessageBox MB_OKCANCEL|MB_ICONEXCLAMATION \
  "${PROGRAM_NAME} is already installed. $\n$\nClick `OK` to remove the \
  previous version or `Cancel` to cancel this upgrade." \
  IDOK uninst
  Abort
  
;Run the uninstaller
uninst:
  ClearErrors
  Exec $INSTDIR\uninstall.exe
  
done:

    InitPluginsDir
    Push $R1
    File /oname=$PLUGINSDIR\spltmp.bmp resources\logo500.bmp
    # advsplash::show 1000 600 400 -1 $PLUGINSDIR\spltmp
    Pop $R1
    Pop $R1
FunctionEnd

# Uninstaller functions
Function un.onInit
    ReadRegStr $INSTDIR HKLM "${REGKEY}" Path
    !insertmacro MUI_STARTMENU_GETFOLDER Application $StartMenuGroup
    !insertmacro SELECT_UNSECTION Main ${UNSEC0000}
FunctionEnd

Function LaunchLink
;  MessageBox MB_OK "Reached LaunchLink $\r$\n \
;                   SMPROGRAMS: $SMPROGRAMS  $\r$\n \
;                   Start Menu Folder: $STARTMENU_FOLDER $\r$\n \
;                   InstallDirectory: $INSTDIR "
  ExecShell "" "$INSTDIR\Storybook Pro.lnk"
FunctionEnd

Function GetJRE
;
;  Find JRE (javaw.exe)
;  1 - in .\jre directory (JRE Installed with application)
;  2 - in JAVA_HOME environment variable
;  3 - in the registry
;  4 - assume javaw.exe in current dir or PATH
 
  Push $R0
  Push $R1
 
  ClearErrors
  StrCpy $R0 "$EXEDIR\jre\bin\javaw.exe"
  IfFileExists $R0 JreFound
  StrCpy $R0 ""
 
  ClearErrors
  ReadEnvStr $R0 "JAVA_HOME"
  StrCpy $R0 "$R0\bin\javaw.exe"
  IfErrors 0 JreFound
 
  ClearErrors
  ReadRegStr $R1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
  ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$R1" "JavaHome"
  StrCpy $R0 "$R0\bin\javaw.exe"
 
  IfErrors 0 JreFound
  StrCpy $R0 "javaw.exe"
 
 JreFound:
  Pop $R1
  Exch $R0
FunctionEnd
