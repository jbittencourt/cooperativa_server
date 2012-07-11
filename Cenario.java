//
//  Cenario.java
//  CoopServer
//
//  Created by Maicon Brauwers on Tue Aug 26 2003.
//  Copyright (c) 2003 __MyCompanyName__. All rights reserved.
//

/** Scene Class
 *  This is a persistent class
 *  @author Maicon Brauwers
 *
 */

import java.util.Vector;
import rdjava.*;
import java.sql.Statement;
import java.sql.SQLException;


public class Cenario extends RDObj {
    
    public int codCenario;
    public int maxUsers;        //max users connected in this scene
    public String nomeCenario;
    public String flashFile;    //flash file of this scene
    
    //esta propriedade nao eh persistente
    private Vector<CoopClientConnection> runningConnections; //vetor das conexoes deste cenario
    
    //esta propriedade nao eh persistente
    //vetor com os objetos do cenario que tiveram seu estado alterado
    private Vector<CoopObjeto> modObjects;
    
    public Cenario() {
        //codigo RDObj
        super();
        String tabela = "cenario";
        String[] fields = { "codCenario","maxUsers","nomeCenario","xmlFile","xmlData"};
        String[] primaryKeys = { "codCenario" };
        
        addTabela(tabela);
        setCampos(tabela,fields);
        setPkFields(tabela,primaryKeys);
        setAutoIncFields(tabela,"codCenario");
        //fim codigo RDObj
        
        runningConnections = new Vector<CoopClientConnection>(15,5);
        modObjects = new Vector<CoopObjeto>(15,5);    
    }
    
    public Cenario(OpVal chaves[]) {
        this();
        le(chaves);
    }
    
    public Cenario(int codCenario) {
        this();
        OpVal[] chaves =  {new OpVal("codCenario",codCenario) };
        le(chaves);
    }
    
    /** Retorna as conexoes que estao rodando neste cenario */
    public Vector getConnections() {
        return runningConnections;
    }
    
    /** Adiciona uma conexao
     *
     */
    public void addConexao(CoopClientConnection con) {
        runningConnections.add(con);
        imprimeUsuarios();
        //con.setCenario(this);
    }
    
    /** Desloga o usuario deste cenario, mas nao mata sua conexao */
    public void logout(CoopClientConnection conexao) {
        //manda mensagem de logout para os outros usuarios deste cenario
        System.out.println("Fazendo logout");
        CoopUser user = conexao.getUser();
        String msg = "<logout iduser=\"" + user.codUser + "\"/>";
        sendMsgToClients(msg);
        
        //remove conexao do usuario da lista de conexao ativas
        runningConnections.remove(conexao);
        conexao.setCenario(null);
    }
       
    /** Adiciona um objeto a lista de objetos modificados
     *
     */
    public void addModObject(CoopObjeto obj) {
        modObjects.add(obj);
        System.out.println("Lista de objetos modificados: ");
        for (int i=0; i<modObjects.size(); i++) {
            CoopObjeto o = modObjects.get(i);
            System.out.println(o + " : " + o.codObjeto + " -> " + o.nome);
        }
    }
    
    /** Retorna os objetos do cenario com estados modificados */
    public Vector getModObjects() {
        return modObjects;
    }
    
    /** Busca um objeto pelo seu id */
    public CoopObjeto findModObject(int id) {
        CoopObjeto obj = null;
        boolean achou = false;
        for (int i=0; i< modObjects.size(); i++) {
            obj = modObjects.get(i);
            if (obj.getId() == id) {
                achou = true;
                break;
            }
        }
        if (achou) {
            return obj;
        }
        else
            return null;
    }
    
    /** Guarda a posicao atual no banco de dados
     *  Guarda tanto o estado atual em CoopObjeto 
     *  como tambem no historico das alteracoes de esta(CoopEstadosObjeto
     */
    public void storeModObject(CoopObjeto obj,CoopUser user) {
        Statement sqlStatement = RDConnection.getStatement();
        
        //salva o estado atual em CoopObjeto
        obj.salva();
        
        //salva no historico
        String sql = " INSERT INTO CoopEstadosObjeto ";
        sql+= "(codObjeto,codCenario,tempo,status,tag,codUser,flagInventario,posX,posY) VALUES ";
        sql+= " (" + obj.getId() + "," + codCenario + ",";
        //System.currentTimeMillis retorna o tempo atual em milissegundos
        sql+= System.currentTimeMillis() + "," + obj.getStatus() + ",";
        sql+= obj.getTag() + "," + user.codUser + "," + obj.getFlagInventario();
        sql+= "," + obj.getPosX() + "," + obj.getPosY() + ")";
        
        try {
            int rowAffected = sqlStatement.executeUpdate(sql);
        }
        catch(SQLException e) {
            System.out.println(e);
        }
    }
    
    /** Atualiza o estado de um objeto
     *  Caso o objeto nao exista ainda adiciona ele a lista de obj modificados
     *  Se ele ja existir apenas atualiza os dados 
     */
    public CoopObjeto updateObject(int id,int status,int tag,int posx, int posy, CoopUser user) {
        CoopObjeto obj = findModObject(id);
        if (obj == null) {
            //objeto nao existe ainda na lista de obj modificados
            obj = new CoopObjeto(id);
            addModObject(obj);
        }
        
        obj.setStatus(status);
        obj.setTag(tag);
        obj.setPosX(posx);
        obj.setPosY(posy);
        
        //guarda no bd a alteracao no objeto
        storeModObject(obj,user);
        
        return obj;
    }
    
    /** Envia uma mensagem para todos os clientes que estao conectados
     *  neste cenario
     */
    public synchronized void sendMsgToClients(String msg) {
        //faz uma copia das conexoes que estao rodando
        //para evitar que envie uma mensagem para um cliente
        //que entre no meio do envio das mensagens
        
        Vector conexoes = (Vector) runningConnections.clone();
        for(int i=0; i<runningConnections.size(); i++) {
            CoopClientConnection con = (CoopClientConnection) conexoes.get(i);
            con.send(msg);
        }
    }
    
    public void imprimeUsuarios() {
        System.out.println("Usuarios conectados no cenario " + codCenario);
        for(int i=0; i<runningConnections.size(); i++) {
            CoopClientConnection con = runningConnections.get(i);
            CoopUser user = con.getUser();
            System.out.print("Usuario: " + user.codUser);
            System.out.println(" -> " + user.nomUser);
        }
    }
    
    /** Retorna todos os objetos deste cenario
     *
     */
    public RDLista getObjects() {
        OpVal[] chave = new OpVal[1];
        RDLista objetos = new RDLista("CoopObjeto",chave);
        return objetos;
    }
    
    //constroi o xml do estado atual do cenario
    public String getInitState() {
        
        String msg = "<cenario_status_info>";
        
        //manda o estado de todos os usuarios conectados neste cenario
        CoopClientConnection userConexao;
        CoopUser userCen;
        for (int i=0; i<runningConnections.size(); i++) {
           userConexao =  runningConnections.get(i); 
           userCen = userConexao.getUser();
           msg += "<user_status ";
           msg += " iduser=\"" + userCen.codUser + "\" ";
           msg += " posx=\"" + userCen.getPosX() + "\" ";
           msg += " posy=\"" + userCen.getPosY() + "\" ";
           msg += ">";
           msg += userCen.getUserInfo(); //adiciona o <userinfo>
           msg += "</user_status>";
        }
        
        //manda o estado de todos os objetos deste cenario
        //...
        CoopObjeto obj;
        RDLista objetos = getObjects();
        while (!objetos.end()) {
           obj = (CoopObjeto) objetos.next();
           msg += obj.getXmlStatus();
        }
     
        msg += "</cenario_status_info>";
        return msg;
    }

    /** Usuario entrou no cenario
     *  Atualizar os dados de posicao do usuario e mandar mensagem
     *  para todos os usuarios informando que este usuario entrou no cenario
     */
    public void userEnterCenario(CoopClientConnection conexao,int posx,int posy, String method) {
        CoopUser user = conexao.getUser();
        user.setPosX(posx);
        user.setPosY(posy);
        sendEnterCenario(user);
    }
    
    /** Envia mensagem para todos os clientes do cenario
     *   informando que este usuario entrou no cenario
     */
    public void sendEnterCenario(CoopUser user) {
        String msg = "<enter_cenario iduser=\"" + user.codUser + "\" ";
        msg += " posx=\"" + user.getPosX() +"\" ";
        msg += " posy=\"" + user.getPosY() + "\" ";
        msg += " avatar=\"" + user.codAvatar + "\" ";
        msg += "></enter_cenario>";
        sendMsgToClients(msg);
    }
    
     /** Salva a posicao atual do usuario no banco de dados
     *  
     */
    public void storePosition(CoopUser user) {
        //nao esta usando por enquanto rdobj por motivos de otimizacao
        
        //pega o statement
        Statement sqlStatement = RDConnection.getStatement();
        //constroi a query
        String sql = " INSERT INTO CoopMovimentos ";
        sql+= "(codUser,codCenario,tempo,posX,posy) VALUES ";
        sql+= " (" + user.codUser + "," + codCenario + ",";
        //System.currentTimeMillis retorna o tempo atual em milissegundos
        sql+= System.currentTimeMillis() + "," + user.getPosX() + "," + user.getPosY() + ")";
        
        try {
            int rowAffected = sqlStatement.executeUpdate(sql);
        }
        catch(SQLException e) {
            System.out.println(e);
        }
    }
    
    /** Usuario movimentou-se.
     *  Atualiza a posicao do usuario e manda a nova posicao para todos
     *  os clientes
     */
    public void userMove(CoopClientConnection conexao,int posx, int posy) {
        //atualiza posicoes x,y
        CoopUser user = conexao.getUser();
        user.setPosX(posx);
        user.setPosY(posy);
        storePosition(user);  //salva a movimentacao no bd
        
        //manda evento move_to para todos clientes neste cenario
        String msg = "<move_to iduser=\"" + user.codUser + "\" ";
        msg += " posx=\"" + user.getPosX() + "\" ";
        msg += " posy=\"" + user.getPosY() + "\" ";
        msg += "></move_to>";
        sendMsgToClients(msg);
    }
    
    /** Guarda a conversa do usuario no bd
     *  @param CoopUser user
     *  @param String msg A mensagem que o usuario falou
     */
    public void storeTalk(CoopUser user, String msg) {
         //pega o statement
        Statement sqlStatement = RDConnection.getStatement();
        //constroi a query
        String sql = " INSERT INTO CoopChat ";
        sql+= "(codUser,codCenario,tempo,strMensagem) VALUES ";
        sql+= " (" + user.codUser + "," + codCenario + ",";
        //System.currentTimeMillis retorna o tempo atual em milissegundos
        sql+= System.currentTimeMillis() + ",\"" + msg + "\")";
        System.out.println(sql);
        try {
            int rowAffected = sqlStatement.executeUpdate(sql);
        }
        catch(SQLException e) {
            System.out.println(e);
        }
    }
    
    /** Usuario Falou
     *  Manda um evento de fala para todos os clientes
     */
    public void userTalk(CoopClientConnection conexao, String data) {
        CoopUser user = conexao.getUser();
        storeTalk(user,data); //guarda conversa no bd
        String msg = "<talk iduser=\"" + user.codUser + "\">";
        msg += data;
        msg += "</talk>";
        sendMsgToClients(msg);
    }
    
    /** Usuario modificou o estado de um objeto */
    public void userChangedObject(CoopClientConnection conexao,int idObj, int status, int tag,int posx, int posy) {
        CoopUser user = conexao.getUser();
        CoopObjeto obj = updateObject(idObj,status,tag,posx,posy,user);
        //envia evento change_object_state para todos os clientes
        String msg = obj.getXmlChangedState();
        sendMsgToClients(msg);
    }
    
    /*  Um usuario adicionou algum item ao inventario
     *
     */
    public void userAddItemInventory(CoopClientConnection con,int codObjeto) {
       //adiciona o item no inventario do usuario
       CoopUser user = con.getUser();
       
       //le o objeto
       CoopObjeto obj = findModObject(codObjeto);
       if (obj == null) {
           obj = new CoopObjeto(codObjeto);
           addModObject(obj);
       }
       
       //atualiza estado do objeto
       //para dizer que este usuario tem este objeto no seu inventario
       obj.codUser = user.codUser;
       obj.flagInventario = 1;
        
       //guarda estado no historico dos estados do objeto e atualiza CoopObjeto
       storeModObject(obj,user);
       
       user.addItemInventory(obj,true);
       //manda mensagem para os clientes 
       String msg = obj.getXmlChangedState();
       sendMsgToClients(msg);
    }
    
    /* Um usuario removeu um item do inventario 
     *
     */
    public void userDropItemInventory(CoopClientConnection con, int codObjeto,int posx, int posy) {
       //remove o item no inventario do usuario
       CoopUser user = con.getUser();
       //le o objeto
       CoopObjeto obj = findModObject(codObjeto);
       if (obj == null) {
           obj = new CoopObjeto(codObjeto);
       }

       user.dropItemInventory(obj);

       //atualiza estado do objeto
       //para dizer que este item nao esta mais no inventario
       obj.codUser = 0;
       obj.flagInventario = 0;
       //salva as alteracoes da posicao do objeto
       obj.setPosX(posx);
       obj.setPosY(posy);
       
       //guarda estado no historico dos estados do objeto e atualiza CoopObjeto
       storeModObject(obj,user);
       
       //manda mensagem para os clientes do estado atual do objeto
       String msg = obj.getXmlChangedState();
       sendMsgToClients(msg);
    }
    
    public void syncUsers() {
        String msg = "<sync />";
        sendMsgToClients(msg);
    }
    
}