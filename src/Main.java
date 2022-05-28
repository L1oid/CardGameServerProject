import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import database.Card;
import database.Database;

public class Main {
    public static void main (String[]args) {
        new NewThread(1, 1111);
        new NewThread(2, 1112);
    }
}

class Network {
    private Socket s;
    private ServerSocket ss;
    private InputStream in;
    private OutputStream out;
    private static int answer;

    private void inReadInt() {
        try {
            byte[] buf = new byte[2000];
            int count = in.read(buf);
            ByteBuffer bb = ByteBuffer.wrap(buf, 0, count);
            answer = bb.getInt();
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
            inReadInt();
            answer++;
            outWriteInt(answer);
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
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println("Поток прерван.");
        }
    }
}
