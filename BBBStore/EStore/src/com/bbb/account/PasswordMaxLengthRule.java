/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.bbb.account;

import com.bbb.common.BBBGenericService;
import atg.security.PasswordRule;
import java.util.Map;


public class PasswordMaxLengthRule extends BBBGenericService
    implements PasswordRule
{

	private int mMaxLength = 20;
    public PasswordMaxLengthRule()
    {
        mMaxLength = 20;
    }

    public int getMaxLength()
    {
        return mMaxLength;
    }

    public void setMaxLength(int pMaxLength)
    {
    	mMaxLength = pMaxLength;
    }

    public boolean checkRule(String password, Map map)
    {
        boolean passed = false;
        if(password == null)
        {
            return false;
        }
        if(password.length() <= mMaxLength)
        {
            passed = true;
        }
        return passed;
    }

    public String getRuleDescription()
    {
        Object params[] = {
            (new StringBuilder()).append("").append(mMaxLength).toString()
        };
        return "KEY_MAX_LENGTH_INCORRECT";
    }

}