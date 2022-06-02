import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.util.ArrayList;

import database.Card;
import database.Unit;
import database.Database;
import database.Const;
import database.GameUser;

public class Main {
    public static void main (String[]args) {
        Database database = new Database("postgres", "12345");
        Connection con = database.getConnection();
        Info info = Info.getInstance();
        new NewThread(1, 5001);
        new NewThread(2, 5002);
    }
}

class Network {
    private Socket s;
    private ServerSocket ss;
    private InputStream in;
    private OutputStream out;
    private int answerInt;
    private String answerString;
    private int status;

    private String curLogin;

    public Network() {
        status = 0;
    }

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

    private void inReadString() {
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
            System.out.println("Status: " + status);
            if(status == 0) {
                inReadInt();
                int status_act = answerInt;
                outWriteInt(1);
                if (status_act == Const.REGISTRATION || status_act == Const.LOGIN) {
                    inReadString();
                    String login = answerString;
                    System.out.println(login);
                    outWriteInt(1);
                    inReadString();
                    String password = answerString;
                    System.out.println(password);
                    if (status_act == Const.REGISTRATION) {
                        int result = GameUser.registration(login, password);
                        if (result == Const.SUCCESS_REGISTRATION) {
                            status = 1;
                            curLogin = login;
                            outWriteInt(result);
                        }
                        else {
                            outWriteInt(result);
                            status = 0;
                        }
                    }
                    else if (status_act == Const.LOGIN) {
                        int result = GameUser.login(login, password);
                        if (result == Const.INVALID_LOGIN || result == Const.WRONG_PASSWORD) {
                            try {
                                outWriteInt(result);
                                status = 0;
                                in.close();
                                out.close();
                                s.close();
                                System.out.println("Close");
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                        }
                        else {
                            try {
                                curLogin = login;
                                outWriteInt(result);
                                status = 1;
                                inReadInt();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else outWriteInt(0);
            }
            else {
                inReadInt();
                int status_act = answerInt;
                if (status_act == Const.EXIT) {
                    try {
                        status = 0;
                        in.close();
                        out.close();
                        s.close();
                        System.out.println("Close");
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                else if (status_act == Const.GET_CARDS) {
                    try {
                        ArrayList<String> cards;
                        cards = Card.getCard();
                        outWriteInt(cards.size());
                        for (int i = 0; i < cards.size(); i++) {
                            System.out.println(cards.get(i));
                            outWriteString(cards.get(i));
                            System.out.println("Iteration: " + i);
                            inReadInt();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if (status_act == Const.GET_UNITS) {
                    try {
                        ArrayList<String> units;
                        units = Unit.getUnit();
                        outWriteInt(units.size());
                        for (int i = 0; i < units.size(); i++) {
                            System.out.println(units.get(i));
                            outWriteString(units.get(i));
                            System.out.println("Iteration: " + i);
                            inReadInt();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if (status_act == Const.SEND_PLAYER_ACTION) {
                    System.out.println(Thread.currentThread().getName());
                    outWriteInt(1);
                    inReadString();
                    outWriteInt(1);
                    Info info = Info.getInstance();
                    info.sendInfo(Thread.currentThread().getName(), answerString);
                }
                else if (status_act == Const.GET_PLAYER_ACTION) {
                    Info info = Info.getInstance();
                    String res = info.getInfo(Thread.currentThread().getName());
                    outWriteString(res);
                }
                else if (status_act == Const.GET_STATS) {
                    try {
                        outWriteString(curLogin + '#' + GameUser.getWinsAndLoses(curLogin));
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if (status_act != Const.EXIT) {
                    outWriteInt(-1);
                }
            }
        }
    }
}

class Info {
    public String str1;
    public String str2;
    private static Info instance;

    private Info() {
        str1 = "-1";
        str2 = "-1";
    }

    public static Info getInstance() {
        if(instance == null) {
            instance = new Info();
        }
        return instance;
    }

    public String getInfo(String name) {
        if (name.equals("Поток1")) {
            String buf = str2;
            str2 = "-1";
            return buf;
        }
        else if (name.equals("Поток2")) {
            String buf = str1;
            str1 = "-1";
            return buf;
        }
        return "-1";
    }

    public void sendInfo(String name, String str_p) {
        if (name.equals("Поток1")) {
            if (str1.equals("-1")) str1 = str_p;
            else str1 += '#' + str_p;
        }
        else if (name.equals("Поток2")) {
            if (str2.equals("-1")) str2 = str_p;
            else str2 += '#' + str_p;
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
        Network network = new Network();
        network.work(socket);
    }
}