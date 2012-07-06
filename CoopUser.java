/*
 * CoopUser.java
 *
 * Created on 14 de Agosto de 2003, 02:11
 */

/**
 * Class representing each user
 * This is a peristent class
 * @author  juliano <juliano@edu.ufrgs.br>
 */

import rdjava.*;
import java.util.Vector;

public class CoopUser extends RDObj {
    
    public int codUser,flaSuper,codHomeCenario,codAvatar,tempo;
    public String nomUser,nomPessoa,strEmail,desSenha,desSenhaPlain;
    
    //posicao do usuario no cenario
    //estas propriedades nao sao persistentes e
    //somente tem semantica quando o usuario esta conectado em um cenario
    private int posx,posy;
    
    /** Lista com os objetos do inventario
     *  Esta nao eh persistente diretamente
     *  Eh salva manualmente */
    private Vector inventory;
    
    public CoopUser() {
        super();
        String tabela = "user";
        String[] fields = { "codUser","flaSuper","nomUser","nomPessoa","desSenha","desSenhaPlain","codHomeCenario","strEmail","tempo", "codAvatar" };
        String[] primaryKeys = { "codUser" };
        
        addTabela(tabela);
        setCampos(tabela,fields);
        setPkFields(tabela,primaryKeys);
        setAutoIncFields(tabela,"codUser");
        
        posx = 1;
        posy = 1;
        
        inventory = new Vector();
    }
    
    public CoopUser(OpVal[] chaves) {
        this();
        le(chaves);
    }
    
    public CoopUser(int codUser) {
        this();
        OpVal chaves[] = new OpVal[1];
        chaves[0] = new OpVal("codUser",codUser);
        le(chaves);
    }
    
    public CoopUser(String nomUser) {
        this();
        OpVal chaves[] = new OpVal[1];
        chaves[0] = new OpVal("nomUser",nomUser);
        le(chaves);
    }
    
    /* Retorna a posicao x do usuario no cenario */
    public int getPosX() {
        return posx;
    }
    
    /* Seta a posicao x do usuario no cenario */
    public void setPosX(int pos) {
        posx = pos;
    }
    
    /* Retorna a posicao y do usuario no cenario */
    public int getPosY() {
        return posy;
    }
    
    /* Seta a posicao y do usuario no cenario */
    public void setPosY(int pos) {
        posy = pos;
    }
    
    /** Retorna o xml do elemento userinfo pra este usuario */
    public String getUserInfo() {

      String msg = "<userinfo ";
      msg += " iduser=\"" + codUser + "\" ";
      msg += " nomUser=\"" + nomUser + "\" ";
      msg += " nomPessoa=\"" + nomPessoa + "\" ";
      msg += " strEmail=\"" + strEmail + "\" ";
      msg += " codHomeCenario=\"" + codHomeCenario + "\" ";
      msg += " codAvatar=\"" + codAvatar + "\" ";
      msg += "></userinfo>";
      return msg;
   }

    
    /** Adiciona um item ao inventario
     *  @param CoopObjeto obj
     *  @param boolean storeBD Se deve guardar o novo objeto no bco de dados, na tabela do inventario
     */
    public void addItemInventory(CoopObjeto obj,boolean storeBD) {        
        inventory.add(obj);
        if (storeBD)
            storeItemInventory(obj);
        imprimeInventario();
    }
    
    /* Remove item do inventario
     *
     */
    public void dropItemInventory(CoopObjeto obj) {
        inventory.remove(obj);
        System.out.println(obj);
        for (int i=0; i< inventory.size(); i++) {
            CoopObjeto o = (CoopObjeto) inventory.get(i);
            System.out.println(o);
        }
        deleteItemInventory(obj);
        imprimeInventario();
    }
    
    /*  Carrega o inventario atual do banco de dados
     *
     */
    public void  loadInventory() {
        OpVal chaves[] = new OpVal[1];
        chaves[0] = new OpVal("codUser",codUser);
        RDLista objetosInventario = new RDLista("CoopInventario",chaves);
        while(!objetosInventario.end()) {
            //obtem o proximo item
            CoopInventario invItem = (CoopInventario) objetosInventario.next();
            //cria um objeto e adiciona ao inventario
            CoopObjeto obj = new CoopObjeto(invItem.codObjeto);
            addItemInventory(obj,false);
        }
    }
    
    /** Salva um item do inventario no bd
     *
     */
    private void storeItemInventory(CoopObjeto obj) {
        CoopInventario invItem = new CoopInventario(codUser,obj.codObjeto);
        if (invItem.isNew()) {
            invItem.codUser = codUser;
            invItem.codObjeto = obj.codObjeto;
            invItem.salva();
        }
    }
    
    /** Deleta um item do inventario no bd
     *
     */
    private void deleteItemInventory(CoopObjeto obj) {
        CoopInventario objInventario = new CoopInventario(codUser,obj.codObjeto);
        if (!objInventario.isNew()) {
            objInventario.deleta();
        }
    }
    
    /* Guarda todo inventario no banco de dados
     *
     */
    private void storeInventory() {
        
    }
     
    public void imprimeInventario() {
        System.out.println("Inventario");
        for (int i=0; i<inventory.size(); i++) {
            CoopObjeto obj = (CoopObjeto) inventory.get(i);
            System.out.println(obj.codObjeto + " -> " + obj.nome);
        }
        System.out.println("Fim Inventario");

    }
}
