
setlocal
title bbb_pub

call "/u02/opt/weblogic/domains/bbb_dev//bin/startManagedWebLogic.cmd" bbb_pub t3://10.210.4.245:7001/ %*

endlocal
