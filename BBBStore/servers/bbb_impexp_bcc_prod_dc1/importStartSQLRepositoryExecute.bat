@echo off
Setlocal EnableDelayedExpansion
set SAVESTAMP=%DATE:/=-%@%TIME::=_%
set SAVESTAMP=%SAVESTAMP: =%
set projectname=%1_%SAVESTAMP%

if [%1] EQU [] goto :blank
if [%2] EQU [] goto :blank
if [%3] EQU [] goto :blank
if [%4] NEQ [] goto :blank

set fileExtension=%2

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
echo  ..\..\bin\startSQLRepository -m DCS.Versioned -m BBBStore.EStore.versioned.catalog -m BBBStore.StoreRest.repositories -m BBBStore.StoreRest.versioned -s bbb_impexp_bcc_prod_dc1 -import %2 -repository %3 -project %projectname% -user publishing -comment %projectname% -activity merchandising.manageCommerceAssets

..\..\bin\startSQLRepository -m DCS.Versioned -m BBBStore.EStore.versioned.catalog -m BBBStore.StoreRest.repositories -m BBBStore.StoreRest.versioned -s bbb_impexp_bcc_prod_dc1 -import %2 -repository %3 -project %projectname% -user publishing -comment %projectname% -activity merchandising.manageCommerceAssets
	goto :afterBlank
)else (
	echo Wrong input File. Please provide the correct xml file with absolute path.
	goto :afterBlank
)

:blank
echo Wrong parameters or no parameters passed.
echo Please pass the following three parameters in mentioned order :    1. ProjectName    2. Import File(Provide xml file with absolute path)    3. Repository Name(with config path)

:afterBlank