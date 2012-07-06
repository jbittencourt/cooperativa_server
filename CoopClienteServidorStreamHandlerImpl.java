/*
 * File:           CoopClienteServidorStreamHandlerImpl.java
 * Date:           September 25, 2003  4:32 PM
 *
 * @author  maicon
 * @version generated by NetBeans XML module
 */
import org.xml.sax.*;

public class CoopClienteServidorStreamHandlerImpl implements CoopClienteServidorStreamHandler {
    
    public static final boolean DEBUG = false;
    private CoopClientConnection conexao;
    private CoopServer server; //referencia ao servidor
    
    public CoopClienteServidorStreamHandlerImpl(CoopClientConnection con) {
        conexao = con;
        server = con.getServer();
    }
    
    public void imprimeAtributos(final Attributes atributos) {
      for (int i=0; i < atributos.getLength(); i++) {
        String name = atributos.getQName(i);
        String type = atributos.getType(i);
        String value = atributos.getValue(i);
        System.err.print("Atr-> Nome: " + name + " ; Tipo: " + type);
        System.err.println(" ; Valor: " + value);
      }
    }
      
    public void handle_change_object_state(final Attributes meta) throws SAXException {
        if (DEBUG) System.err.println("handle_change_object_state: " + meta);
    
        int idObj = Integer.parseInt(meta.getValue("idobj"));
        int status = Integer.parseInt(meta.getValue("status"));
        int tag = Integer.parseInt(meta.getValue("tag"));
        int posx = Integer.parseInt(meta.getValue("posx"));
        int posy = Integer.parseInt(meta.getValue("posy"));
        Cenario cen = conexao.getCenario();
        cen.userChangedObject(conexao,idObj, status,tag,posx,posy);
    }
    
    public void handle_request_enter_cenario(final Attributes meta) throws SAXException {
        if (DEBUG) System.err.println("handle_request_enter_cenario: " + meta);
        
        int idCenario = Integer.parseInt(meta.getValue("idcenario")); //id do cenario
        int celpos = Integer.parseInt(meta.getValue("celpos")); //janela do cenario
        String requestCenarioXml = meta.getValue("request_cenario_xml"); //xml do cenario(opcional)        
        server.loginUserInCenario(conexao,idCenario,celpos,requestCenarioXml);
        
    }
    
    public void handle_identify(final Attributes meta) throws SAXException {
        System.err.println("handle_identify: " + meta);
    
        String nomUser = meta.getValue("username");
        String passwd = meta.getValue("password");
        server.loginUser(conexao,nomUser,passwd);
    }
    
    public void start_talk(final Attributes meta) throws SAXException {
        if (DEBUG) System.err.println("start_talk: " + meta);
    }
    
    public void handle_talk(final java.lang.String data, final Attributes meta) throws SAXException {
        if (DEBUG) System.err.println("handle_talk: " + data);
        int idUser = Integer.parseInt(meta.getValue("iduser"));
        System.out.println("usuario " + idUser + " esta falando");
        
        Cenario cen = conexao.getCenario();
        cen.userTalk(conexao,data);
    }
    
    public void end_talk() throws SAXException {
        if (DEBUG) System.err.println("end_talk()");
    }
    
    public void handle_move_to(final Attributes meta) throws SAXException {
        if (DEBUG) System.err.println("handle_move_to: " + meta);
        
        int posy = Integer.parseInt(meta.getValue("posy"));
        int posx = Integer.parseInt(meta.getValue("posx"));
        int idUser = Integer.parseInt(meta.getValue("iduser"));
        Cenario cen = conexao.getCenario();
        cen.userMove(conexao,posx,posy);              
    }
    
    public void start_cooperativa_cliente_servidor(final Attributes meta) throws SAXException {
        if (DEBUG) System.err.println("start_cooperativa_cliente_servidor: " + meta);
        String versaoCliente = meta.getValue("version");
    
    }
    
    public void end_cooperativa_cliente_servidor() throws SAXException {
        if (DEBUG) System.err.println("end_cooperativa_cliente_servidor()");
        server.logout(conexao);
    }
    
    public void start_navigate(final Attributes meta) throws SAXException {
        if (DEBUG) System.err.println("start_navigate: " + meta);
    }
    
    public void end_navigate() throws SAXException {
        if (DEBUG) System.err.println("end_navigate()");
    }
    
    public void handle_enter_cenario(final Attributes meta) throws SAXException {
        if (DEBUG) System.err.println("handle_enter_cenario: " + meta);
        int idUser = Integer.parseInt(meta.getValue("iduser"));
        int posx = Integer.parseInt(meta.getValue("posx"));
        int posy = Integer.parseInt(meta.getValue("posy"));
        String method = meta.getValue("method");
        
        Cenario cen = conexao.getCenario();
        cen.userEnterCenario(conexao, posx, posy, method);
    }
    
    public void start_chat(final Attributes meta) throws SAXException {
        if (DEBUG) System.err.println("start_chat: " + meta);
    }
    
    public void end_chat() throws SAXException {
        if (DEBUG) System.err.println("end_chat()");
        
        //desloga o usuario do cenario atual
        //Cenario cen = conexao.getCenario();
        //cen.logout(conexao);
    }
    
    /* Desloga o usuario do cenario atual
     *
     */
    public void handle_logout(Attributes meta) throws SAXException {
        Cenario cen = conexao.getCenario();
        cen.logout(conexao);
    }
    
    public void handle_create_user(Attributes meta) throws SAXException {
        String nomUser = meta.getValue("username");
        System.out.println("nomUser : " + nomUser);
        String password = meta.getValue("password");
        String nomPessoa = meta.getValue("nomPessoa");
        int codAvatar = Integer.parseInt(meta.getValue("idAvatar"));
        server.createUser(this.conexao,nomUser,password,nomPessoa, codAvatar);
    }
    
    /** Usuario adicionou um item no inventario
     *
     */
    public void handle_inventory_add_item(Attributes meta) throws SAXException {
        int codObjeto = Integer.parseInt(meta.getValue("idobj"));
        int codUser = Integer.parseInt(meta.getValue("iduser"));
        Cenario cen = conexao.getCenario();
        cen.userAddItemInventory(conexao,codObjeto);    
    }
    
    /** Usuario removeu um item do iventario
     *
     */
    public void handle_inventory_drop_item(Attributes meta) throws SAXException {
        int codObjeto = Integer.parseInt(meta.getValue("idobj"));
        int codUser = Integer.parseInt(meta.getValue("iduser"));
        int posx = Integer.parseInt(meta.getValue("posx"));
        int posy = Integer.parseInt(meta.getValue("posy"));
        Cenario cen = conexao.getCenario();
        cen.userDropItemInventory(conexao,codObjeto, posx,posy);
    }
    
}

