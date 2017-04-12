package com.bbb.email;

import atg.userprofiling.email.TemplateEmailInfoImpl;


public class BBBTemplateEmailInfoImpl extends TemplateEmailInfoImpl{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;   
    String mEmailPersistId;    
        
    public String getEmailPersistId() {
        return mEmailPersistId;
    }


    public void setEmailPersistId(String mEmailPersistId) {
        this.mEmailPersistId = mEmailPersistId;
    }     
}


