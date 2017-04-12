spool update_seo_bkp_spool.sql
set echo off
set define off
set heading off

select 'update BBB_PRODUCT set seo_url='''||replace(seo_url,'''','''''') || ''' where product_id=''' ||product_id || ''';' from bbb_product
spool off
