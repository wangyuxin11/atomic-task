package com.wanda.base.task.utils;

/**
 * description:
 * @author senvon
 * time : 2013-1-28 上午9:23:02
 */
public class StringByteUtils {
    /**
     * 计算utf-8字节长度,0-127加1,其他加3
     * @param s
     * @return
     */
    public static int calculateUtf8Bytes(String s) {
        if (s == null) {
            return 0;
        }
        int byteSize = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) >= 0 && s.charAt(i) <= 127) {
                byteSize++;
            } else {
                byteSize += 3;
            }
        }
        return byteSize;
    }

    /**
     * 按字节数截取字符,0-127算1个字节,其他算3个字节
     * @param s
     * @param subByteSize
     * @return
     */
    public static String subUtf8String(String s, int subByteSize) {
        if (s == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int appendedSize = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) >= 0 && s.charAt(i) <= 127) {
                appendedSize++;
            } else {
                appendedSize += 3;
            }
            if (appendedSize <= subByteSize) {
                sb.append(s.charAt(i));
            } else {
                break;
            }
        }
        return sb.toString();
    }
}
