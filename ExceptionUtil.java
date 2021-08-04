/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.psa.pc.fw.ac.util;

import com.psa.appfw.exception.SystemException;
import com.psa.appfw.exception.ValidationException;
import com.psa.pc.fw.ac.configuration.CacheStatus;
import com.psa.pc.fw.ac.configuration.PCConfiguration;
import com.psa.pc.fw.ac.configuration.PCConfigurationManager;
import com.psa.pc.fw.ac.ctx.IModule;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author attysgg1
 */
public class ExceptionUtil {
    /** Private Constructor to avoid the instance of the object*/
    private ExceptionUtil() {}
        
    public static ValidationException newAppEx(IModule module, Locale locale, String errKey) {
        ValidationException tmp = new ValidationException(module.getContext(), errKey);
        return deriveValidationException(tmp, locale);
    }
        
    public static ValidationException newAppEx(IModule module, Locale locale, String errKey, Object... values) {
        ValidationException tmp = new ValidationException(module.getContext(), errKey, values);
        return deriveValidationException(tmp, locale);
    }
    
    /**
     * If MessageStorageType is File, then it is follow the other API
     * If MessageStorageType is DB
     *  - errKey: it is same as normal Error Code, looks like &lt;MODULE&gt;_&lt;FIELD&gt;_NNNNN
     *  - In pc_config, the format must be error.message.&lt;errKey&gt;[.&lt;language&gt;-&lt;country&gt;]
     *  - Sample for the configured value: error.message.XXX_YYY_00001 and error.message.XXX_YYY_00001.zh-cn
     *  - Need to use %s as the indicator of replacement, instead of {0}
     * 
     * @param module IModule object, context is specified
     * @param locale Locale to identify language and country
     * @param msgStorageTy Indicator to derive message from properties file or pc_config
     * @param errKey Error code
     * @param values Replaced string
     * @return Application Exception
     */
    public static ValidationException newAppEx(IModule module, Locale locale, MessageStorageType msgStorageTy, String errKey, Object... values) {
        if (msgStorageTy == null || msgStorageTy.isFile()) {
            return newAppEx(module, locale, errKey, values);
        } else {
            PCConfiguration cfg = PCConfigurationManager.getConfiguration(module, CacheStatus.ALWAYS);
            String localeKey = String.format("%s-%s", locale.getLanguage(), locale.getCountry().toLowerCase());
            String cfgKey = "en-us".equalsIgnoreCase(localeKey) || "en-sg".equalsIgnoreCase(localeKey) ? 
                    String.format("error.message.%s", errKey) : String.format("error.message.%s.%s", errKey, localeKey);
            String errMsgTemplate = cfg.getProperty(cfgKey);
            String errMsg = StringUtil.isNullOrBlank(errMsgTemplate) ? errKey : String.format(errMsgTemplate, values);
            return new ValidationException(module.getContext(), errMsg, errKey, null);
        }
    }
    
    public static ValidationException newAppEx(IModule module, Locale locale, ErrorInfo info) {
        ValidationException tmp = new ValidationException(module.getContext(), info.getKey(), info.getParam(), info.getField());
        ValidationException appEx = deriveValidationException(tmp, locale);
        appEx.setErrorField(tmp.getErrorField());
        return appEx;
    }
    
    private static ValidationException deriveValidationException(ValidationException tmp, Locale locale) {
        return new ValidationException(tmp.getContext(), tmp.getLocalizedMessage(locale), tmp.getErrorCode(), null);
    }

    public static ValidationException newMulAppEx(IModule module, List<ValidationException> appExLst) {
        ValidationException masterAppEx = new ValidationException(module.getContext(), "Multiple Application Errors");
        if (!ObjectUtil.isNullOrEmpty(appExLst)) {
            appExLst.forEach(p -> masterAppEx.addToExceptions(p));
        }
        return masterAppEx;
    }
    
    public static SystemException newSysEx(IModule module, Locale locale, String errKey) {
        SystemException tmp = new SystemException(module.getContext(), errKey);
        return deriveSystemException(tmp, locale);
    }
    
    public static SystemException newSysEx(IModule module, Locale locale, Exception e, String errKey) {
        SystemException tmp = new SystemException(module.getContext(), errKey, new Exception[] { e });
        return deriveSystemException(tmp, locale);
    }
    
    public static SystemException newSysEx(IModule module, Locale locale, String errKey, Object... values) {
        SystemException tmp = new SystemException(module.getContext(), errKey, values);
        return deriveSystemException(tmp, locale);
    }
    
    public static SystemException newSysEx(IModule module, Locale locale, Exception e, String errKey, Object... values) {
        SystemException tmp = new SystemException(module.getContext(), errKey, values, new Exception[] { e });
        return deriveSystemException(tmp, locale);
    }
    
    private static SystemException deriveSystemException(SystemException tmp, Locale locale) {
        return new SystemException(tmp.getContext(), tmp.getLocalizedMessage(locale), tmp.getErrorCode(), null);
    }
    
    public static SystemException newMulSysEx(IModule module, List<SystemException> sysExLst) {
        SystemException masterSysEx = new SystemException(module.getContext(), "Multiple System/Application Errors");
        if (!ObjectUtil.isNullOrEmpty(sysExLst)) {
            sysExLst.forEach(p -> masterSysEx.addToExceptions(p));
        }
        return masterSysEx;
    }
        
    public static class ErrorInfo {
        private final String key;
        private String field;
        private Object[] param;
        
        private ErrorInfo(String key) {
            this.key = key;
        }
        
        public static ErrorInfo getInstance(String key) {
            return new ErrorInfo(key);
        }
        
        String getKey() {
            return this.key;
        }

        String getField() {
            return field;
        }
        public ErrorInfo setField(String field) {
            this.field = field;
            return this;
        }
        public ErrorInfo setField(String field, String info) {
            if (StringUtil.isNullOrBlank(info)) {
                return setField(field);
            } else {
                this.field = String.format("%s|%s", field, info);
                return this;
            }
        }

        Object[] getParam() {
            return param;
        }
        public ErrorInfo setParam(Object... param) {
            this.param = param;
            return this;
        }
    }
    
    public static enum MessageStorageType {
        DB("DB"), FILE("FILE");
        
        private final String type;
        MessageStorageType(String type) {
            this.type = type;
        }
        
        boolean isDB() {
            return this.equals(DB);
        }
        boolean isFile() {
            return this.equals(FILE);
        }
    }
}
