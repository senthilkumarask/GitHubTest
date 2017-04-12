
setlocal
title bbb_stg

call "/u02/opt/weblogic/domains/bbb_dev//bin/startManagedWebLogic.cmd" bbb_stg t3://10.210.4.245:7001/ %*

endlocal
