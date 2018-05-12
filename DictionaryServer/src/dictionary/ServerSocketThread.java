package dictionary;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class ServerSocketThread extends Thread {

    private Socket socket;
    private HashMap dictionaryMap;
    DataInputStream dis = null;
    DataOutputStream dos = null;

    public ServerSocketThread(Socket socket) {
        this.socket = socket;
    }
    public ServerSocketThread(Socket socket, HashMap dictionaryMap){
        this.socket = socket;
        this.dictionaryMap = dictionaryMap;
    }

    @Override
    public void run() {
        String line = null;
        try {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            String clientMessage = dis.readUTF();
            String[] inputMsgs = clientMessage.split("#");
            String funMsg = inputMsgs[1];
            sendMeaning(inputMsgs[2],funMsg);
            //socket.shutdownOutput();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (dis != null) {
                    dis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (dos != null) {
                    dos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteWord(String inputMsg) {
        String line = null;
        System.out.println("Client:" + inputMsg);
        while (inputMsg!= null && !inputMsg.equals("#quit")){
            String[] strings = inputMsg.split(" ");
            if(strings.length > 1 || !isLetter(inputMsg)){
                line = "Wrong Input.  You should input a word.";
            }else if(dictionaryMap.containsKey(inputMsg)){
                dictionaryMap.remove(inputMsg);
                line = inputMsg + " is deleted";
            }else {
                line = "Sorry,can't find the meaning of this word";
            }
            try {
                dos.writeUTF(line);
                dos.flush();
                printRes(line);
                inputMsg = dis.readUTF();
                System.out.println("Client:" + inputMsg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addWord(String wordMsg) {
        String line = null;
        String[] addMsgs = wordMsg.split(":");
        String inputMsg = addMsgs[0];
        String inputMeaning = addMsgs[1];
        System.out.println("Client:" + wordMsg);
        while (inputMsg!= null && !inputMsg.equals("#quit")){
            String[] strings = inputMsg.split(" ");
            if(strings.length > 1 || !isLetter(inputMsg)){
                line = "Wrong Input.  You should input a word.";
            }else if(dictionaryMap.containsKey(inputMsg)){
                line = "Sorry, " + inputMsg + " has been in this dictionary";
            }else {
                dictionaryMap.put(inputMsg,inputMeaning);
            }
            try {
                if(dos == null){
                    System.out.println("dos is null");
                }
                dos.writeUTF(line);
                dos.flush();
                printRes(line);
                inputMsg = dis.readUTF();
                System.out.println("Client:" + inputMsg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMeaning(String wordMsg,String funcMsg) {
        String line = null;
        System.out.println("Client:" + wordMsg);
        String[] addMsgs = wordMsg.split(":");
        String inputMsg = addMsgs[0];
        while (inputMsg!= null && !inputMsg.equals("#quit")){
            String[] strings = inputMsg.split(" ");
            if(strings.length > 1 || !isLetter(inputMsg)){
                line = "Wrong Input.  You should input a word.";
            }else if(dictionaryMap.containsKey(inputMsg)&&funcMsg.equals("search")){
                line = (String) dictionaryMap.get(inputMsg);
            }else if(dictionaryMap.containsKey(inputMsg)&&funcMsg.equals("delete")){
                dictionaryMap.remove(inputMsg);
                line = inputMsg + " is deleted";
            }else if(dictionaryMap.containsKey(inputMsg)&&funcMsg.equals("add")){
                line = "Sorry, " + inputMsg + " has been in this dictionary";
            }else if(funcMsg.equals("add")){
                if(addMsgs.length>1){
                    String inputMeaning = addMsgs[1];
                    dictionaryMap.put(inputMsg,inputMeaning);
                }
                line = inputMsg + "is added";
            }else {
                line = "Sorry, can't find the meaning.";
            }
            try {
                dos.writeUTF(line);
                dos.flush();
                printRes(line);
                String clientMessage = dis.readUTF();
                String[] inputMsgs = clientMessage.split("#");
                funcMsg = inputMsgs[1];
                wordMsg = inputMsgs[2];
                addMsgs = wordMsg.split(":");
                inputMsg = addMsgs[0];
                System.out.println("Client:" + clientMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void printRes(String line) {
        System.out.println("Server:");
        String[] texts = line.split("\\\\n");
        for(String l : texts){
            System.out.println(l);
        }
    }

    public String fomatText(String text){
        String[] texts = text.split("\\\\n");
        String stanText = null;
        for(String line : texts){
            stanText += line + "\n";
        }
        return stanText;
    }

    public static boolean isLetter(String str) {
        for (int i = str.length(); --i >= 0; ) {
            int chr = str.charAt(i);
            if (chr < 65 || (chr > 90 && chr < 97) || chr > 122)
                return false;
        }
        return true;
    }
}