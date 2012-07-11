/*
 * CoopServer.java
 *
 * Created on 8 de Agosto de 2003, 22:42
 */

import java.util.Properties;
import java.util.Vector;
import java.io.*;
import java.net.*;

import java.security.*;

import rdjava.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.ParserConfigurationException;

//import br.ufrgs.lec.rdjava.*;

/**
 *
 * @author  Juliano Bittencourt <juliano@edu.ufrgs.br>
 */
public class CoopServer {
    
    private String serverPort;
    private String serverName;
    private static Vector<Cenario> runningCenarios; //vetor dos cenarios
    private static syncThread tsync;
    private String iniFile;
    
    
    /** Creates a new instance of CoopServer */
    public CoopServer() {
        runningCenarios = new Vector<Cenario>(10,1);
        
        String currentDir = System.getProperty("user.dir");
        iniFile = currentDir + "/coop.ini";
    }
  
    /** Cria um usuario
     *  Alem de criar um usuario manda uma mensagem se foi bem sucedida a criacao
     *    do usuario e se foi possivel criar o usuario entao ja loga o usuario
     */
    
    public void createUser(CoopClientConnection con,String nomUser, String password, String nomPessoa, int codAvatar) {
        CoopAmbiente ambiente = new CoopAmbiente();
        boolean sucesso = ambiente.createUser(nomUser,password,nomPessoa,codAvatar);
        if (sucesso) {
            //criacao de usuario ok. loga o usuario
            String msg = "<create_user status=\"0\" />";
            con.send(msg);
            loginUser(con, nomUser, password);
        }
        else {
            //falha na criacao do usuario. possivelmente ja havia algum usuario com o mesmo nome
            //envia mensagem ao cliente notificando a falha na criacao do usuario
            String msg = "<create_user status=\"1\" />";
            con.send(msg);
        }
    }
    
    /** Autentica o usuario
     *  Usado em handle_identify no parser
     */
    public void loginUser(CoopClientConnection conexao,String nomUser,String passwd) {
        CoopAmbiente ambiente= new CoopAmbiente();
        //autentica usuario
        boolean sucesso = ambiente.autentica(nomUser,passwd);
        System.out.println("NomUser: " + nomUser + ", senha : " + passwd);
        
        //manda mensagem de confirmacao para o cliente
        if (sucesso) {
          CoopUser user = new CoopUser(nomUser);
          conexao.setUser(user);
          String msg = "<identify_ack status=\"0\">";
          msg += user.getUserInfo();
          msg += "</identify_ack>";
          msg += "<navigate>";
          conexao.send(msg);
          System.out.println("Autenticacao OK");
        }
        else {
          System.out.println("Autenticacao falhou");  
          String msg = "<identify_ack status=\"1\"></identify_ack>";
          conexao.send(msg);
        }
        
    }
    
    /*  Loga o usuario dentro de um cenario
     *  Manda uma mensagem de volta ao clinte com a confirmacao se o login 
     *  foi possivel
     *  Nao confundir este metodo com o metodo loginUser que autentica o cliente
     *  E chamado em handle_request_enter_cenario no parser
     */
    public void loginUserInCenario(CoopClientConnection conexao,int idCenario,int windowPos, String xmlCenario) {
        Cenario cenario = getCenario(idCenario);
        if (cenario == null) {
          //este cenario nao esta rodando ainda, entao cria-lo
          cenario = createCenario(idCenario);  
          System.err.println("cenario nao existia. criando cenario");
        }
        else {
          System.err.println("Cenario existe");    
        }
        
        String xmlCenState = cenario.getInitState(); //estado inicial do cenario
        
        cenario.addConexao(conexao);
        conexao.setCenario(cenario);
        
        //mensagem de confirmacao para o cliente
        String msg = "<request_enter_cenario_ack status=\"0\" idcenario=\"";
        msg += idCenario + "\">";
        
        if (!xmlCenario.equals("0")) {
            //cliente informou o xml do cenario
            msg += "<cenario_xml></cenario_xml>";
        }
        
        //estado inicial do cenario
        msg += xmlCenState;
        
        msg += "</request_enter_cenario_ack>";
        msg += "<chat>";
            
        conexao.send(msg); //envia para o cliente            
    }
    
    /* Desloga usuario do servidor, matando a conexao */
    public void logout(CoopClientConnection conexao) {
       conexao.terminate(); 
    }
    
    /** Metodo de debugacao. Imprime os cenarios */
    public static void imprimeCenarios(boolean imprimeUsuarios) {
        System.out.println("Cenarios ativos: ");
        Cenario cen = null;
        for (int i=0; i < runningCenarios.size(); i++) {
            cen = runningCenarios.get(i);
            System.out.println("Cenario: " + cen.codCenario);
            if (imprimeUsuarios) 
                cen.imprimeUsuarios();
        }    
    }
    
    /** Busca um cenario que esta rodando pelo id */
    public static Cenario getCenario(int id) {
        boolean achou = false;
        Cenario cen = null;
        for (int i=0; i < runningCenarios.size(); i++) {
            cen = runningCenarios.get(i);
            if (cen.codCenario == id) {
                achou = true;
                break;
            }
        }
        if (achou)
           return cen;
        else
           return null;
    }
    
    /* Coloca um novo cenario como ativo */
    public static Cenario createCenario(int id) {
      Cenario cen = new Cenario(id);
      if (cen.isNew())
          return null;
      else {
          runningCenarios.add(cen);
          return cen;
      }
    }
    
    
    /** Retorna os cenarios que estao com usuarios conectados */
    public Vector getCenarios() {
        return runningCenarios;
    }
    
    /** Adiciona um cenario a lista de cenarios
     *
     */
    public void addCenario(Cenario cenario) {
        runningCenarios.add(cenario);
    }
    
    /**
     * Load the program preferences from the coop.ini file.
     */
    public void loadPreferences() {
        
        try {
            FileInputStream fileProp = new FileInputStream(iniFile);
            Properties prop = new Properties();
            prop.load(fileProp);
            
            serverPort = (String) prop.get("serverPort");
            serverName = (String) prop.get("serverName");
        } catch(IOException e) {
            System.out.println("Config file coop.ini not found.");
            System.exit(1);
        }
    }
    
    public void syncCenarios() {
        Cenario cen = null;
        
        for (int i=0; i < runningCenarios.size(); i++) {
            cen = runningCenarios.get(i);
            cen.syncUsers();
        }        
    }
    
    /**
     * Start de main server of de program
     */
    public void startMainServer() {
    
        //conect to database
        RDConnection.connect(iniFile); //estabilish database connection
        
        //create a socket on ServerPort
        ServerSocket serverSocket = null;
        
        Integer port = new Integer(serverPort);
        
        
        try {
            serverSocket = new ServerSocket(port.intValue());
            System.out.println("Server established");
        } catch (IOException e) {
            System.err.println("Could not bind to port: "+serverPort);
            System.exit(1);
        }
        
        tsync = new syncThread(this);
        tsync.start();
        
        
        //wait for connections
        Socket newconnection=null;
        CoopClientConnection runner;
        while(true) {
            try {
                newconnection = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("Could not listen to port:"+serverPort);
                System.exit(1);
            }
            
            runner = new CoopClientConnection(newconnection,this);
            
        }
        
    }
    
    private class syncThread extends Thread {
    
        CoopServer server;
   
        syncThread(CoopServer coop) {
            server = coop;
        }
    
        public void run()  {
            while(true) {
                try {
                    this.sleep(1000);
                } catch (Exception e) {
                    System.out.println("Cannot sleep");
                    System.exit(1);
                }
                
                server.syncCenarios();
            }
        }
    
    }
    
}
