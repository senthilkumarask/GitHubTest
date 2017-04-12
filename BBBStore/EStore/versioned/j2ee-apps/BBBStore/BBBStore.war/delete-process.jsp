<%@page import="atg.epub.project.*"%>
<%@page import="atg.dtm.*"%>
<%@page import="atg.nucleus.*"%>
<%@page import="atg.versionmanager.*"%>
<%@page import="atg.versionmanager.impl.*"%>
<%@page import="javax.transaction.*"%>

<%
   boolean rollback = true;
   TransactionManager tm = (TransactionManager)Nucleus.getGlobalNucleus().resolveName("/atg/dynamo/transaction/TransactionManager");
   VersionManager vm = (VersionManager)Nucleus.getGlobalNucleus().resolveName("/atg/epub/version/VersionManagerService");
   TransactionDemarcation td = new TransactionDemarcation();
   try{
     td.begin(tm);
     atg.deployment.server.SecurityManager.becomeTheSuperDood();
     ProcessHome processHome = ((ProcessHome) ProjectConstants.getPersistentHomes().getProcessHome());
     String processId = request.getParameter("process_id");
     if(processId == null){
        out.println("TRYING HYPNOSIS TO GUESS PROCESS ID..... FAILED. Try explicitly specifying a process_id request parameter. ");
        return;
     }
     atg.epub.project.Process process = processHome.findById(processId);
     if(process == null){
       out.println("wrong process_id parameter; not such process");
       return;
     }
     Project project = process.getProject();
     if(project == null){
       process.delete();
       rollback = false;
       out.println("Process was found but there was no project associated with it. Process was deleted successfully.");
       return;
     }
     String workspaceName = project.getWorkspace();
     WorkspaceRepositoryImpl ws = (WorkspaceRepositoryImpl)vm.getWorkspaceByName(workspaceName);
     ws.setEditable(true);
     ws.setLocked(false);
     project.delete("publishing");
     out.println("Process with id " + processId + " was found and deleted successfully.");
     rollback = false;
   }
   catch(Throwable t){
     t.printStackTrace(System.out);
     out.println(t);
   }
   finally{

     atg.deployment.server.SecurityManager.becomeNOTTheSuperDood();
     try{
       if(td != null)
          td.end(rollback);
     }
     catch(TransactionDemarcationException e){
       e.printStackTrace();
     }
   }
 %>
