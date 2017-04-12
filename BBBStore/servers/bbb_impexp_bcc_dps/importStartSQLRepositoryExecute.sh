export SAVESTAMP=$(date "+%m-%d-%Y@%H_%M_%S")
export projectname=$1_$SAVESTAMP

if [ "$#" -ne "3" ]
then 
	echo "Wrong parameters or no parameters passed."
	echo "Please pass the following three parameters in mentioned order :    1. Project Name    2. Import File(Provide xml file with absolute path)    3. Repository Name(with config path)"
	exit 0
fi

export fileExtension=$2
export len=`echo $fileExtension|wc -c`

 
if [ 6 -gt $len ]
then 
	echo "Wrong input File. Please provide the correct xml file with absolute path."
	exit 0
#Below condition is to check filename does not have any special characters.
elif [[ $fileExtension == *[^a-zA-Z0-9\/\.\-_]* ]]
then 
	echo "Filename should not contain special characters except Underscore "_", Dash "-". "
	exit 0
fi

export fileExtension="${fileExtension: -4}"

# Below check is added to compare whether file is xml file or not
if [ $fileExtension == ".xml" ]
then
echo "Executing Below StartSQLRepository Script."
echo  ./../../bin/startSQLRepository -m DCS.Versioned -m BBBStore.EStore.versioned.catalog -m BBBStore.StoreRest.repositories -m BBBStore.StoreRest.versioned -s bbb_impexp_bcc_dps -import $2 -repository $3 -project $projectname -user publishing -comment $projectname -activity merchandising.manageCommerceAssets

./../../bin/startSQLRepository -m DCS.Versioned -m BBBStore.EStore.versioned.catalog -m BBBStore.StoreRest.repositories -m BBBStore.StoreRest.versioned -s bbb_impexp_bcc_dps -import $2 -repository $3 -project $projectname -user publishing -comment $projectname -activity merchandising.manageCommerceAssets
exit 0
else
	echo "Wrong input File. Please provide the correct xml file with absolute path."
	exit 0
fi