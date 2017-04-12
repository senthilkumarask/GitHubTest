package com.bbb.account;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bbb.cms.manager.LblTxtTemplateManager;

import com.bbb.common.BBBGenericService;
import atg.security.PasswordRule;
import atg.servlet.ServletUtil;

public class PasswordMustNotIncludeCharacterRule extends BBBGenericService
		implements PasswordRule {

	private String mNotRequiredCharacterList;
	
	private LblTxtTemplateManager mLblTxtTemplateManager;
	
	public PasswordMustNotIncludeCharacterRule() {
		mNotRequiredCharacterList = "";
	}

	public String getNotRequiredCharacterList() {
		return mNotRequiredCharacterList;
	}

	public void setNotRequiredCharacterList(final String pNotRequiredCharacterList) {
		mNotRequiredCharacterList = pNotRequiredCharacterList;
	}

	public boolean checkRule(final String password, final Map map) {
		boolean passed = true;
		if (password == null){
			return false;
		}
		if (mNotRequiredCharacterList.length() == 0){
			return true;
		}
			logDebug("NotRequiredCharacterList Pattern is:" + getNotRequiredCharacterList());
		final Pattern whitespace = Pattern.compile(getNotRequiredCharacterList());
		final Matcher matcher = whitespace.matcher(password);
		if(matcher.find()){
			passed = false;
				logDebug("Password contains not to be included chars");
		}

		return passed;
	}

	public String getRuleDescription() {
		return getLblTxtTemplateManager().getErrMsg(
				"err_password_has_whitespaces",
				ServletUtil.getUserLocale().getLanguage(), null, null);
	}

	

	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return mLblTxtTemplateManager;
	}

	public void setLblTxtTemplateManager(
			final LblTxtTemplateManager pLblTxtTemplateManager) {
		this.mLblTxtTemplateManager = pLblTxtTemplateManager;
	}

}