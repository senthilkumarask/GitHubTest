sqlplus BBB_CORE/BBB_CORE << EOF
set echo off 
set heading off
@$1
exit;
EOF