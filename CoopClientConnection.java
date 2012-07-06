import java.io.*;
import java.net.*;

//for xmlhandler
import org.xml.sax.helpers.ParserAdapter;
import org.xml.sax.InputSource;

/*
 * CoopClientCoonection.java
 *
 * Created on 9 de Agosto de 2003, 02:29
 */

/**
 *
 * @author  juliano
 */
public class CoopClientConnection  {
    
    private Socket socket;
    private PrintWriter outXml;
    private InputStreamReader inXml;
    private ReaderThread reader;
    
    //user that is connected thru this thread
    private CoopUser user;
  
    //o cenario ao qual o usuario esta conectado
    private Cenario cenario;
    
    //referencia ao servidor
    private CoopServer server;
        
    /** Creates a new instance of CoopClientCoonection */
    public CoopClientConnection(Socket connection,CoopServer server) {
        this.socket = connection;
        this.server = server;
        inXml = null;
        outXml = null;
        cenario = null;
        user = null;
        
        try {
            inXml = new InputStreamReader(this.socket.getInputStream());
            reader = new ReaderThread(inXml,this);
            reader.start();
        } catch(IOException e) { };
        
        try {
            outXml = new PrintWriter(this.socket.getOutputStream(),true);
            outXml.print("<!DOCTYPE cooperativa_servidor_cliente SYSTEM ");
            outXml.print("\"http://localhost/coop_servidor_cliente.dtd\">");
            outXml.println("<cooperativa_servidor_cliente version=\"0.5\">");
            outXml.flush();
        } catch(IOException e) { };
        
    }

    /** Termina a execucao, fechando o socket */
    public void terminate() {
        try { 
          if (!socket.isClosed())
            //reader.stop();
            socket.close();
        }
        catch(IOException ioe) {
          System.out.println(ioe);    
        }
    }
    
    public CoopServer getServer() {
        return server;
    }
    
    public void setUser(CoopUser user) {
        this.user = user;
    }
    
    public CoopUser getUser() {
        return user;
    }
    
    public void setCenario(Cenario cen) {
        cenario = cen; 
    }
    
    public Cenario getCenario() {
        return cenario;
    }
    
    public Socket getSocket() {
        return socket;
    }
    
    public boolean isSocketInputShutdown() {
        return socket.isInputShutdown();
    }
    
    /** Envia mensagem do servidor para o cliente
     *
     */
    public void send(String str) {
	outXml.println(str); 
        System.out.println(str);
	outXml.flush();
    }
       
   private class ReaderThread extends Thread {
       
     InputStreamReader m_is;
     Writer m_os;
     CoopClientConnection owner;
     
    
     ReaderThread(InputStreamReader is,CoopClientConnection myowner) {
         m_is = is;
         owner = myowner;
         setDaemon(true);
     }
     
     private void testConnection() {
        Socket s = owner.getSocket();
        if (s.isClosed()) {
        //a conexao terminou anormalmente
        //limpa a sessao do usuario 
            System.out.println("Conexao terminou anormalmente");
            Cenario cen = owner.getCenario();
            if (cen != null) {
               //desloga do cenario
               cen.logout(owner);
            }
            owner = null;
             
        }
     }
    
     public void run()  {
         
         InputSource source =  new InputSource(m_is);
         CoopClienteServidorStreamHandlerImpl handler = new CoopClienteServidorStreamHandlerImpl(owner);
         CoopClienteServidorStreamParser parser = new CoopClienteServidorStreamParser( handler,null);
         
	 try {
             parser.parse( source,handler);
         }
         catch(IOException ioe) {
             testConnection();
             System.out.println(ioe);
         }
         catch(org.xml.sax.SAXException saxe) {
             testConnection();
             System.out.println(saxe);
         }
         catch(javax.xml.parsers.ParserConfigurationException e) {
             System.out.println(e);
         }
         

     }

   }
   
}
