package com.bbb.email;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;

import atg.core.util.StringUtils;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.ServletUtil;
import atg.userprofiling.email.TemplateEmailException;
import atg.userprofiling.email.TemplateEmailInfo;
import atg.userprofiling.email.TemplateEmailInfoImpl;
import atg.userprofiling.email.TemplateEmailSender;

import com.bbb.cms.EmailTemplateVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;

public class BBBTemplateEmailSender extends TemplateEmailSender{

    private static final String EMAILTEMPLATEVO = "emailTemplateVO";

    private EmailTemplateManager mEmailTemplateManager;
    private MutableRepository emailPersistRepository;

    public MutableRepository getEmailPersistRepository() {
        return this.emailPersistRepository;
    }

    public void setEmailPersistRepository(final MutableRepository emailPersistRepository) {
        this.emailPersistRepository = emailPersistRepository;
    }
    public EmailTemplateManager getEmailTemplateManager() {
        return this.mEmailTemplateManager;
    }

    public void setEmailTemplateManager(final EmailTemplateManager pEmailTemplateManager) {
        this.mEmailTemplateManager = pEmailTemplateManager;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void sendEmailMessage(final TemplateEmailInfo pEmailInfo, final Collection pRecipients,
            final boolean pRunInSeparateThread, final boolean pPersist) throws TemplateEmailException {

        Map placeHolderValues=null;
        final Map templateParameters = pEmailInfo.getTemplateParameters();

        if(templateParameters==null){
            if (this.isLoggingError()){
                this.logError("BBBTemplateEmailSender: templateParameters is null");
            }
            throw new TemplateEmailException("BBBTemplateEmailSender: templateParameters is null");
        }
        placeHolderValues = (Map) templateParameters.get("placeHolderValues");
        if(placeHolderValues==null){
            if (this.isLoggingError()){
                this.logError("BBBTemplateEmailSender: templateParameters is null");
            }
            throw new TemplateEmailException("BBBTemplateEmailSender: templateParameters is null");
        }

        final EmailTemplateVO emailTemplateVO = this.getEmailTemplateManager().getEmailTemplateData(placeHolderValues);

        if(this.isLoggingDebug()){
            this.logDebug("emailTemplateVO from EmailTemplateManager is : "+emailTemplateVO);
        }


        TemplateEmailInfoImpl templateInfo = null;
        if(pEmailInfo instanceof TemplateEmailInfoImpl){
            templateInfo = (TemplateEmailInfoImpl)pEmailInfo;
            if((emailTemplateVO.getEmailFrom() != null) && !StringUtils.isEmpty(emailTemplateVO.getEmailFrom())){
                templateInfo.setMessageFrom(emailTemplateVO.getEmailFrom());
            }
            if(placeHolderValues.containsKey("emailSubject"))
            {
                templateInfo.setMessageSubject( (String) placeHolderValues.get("emailSubject"));
            }
            else if((emailTemplateVO.getEmailSubject() != null)  && !StringUtils.isEmpty(emailTemplateVO.getEmailSubject())){
                templateInfo.setMessageSubject(emailTemplateVO.getEmailSubject());
            }
            templateInfo.getTemplateParameters().put(EMAILTEMPLATEVO, emailTemplateVO);
        }
        final String emailType = placeHolderValues.get(EmailTemplateManager.EMAIL_TYPE).toString();
        BBBPerformanceMonitor.start("EmailSender", emailType);

        if (null != templateInfo) {
        	super.sendEmailMessage(templateInfo.copy(), pRecipients, pRunInSeparateThread, pPersist);
        } else {
        	this.logError("templateInfo is null");
        }

        BBBPerformanceMonitor.end("EmailSender", emailType);
    }

    /* (non-Javadoc)
     * @see atg.userprofiling.email.TemplateEmailSender#sendEmailSentEvent(atg.userprofiling.email.TemplateEmailInfo, java.lang.Object, javax.mail.Message)
     */
    @Override
	protected synchronized void sendEmailSentEvent(final TemplateEmailInfo pEmailInfo, final Object pRecipient, final Message pMessage)
    {

        final EmailTemplateVO emailTemplateDataVO  = (EmailTemplateVO) pEmailInfo.getTemplateParameters().get("emailTemplateVO");
        if((emailTemplateDataVO != null) && (emailTemplateDataVO.getEmailFlag() != null) && emailTemplateDataVO.getEmailFlag().equalsIgnoreCase("ON")){

            final Date emailSendDate = new Date();

            try {
	        	if(this.isLoggingDebug()){
	        	    this.logDebug("Entering method to persist Email");
	        	}
	//              Persist the Email here via Email persist repository
	        	if(this.getEmailPersistRepository() == null){
	                    this.setEmailPersistRepository((MutableRepository)(ServletUtil.getCurrentRequest().resolveName("/com/bbb/cms/repository/EmailPersistRepository")));
	            }
	            final MutableRepositoryItem emailPersistItem = this.getEmailPersistRepository().createItem(BBBCoreConstants.EMAIL_DATA);
	            if(emailTemplateDataVO.getEmailPersistId() != null){

				      final RqlStatement statementDate =  RqlStatement.parseRqlStatement("(emailId =?0 )");
				      final RepositoryView viewEmailData = this.getEmailPersistRepository().getView("emailData");
				      final Object[] paramId = new Object[1];
				      paramId[0] = emailTemplateDataVO.getEmailPersistId();
		
				      final RepositoryItem[] emailItem = statementDate.executeQuery(viewEmailData, paramId);
				      if((emailItem != null) && (emailItem.length > 0)){
							if(this.isLoggingDebug()){
							    this.logDebug("Email already saved for multiple recepients with same Email Persist Id : " + emailTemplateDataVO.getEmailPersistId());
							}
							return ;
				      } else if ((emailTemplateDataVO.getEmailPersistId() != null)
							&& (emailTemplateDataVO.getEmailType() != null)
							&& (emailTemplateDataVO.getSiteId() != null)) {
						
							if(BBBCoreConstants.ET_FORGOT_PASSWORD.equalsIgnoreCase(emailTemplateDataVO.getEmailType())
									|| BBBCoreConstants.ET_ORDER_CONFIRMATION.equalsIgnoreCase(emailTemplateDataVO.getEmailType())){
								 if(this.isLoggingDebug()){
									 this.logDebug("As the email type is either Order Confirmation or forgot Password,"
											 + "so skipping its persistence in the repository");
								 }
							} else{

							String emailMessage = BBBCoreConstants.BLANK;
							final Object content = pMessage.getContent();
							if (content instanceof Multipart) {
								final Multipart mp = (Multipart) content;
								for (int i = 0; i < mp.getCount(); i++) {
									final BodyPart part = mp.getBodyPart(i);
									emailMessage = (part.getContent().toString())
											.toString();
								}
							}
							emailPersistItem.setPropertyValue(
									BBBCoreConstants.EMAIL_ID,
									emailTemplateDataVO.getEmailPersistId());
							emailPersistItem.setPropertyValue(
									BBBCoreConstants.EMAIL_MESSAGE, emailMessage);
							emailPersistItem.setPropertyValue(
									BBBCoreConstants.EMAIL_TYPE,
									emailTemplateDataVO.getEmailType());
							emailPersistItem.setPropertyValue(
									BBBCoreConstants.EMAIL_DATE, emailSendDate);
							emailPersistItem.setPropertyValue(
									BBBCoreConstants.SITE_ID,
									emailTemplateDataVO.getSiteId());
							this.emailPersistRepository.addItem(emailPersistItem);
							if (this.isLoggingDebug()) {
								this.logDebug("Successfully persited email");
							}
						}
					}
		  }
    		} catch (final RepositoryException re) {

    		    if(this.isLoggingDebug()){
    			this.logError("Error in persisting email in [sendEmailSentEvent] for Email Type " + emailTemplateDataVO.getEmailType() + "Repository Exception : " + re);
    		    }
    		    re.getStackTrace();
    		}
            	catch (final IOException e) {
		    this.logError("IO exception while fetching Mime Data for Email Persist" + e);
		   // e.printStackTrace();
		} catch (final MessagingException e) {
		    this.logError("Messaging exception while fetching Mime Data for Email Persist" + e);
		}
            	// Exiting Email Persistence
        }

        if(this.isLoggingDebug()){
            this.logDebug("Exiting method to persist email");
        }
    }

}







