/*
 * Copyright (c) 2016. BTGD. All Rights Reserved.
 *
 * Class                 : StringUtil.java
 * Description           : Provide a series of String Utility APIs 
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Class {@code StringUtil} provides a series of common String
 * Utility APIs so that the handling associated with String object
 * can be simplified and reused in PCP
 *
 * <p> All APIs are static method, without any object instance
 * required.
 *
 * @author Yang Shengong
 * @version 1.0
 */

public class StringUtil {
    /** Private Constructor to avoid the instance of the object*/
    private StringUtil() {}
    
    /**
     * Check whether a String object is NULL object or blank space 
     * string
     * 
     * <p>Examples:
     * <pre>
     * String str1 = "abc";
     * boolean result1 = StringUtil.isNullOrBlank(str1); //result1 = false
     * 
     * String str2 = "";
     * boolean result2 = StringUtil.isNullOrBlank(str2); //result2 = true
     * 
     * String str3;
     * boolean result3 = StringUtil.isNullOrBlank(str3); //result3 = true
     * 
     * String str4 = "null";
     * boolean result4 = StringUtil.isNullOrBlank(str4); //result4 = false
     * 
     * //The last one is for a special scenario
     * //The value is a space string which lenght is 1
     * String str5 = " ";
     * boolean result5 = StringUtil.isNullOrBlank(str5); //result5 = TRUE
     * </pre>
     * 
     * @param str String object to do check
     * @return true for NULL or blank space string object; false for else
     */
    public static boolean isNullOrBlank(String str) {
        return str == null || str.trim().length() == 0;
    }
    
    /**
     * Initialize a String object with blank space value 
     * if it is a NULL object
     * 
     * <p>Example:
     * <pre>
     * String str1;
     * String result1 = StringUtil.noNull(str1); // result1 = ""
     * 
     * String str2 = "abc";
     * String result2 = StringUtil.noNull(str2); // result2 = "abc"
     * 
     * //For a blank space string, the String object is NOT trimed.
     * String str3 = " ";
     * String result3 = StringUtil.noNull(str3); // result3 = " "
     * </pre>
     * 
     * @param str String object to do conversion
     * @return Non-null String object for a NULL String object, 
     * otherwise, do nothing.
     */
    public static String noNull(String str) {
        return str == null ? "" : str;
    }
    
    /**
     * Trim and uppercase a trim; Any the space blank between two charactors 
     * are NOT trimed
     * 
     * <p>Example:
     * <pre>
     * String str1;
     * String result1 = StringUtil.trimUpperCase(str1); // result1 = ""
     * 
     * String str2 = "abc";
     * String result2 = StringUtil.trimUpperCase(str2); // result2 = "ABC"
     * 
     * String str3 = " A b C ";
     * String result3 = StringUtil.trimUpperCase(str3); // result3 = "A B C"
     * </pre>
     * 
     * @param str String object to do conversion
     * @return String to be trimed and uppercase
     */
    public static String trimUpperCase(String str) {
        return noNull(str).trim().toUpperCase();
    }
    
    /**
     * Convert Boolean value of String type to boolean type.
     * 
     * <p>In CBS, boolean value is stored as a String type (e.g. 
     * "Y", "Yes") or Number type (e.g. 1), the API convert different
     * type of indicator to boolean type so that developer simplify
     * individual codes
     * 
     * <p> Currently, the String indicator which can be coverted are 
     * "Yes", "Y" and "1" only (Case Insensitive)
     * 
     * @param ind String type indicator
     * @return true for "Yes", "Y" and "1" (Case Insensitive), flase 
     * for else
     */
    public static final boolean isTrue(String ind) {
        return "Y".equalsIgnoreCase(ind) || 
               "Yes".equalsIgnoreCase(ind) || 
               "1".equals(ind);
    }
    
    /**
     * Pad zero to a number so that Number type can be format with 
     * fixed length for some business aim, like "00012". Obviously, 
     * zero must be padded from left.
     * 
     * <p>Besides positive number, the API is to cater for negative 
     * number as well.
     * 
     * <p>Example:
     * <pre>
     * String result1 = StringUtil.pad(2, 4); //result1 = "0002"
     * String result2 = StringUtil.pad(3, 1); //result2 = "3"
     * String result3 = StringUtil.pad(13, -1); //result3 = "13"
     * String result4 = StringUtil.pad(-21, 5); //result4 = "-0021";
     * </pre>
     * 
     * @param num Number to pad zero
     *        <p>Positive number, Zero and Negative number are acceptable
     * @param length Fixed length after padding
     *        <p>If parameter 'length' is less than or equal to the length
     *        of formatted number (including 'minus' sign), then the 
     *        length can be ignored and default to use the length of 
     *        formatted number
     * @return padded number. 
     *         <p>The 'minus' is the first charator for negative number; 
     *         However 'plus' is never inlcuded in the padded number for 
     *         positive number.
     */
    public static String pad(int num, int length) {
        String str = String.valueOf(num);
        int diff = length - str.length();
        if(diff <= 0) {
            return str;
        }
        StringBuilder buf = new StringBuilder(str);
        int pos = num < 0 ? 1 : 0;
        for(int i = 0; i < diff; i++) {
            buf.insert(pos, "0");
        }
        return buf.toString();
    }
    
    /**
     * Pad blank space to a string so that String type can be format with 
     * fixed length for some business aim, like "ABC  ". Usually, 
     * blank space must be padded from right.
     * 
     * <p>Example:
     * <pre>
     * String result1 = StringUtil.pad("ABC", 4); //result1 = "ABC "
     * String result2 = StringUtil.pad("ZZ", 1); //result2 = "ZZ"
     * String result3 = StringUtil.pad("K", -1); //result3 = "K"
     * String result4 = StringUtil.pad("A B", 5); //result4 = "A B  ";
     * </pre>
     * 
     * @param s String to pad blank space
     * @param length Fixed length after padding
     *        <p>If parameter 'length' is less than or equal to the length
     *        of formatted string, then the length can be ignored and default 
     *        to use the length of formatted number
     * @return padded string. 
     */
    public static String pad(String s, int length) {
        String str = noNull(s);
        int diff = length - str.length();
        if(diff <= 0) {
            return str;
        }
        StringBuilder buf = new StringBuilder(str);
        for(int i = 0; i < diff; i++) {
            buf.append(" ");
        }
        return buf.toString();
    }
    
    /**
     * Convert a string with specified delimiter to be a list.
     * 
     * <p>If dellimiter is not found in the string, then string 
     * object is the unique element of list; If it is NULL or 
     * blank space string, then an empty list is generated.
     * 
     * <p>Example:
     * <pre>
     * String src1 = "a,b,c";
     * List&lt;String&gt; result1 = StringUtil.asList(src1, ','); 
     * //result1 = "a", "b", "c"
     * 
     * String src2 = "a,b,c";
     * List&lt;String&gt; result2 = StringUtil.asList(src2, '~'); 
     * //result2 = "a,b,c"
     * 
     * String src3 = "a~b,c";
     * List&lt;String&gt; result3 = StringUtil.asList(src3, '~'); 
     * //result3 = "a","b,c"
     * 
     * String src4 = ",a,";
     * List&lt;String&gt; result4 = StringUtil.asList(src4, ','); 
     * //result4 = "a"
     * 
     * String src5 = "";
     * List&lt;String&gt; result5 = StringUtil.asList(src5, ','); 
     * //result5 = Empty List without any element
     * 
     * String src6 = ",";
     * List&lt;String&gt; result6 = StringUtil.asList(src6, ','); 
     * //result6 = Empty List without any element
     * </pre>
     * 
     * @param src String object to convert
     * @param sp delimiter
     * @return List of string after conversion based on specified
     *         delimiter
     */
    public static List<String> asList(String src, char sp) {
        List<String> rs = new ArrayList<>();
        
        if(!isNullOrBlank(src)) {
            StringBuilder buf = new StringBuilder();
            for(int i = 0; i < src.length(); i++) {
                char c = src.charAt(i);
                if(c == sp) {
                    if(buf.length() > 0) {
                        rs.add(buf.toString());
                        buf.delete(0, buf.length());
                    } else {
                        rs.add("");
                    }
                } else {
                    buf.append(c);
                }
            }
            if(buf.length() > 0) {
                rs.add(buf.toString());
            } else {
                rs.add("");
            }
        }
        
        return rs.isEmpty() ? null : rs;
    }
    
    /**
     * Convert a string with specified delimiter to be a list.
     * 
     * <p>If dellimiter is not found in the string, then string 
     * object is the unique element of list; If it is NULL or 
     * blank space string, then an empty list is generated.
     * 
     * <p>Example:
     * <pre>
     * String src1 = "a,b,c";
     * List&lt;String&gt; result1 = StringUtil.asList(src1, ','); 
     * //result1 = "a", "b", "c"
     * 
     * String src2 = "a,b,c";
     * List&lt;String&gt; result2 = StringUtil.asList(src2, '~'); 
     * //result2 = "a,b,c"
     * 
     * String src3 = "a~b,c";
     * List&lt;String&gt; result3 = StringUtil.asList(src3, '~'); 
     * //result3 = "a","b,c"
     * 
     * String src4 = ",a,";
     * List&lt;String&gt; result4 = StringUtil.asList(src4, ','); 
     * //result4 = "a"
     * 
     * String src5 = "";
     * List&lt;String&gt; result5 = StringUtil.asList(src5, ','); 
     * //result5 = Empty List without any element
     * 
     * String src6 = ",";
     * List&lt;String&gt; result6 = StringUtil.asList(src6, ','); 
     * //result6 = Empty List without any element
     * </pre>
     * 
     * @param src String object to convert
     * @param sp delimiter
     * @param removeEmptyElement boolean to indicate if remove empty element from list.
     * @return List of string after conversion based on specified
     *         delimiter
     */
    public static List<String> asList(String src, char sp, boolean removeEmptyElement) {
        List<String> rs = new ArrayList<>();
        
        if(!isNullOrBlank(src)) {
            StringBuilder buf = new StringBuilder();
            for(int i = 0; i < src.length(); i++) {
                char c = src.charAt(i);
                if(c == sp) {
                    if(removeEmptyElement) {
                        if(!isNullOrBlank(buf.toString())) {
                            rs.add(buf.toString().trim());
                            buf.delete(0, buf.length());
                        }
                    } else {
                        if(buf.length() > 0) {
                            rs.add(buf.toString());
                            buf.delete(0, buf.length());
                        } else {
                            rs.add("");
                        }
                    }
                    
                } else {
                    buf.append(c);
                }
            }
            
            if(removeEmptyElement) {
                if(!isNullOrBlank(buf.toString())) {
                    rs.add(buf.toString().trim());
                }
            } else {
                if(buf.length() > 0) {
                    rs.add(buf.toString());
                } else {
                    rs.add("");
                }
            }
        }
        
        return rs.isEmpty() ? null : rs;
    }
    
    /**
     * Convert a string with specified delimiter to be a list, and
     * the delimiter in the content can be escaped.
     * 
     * <p>If dellimiter is not found in the string, then string 
     * object is the unique element of list; If it is NULL or 
     * blank space string, then an empty list is generated.
     * 
     * <p>If there is Escaped charactor before a delimiter in the 
     * string, then the "delimiter" is not thought as a delimiter 
     * to split.
     * <p>If there is Escaped charactor before another escaped 
     * charactor in the string, then the second is not thought as 
     * a Escaped charactor to escape.
     * 
     * <p>Example:
     * <pre>
     * String src1 = "a//,b,/,c";
     * List&lt;String&gt; result1 = StringUtil.asList(src1, ',', '/'); 
     * //result1 = "a", "b", ",c"
     * 
     * String src2 = "a//,b,///,c";
     * List&lt;String&gt; result2 = StringUtil.asList(src2, ',', '/'); 
     * //result2 = "a/" ,"b", "/,c"
     * </pre>
     * 
     * @param src String object to convert
     * @param sp delimiter, which must be differnt form escaped charactor
     * @param escape Escaped char, which must be differnt form delimiter
     * @return List of string after conversion based on specified
     *         delimiter
     */
    public static List<String> asList(String src, char sp, char escape) {
        if(sp == escape) {
            throw new IllegalArgumentException("Delimiter must be different form escaped charactor");
        }
        
        List<String> rs = new ArrayList<>();
        
        if(!isNullOrBlank(src)) {
            StringBuilder buf = new StringBuilder();
            int length = src.length();
            int count = 0;
            while(count < length) {
                char c = src.charAt(count);
                if(c == escape) {
                    if(count == length - 1) {
                        buf.append(c);
                    } else {
                        char n = src.charAt(++count);
                        if(n == sp || n == escape) {
                            buf.append(n);
                        } else {
                            buf.append(c).append(n);
                        }
                    }
                } else if (c == sp) {
                    if(buf.length() > 0) {
                        rs.add(buf.toString());
                        buf.delete(0, buf.length());
                    } else {
                        rs.add("");
                    }
                } else {
                    buf.append(c);
                }
                count++;
            }
            if(buf.length() > 0) {
                rs.add(buf.toString());
            } else {
                rs.add("");
            }
        }
        
        return rs.isEmpty() ? null : rs;
    }
    
    /**
     * Joint a string array to be a string with the specified delimiter
     * 
     * <p>If string array is a null object, then result is a blank space.
     * Any blank space element in the array is NOT ignored
     * 
     * <p>Example:
     * <pre>
     * String[] src1 = new String[]{"a", "b", "c"};
     * String result1 = StringUtil.arrayToString(src1, ',');
     * //result1 = "a,b,c"
     * 
     * String[] src2 = new String[]{"a", "", "c"};
     * String result2 = StringUtil.arrayToString(src2, '~');
     * //result2 = "a~~c"
     * 
     * String[] src3 = new String[]{"a"};
     * String result3 = StringUtil.arrayToString(src3, ',');
     * //result3 = "a"
     * 
     * String[] src4 = null;
     * String result4 = StringUtil.arrayToString(src4, ',');
     * //result4 = ""
     * </pre>
     * 
     * @param sa String array to joint
     * @param sp Delimiter
     * @return Jointted string with the specified delimiter
     */
    public static String arrayToString(String[] sa, char sp) {
        if(sa == null || sa.length == 0) {
            return "";
        } else {
            if(sa.length == 1) {
                return sa[0];
            } else {
                StringBuilder buf = new StringBuilder();
                for(int i = 0; i < sa.length; i++) {
                    buf.append(sa[i]);
                    if(i < sa.length - 1) {
                        buf.append(sp);
                    }
                }
                return buf.toString();
            } 
        }
    }
    
    /**
     * Joint a string list to be a string with the specified delimiter
     * 
     * <p>If string list is a null or empty object, then result is a 
     * blank space. Any blank space element in the array is NOT ignored.
     * 
     * <p>Example:
     * <pre>
     * List&lt;String&gt; src1 = new ArrayList&lt;&gt;();
     * src1.add("a");
     * src1.add("b");
     * src1.add("c");
     * String result1 = StringUtil.arrayToString(src1, ',');
     * //result1 = "a,b,c"
     * 
     * List&lt;String&gt; src2 = new ArrayList&lt;&gt;();
     * src2.add("a");
     * src2.add("");
     * src2.add("c");
     * String result2 = StringUtil.arrayToString(src2, '~');
     * //result2 = "a~~c"
     * 
     * List&lt;String&gt; src3 = new ArrayList&lt;&gt;();
     * src3.add("a");
     * String result3 = StringUtil.arrayToString(src3, ',');
     * //result3 = "a"
     * 
     * List&lt;String&gt; src4 = null;
     * String result4 = StringUtil.arrayToString(src4, ',');
     * //result4 = ""
     * </pre>
     * 
     * @param sa String array to joint
     * @param sp Delimiter
     * @return Jointted string with the specified delimiter
     */
    public static String arrayToString(List<String> sa, char sp) {
        if(sa == null || sa.isEmpty()) {
            return "";
        } else {
            int length = sa.size();
            if(length == 1) {
                return sa.get(0);
            } else {
                StringBuilder buf = new StringBuilder();
                sa.forEach(ele -> buf.append(ele).append(sp));
                return buf.deleteCharAt(buf.length() - 1).toString();
            } 
        }
    }
    
    /**
     * Add the elements in the array into a set object, the duplicated is added once 
     * and Null/Blank string is ignored 
     * 
     * @param sa String array to set
     * @return Set to include the element in the array
     */
    public static Set<String> arrayToSet(String[] sa) {
        Set<String> rs = new HashSet<>(); 
        if(sa != null && sa.length > 0) {
            for(String ele : sa) {
                if(isNullOrBlank(ele)) {
                    continue;
                }
                rs.add(ele);
            }
        }
        return rs.isEmpty() ? null : rs;
    }
    
    /**
     * Check whether all characters in a string are visible.
     * <p>Visible character range from ACSII code 32 to 126
     * 
     * @param src Checking string
     * @return true if all characters are visible, otherwise false
     */
    public static boolean allVisibleCharacter(String src) {
        StringBuilder regex = new StringBuilder();
        regex.append("[").append((char)32).append("-").append((char)126).append("]*");
        return Pattern.matches(regex.toString(), src);
    }
}
