package com.psa.pc.fw.ac.util;

import com.psa.pc.fw.ac.configuration.PCConfiguration;
import com.psa.pc.fw.ac.configuration.PCConfigurationManager;
import com.psa.pc.fw.ac.ctx.AssistentModule;
import com.psa.pc.fw.ac.logger.ACLoggerFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;

public class PropertyUtil {
    
    /** Logback Logger instance for the performing of application log */
    private static Logger logger = ACLoggerFactory.getLogger(PropertyUtil.class);
    
    private static Map<String, Properties> config = new HashMap<>();
        
    private PropertyUtil() {}
    
    public static Properties load(String filename) throws IOException {
        logger.info("filename:{}", filename);
        if(config.containsKey(filename)) {
            return config.get(filename);
        }
        Properties property = new Properties();
        InputStream is = null;
        try {
            is = PropertyUtil.class.getResourceAsStream(filename);
            property.load(is);
            config.put(filename, property);
            return property;
        } catch (IOException e) {
            logger.error("Error loading properties file. filename:" + filename, e);
            throw e;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
    
    /**
     * Load ApplicationComponent.properties based on different cluster
     * @return PCConfiguration
     */
    public static PCConfiguration loadACProperties() {
        PCConfiguration acCfg = PCConfigurationManager.getConfiguration(AssistentModule.AC);
        String cluster = System.getProperty("weblogic.Name");
        logger.debug("Cluster name is {}", cluster);

        String filepath = acCfg.getProperty("ac.config.path");
        if(!StringUtil.isNullOrBlank(cluster)) {
            List<String> clusterLst = acCfg.getPropertyList("api.cluster");
            logger.debug("Configured API clusters - {}", StringUtil.arrayToString(clusterLst, ','));
            if(!ObjectUtil.isNullOrEmpty(clusterLst) && clusterLst.stream().anyMatch(p -> cluster.contains(p))) {
                filepath = acCfg.getProperty("ac.config.path.api");
            }
        }
        
        PCConfiguration fileCfg = null;
        if(!StringUtil.isNullOrBlank(filepath)) {
            try {
                File file = new File(filepath, "ApplicationComponent.properties");
                fileCfg = PCConfigurationManager.getConfiguration(file);
            } catch (Exception ex) {
                logger.error("Cannot get web service domain from <ApplicationComponent.properties>", ex);
            }
        } else {
            logger.info("ApplicationComponent.properties filepath is NOT configured.");
        }
        
        return fileCfg;
    }
}
