/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.psa.pc.fw.ac.util;

import com.psa.pc.fw.ac.ctx.PCContext;
import com.psa.pc.fw.ac.logger.ACLoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import org.slf4j.Logger;

/**
 *
 * @author ngch
 */
public class ResourceBundleUtil {
    
    private static Logger logger = ACLoggerFactory.getLogger(ResourceBundleUtil.class);
    private ResourceBundle resourcebundle;
    
    public ResourceBundleUtil(PCContext ctx) {
        resourcebundle = ResourceBundle.getBundle(ctx.getModule().getContext().replace('.', '_'), ctx.getLocale(), new UTF8Control());
    }
    
    public ResourceBundleUtil(PCContext ctx, Locale locale) {
        resourcebundle = ResourceBundle.getBundle(ctx.getModule().getContext().replace('.', '_'), locale, new UTF8Control());
    }
    
    public String getMessage(String key, Object... value) {
        return String.format(resourcebundle.getString(key), value);
    }
}

class UTF8Control extends ResourceBundle.Control {
    public ResourceBundle newBundle
        (String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
            throws IllegalAccessException, InstantiationException, IOException
    {
        // The below is a copy of the default implementation.
        String bundleName = toBundleName(baseName, locale);
        String resourceName = toResourceName(bundleName, "properties");
        ResourceBundle bundle = null;
        InputStream stream = null;
        if (reload) {
            URL url = loader.getResource(resourceName);
            if (url != null) {
                URLConnection connection = url.openConnection();
                if (connection != null) {
                    connection.setUseCaches(false);
                    stream = connection.getInputStream();
                }
            }
        } else {
            stream = loader.getResourceAsStream(resourceName);
        }
        if (stream != null) {
            try {
                // Only this line is changed to make it to read properties files as UTF-8.
                bundle = new PropertyResourceBundle(new InputStreamReader(stream, "UTF-8"));
            } finally {
                stream.close();
            }
        }
        return bundle;
    }
}