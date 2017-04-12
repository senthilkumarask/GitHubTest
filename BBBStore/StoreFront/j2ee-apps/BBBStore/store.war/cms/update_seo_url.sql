update BBB_PRODUCT set seo_url=seo_url ||'/' where 
lower(seo_url) not like '%jsp%'
and seo_url is not null;
