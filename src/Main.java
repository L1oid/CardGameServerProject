import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Main {
    public static void main (String[]args){
        Network network = new Network();
        network.work();
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
            System.out.println(e);
            try {
                in.close();
                out.close();
                s.close();
                System.out.println("Close");
            } catch (Exception e2) {
                System.out.println(e2);
            }
        }
    }

    private void outWriteInt(int res) {
		try {
            byte[] buf = new byte[1000];
            buf = ByteBuffer.allocate(4).putInt(res).array();
            out.write(buf);
        } catch (Exception e) {
            System.out.println(e);
            try {
                in.close();
                out.close();
                s.close();
                System.out.println("Close");
            } catch (Exception e2) {
                System.out.println(e2);
            }
        }
    }

    public void work() {
        try {
            ss = new ServerSocket(1111);
        } catch (Exception e) {
            System.out.println(e);
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
                System.out.println(e);
            }
            inReadInt();
            answer++;
            outWriteInt(answer);
        }
    }
}
