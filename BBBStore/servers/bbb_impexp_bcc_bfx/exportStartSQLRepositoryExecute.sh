if [ "$#" -ne "2" ]
then 
	echo "Wrong parameters or no parameters passed."
	echo "Please pass the following two parameters in mentioned order :    1. Export File(Provide xml file with absolute path)    2. Repository Name(with config path)"
	exit 0
fi

export fileExtension=$1
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
echo ./../../bin/startSQLRepository -m DCS.Versioned -m BBBStore.EStore.versioned.catalog -s bbb_impexp_bcc_bfx -export all $1 -repository $2

./../../bin/startSQLRepository -m DCS.Versioned -m BBBStore.EStore.versioned.catalog -s bbb_impexp_bcc_bfx -export all $1 -repository $2
exit 0
else
	echo "Wrong input File. Please provide the correct xml file with absolute path."
	exit 0
fi