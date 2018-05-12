package dictionary;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class MultiServerSocket {
    static HashMap dictionaryMap = null;

    private static ServerSocket SERVER_SOCKET =null;;

    static{
        try {
            SERVER_SOCKET = new ServerSocket(8888);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MultiServerSocket mss = new MultiServerSocket();
        mss.initDicMap();
        try {
            System.out.println("******服务器已启动，等待客户端连接*****");
            Socket socket = null;
            while(true){
                //循环监听客户端的连接
                socket = SERVER_SOCKET.accept();
                //新建一个线程ServerSocket，并开启
                new ServerSocketThread(socket,dictionaryMap).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initDicMap() {
        dictionaryMap = new HashMap();
        String line = null;
        try {
            File file = new File("src/dictionary/collins.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));

            line = br.readLine();
            while(line != null){
                Scanner in = new Scanner(line);
                String word = in.next();
                int offset = word.length();
                String wordMeaning = line.substring(offset);
                dictionaryMap.put(word,wordMeaning);
                line = br.readLine();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}