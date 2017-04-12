@echo off

if [%1] EQU [] goto :blank
if [%2] EQU [] goto :blank
if [%3] NEQ [] goto :blank

set fileExtension=%1

rem Below cmd is executed to check filename does not have any special characters except _ and -.
cmd /c echo %fileExtension%|findstr /r ".*[^a-zA-Z0-9\:\/\.\_\-\\].*"

if %errorlevel% equ 0 ( 
	echo Filename should not contain special characters except Underscore "_", Dash "-". 
	goto :afterBlank
)

set fileExtension=%fileExtension:~-4%

rem Below check is added to compare whether file is xml file or not

if "%fileExtension%"==".xml" (
echo Executing Below StartSQLRepository Script.
echo ..\..\bin\startSQLRepository -m DCS.Versioned -m BBBStore.EStore.versioned.catalog -s bbb_impexp_bcc_uate -export all %1 -repository %2

..\..\bin\startSQLRepository -m DCS.Versioned -m BBBStore.EStore.versioned.catalog -s bbb_impexp_bcc_uate -export all %1 -repository %2
	goto :afterBlank
)else (
	echo Wrong input File. Please provide the correct xml file with absolute path.
	goto :afterBlank
)

:blank
echo Wrong parameters or no parameters passed.
echo Please pass the following two parameters in mentioned order :    1. Export File(Provide xml file with absolute path)    2. Repository Name(with config path)

:afterBlank