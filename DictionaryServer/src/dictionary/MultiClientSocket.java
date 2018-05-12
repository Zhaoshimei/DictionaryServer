package dictionary;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;


public class MultiClientSocket extends Frame {
    TextField tfTxt = new TextField(60);
    TextField tfTxt2 = new TextField(60);
    TextArea taContent = new TextArea();
    Socket socket = null;
    DataOutputStream dos = null;
    String line = "";
    DataInputStream dis = null;

    /**
     * Constructs a new instance of <code>Frame</code> that is
     * initially invisible.  The title of the <code>Frame</code>
     * is empty.
     *
     * @throws HeadlessException when
     *                           <code>GraphicsEnvironment.isHeadless()</code> returns <code>true</code>
     * @see GraphicsEnvironment#isHeadless()
     * @see Component#setSize
     * @see Component#setVisible(boolean)
     */
    public MultiClientSocket() throws HeadlessException {
        super("dictionary");
    }

    public static void main(String[] args) {
        new MultiClientSocket().lanuchFrame();
    }

    private void lanuchFrame() {
        Panel p1 = new Panel(new FlowLayout(0,5,5));
        Panel p2 = new Panel(new FlowLayout(0,5,5));
        Panel p3 = new Panel(new GridLayout(2,1,5,5));
        Panel buttonP  = new Panel(new GridLayout(1,2,10,10));
        Panel buttonP2  = new Panel(new GridLayout(1,2,10,10));
        setLocation(400, 300);
        this.setSize(500, 400);
        taContent.setFont(new Font("",Font.BOLD,20));
        Button bSearch = new Button("Search");
        Button bDelete = new Button("Delete");
        Button bAdd = new Button("Add");
        bSearch.addActionListener(new SearchMoniter());
        bDelete.addActionListener(new DeleteMoniter());
        bAdd.addActionListener(new AddMoniter());

        buttonP.add(bSearch);
        buttonP.add(bDelete);
        buttonP2.add(bAdd);
        tfTxt.setSize(200,300);
        p1.add(new Label("   word    "));
        p1.add(tfTxt);
        p1.add(buttonP);
        p2.add(new Label("meaning"));
        p2.add(tfTxt2);
        p2.add(buttonP2);
        p3.add(p1);
        p3.add(p2);
        add(p3,BorderLayout.SOUTH);
        add(taContent, BorderLayout.NORTH);
        pack();
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnect();
                System.exit(0);
            }
        });
        tfTxt.addActionListener(new TFListener());
        setVisible(true);
        connect();

        while (!socket.isClosed()){
            try {
                String str = dis.readUTF();
                taContent.append(getMeaning(str));
            } catch (SocketException se) {
                System.out.println("The client is quit");
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void disconnect() {
        try {
            dos.writeUTF("#quit");
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connect() {
        try {
            socket = new Socket("127.0.0.1", 8888);
            // 获取输出流向服务端写入数据
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getMeaning(String wordMeaning) {
        String serverStr = "\nServer:\n★★★ The meaning ★★★\n";
        String[] texts = wordMeaning.split("\\\\n");
        for(String line : texts){
            serverStr = serverStr + line + "\n";
        }
        return serverStr;
    }

    private class TFListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String str = tfTxt.getText().trim();
            taContent.append("Client:" + str);
            tfTxt.setText("");
            try {
                dos.writeUTF("#search#"+str);
                dos.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
    private class SearchMoniter implements ActionListener{

        /**
         * Invoked when an action occurs.
         *
         * @param e
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            String str = tfTxt.getText().trim();
            taContent.append("Client:" + str);
            tfTxt.setText("");
            try {
                dos.writeUTF("#search#"+str);
                dos.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
    private class DeleteMoniter implements ActionListener{

        /**
         * Invoked when an action occurs.
         *
         * @param e
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            String str = tfTxt.getText().trim();
            taContent.append("Client:" + str);
            tfTxt.setText("");
            try {
                dos.writeUTF("#delete#"+str);
                dos.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
    private class AddMoniter implements ActionListener{

        /**
         * Invoked when an action occurs.
         *
         * @param e
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            String str = tfTxt.getText().trim();
            String str2 = tfTxt2.getText().trim();
            taContent.append("Client:" + str + ":" + str2);
            tfTxt.setText("");
            tfTxt2.setText("");
            try {
                dos.writeUTF("#add#"+str+ ":" + str2);
                dos.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}