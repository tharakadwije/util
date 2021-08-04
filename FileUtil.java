/*
 * Copyright (c) 2016. BTGD. All Rights Reserved.
 *
 * Class                 : IOUtil.java
 * Description           : Provide a series of IO Utility APIs 
 *
 * Modification History
 * Date                    Modifier Name                Reason
 * ---------                ------------              -------------
 * 14-Nov-2016             Yang Shengong               Initial version
 *
 * @version 1.0
 * @since release 1.0
 * @author Yang Shengong
 */

package com.psa.pc.fw.ac.util;

import com.psa.pc.fw.ac.constant.PCConstants;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.List;

/**
 * Class {@code FileUtil} provides a series of common IO 
 * Utility APIs so that the handling associated with IO operation
 * can be simplified and reused in PC
 * 
 * <p> All APIs are static method, without any object instance 
 * required.
 * 
 * @author Yang Shengong
 * @version 1.0
 */

public class FileUtil {
    /** Default buffer size for IO operation */
    private static int BUFFER_SIZE = 1024;
    
    /** Private Constructor to avoid the instance of the object*/
    private FileUtil() {}
    
    /**
     * Read data from Input stream and then write into byte array
     * 
     * @param is InputStream
     * @return Byte Array from Input stream
     * 
     * @throws IOException if read byte code from input stream and 
     *         write byte code into output stream
     */
    public static byte[] writeToByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buf = new byte[BUFFER_SIZE];
        int count = -1;
        while (is.available() > 0) {
            count = is.read(buf);
            os.write(buf, 0, count);
        }
        os.close();
        return os.toByteArray();
    }

    /**
     * Read data from a file and then write into byte array
     * 
     * @param file File to read
     * @return Byte Array from Input stream
     * 
     * @throws IOException when read byte code from input stream and 
     *         write byte code into output stream
     */
    public static byte[] writeToByteArray(File file) throws IOException {
        return writeToByteArray(new FileInputStream(file));
    }
    
    /**
     * Write object list into the specified file with Jackson format; Any content existed in the file
     * will be cleared before write new content.
     * 
     * <p>If object contain child object, 
     * then the GETTER API to get parent object in the child must add 
     * 'JsonIgnore' Annotation
     * If cannot converse Object to Json, then file is removed.
     * 
     * @param objLst, Object List
     * @param file - If file doesn't exist, then nothing to do
     * 
     * @throws Exception when read byte code from input stream and 
     *         write byte code into output stream or fail to convert object
     *         to Json format
     */
    public static void writeJsonToFile(List<Object> objLst, File file) throws Exception {
        writeJsonToFile(objLst, file, false);
    }
    
    /**
     * Write object list into the specified file with Jackson format; Any content existed in the file
     * will be retained with APPEND mode.
     * 
     * <p>If object contain child object, 
     * then the GETTER API to get parent object in the child must add 
     * 'JsonIgnore' Annotation
     * If cannot converse Object to Json, then file is removed.
     * 
     * @param objLst, Object List
     * @param file - If file doesn't exist, then nothing to do
     * 
     * @throws Exception when read byte code from input stream and 
     *         write byte code into output stream or fail to convert object
     *         to Json format
     */
    public static void appendJsonToFile(List<Object> objLst, File file) throws Exception {
        writeJsonToFile(objLst, file, true);
    }
    
    private static void writeJsonToFile(List<Object> objLst, File file, boolean appended) throws Exception {
        if(ObjectUtil.isNullOrEmpty(objLst) || file == null) {
            return;
        }
        if(!file.exists() && !file.createNewFile() || file.isDirectory() || !file.canWrite()) {
            return;
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, appended);
            boolean isValid = true;
            for(Object ele : objLst) {
                String json = JsonUtil.toJson(ele);
                if(StringUtil.isNullOrBlank(json)) {
                    isValid = false;
                    break;
                }
                fos.write(json.getBytes());
                fos.write(PCConstants.LINE_SEPARATOR.getBytes());
            }
            
            if(!isValid) {
                file.delete();
                throw new Exception("Fail to Json Conversion");
            }
        } finally {
            if(fos != null) {
                fos.close();
            }
        }
    }
    
    /**
     * Write string into file
     * <p>If the file has existed, the original file content will be covered
     * 
     * @param content File content
     * @param file File
     * 
     * @throws IOException Exception is thrown if system hit IO error when write it to file
     */
    public static void writeStringToFile(String content, File file) throws IOException {
        if(StringUtil.isNullOrBlank(content) || file == null) {
            return;
        }
        if(!file.exists() && !file.createNewFile() || file.isDirectory() || !file.canWrite()) {
            return;
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(content.getBytes());
        } finally {
            if(fos != null) {
                fos.close();
            }
        }
    }
    
    /**
     * Get filename suffix
     * 
     * <p>If filepath is included in the filename, the API can throw 
     * IllegalArgumentException
     * 
     * @param filename Filename without filepath
     * @return Suffix of filename
     */
    public static String getFilenameSuffix(String filename) {
        String suffix = "";
        
        if(!StringUtil.isNullOrBlank(filename)) {
            if(filename.indexOf(File.pathSeparatorChar) > -1) {
                throw new IllegalArgumentException("Invalid filename.");
            }
            List<String> segement = StringUtil.asList(filename, '.');
            int size = segement.size();
            if(size > 1) {
                suffix = segement.get(size - 1);
            }
        }
        
        return suffix;
    }
}
