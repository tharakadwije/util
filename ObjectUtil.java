/*
 * Copyright (c) 2016. BTGD. All Rights Reserved.
 *
 * Class                 : ObjectUtil.java
 * Description           : Provide a series of Object Utility APIs 
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

import java.io.Serializable;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import java.math.BigDecimal;

import java.math.BigInteger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.SerializationUtils;

/**
 * Class {@code ObjectUtil} provides a series of common Object
 * Utility APIs so that the handling associated with object
 * can be simplified and reused in PCP
 *
 * <p> All APIs are static method, without any object instance
 * required.
 *
 * @author Yang Shengong
 * @version 1.0
 */

public class ObjectUtil {
    /** Private Constructor to avoid the instance of the object*/
    private ObjectUtil() {}
    
    /**
     * Check whether an object is null or empty
     * 
     * <p>It is cater for different type, including normal Object, 
     * Collection, Map, Set, String and Array
     * 
     * <p>Normal Object: check NULL
     * <p>Collection, Map and Set: check NULL and Empty
     * <p>Array: check NULL and length
     * <p>String: check NULL and blank space which is same as 
     * StringUtil.isNullOrBlank
     * 
     * <p>Example:
     * <pre>
     * Object obj1 = null;
     * boolean result1 = ObjectUtil.isNullOrEmpty(obj1); //result1 = true
     * 
     * List&lt;String&gt; obj2 = new ArrayList&lt;&gt;();
     * boolean result2 = ObjectUtil.isNullOrEmpty(obj2); //result2 = true
     * 
     * Map&lt;String&gt;,&lt;String&gt; obj3 = ...; //There are 3 enties
     * boolean result3 = ObjectUtil.isNullOrEmpty(obj3); //result3 = false
     * </pre>
     * 
     * @param obj Object
     * @return true if object is null or empty
     * 
     * see com.psa.pc.ac.util.StringUtil#isNullOrBlank
     */
    public static final boolean isNullOrEmpty(Object obj) {
        if(obj == null) {
            return true;
        } else if(obj instanceof String) {
            String objStr = String.class.cast(obj);
            return objStr.trim().length() == 0;
        } else if(obj instanceof Collection) {
            Collection objCol = Collection.class.cast(obj);
            return objCol.isEmpty();
        } else if(obj instanceof Map) {
            Map objMap = Map.class.cast(obj);
            return objMap.isEmpty();
        } else if(obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        } else {
            return false;
        }
    }
    
    /**
     * Get the non-null list
     * <p>An empty list is assigned if null list is passed in
     * 
     * @param lst List to convert
     * @return Non-null list
     */
    public static final <T> List<T> noNull(List<T> lst) {
        return lst == null ? new ArrayList<>() : lst;
    }
    
    /**
     * Get the non-null map
     * <p>An empty map is assigned if null map is passed in
     * 
     * @param map Map to convert
     * @return Non-null map
     */
    public static final <U, V> Map<U, V> noNull(Map<U, V> map) {
        return map == null ? new HashMap<>() : map;
    }

    /**
     * Check whether an entry with specified key exist in map
     * 
     * @param valueMap Map container
     * @param key Key
     * @return true when value of key in valueMap is not null
     */
    public static final boolean isValueOfMapNull(Map valueMap, String key) {
        return isNullOrEmpty(valueMap) || isNullOrEmpty(valueMap.get(key));
    }
    
    /**
     * Deep clone an object
     * 
     * @param obj Original object
     * @return cloned object
     */
    public static final <T extends Serializable> T clone(T obj) {
        return SerializationUtils.clone(obj);
    }
    
    /**
     * Deep clone an object list
     * 
     * @param objLst Original object list
     * @return cloned object list
     */
    public static final <T extends Serializable> List<T> clone(List<T> objLst) {
        if(isNullOrEmpty(objLst)) {
            return new ArrayList<>();
        }
        
        List<T> clonedLst = new ArrayList<>();
        for(T obj : objLst) {
            T cloned = SerializationUtils.clone(obj);
            clonedLst.add(cloned);
        }
        
        return clonedLst;
    }
    
    /**
     * Get object values based on the different data type
     * 
     * <p>For Date and its subclass, it is format with yyyyMMddHHmmss
     * 
     * @param obj Object
     * @return value with string type
     */
    public static String toString(Object obj) {
        if(obj == null) {
            return "";
        } else if (obj instanceof String) {
            return (String) obj;
        } else if (obj instanceof BigDecimal || obj instanceof BigInteger || 
                   obj instanceof Double || obj instanceof Float || 
                   obj instanceof Long || obj instanceof Integer || 
                   obj instanceof Short || obj instanceof Byte || 
                   obj instanceof Boolean) {
            return obj.toString();
        } else if (obj.getClass() == double.class || obj.getClass() == float.class || 
                   obj.getClass() == long.class || obj.getClass() == int.class || 
                   obj.getClass() == short.class || obj.getClass() == byte.class || 
                   obj.getClass() == boolean.class) {
            return String.valueOf(obj);
        } else if (obj instanceof Date) {
            return DateUtil.dateToString((Date) obj);
        } else {
            return obj.toString();
        }
    }
    
    /**
     * Store hierachical map information into a map with simplified structure
     * 
     * <p>if source map contain complex type value, then the value is stored in 
     * the final map directly without additional handling
     * 
     * Example:
     * { 
     * "ref_m" : "ABCD 1234",
     * "object_id" : 456,
     * "first_event_c" : "TLIC",
     * "Cntr_event" : {
     *     "event_c" : "DISC",
     *     "length_q" : 20,
     *     "org_c" : "PSA"
     * },
     * "disc_vessl_object : {
     *     "vsl_m" : "APL CHIWAN",
     *     "voy_n" : "001E",
     *     "Vessel_berth" : {
     *         "berth_seq_n" : 1,
     *         "dt_ops_reason_c" : "ST01"
     *     }
     * }
     * }
     * <pre>
     * Map&lt;String, Object&gt; src = ... //from above example
     * Map&lt;String, String&gt; result = ObjectUtil.simplifyHierarchicalMap(src);
     * </pre>
     * 
     * <p>Result as below:
     * ref_m = "ABCD 1234"
     * object_id = "456"
     * first_event_c = "TLIC"
     * Cntr_evnet.event_c = "DISC"
     * Cntr_evnet.length_q = "20"
     * Cntr_evnet.org_c = "PSA"
     * disc_vessl_object.vsl_m = "APL CHIWAN"
     * disc_vessl_object.voy_n = "001E"
     * disc_vessl_object.Vessel_berth.berth_seq_n = "1"
     * disc_vessl_object.Vessel_berth.dt_ops_reason_c = "ST01"
     * 
     * @param src Source map - map value should be a basic type or another map
     *        <p>Usually, the map should be converted from a Jackson string
     * @return a map with simplified structure
     */
    public static Map<String, String> simplifyHierarchicalMap(Map<String, Object> src) {
        Map<String, String> dest = new HashMap<>();
        simplifyHierarchicalMap("", src, dest);
        return dest;
    }

    @SuppressWarnings("unchecked")
    private static void simplifyHierarchicalMap(String parentKey, Map<String, Object> src, Map<String, String> dest) {
        if(!isNullOrEmpty(src)) {
            src.forEach((key, value) -> {
                        String finalKey = StringUtil.isNullOrBlank(parentKey) ? key : parentKey + "." + key;
                        if(value instanceof Map) {
                            simplifyHierarchicalMap(finalKey, (Map) value, dest);
                        } else {
                            dest.put(finalKey, toString(value));
                        }
                        });
        }
    }
    
    public static BigInteger toBigInteger(Object obj) {
        return toNumber(obj, BigInteger.class);
    }
    
    public static <T extends Number> T toNumber(Object obj, Class<T> cls) {
        if (ObjectUtil.isNullOrEmpty(obj)) {
            return null;
        }
        if (cls == null) {
            throw new IllegalArgumentException("Class type is NOT provided.");
        }
        
        try {
            Constructor<T> c = cls.getConstructor(String.class);
            return c.newInstance(toString(obj));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid class without a constructor with String parameter", ex);
        }
    }
    
    /**
     * To compare two Domain Object based on the specified fields
     *  - throw system exception if compared object is not GPN module domain object
     *  - throw system exception if any one of compared objects is null
     *  - throw system exception if class type of compared objects is different
     *  - throw system exception if provided field is not found
     * 
     * Rule:
     *  - All fields are converted to String and then compare, that means the complex data type cannot be compared as value
     *  - If fields are not provided, then compare all fields in the object
     * 
     * @param obj1 the first object
     * @param obj2 the second object
     * @param caseSensitive Case sensitive indicator
     * @param fields the specified fields to be compared, it is optional
     * @return compared results, key is field name, and value is two object values 
     */
    public static Map<String, List<String>> compare(Object obj1, Object obj2, boolean caseSensitive, String... fields) {
        
        if (obj1 == null || obj2 == null) {
            throw new IllegalArgumentException("NULL objects cannot be compared using the API");
        }
        
        Class cls = obj1.getClass();
        if (!cls.getPackage().getName().startsWith("com.psa.pc") || !(cls instanceof Serializable)) {
            throw new IllegalArgumentException("Only two domain objects belong to GPN modules can be compared.");
        }
        
        if (obj1.getClass() != obj2.getClass()) {
            throw new IllegalArgumentException("Only two objects with same class type can be compared.");
        }
        
        Set<String> fieldSet = ObjectUtil.isNullOrEmpty(fields) ? null : Stream.of(fields).collect(Collectors.toSet());
        Field[] fieldList = cls.getDeclaredFields();
        
        Set<String> allFieldName = Stream.of(fieldList).map(Field::getName).collect(Collectors.toSet());
        if (!ObjectUtil.isNullOrEmpty(fieldSet)) {
            Set<String> invalidField = fieldSet.stream().filter(p -> !allFieldName.contains(p)).collect(Collectors.toSet());
            if (!ObjectUtil.isNullOrEmpty(invalidField)) {
                throw new IllegalArgumentException("Some configured fields are not included in domain object: " + invalidField);
            }
        } else {
            fieldSet = allFieldName;
        }
        
        Map<String, List<String>> result = new HashMap<>();
        
        try {
            for (Field field : fieldList) {
                if (fieldSet.contains(field.getName())) {
                    field.setAccessible(true);
                    String val1 = ObjectUtil.toString(field.get(obj1));
                    String val2 = ObjectUtil.toString(field.get(obj2));
                    if (caseSensitive && !val1.equals(val2) || !caseSensitive && !val1.equalsIgnoreCase(val2)) {
                        result.put(field.getName(), Stream.of(val1, val2).collect(Collectors.toList()));
                    }
                }
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
        
        return ObjectUtil.isNullOrEmpty(result) ? null : result;
    }
    
    public static boolean compare(Set<String> set1, Set<String> set2) {
        if (ObjectUtil.isNullOrEmpty(set1) && !ObjectUtil.isNullOrEmpty(set2) || !ObjectUtil.isNullOrEmpty(set1) && ObjectUtil.isNullOrEmpty(set2)) {
            return false;
        }
        if (ObjectUtil.isNullOrEmpty(set1) && ObjectUtil.isNullOrEmpty(set2)) {
            return true;
        }
        
        return set1.size() == set2.size() && set1.stream().allMatch(q -> set2.contains(q));
    }
    
    
    
    /**
     * Trim and uppercase object value
     * Rule:
     *  - Only String, Map and List type can handled, the handing can be ignored for the rest type
     *  - String : do directly
     *  - Map: map value do the change and ignoredKey is provided to allow some fields are ignored to handle
     *  - List : handle list element value
     * 
     * @param obj 
     * @param ingoredKey the specified fields to be no UpperCase, it is optional
     * @return Object to uppercase when Object is Map except key is ingoreKey
     */
    public static Object trimUpperCase(Object obj, String... ingoredKey) {
        if(obj == null) {
            return null;
        }
        
        Set<String> ignoredSet = isNullOrEmpty(ingoredKey) ? new HashSet<>() : Stream.of(ingoredKey).collect(Collectors.toSet());
        if(obj instanceof String) {
            String objStr = String.class.cast(obj);
            return StringUtil.trimUpperCase(objStr);
        }else if(obj instanceof Map) {
            Map objMap = Map.class.cast(obj);
            if (objMap.isEmpty()) {
                return null;
            }
            objMap.forEach((key, value) -> {
                if(!ignoredSet.contains(key)) {
                    objMap.put(key, trimUpperCase(value, ingoredKey));
                }
            });
            return objMap;
        } else if(obj instanceof List) {
            List objlist = List.class.cast(obj);
            if (objlist.isEmpty()) {
                return null;
            }
            List derived = new ArrayList<>();
            objlist.forEach(ele -> derived.add(trimUpperCase(ele, ingoredKey)));
            return derived;
        } else {
            return obj;
        }
    }
}
