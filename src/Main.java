import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.util.ArrayList;

import database.Card;
import database.Database;
import database.Const;



public class Main {
    public static void main (String[]args) {
        Database database = new Database("postgres", "system");
        Connection con = database.getConnection();
        new NewThread(1, 5001);
        new NewThread(2, 5002);
    }
}

class Network {
    private Socket s;
    private ServerSocket ss;
    private InputStream in;
    private OutputStream out;
    private static int answerInt;
    private static String answerString;

    private void inReadInt() {
        try {
            byte[] buf = new byte[2000];
            int count = in.read(buf);
            ByteBuffer bb = ByteBuffer.wrap(buf, 0, count);
            answerInt = bb.getInt();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                in.close();
                out.close();
                s.close();
                System.out.println("Close");
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    private void outWriteInt(int res) {
		try {
            byte[] buf = new byte[1000];
            buf = ByteBuffer.allocate(4).putInt(res).array();
            out.write(buf);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                in.close();
                out.close();
                s.close();
                System.out.println("Close");
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    private void inReadSting() {
        try {
            byte[] buf = new byte[2000];
            int count = in.read(buf);
            answerString = new String(buf, 0, count);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                in.close();
                out.close();
                s.close();
                System.out.println("Close");
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    private void outWriteString(String res) {
        try {
            out.write(res.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            try {
                in.close();
                out.close();
                s.close();
                System.out.println("Close");
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public void work(int socket) {
        try {
            ss = new ServerSocket(socket);
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                System.out.println("Waiting connection...");
                s = ss.accept();
                System.out.println("Local port: " + s.getLocalPort());
                System.out.println("Remote port: " + s.getPort());
                in = s.getInputStream();
                out = s.getOutputStream();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                ArrayList<String> cards;
                cards = Card.getCard();
                outWriteInt(cards.size());
                for (int i = 0; i < cards.size(); i++) {
                    Thread.sleep(Const.DELAY);
                    System.out.println(cards.get(i));
                    outWriteString(cards.get(i));
                    System.out.println("Iteration: " + i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

class NewThread implements Runnable {
    Thread t;
    int socket;
    int id;
    NewThread(int id_p, int socket_p) {
        id = id_p;
        socket = socket_p;
        t = new Thread(this, "Поток" + id);
        System.out.println("Поток создан: " + t);
        t.start();
    }

    public void run() {
        try {
            Network network = new Network();
            network.work(socket);
            Thread.sleep(Const.DELAY);
        } catch (InterruptedException e) {
            System.out.println("Поток прерван.");
        }
    }
}
