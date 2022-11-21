import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client{
    public String name;
    public Client(int port, String host) throws UnknownHostException, IOException{
        Socket conn=new Socket(host,port);
        System.out.println("You're in!");
        new Read(conn,this).start();
        new Write(conn,this).start();
        System.out.println("Thanks For Coming!");

    }
    public static void main(String[] args){
        if(args.length<2){
            System.out.println("Syntax: java Client hostName port");
        }
        try{
            new Client(Integer.parseInt(args[1]),args[0]);
        }catch(Exception err){
            System.out.println("Syntax error: should be \"java Client hostName port\"");
            System.out.println(err.getMessage());
        }
    }
}

class Read extends Thread{
    private BufferedReader r;
    private Client client;
    public Read(Socket conn,Client client) throws IOException{
        r=new BufferedReader(new InputStreamReader(conn.getInputStream()));
        this.client=client;
    }
    public void run(){

        try{
            String res=r.readLine();
            System.out.println("\n"+res);
            if(client.name!=null){
                System.out.print("["+client.name+"]: ");
            }
        }catch(Exception err){
            System.out.println("Error: "+err.getMessage());
        }

    }
}

class Write extends Thread{
    private PrintWriter wr;
    private Socket conn;
    private Client client;
    public Write(Socket conn,Client client) throws IOException{
        this.conn=conn;
        this.client=client;
        try{
            wr=new PrintWriter(conn.getOutputStream(),true);
        }catch(IOException err){
            System.out.println("ERROR: "+err.getMessage());
        }
    }
    public void run(){
        Console console=System.console();
        client.name=console.readLine("\nEnter your name: ");
        wr.println(client.name);
        String msg;
        while(true){
            msg=console.readLine("["+client.name+"]: ");
            wr.println(msg);
            if(msg.equals("bye"))break;
        }
        try {
            conn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
