<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<!DOCTYPE process SYSTEM "dynamosystemresource:/atg/dtds/pdl/pdl_1.0.dtd">
<process author="admin" creation-time="1328876824182" enabled="true" last-modified-by="admin" modification-time="1354115458745">
  <segment migrate-from="1329136205227,1330590499037" migrate-subjects="true">
    <segment-name>main</segment-name>
    <!--================================-->
    <!--== startWorkflow  -->
    <!--================================-->
    <event id="1">
      <event-name>atg.workflow.StartWorkflow</event-name>
      <filter operator="eq">
        <event-property>
          <property-name>processName</property-name>
        </event-property>
        <constant>/staging/pimWorkflow.wdl</constant>
      </filter>
      <filter operator="eq">
        <event-property>
          <property-name>segmentName</property-name>
        </event-property>
        <constant>main</constant>
      </filter>
      <attributes>
        <attribute name="atg.workflow.elementType">
          <constant>startWorkflow</constant>
        </attribute>
        <attribute name="atg.workflow.description">
          <constant>A basic workflow which focuses on creating and editing arbitrary assets. Deploys to staging and production targets.</constant>
        </attribute>
        <attribute name="atg.workflow.displayName">
          <constant>PIM Emergency</constant>
        </attribute>
      </attributes>
    </event>
    <!--================================-->
    <!--== Create project without a workflow and process' project name  -->
    <!--================================-->
    <action id="2">
      <action-name>createProjectForProcess</action-name>
    </action>
    <!--================================-->
    <!--== author  -->
    <!--================================-->
    <label id="3">
      <attributes>
        <attribute name="atg.workflow.elementType">
          <constant>task</constant>
        </attribute>
        <attribute name="atg.workflow.assignable">
          <constant type="java.lang.Boolean">true</constant>
        </attribute>
        <attribute name="atg.workflow.acl">
          <constant>Profile$role$epubManager:write,execute;Profile$role$epubUser:write,execute;Admin$role$administrators-group:write,execute;Admin$role$managers-group:write,execute;Profile$role$epubSuperAdmin:write,execute;Profile$role$epubAdmin:write,execute</constant>
        </attribute>
        <attribute name="atg.workflow.description">
          <constant>Create and modify assets for eventual deployment</constant>
        </attribute>
        <attribute name="atg.workflow.name">
          <constant>author</constant>
        </attribute>
        <attribute name="atg.workflow.displayName">
          <constant>Author</constant>
        </attribute>
      </attributes>
    </label>
    <fork exclusive="true" id="4">
      <branch id="4.1">
        <!--================================-->
        <!--== Review  -->
        <!--================================-->
        <event id="4.1.1">
          <event-name>atg.workflow.TaskOutcome</event-name>
          <filter operator="eq">
            <event-property>
              <property-name>processName</property-name>
            </event-property>
            <constant>/staging/pimWorkflow.wdl</constant>
          </filter>
          <filter operator="eq">
            <event-property>
              <property-name>segmentName</property-name>
            </event-property>
            <constant>main</constant>
          </filter>
          <filter operator="eq">
            <event-property>
              <property-name>outcomeElementId</property-name>
            </event-property>
            <constant>4.1.1</constant>
          </filter>
          <attributes>
            <attribute name="atg.workflow.elementType">
              <constant>outcome</constant>
            </attribute>
            <attribute name="atg.workflow.name">
              <constant>Review</constant>
            </attribute>
          </attributes>
        </event>
        <!--================================-->
        <!--== Set variable process of type string to Project's Process id  -->
        <!--================================-->
        <action id="4.1.2">
          <action-name construct="variable-declaration-action">modify</action-name>
          <action-param name="modified">
            <variable type="java.lang.String">process</variable>
          </action-param>
          <action-param name="operator">
            <constant>assign</constant>
          </action-param>
          <action-param name="modifier">
            <subject-property>
              <property-name>id</property-name>
            </subject-property>
          </action-param>
        </action>
        <!--================================-->
        <!--== Set variable projectName of type string to Project's Current project's Name  -->
        <!--================================-->
        <action id="4.1.3">
          <action-name construct="variable-declaration-action">modify</action-name>
          <action-param name="modified">
            <variable type="java.lang.String">projectName</variable>
          </action-param>
          <action-param name="operator">
            <constant>assign</constant>
          </action-param>
          <action-param name="modifier">
            <subject-property>
              <property-name>project</property-name>
              <property-name>displayName</property-name>
            </subject-property>
          </action-param>
        </action>
        <!--================================-->
        <!--== Set variable workspace of type string to Project's Current project's Workspace  -->
        <!--================================-->
        <action id="4.1.4">
          <action-name construct="variable-declaration-action">modify</action-name>
          <action-param name="modified">
            <variable type="java.lang.String">workspace</variable>
          </action-param>
          <action-param name="operator">
            <constant>assign</constant>
          </action-param>
          <action-param name="modifier">
            <subject-property>
              <property-name>project</property-name>
              <property-name>workspace</property-name>
            </subject-property>
          </action-param>
        </action>
        <!--================================-->
        <!--== Change Project's Current project's Editable to false  -->
        <!--================================-->
        <action id="4.1.5">
          <action-name construct="modify-action">modify</action-name>
          <action-param name="modified">
            <subject-property>
              <property-name>project</property-name>
              <property-name>editable</property-name>
            </subject-property>
          </action-param>
          <action-param name="operator">
            <constant>assign</constant>
          </action-param>
          <action-param name="modifier">
            <constant type="java.lang.Boolean">false</constant>
          </action-param>
        </action>
        <!--================================-->
        <!--== Check assets are up to date  -->
        <!--================================-->
        <action id="4.1.6">
          <action-name>assetsUpToDate</action-name>
        </action>
        <!--================================-->
        <!--== Send task notification templates:/readyForReviewTemplate.jsp to owner or permitted actors of task 1) author  -->
        <!--================================-->
        <action id="4.1.7">
          <action-name>emailNotifyTaskActors</action-name>
          <action-param name="scenarioPathInfo">
            <constant>templates:/readyForReviewTemplate.jsp</constant>
          </action-param>
          <action-param name="recipientSet">
            <constant>ownerOrPermittedActors</constant>
          </action-param>
          <action-param name="taskElementId">
            <constant>3</constant>
          </action-param>
        </action>
        <!--================================-->
        <!--== Approve and deploy project to target Staging  -->
        <!--================================-->
        <action id="4.1.8">
          <action-name>approveAndDeployProject</action-name>
          <action-param name="target">
            <constant>Staging</constant>
          </action-param>
        </action>
        <!--================================-->
        <!--== Wait for deployment to complete on target Staging  -->
        <!--================================-->
        <event id="4.1.9">
          <event-name>atg.deployment.DeploymentStatus</event-name>
          <filter operator="eq">
            <event-property>
              <property-name>targetId</property-name>
            </event-property>
            <constant>Staging</constant>
          </filter>
        </event>
        <fork id="4.1.10">
          <branch id="4.1.10.1">
            <!--================================-->
            <!--== Deployment completed event status is success on target Staging  -->
            <!--================================-->
            <condition id="4.1.10.1.1">
              <filter operator="deploymentCompleted">
                <constant>1</constant>
                <constant>Staging</constant>
              </filter>
            </condition>
          </branch>
          <branch id="4.1.10.2">
            <!--================================-->
            <!--== Deployment completed event status is failure on target Staging  -->
            <!--================================-->
            <condition id="4.1.10.2.1">
              <filter operator="deploymentCompleted">
                <constant>0</constant>
                <constant>Staging</constant>
              </filter>
            </condition>
            <!--================================-->
            <!--== Release asset locks  -->
            <!--================================-->
            <action id="4.1.10.2.2">
              <action-name>releaseAssetLocks</action-name>
            </action>
            <!--================================-->
            <!--== Set variable failedState of type string to production  -->
            <!--================================-->
            <action id="4.1.10.2.3">
              <action-name construct="variable-declaration-action">modify</action-name>
              <action-param name="modified">
                <variable type="java.lang.String">failedState</variable>
              </action-param>
              <action-param name="operator">
                <constant>assign</constant>
              </action-param>
              <action-param name="modifier">
                <constant>production</constant>
              </action-param>
            </action>
            <!--================================-->
            <!--== Send task notification templates:/failedDeploymentTemplate.jsp to owner or permitted actors of task 1) author  -->
            <!--================================-->
            <action id="4.1.10.2.4">
              <action-name>emailNotifyTaskActors</action-name>
              <action-param name="scenarioPathInfo">
                <constant>templates:/failedDeploymentTemplate.jsp</constant>
              </action-param>
              <action-param name="recipientSet">
                <constant>ownerOrPermittedActors</constant>
              </action-param>
              <action-param name="taskElementId">
                <constant>3</constant>
              </action-param>
            </action>
            <jump id="4.1.10.2.5" target="3"/>
          </branch>
        </fork>
        <!--================================-->
        <!--== verifyStaging  -->
        <!--================================-->
        <label id="4.1.11">
          <attributes>
            <attribute name="atg.workflow.assignable">
              <constant type="java.lang.Boolean">true</constant>
            </attribute>
            <attribute name="atg.workflow.elementType">
              <constant>task</constant>
            </attribute>
            <attribute name="atg.workflow.acl">
              <constant>Profile$role$epubSuperAdmin:write,execute;Admin$role$managers-group:write,execute;Profile$role$epubManager:write,execute;Profile$role$epubUser:write,execute</constant>
            </attribute>
            <attribute name="atg.workflow.description">
              <constant>Verify the deployment to Staging was successful and that all deployed assets look and function appropriately</constant>
            </attribute>
            <attribute name="atg.workflow.name">
              <constant>verifyStaging</constant>
            </attribute>
            <attribute name="atg.workflow.displayName">
              <constant>Verify Staging Deployment</constant>
            </attribute>
          </attributes>
        </label>
        <fork exclusive="true" id="4.1.12">
          <branch id="4.1.12.1">
            <!--================================-->
            <!--== accept  -->
            <!--================================-->
            <event id="4.1.12.1.1">
              <event-name>atg.workflow.TaskOutcome</event-name>
              <filter operator="eq">
                <event-property>
                  <property-name>processName</property-name>
                </event-property>
                <constant>/staging/pimWorkflow.wdl</constant>
              </filter>
              <filter operator="eq">
                <event-property>
                  <property-name>segmentName</property-name>
                </event-property>
                <constant>main</constant>
              </filter>
              <filter operator="eq">
                <event-property>
                  <property-name>outcomeElementId</property-name>
                </event-property>
                <constant>4.1.12.1.1</constant>
              </filter>
              <attributes>
                <attribute name="atg.workflow.elementType">
                  <constant>outcome</constant>
                </attribute>
                <attribute name="atg.workflow.description">
                  <constant>Accept the deployment to Staging target</constant>
                </attribute>
                <attribute name="atg.workflow.name">
                  <constant>accept</constant>
                </attribute>
                <attribute name="atg.workflow.displayName">
                  <constant>Accept Staging Deployment</constant>
                </attribute>
              </attributes>
            </event>
            <!--================================-->
            <!--== Send task notification templates:/stagingDeploymentSuccessTemplate.jsp to owner or permitted actors of task 1) author  -->
            <!--================================-->
            <action id="4.1.12.1.2">
              <action-name>emailNotifyTaskActors</action-name>
              <action-param name="scenarioPathInfo">
                <constant>templates:/stagingDeploymentSuccessTemplate.jsp</constant>
              </action-param>
              <action-param name="recipientSet">
                <constant>ownerOrPermittedActors</constant>
              </action-param>
              <action-param name="taskElementId">
                <constant>3</constant>
              </action-param>
            </action>
            <!--================================-->
            <!--== Validate project is deployed on target Staging  -->
            <!--================================-->
            <action id="4.1.12.1.3">
              <action-name>validateProjectDeployed</action-name>
              <action-param name="target">
                <constant>Staging</constant>
              </action-param>
            </action>
          </branch>
          <branch id="4.1.12.2">
            <!--================================-->
            <!--== revertAssetsOnStagingNow  -->
            <!--================================-->
            <event id="4.1.12.2.1">
              <event-name>atg.workflow.TaskOutcome</event-name>
              <filter operator="eq">
                <event-property>
                  <property-name>processName</property-name>
                </event-property>
                <constant>/staging/pimWorkflow.wdl</constant>
              </filter>
              <filter operator="eq">
                <event-property>
                  <property-name>segmentName</property-name>
                </event-property>
                <constant>main</constant>
              </filter>
              <filter operator="eq">
                <event-property>
                  <property-name>outcomeElementId</property-name>
                </event-property>
                <constant>4.1.12.2.1</constant>
              </filter>
              <attributes>
                <attribute name="atg.workflow.elementType">
                  <constant>outcome</constant>
                </attribute>
                <attribute name="atg.workflow.description">
                  <constant>Revert assets from Staging target</constant>
                </attribute>
                <attribute name="atg.workflow.name">
                  <constant>revertAssetsOnStagingNow</constant>
                </attribute>
                <attribute name="atg.workflow.displayName">
                  <constant>Revert Assets on Staging Immediately</constant>
                </attribute>
              </attributes>
            </event>
            <!--================================-->
            <!--== Revert assets immediately on target Staging  -->
            <!--================================-->
            <action id="4.1.12.2.2">
              <action-name>revertAssetsOnTargetNow</action-name>
              <action-param name="target">
                <constant>Staging</constant>
              </action-param>
            </action>
            <!--================================-->
            <!--== waitForRevertDeploymentToComplete  -->
            <!--================================-->
            <label id="4.1.12.2.3">
              <attributes>
                <attribute name="atg.workflow.assignable">
                  <constant type="java.lang.Boolean">false</constant>
                </attribute>
                <attribute name="atg.workflow.elementType">
                  <constant>task</constant>
                </attribute>
                <attribute name="atg.workflow.acl">
                  <constant>Admin$role$managers-group:write,execute;Profile$role$epubSuperAdmin:write,execute;Profile$role$epubManager:write,execute;Profile$role$epubUser:write,execute</constant>
                </attribute>
                <attribute name="atg.workflow.description">
                  <constant>Wait for revert deployment to complete on Staging target</constant>
                </attribute>
                <attribute name="atg.workflow.name">
                  <constant>waitForRevertDeploymentToComplete</constant>
                </attribute>
                <attribute name="atg.workflow.displayName">
                  <constant>Wait for Staging Revert Deployment Completion</constant>
                </attribute>
              </attributes>
            </label>
            <fork exclusive="true" id="4.1.12.2.4">
              <branch id="4.1.12.2.4.1">
                <!--================================-->
                <!--== Wait for deployment to complete on target Staging  -->
                <!--================================-->
                <event id="4.1.12.2.4.1.1">
                  <event-name>atg.deployment.DeploymentStatus</event-name>
                  <filter operator="eq">
                    <event-property>
                      <property-name>targetId</property-name>
                    </event-property>
                    <constant>Staging</constant>
                  </filter>
                </event>
                <fork id="4.1.12.2.4.1.2">
                  <branch id="4.1.12.2.4.1.2.1">
                    <!--================================-->
                    <!--== Deployment completed event status is success on target Staging  -->
                    <!--================================-->
                    <condition id="4.1.12.2.4.1.2.1.1">
                      <filter operator="deploymentCompleted">
                        <constant>1</constant>
                        <constant>Staging</constant>
                      </filter>
                    </condition>
                  </branch>
                  <branch id="4.1.12.2.4.1.2.2">
                    <!--================================-->
                    <!--== Deployment completed event status is failure on target Staging  -->
                    <!--================================-->
                    <condition id="4.1.12.2.4.1.2.2.1">
                      <filter operator="deploymentCompleted">
                        <constant>0</constant>
                        <constant>Staging</constant>
                      </filter>
                    </condition>
                    <jump id="4.1.12.2.4.1.2.2.2" target="4.1.11"/>
                  </branch>
                </fork>
              </branch>
            </fork>
            <!--================================-->
            <!--== Reopen project  -->
            <!--================================-->
            <action id="4.1.12.2.5">
              <action-name>reopenProject</action-name>
            </action>
            <!--================================-->
            <!--== Release asset locks  -->
            <!--================================-->
            <action id="4.1.12.2.6">
              <action-name>releaseAssetLocks</action-name>
            </action>
            <jump id="4.1.12.2.7" target="3"/>
          </branch>
        </fork>
        <!--================================-->
        <!--== productionApproval  -->
        <!--================================-->
        <label id="4.1.13">
          <attributes>
            <attribute name="atg.workflow.assignable">
              <constant type="java.lang.Boolean">true</constant>
            </attribute>
            <attribute name="atg.workflow.elementType">
              <constant>task</constant>
            </attribute>
            <attribute name="atg.workflow.acl">
              <constant>Profile$role$epubManager:write,execute;Admin$role$managers-group:write,execute;Profile$role$epubSuperAdmin:write,execute</constant>
            </attribute>
            <attribute name="atg.workflow.description">
              <constant>Approve for deployment to Production target</constant>
            </attribute>
            <attribute name="atg.workflow.name">
              <constant>productionApproval</constant>
            </attribute>
            <attribute name="atg.workflow.displayName">
              <constant>Approve for Production Deployment</constant>
            </attribute>
          </attributes>
        </label>
        <fork exclusive="true" id="4.1.14">
          <branch id="4.1.14.1">
            <!--================================-->
            <!--== approveAndDeploy  -->
            <!--================================-->
            <event id="4.1.14.1.1">
              <event-name>atg.workflow.TaskOutcome</event-name>
              <filter operator="eq">
                <event-property>
                  <property-name>processName</property-name>
                </event-property>
                <constant>/staging/pimWorkflow.wdl</constant>
              </filter>
              <filter operator="eq">
                <event-property>
                  <property-name>segmentName</property-name>
                </event-property>
                <constant>main</constant>
              </filter>
              <filter operator="eq">
                <event-property>
                  <property-name>outcomeElementId</property-name>
                </event-property>
                <constant>4.1.14.1.1</constant>
              </filter>
              <attributes>
                <attribute name="atg.workflow.elementType">
                  <constant>outcome</constant>
                </attribute>
                <attribute name="atg.workflow.description">
                  <constant>Approve for immediate deployment to Production target</constant>
                </attribute>
                <attribute name="atg.workflow.name">
                  <constant>approveAndDeploy</constant>
                </attribute>
                <attribute name="atg.workflow.displayName">
                  <constant>Approve and Deploy to Production</constant>
                </attribute>
              </attributes>
            </event>
            <!--================================-->
            <!--== Approve and deploy project to target Production  -->
            <!--================================-->
            <action id="4.1.14.1.2">
              <action-name>approveAndDeployProject</action-name>
              <action-param name="target">
                <constant>Production</constant>
              </action-param>
            </action>
          </branch>
          <branch id="4.1.14.2">
            <!--================================-->
            <!--== approve  -->
            <!--================================-->
            <event id="4.1.14.2.1">
              <event-name>atg.workflow.TaskOutcome</event-name>
              <filter operator="eq">
                <event-property>
                  <property-name>processName</property-name>
                </event-property>
                <constant>/staging/pimWorkflow.wdl</constant>
              </filter>
              <filter operator="eq">
                <event-property>
                  <property-name>segmentName</property-name>
                </event-property>
                <constant>main</constant>
              </filter>
              <filter operator="eq">
                <event-property>
                  <property-name>outcomeElementId</property-name>
                </event-property>
                <constant>4.1.14.2.1</constant>
              </filter>
              <attributes>
                <attribute name="atg.workflow.elementType">
                  <constant>outcome</constant>
                </attribute>
                <attribute name="atg.workflow.description">
                  <constant>Approve project for deployment to Production target</constant>
                </attribute>
                <attribute name="atg.workflow.name">
                  <constant>approve</constant>
                </attribute>
                <attribute name="atg.workflow.displayName">
                  <constant>Approve for Production Deployment</constant>
                </attribute>
              </attributes>
            </event>
            <!--================================-->
            <!--== Approve project for target Production  -->
            <!--================================-->
            <action id="4.1.14.2.2">
              <action-name>approveProject</action-name>
              <action-param name="target">
                <constant>Production</constant>
              </action-param>
            </action>
          </branch>
          <branch id="4.1.14.3">
            <!--================================-->
            <!--== reject  -->
            <!--================================-->
            <event id="4.1.14.3.1">
              <event-name>atg.workflow.TaskOutcome</event-name>
              <filter operator="eq">
                <event-property>
                  <property-name>processName</property-name>
                </event-property>
                <constant>/staging/pimWorkflow.wdl</constant>
              </filter>
              <filter operator="eq">
                <event-property>
                  <property-name>segmentName</property-name>
                </event-property>
                <constant>main</constant>
              </filter>
              <filter operator="eq">
                <event-property>
                  <property-name>outcomeElementId</property-name>
                </event-property>
                <constant>4.1.14.3.1</constant>
              </filter>
              <attributes>
                <attribute name="atg.workflow.elementType">
                  <constant>outcome</constant>
                </attribute>
                <attribute name="atg.workflow.description">
                  <constant>Reject project for deployment to Production target</constant>
                </attribute>
                <attribute name="atg.workflow.name">
                  <constant>reject</constant>
                </attribute>
                <attribute name="atg.workflow.displayName">
                  <constant>Reject Production Deployment</constant>
                </attribute>
              </attributes>
            </event>
            <!--================================-->
            <!--== Set variable rejectState of type string to production approval  -->
            <!--================================-->
            <action id="4.1.14.3.2">
              <action-name construct="variable-declaration-action">modify</action-name>
              <action-param name="modified">
                <variable type="java.lang.String">rejectState</variable>
              </action-param>
              <action-param name="operator">
                <constant>assign</constant>
              </action-param>
              <action-param name="modifier">
                <constant>production approval</constant>
              </action-param>
            </action>
            <!--================================-->
            <!--== Send task notification templates:/rejectDeploymentTemplate.jsp to owner or permitted actors of task 1) author  -->
            <!--================================-->
            <action id="4.1.14.3.3">
              <action-name>emailNotifyTaskActors</action-name>
              <action-param name="scenarioPathInfo">
                <constant>templates:/rejectDeploymentTemplate.jsp</constant>
              </action-param>
              <action-param name="recipientSet">
                <constant>ownerOrPermittedActors</constant>
              </action-param>
              <action-param name="taskElementId">
                <constant>3</constant>
              </action-param>
            </action>
            <jump id="4.1.14.3.4" target="4.1.11"/>
          </branch>
        </fork>
        <!--================================-->
        <!--== waitForDeploymentToComplete  -->
        <!--================================-->
        <label id="4.1.15">
          <attributes>
            <attribute name="atg.workflow.elementType">
              <constant>task</constant>
            </attribute>
            <attribute name="atg.workflow.assignable">
              <constant type="java.lang.Boolean">false</constant>
            </attribute>
            <attribute name="atg.workflow.acl">
              <constant>Profile$role$epubAdmin:write,execute;Profile$role$epubSuperAdmin:write,execute;Admin$role$administrators-group:write,execute</constant>
            </attribute>
            <attribute name="atg.workflow.description">
              <constant>Wait for deployment to complete on Production target</constant>
            </attribute>
            <attribute name="atg.workflow.name">
              <constant>waitForDeploymentToComplete</constant>
            </attribute>
            <attribute name="atg.workflow.displayName">
              <constant>Wait for Production Deployment Completion</constant>
            </attribute>
          </attributes>
        </label>
        <fork exclusive="true" id="4.1.16">
          <branch id="4.1.16.1">
            <!--================================-->
            <!--== Wait for deployment to complete on target Production  -->
            <!--================================-->
            <event id="4.1.16.1.1">
              <event-name>atg.deployment.DeploymentStatus</event-name>
              <filter operator="eq">
                <event-property>
                  <property-name>targetId</property-name>
                </event-property>
                <constant>Production</constant>
              </filter>
            </event>
            <fork id="4.1.16.1.2">
              <branch id="4.1.16.1.2.1">
                <!--================================-->
                <!--== Deployment completed event status is success on target Production  -->
                <!--================================-->
                <condition id="4.1.16.1.2.1.1">
                  <filter operator="deploymentCompleted">
                    <constant>1</constant>
                    <constant>Production</constant>
                  </filter>
                </condition>
              </branch>
              <branch id="4.1.16.1.2.2">
                <!--================================-->
                <!--== Deployment completed event status is failure on target Production  -->
                <!--================================-->
                <condition id="4.1.16.1.2.2.1">
                  <filter operator="deploymentCompleted">
                    <constant>0</constant>
                    <constant>Production</constant>
                  </filter>
                </condition>
                <!--================================-->
                <!--== Set variable failedState of type string to production  -->
                <!--================================-->
                <action id="4.1.16.1.2.2.2">
                  <action-name construct="variable-declaration-action">modify</action-name>
                  <action-param name="modified">
                    <variable type="java.lang.String">failedState</variable>
                  </action-param>
                  <action-param name="operator">
                    <constant>assign</constant>
                  </action-param>
                  <action-param name="modifier">
                    <constant>production</constant>
                  </action-param>
                </action>
                <!--================================-->
                <!--== Send task notification templates:/failedDeploymentTemplate.jsp to owner or permitted actors of task 1) author  -->
                <!--================================-->
                <action id="4.1.16.1.2.2.3">
                  <action-name>emailNotifyTaskActors</action-name>
                  <action-param name="scenarioPathInfo">
                    <constant>templates:/failedDeploymentTemplate.jsp</constant>
                  </action-param>
                  <action-param name="recipientSet">
                    <constant>ownerOrPermittedActors</constant>
                  </action-param>
                  <action-param name="taskElementId">
                    <constant>3</constant>
                  </action-param>
                </action>
                <jump id="4.1.16.1.2.2.4" target="4.1.13"/>
              </branch>
            </fork>
          </branch>
        </fork>
        <!--================================-->
        <!--== verifyProduction  -->
        <!--================================-->
        <label id="4.1.17">
          <attributes>
            <attribute name="atg.workflow.assignable">
              <constant type="java.lang.Boolean">true</constant>
            </attribute>
            <attribute name="atg.workflow.elementType">
              <constant>task</constant>
            </attribute>
            <attribute name="atg.workflow.acl">
              <constant>Profile$role$epubUser:write,execute;Profile$role$epubManager:write,execute;Admin$role$managers-group:write,execute;Profile$role$epubSuperAdmin:write,execute</constant>
            </attribute>
            <attribute name="atg.workflow.description">
              <constant>Verify that the deployment to Production was successful and that all deployed assets look and function appropriately</constant>
            </attribute>
            <attribute name="atg.workflow.name">
              <constant>verifyProduction</constant>
            </attribute>
            <attribute name="atg.workflow.displayName">
              <constant>Verify Production Deployment</constant>
            </attribute>
          </attributes>
        </label>
        <fork exclusive="true" id="4.1.18">
          <branch id="4.1.18.1">
            <!--================================-->
            <!--== accept  -->
            <!--================================-->
            <event id="4.1.18.1.1">
              <event-name>atg.workflow.TaskOutcome</event-name>
              <filter operator="eq">
                <event-property>
                  <property-name>processName</property-name>
                </event-property>
                <constant>/staging/pimWorkflow.wdl</constant>
              </filter>
              <filter operator="eq">
                <event-property>
                  <property-name>segmentName</property-name>
                </event-property>
                <constant>main</constant>
              </filter>
              <filter operator="eq">
                <event-property>
                  <property-name>outcomeElementId</property-name>
                </event-property>
                <constant>4.1.18.1.1</constant>
              </filter>
              <attributes>
                <attribute name="atg.workflow.elementType">
                  <constant>outcome</constant>
                </attribute>
                <attribute name="atg.workflow.description">
                  <constant>Accept the deployment to Production target</constant>
                </attribute>
                <attribute name="atg.workflow.name">
                  <constant>accept</constant>
                </attribute>
                <attribute name="atg.workflow.displayName">
                  <constant>Accept Production Deployment</constant>
                </attribute>
              </attributes>
            </event>
            <!--================================-->
            <!--== Validate project is deployed on target Production  -->
            <!--================================-->
            <action id="4.1.18.1.2">
              <action-name>validateProjectDeployed</action-name>
              <action-param name="target">
                <constant>Production</constant>
              </action-param>
            </action>
            <!--================================-->
            <!--== Check in project's workspace  -->
            <!--================================-->
            <action id="4.1.18.1.3">
              <action-name>checkInProject</action-name>
            </action>
            <!--================================-->
            <!--== Complete project  -->
            <!--================================-->
            <action id="4.1.18.1.4">
              <action-name>completeProject</action-name>
            </action>
            <!--================================-->
            <!--== Complete process  -->
            <!--================================-->
            <action id="4.1.18.1.5">
              <action-name>completeProcess</action-name>
            </action>
          </branch>
          <branch id="4.1.18.2">
            <!--================================-->
            <!--== revertAssetsOnlyOnProductionNow  -->
            <!--================================-->
            <event id="4.1.18.2.1">
              <event-name>atg.workflow.TaskOutcome</event-name>
              <filter operator="eq">
                <event-property>
                  <property-name>processName</property-name>
                </event-property>
                <constant>/staging/pimWorkflow.wdl</constant>
              </filter>
              <filter operator="eq">
                <event-property>
                  <property-name>segmentName</property-name>
                </event-property>
                <constant>main</constant>
              </filter>
              <filter operator="eq">
                <event-property>
                  <property-name>outcomeElementId</property-name>
                </event-property>
                <constant>4.1.18.2.1</constant>
              </filter>
              <attributes>
                <attribute name="atg.workflow.elementType">
                  <constant>outcome</constant>
                </attribute>
                <attribute name="atg.workflow.description">
                  <constant>Revert assets from Production target</constant>
                </attribute>
                <attribute name="atg.workflow.name">
                  <constant>revertAssetsOnlyOnProductionNow</constant>
                </attribute>
                <attribute name="atg.workflow.displayName">
                  <constant>Revert Assets on Production Immediately</constant>
                </attribute>
              </attributes>
            </event>
            <!--================================-->
            <!--== Revert assets immediately on target Production  -->
            <!--================================-->
            <action id="4.1.18.2.2">
              <action-name>revertAssetsOnTargetNow</action-name>
              <action-param name="target">
                <constant>Production</constant>
              </action-param>
            </action>
            <!--================================-->
            <!--== waitForRevertDeploymentToComplete  -->
            <!--================================-->
            <label id="4.1.18.2.3">
              <attributes>
                <attribute name="atg.workflow.assignable">
                  <constant type="java.lang.Boolean">false</constant>
                </attribute>
                <attribute name="atg.workflow.elementType">
                  <constant>task</constant>
                </attribute>
                <attribute name="atg.workflow.acl">
                  <constant>Profile$role$epubManager:write,execute;Profile$role$epubUser:write,execute;Admin$role$managers-group:write,execute;Profile$role$epubSuperAdmin:write,execute</constant>
                </attribute>
                <attribute name="atg.workflow.description">
                  <constant>Wait for revert deployment to complete on Production target</constant>
                </attribute>
                <attribute name="atg.workflow.name">
                  <constant>waitForRevertDeploymentToComplete</constant>
                </attribute>
                <attribute name="atg.workflow.displayName">
                  <constant>Wait for Production Revert Deployment Completion</constant>
                </attribute>
              </attributes>
            </label>
            <fork exclusive="true" id="4.1.18.2.4">
              <branch id="4.1.18.2.4.1">
                <!--================================-->
                <!--== Wait for deployment to complete on target Production  -->
                <!--================================-->
                <event id="4.1.18.2.4.1.1">
                  <event-name>atg.deployment.DeploymentStatus</event-name>
                  <filter operator="eq">
                    <event-property>
                      <property-name>targetId</property-name>
                    </event-property>
                    <constant>Production</constant>
                  </filter>
                </event>
                <fork id="4.1.18.2.4.1.2">
                  <branch id="4.1.18.2.4.1.2.1">
                    <!--================================-->
                    <!--== Deployment completed event status is success on target Production  -->
                    <!--================================-->
                    <condition id="4.1.18.2.4.1.2.1.1">
                      <filter operator="deploymentCompleted">
                        <constant>1</constant>
                        <constant>Production</constant>
                      </filter>
                    </condition>
                  </branch>
                  <branch id="4.1.18.2.4.1.2.2">
                    <!--================================-->
                    <!--== Deployment completed event status is failure on target Production  -->
                    <!--================================-->
                    <condition id="4.1.18.2.4.1.2.2.1">
                      <filter operator="deploymentCompleted">
                        <constant>0</constant>
                        <constant>Production</constant>
                      </filter>
                    </condition>
                    <jump id="4.1.18.2.4.1.2.2.2" target="4.1.17"/>
                  </branch>
                </fork>
              </branch>
            </fork>
            <jump id="4.1.18.2.5" target="4.1.11"/>
          </branch>
        </fork>
      </branch>
      <branch id="4.2">
        <!--================================-->
        <!--== Delete  -->
        <!--================================-->
        <event id="4.2.1">
          <event-name>atg.workflow.TaskOutcome</event-name>
          <filter operator="eq">
            <event-property>
              <property-name>processName</property-name>
            </event-property>
            <constant>/staging/pimWorkflow.wdl</constant>
          </filter>
          <filter operator="eq">
            <event-property>
              <property-name>segmentName</property-name>
            </event-property>
            <constant>main</constant>
          </filter>
          <filter operator="eq">
            <event-property>
              <property-name>outcomeElementId</property-name>
            </event-property>
            <constant>4.2.1</constant>
          </filter>
          <attributes>
            <attribute name="atg.workflow.elementType">
              <constant>outcome</constant>
            </attribute>
            <attribute name="atg.workflow.name">
              <constant>Delete</constant>
            </attribute>
          </attributes>
        </event>
        <!--================================-->
        <!--== Delete project  -->
        <!--================================-->
        <action id="4.2.2">
          <action-name>deleteProject</action-name>
        </action>
      </branch>
    </fork>
  </segment>
</process>
