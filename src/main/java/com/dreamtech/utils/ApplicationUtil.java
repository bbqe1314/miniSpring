package com.dreamtech.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ApplicationUtil {

    private final static String[] hexArray = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    /**
     * 获取某个类的extends类的单个泛型
     * 比如 A extends B<C> 则return C
     *
     * @param cls 类
     * @return 泛型名称
     */
    public static String getSingleGenericNameFromEtd(Class cls) {
        Type type = cls.getGenericSuperclass();
        //将type强转成Parameterized
        ParameterizedType pt = (ParameterizedType) type;
        Type[] actualTypes = pt.getActualTypeArguments();
        return actualTypes[0].getTypeName();
    }

    /**
     * 获取某个类的implements类的单个泛型
     * 比如 A implements B<C> 则return C
     *
     * @param cls 类
     * @return 泛型名称
     */
    public static String getSingleGenericNameFromImpl(Class cls) {
        Type[] type = cls.getGenericInterfaces();
        ParameterizedType pt = (ParameterizedType) type[0];
        Type[] actualTypes = pt.getActualTypeArguments();
        return actualTypes[0].getTypeName();
    }

    /**
     * 将字符串的第一个首字母大写
     *
     * @param str Str
     * @return str
     */
    public static String firstToLower(String str) {
        String first = str.substring(0, 1);
        String after = str.substring(1);
        first = first.toLowerCase();
        return first + after;
    }

    /**
     * inputStreamToByteArrayOutputStream
     *
     * @param inputStream inputStream
     * @return ByteArrayOutputStream
     * @throws IOException IO
     */
    public static ByteArrayOutputStream inputStreamToByteArrayOutputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buffer = new byte[inputStream.available()];
        int len;

        while ((len = inputStream.read(buffer)) > -1) {
            baos.write(buffer, 0, len);
        }
        baos.flush();
        return baos;
    }

    /**
     * 获取当前时间
     * 格式： 2020-01-31-15：30
     *
     * @return 时间
     */
    public static String currentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    /**
     * bufferedReaderToString
     *
     * @param br br
     * @return String
     * @throws IOException IO
     */
    public static String bufferedReaderToString(BufferedReader br) throws IOException {
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            builder.append(line);
        }
        return builder.toString();
    }

    /**
     * 对指定的字符串进行MD5加密
     *
     * @param str 原始字符串
     * @return 加密后的md5  小写 32位
     */
    public static String strToMd5(String str) {
        try {
            //创建具有MD5算法的信息摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            //使用指定的字节数组对摘要进行最后更新，然后完成摘要计算
            byte[] bytes = md.digest(str.getBytes());
            //将得到的字节数组变成字符串返回
            String s = byteArrayToHex(bytes);
            return s.toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 将字节数组转换成十六进制，并以字符串的形式返回
     * 128位是指二进制位。二进制太长，所以一般都改写成16进制，
     * 每一位16进制数可以代替4位二进制数，所以128位二进制数写成16进制就变成了128/4=32位。
     */
    private static String byteArrayToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(byteToHex(b));
        }
        return sb.toString();
    }

    /**
     * 将一个字节转换成十六进制，并以字符串的形式返回
     */
    private static String byteToHex(byte b) {
        int n = b;
        if (n < 0)
            n = n + 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexArray[d1] + hexArray[d2];
    }


}
