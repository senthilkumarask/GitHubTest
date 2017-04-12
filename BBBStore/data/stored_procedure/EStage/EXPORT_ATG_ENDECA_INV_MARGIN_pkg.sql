CREATE OR REPLACE PACKAGE ECOMADMIN.EXPORT_ATG_ENDECA_INV_MARGIN IS 

  /* TODO enter package declarations (types, exceptions, methods etc) here */ 
  PROCEDURE exportInvMar ( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

   FUNCTION dump_csv ( p_query varchar2, -- query to fired
                        p_separator varchar2 default '|', -- delimeter defaulted to pipe
                        p_dir varchar2, -- database machine folder, should be DirectoryName, as in all_directories table
                        p_filename varchar2, -- name of the output file
                        p_static varchar2, -- static or one time content to be written just below header and before query-content
                        p_header varchar2 ) -- header to be put in the output file
                        return number; -- returns the number of rows written, excluding header
END EXPORT_ATG_ENDECA_INV_MARGIN;
/
