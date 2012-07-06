/*
 * CoopInventario.java
 *
 * Created on December 5, 2003, 10:03 AM
 */

/**
 *
 * @author  maicon
 */

import rdjava.*;

/** Classe do inventario
 *  Eh um associacao ente CoopUser e CoopObjeto, relacionando um usuario
 *  com os objetos os quais ele possui no seu inventario
 */
public class CoopInventario extends RDObj {

    public int codUser,codObjeto;
    
    /** Creates a new instance of CoopInventario */
    public CoopInventario() {
       super();
       String tabela = "CoopInventario";
       String[] fields = { "codUser","codObjeto"};
       String[] primaryKeys = { "codUser","codObjeto" };
        
       addTabela(tabela);
       setCampos(tabela,fields);
       setPkFields(tabela,primaryKeys);
    
    }

    public CoopInventario(OpVal[] chaves) {
        this();
        le(chaves);
    }
    
    /** Cria um objeto do inventario 
     *  @param int codUser
     *  @param int codObjeto
     */
    public CoopInventario(int codUser, int codObjeto) {
        this();
        OpVal[] chaves = new OpVal[2];
        chaves[0] = new OpVal("codUser",codUser);
        chaves[1] = new OpVal("codObjeto",codObjeto);
        le(chaves);
    }
}
