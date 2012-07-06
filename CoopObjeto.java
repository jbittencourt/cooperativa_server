/*
 * CoopObjeto.java
 *
 * Created on September 26, 2003, 10:39 AM
 */

/**
 *
 * @author  maicon
 */

import rdjava.*;

/** CLASSE QUE REPRESENTA UM OBJETO DO CENARIO **/

public class CoopObjeto extends RDObj {

    public int codObjeto,codCenario,status,tag,flagInventario,codUser,posX,posY;
    public String nome;
    
    public CoopObjeto() {
        super();
        String tabela = "CoopObjeto";
        String[] fields = { "codObjeto","codCenario","nome","status","tag","posX","posY","flagInventario","codUser" };
        String[] primaryKeys = { "codObjeto" };
        
        addTabela(tabela);
        setCampos(tabela,fields);
        setPkFields(tabela,primaryKeys);
        setAutoIncFields(tabela,"codObjeto");
    }
    
    public CoopObjeto(OpVal[] chaves) {
        this();
        le(chaves);
    }

    public CoopObjeto(int codObjeto) {
        this();
        OpVal chaves[] = new OpVal[1];
        chaves[0] = new OpVal("codObjeto",codObjeto);
        le(chaves);
    }

    /** Le um objeto pelo id e ja seta os novos status e tag
     *
     */
    
    public CoopObjeto(int id, int status, int tag) {
        this(id);
        setStatus(status);
        setTag(tag);
    }
 
    /** Retorna a string xml do estado do objeto
     *
     */
    public String getXmlStatus() {
        String msg = "<object_status idobj=\"" + codObjeto + "\" ";
        msg += " status=\"" + status + "\" tag=\"" + tag + "\" ";
        msg += " flagInventario=\"" + flagInventario + "\" ";
        msg += " iduser=\"" + codUser + "\"";
        msg += " posx=\"" + posX + "\" posy=\"" + posY + "\">";
        msg += "</object_status>";
        return msg;
    }
    
    
    /** Retorna a string xml do estado do objeto
     *
     */
    public String getXmlChangedState() {
        String msg = "<change_object_state idobj=\"" + codObjeto + "\" ";
        msg += " status=\"" + status + "\" tag=\"" + tag + "\" ";
        msg += " flagInventario=\"" + flagInventario + "\" ";
        msg += " codUser=\"" + codUser + "\"";
        msg += " posx=\"" + posX + "\" posY=\"" + posY + "\">";
        msg += "</change_object_state>";
        return msg;
        
    }
    
    public int getId() {
        return codObjeto;
    }
    
    public void setId(int id) {
        codObjeto = id;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public int getTag() {
        return tag;
    }
    
    public void setTag(int tag) {
        this.tag = tag;
    }
    
    public int getFlagInventario() {
        return flagInventario;
    }
    
    public void setFlagInventario(int flag) {
        flagInventario = flag;
    }
    
    public int getCodUser() {
        return codUser;
    }
    
    public void setCodUser(int id) {
        codUser = id;
    }
    
    public int getPosX() {
        return posX;
    }
    
    public void setPosX(int x) {
        posX = x;
    }
    
    public int getPosY() {
        return posY;
    }
    
    public void setPosY(int y) {
        posY = y;
    }
}