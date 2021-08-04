/*
 * Copyright (c) 2016. BTGD. All Rights Reserved.
 *
 * Class                 : SQLUtil.java
 * Description           : Provide a series of SQL Utility APIs 
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

import com.psa.pc.fw.ac.constant.PCConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Class {@code SQLUtil} provides a series of common SQL 
 * Utility APIs so that the handling associated with SQL operation 
 * can be simplified and reused in PCP
 * 
 * <p> All APIs are static method, without any object instance 
 * required.
 * 
 * @author Yang Shengong
 * @version 1.0
 */

public class SQLUtil {
    /** Private Constructor to avoid the instance of the object*/
    private SQLUtil() {}
    
    /**
     * Construct a SQL IN clause, which is nested into another SQL
     * 
     * <p>Example:
     * <pre>
     * List&lt;String&gt; condLst1 = ...//elements: "1", "2", "3"
     * String result1 = SQLUtil.toINClause("o.ID", condLst1);
     * //result1 = o.ID IN ("1", "2", "3")
     * </pre>
     * 
     * @param column Column name including alias
     * @param cond List of condition to construct IN clause
     * @return SQL IN clause
     */
    public static final <T> String toINClause(String column, List<T> cond) {
        if(StringUtil.isNullOrBlank(column) || ObjectUtil.isNullOrEmpty(cond)) {
            return "";
        }
        StringBuilder buf = new StringBuilder();
        buf.append(column).append(" IN (").append(toINSubClause(cond)).append(')');
        return buf.toString();
    }
    
    /**
     * Construct a SQL NOT IN clause, which is nested into another SQL
     * 
     * <p>Example:
     * <pre>
     * List&lt;String&gt; condLst1 = ...//elements: "1", "2", "3"
     * String result1 = SQLUtil.toNotINClause("o.ID", condLst1);
     * //result1 = o.ID NOT IN ("1", "2", "3")
     * </pre>
     * 
     * @param column Column name including alias
     * @param cond List of condition to construct IN clause
     * @return SQL NOT IN clause
     */
    public static final <T> String toNotINClause(String column, List<T> cond) {
        if(StringUtil.isNullOrBlank(column) || ObjectUtil.isNullOrEmpty(cond)) {
            return "";
        }
        StringBuilder buf = new StringBuilder();
        buf.append(column).append(" NOT IN (").append(toINSubClause(cond)).append(')');
        return buf.toString();
    }
    
    /**
     * Constuct batch IN SQL clause which is nested into another SQL
     * 
     * <p>If batch size is less than 1, the size is reset to be 500
     * 
     * <p>Example:
     * <pre>
     * List&lt;String&gt; condLst1 = ...//elements: "1", "2", "3", "4", "5"
     * List&lt;String&gt; result1 = SQLUtil.toBatchINClause("o.ID", condLst1, 2);
     * //result1 = o.ID IN ('1', '2'), o.ID IN ('3', '4'), o.ID IN('5')
     * </pre>
     * 
     * @param column Column name including alias
     * @param cond List of condition to construct IN clause
     * @param batchSize Batch size
     * @return List of SQL IN clause
     */
    public static final <T> List<String> toBatchINClause(String column, List<T> cond, int batchSize) {
        if(StringUtil.isNullOrBlank(column) || ObjectUtil.isNullOrEmpty(cond)) {
            return new ArrayList<>();
        }
        if(batchSize < 1) {
            batchSize = 500;
        }
        int size = cond.size();
        int total = size % batchSize == 0 ? size / batchSize : size / batchSize + 1;
        int count = 0;
        List<String> inLst = new ArrayList<>();
        while(count < total) {
            int from = count * batchSize;
            int to = count < total - 1 ? (count + 1) * batchSize : size;
            List<T> lst = cond.subList(from, to);
            StringBuilder buf = new StringBuilder();
            buf.append(column).append(" IN (").append(toINSubClause(lst)).append(')');
            inLst.add(buf.toString());
            count++;
        }
        return inLst;
    }
    
    /**
     * Construct IN clause content, key words 'IN' and brackets are not included
     * 
     * <p>Example:
     * <pre>
     * List&lt;String&gt; condLst1 = ...//elements: "a", "b", "c", "d", "e"
     * List&lt;String&gt; result1 = SQLUtil.toINSubClause(condLst1);
     * //result1 = 'a', 'b', 'c', 'd', 'e'
     * </pre>
     * 
     * @param cond List of condition to construct IN clause
     * @return List of SQL IN clause content
     */
    public static <T> String toINSubClause(List<T> cond) {
        StringBuilder buf = new StringBuilder();
        cond.forEach((ele) -> {
                     buf.append("'").append(escapeQuotes(ObjectUtil.toString(ele))).append("', ");
                     });
        buf.delete(buf.length() - 2, buf.length());
        return buf.toString();
    }
    
    /**
     * Construct a SQL TO clause, which is nested into another SQL
     * 
     * <p>Example:
     * <pre>
     * List&lt;BigDecimal&gt; condLst1 = ...//elements: 1, 2, 3
     * String result1 = SQLUtil.toORClause("o.ID", condLst1);
     * //result1 = (o.ID = 1 OR o.ID = 2 OR o.ID = 3)
     * </pre>
     * 
     * @param column COLUMN name including alias
     * @param cond List of condition to construct OR clause
     * @return SQL OR clause
     */
    public static final <T extends Number> String toORClause(String column, List<T> cond) {
        if(StringUtil.isNullOrBlank(column) || ObjectUtil.isNullOrEmpty(cond)) {
            return "";
        }
        StringBuilder buf = new StringBuilder();
        cond.forEach((ele) -> {
                     buf.append(column).append(" = ").append(ele).append(" OR ");
                     });
        buf.delete(buf.length() - 4, buf.length()).insert(0, "(").append(")");
        return buf.toString();
    }
    
    /**
     * Construct a SQL TO clause, which is nested into another SQL
     * 
     * <p>Example:
     * <pre>
     * List&lt;String&gt; condLst1 = ...//elements: "AA","BB","CC"
     * String result1 = SQLUtil.toORClause("o.ID", condLst1);
     * //result1 = (o.ID LIKE '%AA%' OR o.ID LIKE '%BB%' OR o.ID LIKE '%CC%')
     * </pre>
     * 
     * @param column COLUMN name including alias
     * @param cond List of condition to construct OR clause
     * @return SQL OR clause
     */
    public static final <T> String toORLikeClause(String column, List<T> cond) {
        if(StringUtil.isNullOrBlank(column) || ObjectUtil.isNullOrEmpty(cond)) {
            return "";
        }
        StringBuilder buf = new StringBuilder();
        cond.forEach((ele) -> {
                     buf.append(toLikeClause(column,escapeQuotes(ObjectUtil.toString(ele)))).append(" OR ");
                     });
        buf.delete(buf.length() - 4, buf.length()).insert(0, "(").append(")");
        return buf.toString();
    }
    
    
    
    
     /**
      * Constuct batch OR SQL clause which is nested into another SQL
      * 
      * <p>If batch size is less than 1, the size is reset to be 500
      * 
      * <p>Example:
      * <pre>
      * List&lt;BigDecimal&gt; condLst1 = ...//elements: 1, 2, 3, 4, 5
      * List&lt;BigDecimal&gt; result1 = SQLUtil.toBatctORClause("o.ID", condLst1, 2);
      * //result1 = (o.ID = 1 OR o.ID = 2), (o.ID = 3 OR o.ID = 4), (o.ID = 5)
      * </pre>
      * 
      * @param column Column name including alias
      * @param cond List of condition to construct OR clause
      * @param batchSize Batch size
      * @return List of SQL OR clause
      */
    public static final <T extends Number> List<String> toBatchORClause(String column, List<T> cond, int batchSize) {
        if(StringUtil.isNullOrBlank(column) || ObjectUtil.isNullOrEmpty(cond)) {
            return new ArrayList<>();
        }
        if(batchSize < 1) {
            batchSize = 500;
        }
        int size = cond.size();
        int total = size % batchSize == 0 ? size / batchSize : size / batchSize + 1;
        int count = 0;
        List<String> inLst = new ArrayList<>();
        while(count < total) {
            int from = count * batchSize;
            int to = count < total - 1 ? (count + 1) * batchSize : size;
            List<T> lst = cond.subList(from, to);
            inLst.add(toORClause(column, lst));
            count++;
        }
        return inLst;
    }
    
    

    /**
     * Escape Quote (') when construct dynamic SQL
     * 
     * <p>Example:
     * <pre>
     * String src1 = "a'b";
     * String result1 = SQLUtil.escapeQuotes(src1); //result1 = "a''b"
     * </pre>
     * 
     * @param str String to escape quote
     * @return escaped string
     */
    public static final String escapeQuotes(String str) {
        return str.replaceAll("'", "''");
    }

     /**
      * Escape like charactor when construct dynamic SQL
      * 
      * <p>Notice that the API cannot generate the whole LIKE clause, 
      * just escape the special charactors (underline and percent sign) 
      * existed in value. Developer need to code more to construct required
      * LIKE clause.
      * 
      * <p>SQLUtil.toLikeClause(String src, String escapeChar) can 
      * construct a complete LIKE clause with some limitation.
      * 
      * <p>Example:
      * <pre>
      * String src1 = "a%b";
      * String result1 = SQLUtil.escapeLike(src1, "#"); //result1 = "a#%b"
      * 
      * String src2 = "_a%b'#c";
      * String result2 = SQLUtil.escapeLike(src2, "#"); //result2 = "#_a#%b''##c"
      * 
      * //Please refe to below source code to code a complete LIKE clause
      * String colM = "o.companyM";
      * String value = "AB%C";
      * String sql = colM + " LIKE '%" + SQLUtil.escapeLike(value, "#") + "%' ESCAPE '#'";
      * //sql = "o.companyM LIKE '%AB#%C%' ESCAPE '#'"
      * </pre>
      * 
      * @param src Value to escape like charactor
      * @param escapeChar Specified escaped charactor
      * @return escaped string
      * 
      * @see com.psa.pc.fw.ac.util.SQLUtil#toLikeClause(String, String) 
      */
    public static final String escapeLike(String src, String escapeChar) {
        return StringUtil.isNullOrBlank(src) ? src : src.replaceAll("'", "''")
                                                        .replaceAll(escapeChar, escapeChar + escapeChar)
                                                        .replaceAll("%", escapeChar + "%")
                                                        .replaceAll("_", escapeChar + "_");
    }

    /**
     * Construct a LIKE clause based on column name and value, however it is 
     * cater for a usual common scenario only - '%XXX%'. If there is requirement 
     * to cater for 'XXX%', '_XX' and so on, then developer has to refe to 
     * SQLUtil.escapeLike(String) to construct LIKE clause by case.
     * 
     * <p>Example:
     * <pre>
     * String colM = "o.companyM";
     * String value = "AB%C";
     * String sql = SQLUtil.toLikeClause(colM, value, MatchingMode.ANY);
     * //sql = "o.companyM LIKE '%AB#%C%' ESCAPE '#'" 
     * 
     * String colM = "o.companyM";
     * String value = "AB%C";
     * String sql = SQLUtil.toLikeClause(colM, value, MatchingMode.HEAD);
     * //sql = "o.companyM LIKE 'AB#%C%' ESCAPE '#'" 
     * 
     * String colM = "o.companyM";
     * String value = "AB%C";
     * String sql = SQLUtil.toLikeClause(colM, value, MatchingMode.TAIL);
     * //sql = "o.companyM LIKE '%AB#%C' ESCAPE '#'" 
     * </pre>
     * 
     * @param column Column name
     * @param value Value need to escape LIKE character
     * @param mode Matching mode
     * @return LIKE clause
     * 
     * @see com.psa.pc.fw.ac.util.SQLUtil#escapeLike(String, String) 
     */
    public static final String toLikeClause(String column, String value, MatchingMode mode) {
        if(StringUtil.isNullOrBlank(column) || StringUtil.isNullOrBlank(value)) {
            return "";
        }
        if(mode == null) {
            mode = MatchingMode.ANY;
        }
        StringBuilder buf = new StringBuilder();
        buf.append(column).append(" LIKE '")
           .append(!mode.isHead() ? "%" : PCConstants.EMPTY_STRING)
           .append(escapeLike(value, "#"))
           .append(!mode.isTail() ? "%" : PCConstants.EMPTY_STRING)
           .append("' ESCAPE '#'");
        return buf.toString();
    }
    
    /**
     * Construct a LIKE clause based on column name and value with ANY Matching Mode
     * 
     * @param column Column name
     * @param value Value need to escape LIKE character
     * @return LIKE clause
     */
    public static final String toLikeClause(String column, String value) {
        return toLikeClause(column, value, MatchingMode.ANY);
    }

    /**
     * Escape '&amp;' when construct dynamic SQL
     * 
     * @param src Value to escape '&amp;'
     * @return escaped string
     */
    public static final String escapeSplChars(String src) {
        return StringUtil.isNullOrBlank(src) ? src : src.replaceAll("'", "''").replaceAll("&", "\\&");
    }
    
    /**
     * Enum {@code MatchingMode} define three matching mode:
     * <p>HEAD, TAIL, ANY
     */
    public enum MatchingMode {
        HEAD("head"), TAIL("tail"), ANY("any");
        
        private String mode;
        
        private MatchingMode(String mode) {
            this.mode = mode;
        }
        
        private boolean isHead() {
            return this.equals(HEAD);
        }        
        private boolean isTail() {
            return this.equals(TAIL);
        }        
        private boolean isAny() {
            return this.equals(ANY);
        }
    }
}
