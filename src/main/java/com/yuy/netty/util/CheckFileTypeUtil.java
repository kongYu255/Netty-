package com.yuy.netty.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CheckFileTypeUtil {
    private static final Map<String, String> fileType = new HashMap<>();

    static {
        fileType.put("FFD8FFE0", "jpg,jpeg");
        fileType.put("89504E47", "png");
        fileType.put("47494638", "gif");
        fileType.put("D0CF11E0", "doc");
        fileType.put("504B0304", "docx");
        fileType.put("25504446", "pdf");
        fileType.put("31323331", "txt");
        fileType.put("00000000", "空文件");
    }

    public static String getFileTypeByFilePath(String filePath) {
        return fileType.get(getFileHeader(filePath));
    }

    public static String getFileTypeByBytes(byte[] bytes) {
        if (bytes.length <= 4) {
            return fileType.get(bytesToHexString(bytes));
        }
        byte[] src = new byte[4];
        for (int i = 0; i < 4; i++) {
            src[i] = bytes[i];
        }
        return fileType.get(bytesToHexString(src));
    }

    private static String getFileHeader(String filePath) {
        FileInputStream inputStream = null;
        String value = null;
        try {
            inputStream = new FileInputStream(filePath);
            byte[] b = new byte[4];
            inputStream.read(b, 0, b.length);
            value = bytesToHexString(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

    /**
     * @param src 要读取文件头信息的文件的byte数组
     * @return 文件头信息
     * @author wlx
     * <p>
     * 方法描述：将要读取文件头信息的文件的byte数组转换成string类型表示
     */
    private static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (byte aSrc : src) {
            // 以十六进制（基数 16）无符号整数形式返回一个整数参数的字符串表示形式，并转换为大写
            hv = Integer.toHexString(aSrc & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        return builder.toString();
    }

    public static void main(String[] args) {

    }

}