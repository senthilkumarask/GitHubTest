SET DEFINE OFF;
SET SERVEROUTPUT ON;

  CREATE OR REPLACE PACKAGE BBB_CORE_PRV.ATG_GS_ENDECAEXPORT_PKG IS

	-- Log messages
		PROCEDURE GS_Log_message(p_process_cd VARCHAR2, p_process_sub_cd VARCHAR2, p_message VARCHAR2, p_row_status CHAR);
  
	-- files from core tables
		PROCEDURE Export_GS_ColorColorGroups ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);
		PROCEDURE Export_GS_Features ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);
		PROCEDURE Export_GS_SKU_Features ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);
		PROCEDURE Export_GS_Reviews( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);

	-- files for SKUs
		PROCEDURE Export_GS_SKUs( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);
		PROCEDURE Export_GS_Product_SKUs( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);

	-- files for Products
		PROCEDURE Export_GS_Accessories( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);
		PROCEDURE Export_GS_Lead_Products( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);
		PROCEDURE Export_GS_Simple_Products( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);

	-- files for Brands
		PROCEDURE Export_GS_Brands( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);
	
	-- files for Attributes
		PROCEDURE Export_GS_Item_Attributes ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);
		PROCEDURE Export_GS_Product_Item_Attr ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);
		PROCEDURE Export_GS_SKU_Item_Attributes ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);

	-- files for Taxonomy
		PROCEDURE Export_GS_BBB_Channel_Category(p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);
		PROCEDURE Export_GS_BAB_Channel_Category(p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);
		PROCEDURE Export_GS_CA_Channel_Category(p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);
		PROCEDURE Export_GS_BBB_Taxonomy(p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);
		PROCEDURE Export_GS_BAB_Taxonomy(p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);
		PROCEDURE Export_GS_CA_Taxonomy(p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);
		PROCEDURE Export_GS_Category_Products ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);

	-- file for prices
		PROCEDURE Export_GS_Prices ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);

	-- files for product and sku attributes
		PROCEDURE Export_GS_Product_Site_Prop(p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);
		PROCEDURE Export_GS_SKU_Site_Properties(p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);
		PROCEDURE Export_GS_GoFile(p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);

		FUNCTION dump_csv ( p_query varchar2, -- query to fired
							p_separator varchar2 default '|', -- delimeter defaulted to pipe
							p_dir varchar2, -- database machine folder, should be DirectoryName, as in all_directories table
							p_filename varchar2, -- name of the output file
							p_static varchar2, -- static or one time content to be written just below header and before query-content
							p_header varchar2 ) -- header to be put in the output file
							return number; -- returns the number of rows written, excluding header
END ATG_GS_EndecaExport_Pkg;
/
show error;
COMMIT;