import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class Server{

    Set<User> activeUsers=new HashSet<>();
    int port;
    public Server(int p_num) throws Exception{
        port=p_num;
        try (ServerSocket serverInstance = new ServerSocket(port)) {
            System.out.println("listening on "+port);
            while(true){
                Socket acceptor=serverInstance.accept();
                User newbie=new User(this, acceptor);
                System.out.println(newbie.name+" joined");
                newbie.start();
            }
        }
    }
    void transmit(String msg, User current) {
        for (User user : activeUsers) {
            if (user != current) {
                user.send(msg);
            }
            else{
                System.out.println(msg);
            }
        }
    }
    public static void main(String[] args) throws Exception{
        if(args.length<1){
            throw new Exception("Syntax: java Server PORT_NUMBER");
        }
        new Server(Integer.parseInt(args[0]));
    }
}

class User extends Thread{
    public String name;
    private Server host;
    private Socket conn;
    private PrintWriter wr;
    public User(Server host,Socket conn) {
        this.conn=conn;
        this.host=host;
    }

    public void run(){
        try{
            BufferedReader parser=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            wr=new PrintWriter(conn.getOutputStream(),true);
            if(host.activeUsers.isEmpty()){
                wr.println("Hey Early bird, You are first");
            }
            else{
                wr.println("We have some friend(s) for you!");
                for(User i:host.activeUsers){
                    if(i.name!=null)
                        wr.println(i.name+", ");
                }
                wr.println();
            }
            name=parser.readLine();
            host.activeUsers.add(this);
            String msg=name+" Connected!";
            host.transmit(msg,this);
            while(true){
                msg=parser.readLine();
                String msg1="["+name+"] "+msg;
                host.transmit(msg1, this);
                if(msg.equals("bye"))break;

            }
            host.activeUsers.remove(this);
            conn.close();
            msg=name+" offline";
            System.out.println(msg);
            host.transmit(msg, this);

        }catch(Exception err){
            try {
                throw new Exception("Error: "+err.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    void send(String msg){
        wr.println(msg);
    }
}
