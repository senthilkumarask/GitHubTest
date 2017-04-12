CREATE OR REPLACE PACKAGE BODY ECOMADMIN.EXPORT_ATG_ENDECA_INV_MARGIN AS
  
  PROCEDURE exportInvMar ( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2)
  IS
    l_rows number; -- pkg var to capture the number of rows affected from the function
    endeca_query varchar(4000); -- pkg var to capture the SQL query
  BEGIN
      endeca_query:='select prodID||''|''||Site_ID||''|''||round(MARGIN,2)||''|''||case when (INVENTORY < 0) 
        then 0 else INVENTORY end 
        from (select b2.prodID, ''BedBathUS''as Site_ID, avg(BBB_MARGIN) as MARGIN, sum(a2.BBB_INV) as INVENTORY 
        from (select b.sku as skuID, case when (JDA_RETAIL_PRICE = 0) then 0 else (JDA_RETAIL_PRICE - COST)*100/JDA_RETAIL_PRICE 
        end as BBB_MARGIN, (c.AFS_QTY + c.BBB_ALT_AFS_QTY) as BBB_INV from ecomadmin.items b, ecomadmin.dom_inventory c 
        where c.sku in (select related_sku_id from ecomadmin.pim_pre_stg_related_items2 where relation_type_cd = ''PR'' ) 
        and c.SKU = b.sku and WEB_OFFERED_FLAG = ''Y'' AND DISABLE_FLAG = ''N''
        and collection_sku_flag = ''N'') a2, 
        (select b1.colProdID as prodID, a1.related_sku_id as skuID
        from pim_pre_stg_related_items2 a1, 
        (select sku as colProdID, related_sku_id as chldPrdID from pim_pre_stg_related_items2 where relation_type_cd = ''CO'') b1
        where b1.chldPrdID = a1.sku
        union
        select sku as prodID, related_sku_id as skuID from pim_pre_stg_related_items2 where relation_type_cd = ''PR'') b2
        where b2.skuID = a2.skuID
        group by b2.prodID
      
        UNION
      
        select b2.prodID, ''BuyBuyBaby''as Site_ID, avg(BAB_MARGIN) as MARGIN, sum(a2.BAB_INV) as INVENTORY 
        from (
        select b.sku as skuID, 
        case when (JDA_RETAIL_PRICE = 0) then 0 else (JDA_RETAIL_PRICE - COST)*100/JDA_RETAIL_PRICE end as BAB_MARGIN, 
        (c.AFS_QTY + c.BAB_ALT_AFS_QTY) as BAB_INV
        from ecomadmin.items b, ecomadmin.dom_inventory c
        where c.sku in ( select related_sku_id from ecomadmin.pim_pre_stg_related_items2 where relation_type_cd = ''PR'' )
        and c.SKU = b.sku
        and BAB_WEB_OFFERED_FLAG = ''Y'' AND BAB_DISABLE_FLAG = ''N''
        and collection_sku_flag = ''N'') a2, 
        (select b1.colProdID as prodID, a1.related_sku_id as skuID
        from pim_pre_stg_related_items2 a1, 
        (select sku as colProdID, related_sku_id as chldPrdID from pim_pre_stg_related_items2 where relation_type_cd = ''CO'') b1
        where b1.chldPrdID = a1.sku
        union
        select sku as prodID, related_sku_id as skuID from pim_pre_stg_related_items2 where relation_type_cd = ''PR'') b2
        where b2.skuID = a2.skuID
        group by b2.prodID
    
        UNION
    
        select b2.prodID, ''BedBathCanada''as Site_ID, avg(CA_MARGIN) as MARGIN, sum(a2.CA_INV) as INVENTORY 
        from (
        select b.sku as skuID, 
        case when (CA_JDA_RETAIL_PRICE = 0) then 0 else (CA_JDA_RETAIL_PRICE - CA_COST)*100/CA_JDA_RETAIL_PRICE end as CA_MARGIN, 
        case when b.CA_ECOM_FULFILLMENT_FLAG = ''E'' then (c.AFS_QTY + c.CA_ALT_AFS_QTY) else c.CA_ALT_AFS_QTY end as CA_INV
        from ecomadmin.items b, ecomadmin.dom_inventory c
        where c.sku in ( select related_sku_id from ecomadmin.pim_pre_stg_related_items2 where relation_type_cd = ''PR'' )
        and c.SKU = b.sku
        and CA_WEB_OFFERED_FLAG = ''Y'' AND CA_DISABLE_FLAG = ''N''
        and collection_sku_flag = ''N'') a2, 
        (select b1.colProdID as prodID, a1.related_sku_id as skuID
        from pim_pre_stg_related_items2 a1, 
        (select sku as colProdID, related_sku_id as chldPrdID from pim_pre_stg_related_items2 where relation_type_cd = ''CO'') b1
        where b1.chldPrdID = a1.sku
        union
        select sku as prodID, related_sku_id as skuID from pim_pre_stg_related_items2 where relation_type_cd = ''PR'') b2
        where b2.skuID = a2.skuID
        group by b2.prodID)';
    
  select dump_csv( endeca_query, '|', p_feedType, p_filename, null, 'PROD_ID|SITE_ID|MARGIN|INVENTORY' ) into l_rows from dual;
  dbms_output.put_line('exportInvMar: '||l_rows);
  
  SELECT dump_csv( endeca_query, '|', p_feedType, 'full.go', NULL, 'SYSDATE' ) INTO l_rows FROM dual;
  dbms_output.put_line('full.go' || l_rows);
  
  end exportInvMar;
    
  FUNCTION  dump_csv( p_query        in varchar2,
                    p_separator in varchar2 default '|',
                    p_dir       in varchar2 ,
                    p_filename  in varchar2,
                    p_static    in varchar2,
                    p_header in varchar2)
    RETURN number
    is
      l_output        utl_file.file_type;
      l_theCursor     integer default dbms_sql.open_cursor;
      l_columnValue   varchar2(2000);
      l_status        integer;
      l_colCnt        number default 0;
      l_separator     varchar2(10) default '';
      l_cnt           number default 0;
      l_dir           varchar2(20);
    BEGIN
 
    IF p_dir = 'PARTIAL' 
      then l_dir:= 'ATG_APP_PARTIAL';
    ELSE 
      l_dir:= 'ATG_APP_FULL';
    END IF;
    
    dbms_sql.parse(  l_theCursor,  p_query, dbms_sql.native );

    l_output := utl_file.fopen( l_dir, p_filename, 'w', 32767 );

    utl_file.put( l_output, p_header );
    utl_file.new_line( l_output );

    if ( p_static is not null ) then
      utl_file.put( l_output, p_static );
      utl_file.new_line( l_output );
    end if;
    
    for i in 1 .. 255 loop
      begin
        dbms_sql.define_column( l_theCursor, i, l_columnValue, 2000 );
        l_colCnt := i;
        exception
        when others then
          if ( sqlcode = -1007 ) then exit;
          else raise;
          end if;
      end;
    end loop;
  
    dbms_sql.define_column( l_theCursor, 1, l_columnValue, 2000 );

    l_status := dbms_sql.execute(l_theCursor);
    
    if  (dbms_sql.fetch_rows(l_theCursor) > 0 ) then
      loop
        l_separator := '';
        for i in 1 .. l_colCnt loop
          dbms_sql.column_value( l_theCursor, i, l_columnValue );
          utl_file.put( l_output, l_separator || l_columnValue );
          l_separator := p_separator;
        end loop;
        utl_file.new_line( l_output );
        l_cnt := l_cnt+1;
        exit when ( dbms_sql.fetch_rows(l_theCursor) <= 0 );
      end loop;
      dbms_sql.close_cursor(l_theCursor);
    end if;
    utl_file.fclose( l_output );
    return l_cnt;
  end dump_csv;
  
END EXPORT_ATG_ENDECA_INV_MARGIN;
/
