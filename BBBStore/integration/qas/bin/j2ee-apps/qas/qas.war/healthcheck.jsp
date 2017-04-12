<%@ page session="false" %>
<%@ page language="java" import="java.sql.*,java.io.*,java.util.*"%>
<%@ page language="java" import="javax.naming.InitialContext,javax.sql.DataSource, javax.naming.NameNotFoundException"%>
<%@ page language="java" import="java.sql.Connection"%>
<%@ page language="java" import="java.sql.ResultSet"%>
                
                <HTML>
                <HEAD>
                <TITLE>Test page for Validating DB connections/Server Response</TITLE>
                </HEAD>
<%
                String[] dsource = {"ATGProductionDS","ATGSwitchingDS_A","ATGSwitchingDS_B","ATGStagingDS"};

                                                  DataSource ds = null; // Changes
                                                  Connection con = null;
                                                  PreparedStatement query = null;
                                                  String sql = null;
                                                  ResultSet rs = null;
                                                  boolean status=false;
                                                  InitialContext ctx = new InitialContext();
                                                  int success=0;
                                                  for (int i = 0; i < dsource.length; i++) {
												  
												   System.out.println("Checking connection of data source" +  dsource[i]);
%>
<p>Checking connection of data source <%=dsource[i]%> </p>
<%
                                                                   try {
                                                                                    ds = (DataSource) ctx.lookup(dsource[i]);							
                                                                                    con = ds.getConnection();
                                                                                    query = con.prepareStatement("select sysdate  from dual");
                                                                                    rs = query.executeQuery();
																					
                                                                                    while (rs.next()) {
																					
                                                                                                     status=true;
                                                                                                     success++;
																									
                                                                                    }
																	
                                                                   } catch (NameNotFoundException e) {                                                                                     
                                                                   
                                                                   } catch (Exception e) {
                                                                                    System.out.println("healthcheck.jsp  - DB connection testing error:"+e);
																					out.println("healthcheck.jsp  - DB connection testing error:" );
                                                                                    status = false;                 
                                                                                  
                                                                   }
                                                                   finally{
                                                                                    try {
                                                                                                     rs.close();
                                                                                    }catch (Exception ex) {}
                                                                                    try {
                                                                                                     con.close();
                                                                                    } catch (Exception ex) {}
                                                                   }
                                                  if(status== true){
                                 %>OKAY<% }else{%>
                                 FAIL
                                 <%}%>
                                                  <%}%>
                                                  

</BODY>
</HTML>

