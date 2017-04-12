#!/bin/ksh
grep trunk ../core/src/com/bbb/VersionNumber/svnurl.properties
if [ $? -eq 0 ]
then
trunk=`cat ../core/src/com/bbb/VersionNumber/svnurl.properties|awk -F "/" '{print $6}'`
>../core/src/com/bbb/VersionNumber/svnurl.properties
echo "build.from=$trunk" >> ../core/src/com/bbb/VersionNumber/svnurl.properties
fi
grep Tg ../core/src/com/bbb/VersionNumber/svnurl.properties
if [ $? -eq 0 ]
then
tag=`cat ../core/src/com/bbb/VersionNumber/svnurl.properties|awk -F "/" '{print $7}'`
>../core/src/com/bbb/VersionNumber/svnurl.properties
echo "build.from=$tag" >>../core/src/com/bbb/VersionNumber/svnurl.properties
fi


