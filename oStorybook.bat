@echo off

rem echo working dir: %CD%
rem echo cmd: %CMDCMDLINE%
rem echo systemroot: %SystemRoot%
rem echo arg0: %0
rem echo arg1: %1
rem echo dp0: %~dp0

pushd %CD%
cd %~dp0
java -Dfile.encoding=UTF-8 -XX:MaxPermSize=256m -Xmx300m -jar oStorybook.jar %1 %2
popd
