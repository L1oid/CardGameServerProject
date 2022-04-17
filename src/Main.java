import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main (String[]args){
        Network network = new Network();
        network.work();
    }
}

class Network {
    Socket s;
    ServerSocket ss;
    InputStream in;
    OutputStream out;
    static String answer;
    public void workWithString() {
        while(true) {
            try {
                byte[] buf = new byte[2000];
                int count = in.read(buf);
                String word = new String(buf, 0, count);
                //answer = бла бла бла;
                out.write(answer.getBytes());
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
                break;
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
            workWithString();
        }
    }
}