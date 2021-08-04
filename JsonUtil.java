/*
 * Copyright (c) 2016. BTGD. All Rights Reserved.
 *
 * Class                 : JsonUtil.java
 * Description           : Provide a series of Jackson Utility APIs 
 *
 * Modification History
 * Date                    Modifier Name                Reason
 * ---------                ------------              -------------
 * 20-Sep-2016             Yang Shengong               Initial version
 *
 * @version 1.0
 * @since release 1.0
 * @author Yang Shengong
 */

package com.psa.pc.fw.ac.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class {@code JsonUtil} provides a series of common Jackson 
 * Utility APIs so that the handling associated with Jackon conversion
 * can be simplified and reused in PCP
 * 
 * <p> All APIs are static method, without any object instance 
 * required.
 * 
 * @author Yang Shengong
 * @version 1.0
 */

public class JsonUtil {
    /** Logback Logger instance for the preforming of application log */
    private static Logger logger = LoggerFactory.getLogger(JsonUtil.class);
    
    /** Private Constructor to avoid the instance of the object*/
    private JsonUtil() {}
    
    /**
     * Method to convert object to json String
     * <p>Date type is standardized to ISO 8601 format
     * 
     * @param obj Entity object
     * @return Jackson string
     */
    public static String toJson(Object obj) {
        String result = "";
        if(obj != null) {
            try {
                ObjectMapper mapper = getMapper();
                result = mapper.writeValueAsString(obj);
            } catch (final IOException e) {
                logger.error("IOException while transform object to json string:", e);
            }
        }
        return result;
    }
    
    /**
     * Method to convert object to json String based on the specified Json View class
     * <p>Date type is standardized to ISO 8601 format
     * 
     * @param obj Entity object
     * @param viewCls Json View class
     * @return Jackson string
     */
    public static String toJsonWithView(Object obj, Class viewCls) {
        String result = "";
        if(obj != null) {
            try {
                ObjectMapper mapper = getMapper();
                mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION); 
                result = mapper.writerWithView(viewCls).writeValueAsString(obj);
            } catch (final IOException e) {
                logger.error("IOException while transform object to json string:", e);
            }
        }
        return result;
    }
    
    private static ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
        mapper.setTimeZone(Calendar.getInstance().getTimeZone());
        return mapper;
    }

    /**
     * Convert JSON String to specified Class
     * 
     * @param json Jackson string
     * @param clazz Entity type
     * @return Entity object
     */
    @SuppressWarnings("unchecked")
    public static <T> T toObject(final String json, final Class<T> clazz) {
        T obj = null;
        try {
            ObjectMapper mapper = getMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            obj = mapper.readValue(json, clazz);
        } catch (Exception e) {
            logger.error("Exception while transform json string to object", e);
        }
        return obj;
    }

    /**
     * Convert JSON file to specified Class object list
     * 
     * @param fileFullPath String The file full path
     * @param cls Entity type
     * @return List&lt;T&gt; The object list, if there is any error when converting, the list will be null.
     */
    public static <T> List<T> toObjectList(String fileFullPath, Class<T> cls) {
        try {
            Path p = Paths.get(fileFullPath);
            Stream<String> lines = Files.lines(p);
            
            List<T> objectList = new ArrayList<>();
            for (Iterator<String> it = lines.iterator(); it.hasNext();) {
                String str = it.next();
                if (StringUtil.isNullOrBlank(str)) {
                    continue;
                }
                Object obj = JsonUtil.toObject(str, cls);
                if(obj == null) {
                    throw new Exception("Invalid Jackson String");
                }
                objectList.add(cls.cast(obj));
            }
            
            return objectList;
        } catch (Exception e) {
            logger.error("Exception while transform json string to object", e);
            return new ArrayList<>();
        }
    }
    
    public static final void revertDateValue(Map<String, Object> param, String... fields) {
        if(!ObjectUtil.isNullOrEmpty(param) && !ObjectUtil.isNullOrEmpty(fields)) {
            String d4 = "\\d{4}";
            String d3 = "\\d{3}";
            String d2 = "\\d{2}";
            Pattern p1 = Pattern.compile(String.format("%s-%s-%s", d4, d2, d2));
            Pattern p2 = Pattern.compile(String.format("%s-%s-%sT%s:%s", d4, d2, d2, d2, d2));
            Pattern p3 = Pattern.compile(String.format("%s-%s-%sT%s:%s:%s", d4, d2, d2, d2, d2, d2));
            Pattern p4 = Pattern.compile(String.format("%s-%s-%sT%s:%s:%s\\.%sZ", d4, d2, d2, d2, d2, d2, d3));
            for(String key : fields) {
                if(param.containsKey(key)) {
                    Object value = param.get(key);
                    if(value != null) {
                        if(value instanceof Long) {
                            Date date = new Date();
                            date.setTime((Long) value);
                            param.put(key, date);
                        } else if(value instanceof String) {
                            if(p2.matcher((String) value).matches()) {
                                param.put(key, DateUtil.stringToDate((String) value, DateUtil.ISO8601_yyyyMMddHHmm));
                            } else if(p3.matcher((String) value).matches()) {
                                param.put(key, DateUtil.stringToDate((String) value, DateUtil.ISO8601_yyyyMMddHHmmss));
                            } else if(p4.matcher((String) value).matches()) {
                                param.put(key, DateUtil.stringToDate((String) value, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
                            }
                        }
                    }
                }
            }
        }
    }
}
