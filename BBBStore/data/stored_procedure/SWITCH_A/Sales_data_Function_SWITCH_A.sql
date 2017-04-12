-- build version: 2.04.01.001
SET ECHO ON;
SET SERVEROUTPUT ON;
SET DEFINE OFF;

CREATE OR REPLACE PROCEDURE BBB_SWITCH_A.calculate_product_sales
IS
product_count NUMBER := 0;
unit_sale_var varchar2(54);
order_sales_var varchar2(54);
total_sales_var varchar2(54);

max_unit_sale NUMBER(10) DEFAULT 0;
max_order_sales varchar2(54) DEFAULT '0';
max_total_sales varchar2(54) DEFAULT '0';
currentSiteId varchar2(20);
siteId VARCHAR2(40);

WEB_OFFERED_FLAG_VAR NUMBER(1) DEFAULT 0;
DISABLE_FLAG_VAR NUMBER(1) DEFAULT 1;

BEGIN
	BEGIN
    BEGIN	
      execute immediate 'delete from bbb_core.BBB_SALES_DATA where collection_flag = 1';
      --execute immediate 'drop table bbb_core.BBB_SALES_DATA';
	EXCEPTION 
		WHEN OTHERS THEN
			dbms_output.put_line('Error deleting sales data records'); 
	END;	                     
	--execute immediate 'create table endeca_prod_sales_data (product_id varchar2(40), site_id varchar2(40), UNIT_SALES varchar2(54), ORDER_SALES varchar2(54), TOTAL_SALES varchar2(54), LAST_MOD_DATE TIMESTAMP)';
    FOR collectionProductId IN (SELECT product_id FROM BBB_PRODUCT where COLLECTION_FLAG = 1)
		LOOP
			FOR siteId IN (select id  from DCS_SITE) 
			LOOP
				currentSiteId := siteId.id;
				WEB_OFFERED_FLAG_VAR:=0;
				DISABLE_FLAG_VAR:=1;	
				select WEB_OFFERED_FLAG,DISABLE_FLAG into WEB_OFFERED_FLAG_VAR,DISABLE_FLAG_VAR  FROM BBB_PRODUCT where product_id=collectionProductId.product_id;
				IF NOT currentSiteId = 'BedBathUS' THEN
					BEGIN
						select t.attribute_value_boolean into WEB_OFFERED_FLAG_VAR from BBB_PROD_SITE_TRANSLATIONS s, BBB_PROD_TRANSLATIONS t
							where s.product_id = collectionProductId.product_id
							and t.site_id = currentSiteId
							and t.translation_id = s.translation_id
							and t.attribute_name in 'webOffered';
					EXCEPTION 
						WHEN NO_DATA_FOUND THEN 
						WEB_OFFERED_FLAG_VAR:=0;
					END;
					BEGIN
						select t.attribute_value_boolean into DISABLE_FLAG_VAR from BBB_PROD_SITE_TRANSLATIONS s, BBB_PROD_TRANSLATIONS t
							where s.product_id = collectionProductId.product_id
							and t.site_id = currentSiteId
							and t.translation_id = s.translation_id
							and t.attribute_name in 'prodDisable';
					EXCEPTION 
						WHEN NO_DATA_FOUND THEN 
							DISABLE_FLAG_VAR:=1;
					END;
				END IF;
				IF  WEB_OFFERED_FLAG_VAR=1 and DISABLE_FLAG_VAR=0 THEN 		
					max_unit_sale := 0;
					max_order_sales := '0';
					max_total_sales := '0';
					unit_sale_var := '0';
          FOR childProductId IN (select rel.product_id from BBB_PRD_RELN rel, BBB_PRD_PRD_RELN prd_rel where prd_rel.product_id = collectionProductId.product_id and rel.product_relan_id = prd_rel.product_relan_id) 
					LOOP
						BEGIN
              --dbms_output.put_line(currentSiteId||childProductId.product_id);	
              WEB_OFFERED_FLAG_VAR:=0;
              DISABLE_FLAG_VAR:=1;	
              select WEB_OFFERED_FLAG,DISABLE_FLAG into WEB_OFFERED_FLAG_VAR,DISABLE_FLAG_VAR  FROM BBB_PRODUCT where product_id=childProductId.product_id;
              IF NOT currentSiteId = 'BedBathUS' THEN
                BEGIN
                  select t.attribute_value_boolean into WEB_OFFERED_FLAG_VAR from BBB_PROD_SITE_TRANSLATIONS s, BBB_PROD_TRANSLATIONS t
                      where s.product_id = childProductId.product_id
                      and t.site_id = currentSiteId
                      and t.translation_id = s.translation_id
                      and t.attribute_name in 'webOffered';
                EXCEPTION 
                  WHEN NO_DATA_FOUND THEN 
                  WEB_OFFERED_FLAG_VAR:=0;
                END;
                BEGIN
                  select t.attribute_value_boolean into DISABLE_FLAG_VAR from BBB_PROD_SITE_TRANSLATIONS s, BBB_PROD_TRANSLATIONS t
                    where s.product_id = childProductId.product_id
                    and t.site_id = currentSiteId
                    and t.translation_id = s.translation_id
                    and t.attribute_name in 'prodDisable';
                EXCEPTION 
                  WHEN NO_DATA_FOUND THEN 
                    DISABLE_FLAG_VAR:=1;	
                END;
              END IF;
              
              IF  WEB_OFFERED_FLAG_VAR=1 and DISABLE_FLAG_VAR=0 THEN 		
                select UNIT_SALES, ORDER_SALES, TOTAL_SALES into unit_sale_var, order_sales_var, total_sales_var from bbb_core.BBB_SALES_DATA 
                              where PRODUCT_ID = childProductId.product_id and SITE_ID = currentSiteId and collection_flag = 0;
                IF (to_number(order_sales_var) > max_order_sales) THEN
                  max_unit_sale := to_number(unit_sale_var);
                  max_order_sales := order_sales_var;
                  max_total_sales := total_sales_var;
                END IF;
              END IF;  
            EXCEPTION 
              WHEN NO_DATA_FOUND THEN   
                WEB_OFFERED_FLAG_VAR:=0;
                DISABLE_FLAG_VAR:=1;	
            END;
					END LOOP;
					execute immediate 'insert into bbb_core.BBB_SALES_DATA (PRODUCT_ID, SITE_ID, UNIT_SALES, ORDER_SALES, TOTAL_SALES, COLLECTION_FLAG, LAST_MOD_DATE) values ('''||collectionProductId.product_id||''', '''||currentSiteId||''', '''||max_unit_sale||''', '''||max_order_sales||''', '''||max_total_sales||''', 1,SYSTIMESTAMP)';		
				END IF;	
			END LOOP;
			execute immediate 'commit';
		END LOOP;	
	EXCEPTION
		WHEN OTHERS THEN  -- handles all other errors
    DBMS_OUTPUT.PUT_LINE (SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
		dbms_output.put_line('Error!');
	END;
	
	
END calculate_product_sales;

/
SHOW ERROR;
COMMIT;