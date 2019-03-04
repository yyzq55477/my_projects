package com.zjtzsw.embed.paas.sdk;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class BASE64Utils {
    /**
     * 文件转换为Base64字符串
     * @param imgFile
     * @return
     */
    public static String fileToBase64(String File)  
    {
        //String imgFile = "d://test.jpg";//待处理的图片  
        //InputStream in = null;  
        byte[] data = null;  
        //读取图片字节数组  
        try(InputStream in = new FileInputStream(File))   
        {
            data = new byte[in.available()];  
            in.read(data);  
            //in.close();  
        }
        catch (IOException e)   
        {  
            e.printStackTrace();  
        }  
        //对字节数组Base64编码  
        BASE64Encoder encoder = new BASE64Encoder();  
        return encoder.encode(data);//返回Base64编码过的字节数组字符串  
    }  
      
    /**
     * base64字符串转换成文件
     * @param imgStr
     * @return
     */
    public static boolean base64ToFile(String base64Str,String filePath)  
    {   
        if (base64Str == null) //图像数据为空  
            return false;  
        BASE64Decoder decoder = new BASE64Decoder();  
        try   
        {  
            //Base64解码  
            byte[] b = decoder.decodeBuffer(base64Str);  
            for(int i=0;i<b.length;++i)  
            {  
                if(b[i]<0)  
                {//调整异常数据  
                    b[i]+=256;  
                }  
            }
            //生成jpeg图片  
            //String imgFilePath = "d://222.jpg";//新生成的图片  
            OutputStream out = new FileOutputStream(filePath);      
            out.write(b);  
            out.flush();  
            out.close();  
            return true;  
        }   
        catch (Exception e)   
        {  
            return false;  
        }
    }  

}
