
setlocal
title bbb_prod

call "/u02/opt/weblogic/domains/bbb_dev//bin/startManagedWebLogic.cmd" bbb_prod t3://10.210.4.245:7001/ %*

endlocal
