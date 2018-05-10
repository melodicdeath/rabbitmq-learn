package melodicdeath;

import java.util.Map;

public class MessageHandler {
    //没有设置默认的处理方法的时候，方法名是handleMessage
    public void handleMessage(byte[] message){
        System.out.println("---------handleMessage-------------");
        System.out.println(new String(message));
    }


    //通过设置setDefaultListenerMethod时候指定的方法名
    public void onMessage(String message){
        System.out.println("---------onMessage-------------");
        System.out.println(message);
    }

    //以下指定不同的队列不同的处理方法名
    public void onMessage2(String message){
        System.out.println("---------onMessage2-------------");
        System.out.println(message);
    }

    public void onMessage(byte[] message){
        System.out.println("---------onMessage_byte-------------");
        System.out.println(new String(message));
    }

    //以下指定不同的队列不同的处理方法名
    public void onMessage2(byte[] message){
        System.out.println("---------onMessage2_byte-------------");
        System.out.println(new String(message));
    }

    public void onMessage2(Map message){
        System.out.println("---------onMessage2_map-------------");
        System.out.println(message.toString());
    }

    public void onMessage2(Order message){
        System.out.println("---------onMessage2_map-------------");
        System.out.println(message.toString());
    }
}
