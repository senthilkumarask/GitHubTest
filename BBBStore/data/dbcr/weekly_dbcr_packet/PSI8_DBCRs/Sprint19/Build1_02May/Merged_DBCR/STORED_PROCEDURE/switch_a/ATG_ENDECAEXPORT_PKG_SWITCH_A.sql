-- Build Version : PSI7 DRACO Release Sprint10 Build1_24Jul
SET ECHO ON;
SET DEFINE OFF;
SET SERVEROUTPUT ON;

--------------------------------------------------------
--  File created - Tuesday-January-15-2013   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Package ATG_ENDECAEXPORT_PKG
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE BBB_SWITCH_A.ATG_ENDECAEXPORT_PKG IS

  -- Log messages
   PROCEDURE Log_message(p_process_cd VARCHAR2, p_process_sub_cd VARCHAR2, p_message VARCHAR2, p_row_status CHAR);
  
  -- files from core tables

    PROCEDURE Export_ColorColorGroups ( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

    PROCEDURE Export_Features ( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);
    
    PROCEDURE Export_SKU_Features ( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

    PROCEDURE Export_Reviews( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);
	
-- For College

	PROCEDURE Export_Colleges_States( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);
	
	PROCEDURE Export_Colleges_Taxonomy( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);	
-- files for SKUs

-- no file for this just populating the endeca_export_products and endeca_export_skus tables
    PROCEDURE Export_Products_And_Skus ( p_lastModifiedDate varchar2, p_feedtype varchar2);

    PROCEDURE Export_SKUs( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

    PROCEDURE Export_Collection_SKUs( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

    PROCEDURE Export_Product_SKUs( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

-- files for Products

    PROCEDURE Export_UnlikeProducts( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

    PROCEDURE Export_Accessories( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

    PROCEDURE Export_Lead_Products( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

    PROCEDURE Export_Simple_Products( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

    PROCEDURE Export_Collection_Products( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

-- files for Brands

    PROCEDURE Export_Brands( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

-- files for media

    PROCEDURE Export_Prod_Media_Sites ( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

    PROCEDURE Export_Media ( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

-- files for Attributes

    PROCEDURE Export_Item_Attributes ( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

    PROCEDURE Export_Product_Item_Attributes ( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

    PROCEDURE Export_SKU_Item_Attributes ( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

-- files for Taxonomy

    PROCEDURE Export_BBB_Taxonomy ( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

    PROCEDURE Export_BAB_Taxonomy ( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

    PROCEDURE Export_CA_Taxonomy ( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

    PROCEDURE Export_Category_Products ( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

-- files for static pages

    PROCEDURE Export_StaticPages_Site ( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

    PROCEDURE Export_StaticPages ( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

-- files for guides

    PROCEDURE Export_Guides_Sites ( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

    PROCEDURE Export_Guides ( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

-- file for prices

    PROCEDURE Export_Prices ( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);
	
	PROCEDURE Export_Prices_Updates ( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);
	
-- Search Config

	PROCEDURE Export_Search_Config_Items ( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);	
	PROCEDURE Export_SearchGroup_RelRanks ( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);	

-- files for product and sku attributes

    PROCEDURE Export_Product_Site_Properties(p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

    PROCEDURE Export_SKU_Site_Properties(p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);
	PROCEDURE Export_EXIM_Customization_Code(p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);
	
	PROCEDURE Export_Prod_site_sales_data(p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);
	
	-- files for Harmon Tabs || R2.2.1
    PROCEDURE Export_Tabs_Sites( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);
    PROCEDURE Export_Prod_Harmon_Tabs( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);
	
    PROCEDURE Export_GoFile(p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);
    
    PROCEDURE Export_OPB(p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);
    FUNCTION dump_csv ( p_query varchar2, -- query to fired
                        p_separator varchar2 default '|', -- delimeter defaulted to pipe
                        p_dir varchar2, -- database machine folder, should be DirectoryName, as in all_directories table
                        p_filename varchar2, -- name of the output file
                        p_static varchar2, -- static or one time content to be written just below header and before query-content
                        p_header varchar2 ) -- header to be put in the output file
                        return number; -- returns the number of rows written, excluding header

    PROCEDURE export_category_rank(p_filename varchar2);
END ATG_EndecaExport_Pkg;

/
SHOW ERROR;
COMMIT;
