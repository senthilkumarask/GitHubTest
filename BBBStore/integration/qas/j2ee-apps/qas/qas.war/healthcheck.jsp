<%@ page session="false" %>
<%@ page language="java" import="java.sql.*,java.io.*,java.util.*"%>
<%@ page language="java" import="javax.naming.InitialContext,javax.sql.DataSource, javax.naming.NameNotFoundException"%>
<%@ page language="java" import="java.sql.Connection"%>
                
                <HTML>
                <HEAD>
                <TITLE>Test page for Validating DB ATGProductionDS,ATGSwitchingDS_A,ATGSwitchingDS_B,EncryptionDS connections Response</TITLE>
                </HEAD>
<%
								
								String[] dsource = {"ATGProductionDS","ATGSwitchingDS_A","ATGSwitchingDS_B","EncryptionDS"};

                                  DataSource ds = null; // Changes
                                  Connection con = null;
                                  boolean status=false;
                                  int successCount=0;
                                  InitialContext ctx = new InitialContext();
                                   for (int i = 0; i < dsource.length; i++) {

									System.out.println("Checking connection for data source " +  dsource[i]);

                                                    try {
                                                                    ds = (DataSource) ctx.lookup(dsource[i]);							
                                                                    con = ds.getConnection();
                                                                    status=con.isValid(2);
                                                                    if (status == true){
                                                                    	successCount++;
                                                                    }
		
                                                    } catch (NameNotFoundException e) {                                                                                     
                                                    
                                                    } catch (Exception e) {
                                                                     System.out.println("healthcheck.jsp  - DB connection testing error:"+e);
																		out.println("healthcheck.jsp  - DB connection testing error:" );
                                                                     status = false;                 
                                                                   
                                                    }
                                                    finally{
                                                                     try {
                                                                       con.close();
                                                                     } catch (Exception ex) {}
                                                    }
                                      }
                                   System.out.println("Checking successCount-- "  +  successCount + " and dataSource length " + dsource.length);  
								 if(successCount == dsource.length){
                                 	%>OKAY<% }
								 else{%>
                                 	FAIL
                                 <%}%>

</BODY>
</HTML>