package hefu.pad.sdk.utils;

import java.io.UnsupportedEncodingException;

/**
 * Author by Winds on 2016/10/18.
 * Email heardown@163.com.
 */
public class ByteUtil {

    /**
     * 字节数组转换成对应的16进制表示的字符串
     *
     * @param src
     * @return
     */
    public static String bytes2HexStr(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return "";
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            builder.append(buffer);
        }
        return builder.toString().toUpperCase();
    }

    /**
     * 十六进制字节数组转字符串
     *
     * @param src    目标数组
     * @param dec    起始位置
     * @param length 长度
     * @return
     */
    public static String bytes2HexStr(byte[] src, int dec, int length) {
        byte[] temp = new byte[length];
        System.arraycopy(src, dec, temp, 0, length);
        return bytes2HexStr(temp);
    }

    /**
     * 16进制字符串转10进制数字
     *
     * @param hex
     * @return
     */
    public static long hexStr2decimal(String hex) {
        return Long.parseLong(hex, 16);
    }

    /**
     * 把十进制数字转换成足位的十六进制字符串,并补全空位
     *
     * @param num
     * @return
     */
    public static String decimal2fitHex(long num) {
        String hex = Long.toHexString(num).toUpperCase();
        if (hex.length() % 2 != 0) {
            return "0" + hex;
        }
        return hex.toUpperCase();
    }

    /**
     * 把十进制数字转换成足位的十六进制字符串,并补全空位
     *
     * @param num
     * @param strLength 字符串的长度
     * @return
     */
    public static String decimal2fitHex(long num, int strLength) {
        String hexStr = decimal2fitHex(num);
        StringBuilder stringBuilder = new StringBuilder(hexStr);
        while (stringBuilder.length() < strLength) {
            stringBuilder.insert(0, '0');
        }
        return stringBuilder.toString();
    }

    public static String fitDecimalStr(int dicimal, int strLength) {
        StringBuilder builder = new StringBuilder(String.valueOf(dicimal));
        while (builder.length() < strLength) {
            builder.insert(0, "0");
        }
        return builder.toString();
    }

    /**
     * 字符串转十六进制字符串
     *
     * @param str
     * @return
     */
    public static String str2HexString(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder();
        byte[] bs = null;
        try {

            bs = str.getBytes("utf8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
        }
        return sb.toString();
    }

    /**
     * 把十六进制表示的字节数组字符串，转换成十六进制字节数组
     *
     * @param
     * @return byte[]
     */
    public static byte[] hexStr2bytes(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toUpperCase().toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (hexChar2byte(achar[pos]) << 4 | hexChar2byte(achar[pos + 1]));
        }
        return result;
    }

    /**
     * 把16进制字符[0123456789abcde]（含大小写）转成字节
     *
     * @param c
     * @return
     */
    private static int hexChar2byte(char c) {
        switch (c) {
            case '0':
                return 0;
            case '1':
                return 1;
            case '2':
                return 2;
            case '3':
                return 3;
            case '4':
                return 4;
            case '5':
                return 5;
            case '6':
                return 6;
            case '7':
                return 7;
            case '8':
                return 8;
            case '9':
                return 9;
            case 'a':
            case 'A':
                return 10;
            case 'b':
            case 'B':
                return 11;
            case 'c':
            case 'C':
                return 12;
            case 'd':
            case 'D':
                return 13;
            case 'e':
            case 'E':
                return 14;
            case 'f':
            case 'F':
                return 15;
            default:
                return -1;
        }
    }

    /**
     * 字符串转字节数组
     *
     * @param str        要转换的字符串
     * @param charEncode 字符编码，如：UTF-8、GB2312，UTF-8编码一个汉字占三个字节，GB2312一个汉字占两个字节
     * @return 字节数组
     */
    public static byte[] stringToByte(String str, String charEncode) {
        byte[] destObj = null;
        try {
            if (null == str || str.trim().equals("")) {
                destObj = new byte[0];
                return destObj;
            } else {
                destObj = str.getBytes(charEncode);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return destObj;
    }
    /**
     * 字节转换成十六进制字符串
     * @param b 字节
     * @param split 分隔字符串，主要用于输出调试
     * @return
     */
    public static String byteToHexStr(byte b, String split) {
        String hs="";
        String stmp="";
        stmp=(Integer.toHexString(b & 0XFF));
        if (stmp.length()==1) hs=hs+"0"+stmp;
        else hs=hs+stmp;
        hs = hs + split;
        return hs.toUpperCase();
    }
    /**
     * int转换成byte
     *
     * @param number int型数据
     * @return byte数组
     */
    public static byte intToByte(int number) {
        int temp = number;
        byte b = new Integer(temp & 0xff).byteValue();
        return b;
    }
}
