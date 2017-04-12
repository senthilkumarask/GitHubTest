/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  BBBReferralControler.java
 *
 *  DESCRIPTION: A pipeline servlet handling the wedding channel &
 * 				commission junction integration logic.
 *  HISTORY:
 *  02/06/12 Initial version
 *
 */
package com.bbb.pipeline;

import atg.servlet.security.param.OverridableParameterValidator;
import java.util.regex.Matcher;

/**
 * DESCRIPTION: Parameter validator overridden to include case insenstive condition for illegal attributes check.
 * 
 * @author agoe21
 */
public class BBBParameterValidator extends OverridableParameterValidator {


   protected boolean isSuspiciousParamValue(String pParamName, String pValue)
    {
        Matcher matcherCombined = getCombinedPattern().matcher(pValue);
        if(matcherCombined.find())
        {
            if(isLoggingDebug())
                logDebug((new StringBuilder()).append("Found suspicious group from combination pattern: ").append(matcherCombined.group(0)).toString());
            for(Matcher matcherTag = getTagPattern().matcher(pValue); matcherTag.find();)
            {
                String strTag = matcherTag.group(1);
                if(!isOnlyDisallowIllegalTagNames() || getIllegalTagNamesSet().contains(strTag.toLowerCase()))
                {
                    vlogWarning("Found possible illegal HTML tag \"{0}\" in param \"{1}\".", new Object[] {
                        strTag, pParamName
                    });
                    return true;
                }
            }

            for(Matcher matcherAttr = getAttributePattern().matcher(pValue); matcherAttr.find();)
            {
                String strAttr = matcherAttr.group(1);
                if(!isOnlyDisallowIllegalAttributeNames() || getIllegalAttributeNamesSet().contains(strAttr.toLowerCase()))
                {
                    vlogWarning("Found possible illegal HTML attribute \"{0}\" in param {1}", new Object[] {
                        strAttr, pParamName
                    });
                    return true;
                }
            }

            if(getIllegalPattern() != null)
            {
                Matcher matcherIllegal = getIllegalPattern().matcher(pValue);
                if(matcherIllegal.find())
                {
                    String strString = matcherIllegal.group(0);
                    vlogWarning("Found illegal param string \"{0}\" in param {1}", new Object[] {
                        strString, pParamName
                    });
                    return true;
                }
            }
        }
        return false;
    }
}
