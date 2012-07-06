//
//  RDLista.java
//  RDJava2
//
//  Created by Maicon Brauwers on Tue Jun 17 2003.
//  Copyright (c) 2003 __MyCompanyName__. All rights reserved.
//

package rdjava;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RDLista {
    
    private String objClass,ordem,tipoJoin;
    private ArrayList foreignKeys;
    private String[] tabelas,campos;
    private Statement sqlStatement;
    /** Contador o elemento atual **/
    private int elAtual;
    public ArrayList records;
    
    
    public RDLista(String objectClass) {
        init(objectClass);
        lista();
    }

    public RDLista(String objectClass,String ordemReg) {
        init(objectClass);
        ordem = ordemReg;
        lista();
    }

    public RDLista(String objectClass,String ordemReg,String join) {
        init(objectClass);
        ordem = ordemReg;
        tipoJoin = join;
        lista();
    }
    
    public RDLista(String objectClass,OpVal[] chaves) {
        init(objectClass);	
        //chama o metodo de listagem
        lista(chaves);
    }

    public RDLista(String objectClass,OpVal[] chaves,String ordemReg) {
        init(objectClass);
        ordem = ordemReg;
        lista(chaves);
    }

    public RDLista(String objectClass,OpVal[] chaves,String ordemReg,String join) {
        init(objectClass);
        ordem = ordemReg;
        tipoJoin = join;
        lista(chaves);
    }
    
    
    /**  Inicializa as propriedades do objeto
     *
     */
    
    public void init(String objectClass) {
        objClass = objectClass;
        elAtual = 0;
        
        try {
            // cria uma instancia do objeto para poder pegar as chaves estrangeiras
            Class classe = Class.forName(objClass);
            RDObj obj = (RDObj) classe.newInstance();

            tabelas = obj.getTables();
            campos = obj.getFieldNamesOfDb();
            foreignKeys = obj.getForeingKeys();

            records = new ArrayList();

            //usa a conexao padrao
            sqlStatement = RDConnection.getStatement();

            //tipo de join padrao
            tipoJoin = "INNER JOIN";
            ordem = "";
        }
        
        catch(IllegalAccessException e) { System.out.println(e); }
        catch(InstantiationException e) { System.out.println(e); }
        catch(ClassNotFoundException e) { System.out.println(e); }        

    }

    /** Chama os metodos de query no bd e da criacao dos objetos
     *  
     */
    public void lista() {	
        ResultSet rs = getResultSet(null);  //null porque nao passa nenhuma chave 
        createObjects(rs);
    }

    /** Chama os metodos de query no bd e da criacao dos objetos
      *
      */    
    public void lista(OpVal[] chaves) {
        ResultSet rs = getResultSet(chaves);
        createObjects(rs);
    }

    /**  Faz a consulta ao banco de dados e retorna o objeto ResultSet 
     *
     */
    
    public ResultSet getResultSet(OpVal[] chaves) {

        //junta as chaves estrangeiras e as chaves where num so array
        OpVal keys[] = new OpVal[chaves.length+foreignKeys.size()];

        //coloca as chaves estrangeiras
        if (foreignKeys.size() > 0) {
            for (int i=0; i<foreignKeys.size();i++) {
                keys[i] = (OpVal) foreignKeys.get(i);	
            }
        }

        //coloca as chaves where
        if (chaves.length > 0) {
            System.arraycopy(chaves,0,keys,foreignKeys.size(),chaves.length);
        }

        RDRecBuilder rec = new RDRecBuilder(tabelas,campos,sqlStatement,tipoJoin);
        rec.setOrder(ordem);
        ResultSet rs = rec.getListOfRecords(chaves);
        return rs;
    }

    /** Cria os objetos e coloca no conteiner records
     *
     */
    
    private void createObjects(ResultSet rs) {
        try {
            while(rs.next()) {
                try {
                    Class classe = Class.forName(objClass);
                    RDObj obj = (RDObj) classe.newInstance();
                    obj.parseFieldsFromResultSetRow(rs);
                    obj.setKeys();
                    obj.setExisting();
                    records.add(obj);
                }
                catch(IllegalAccessException e) { System.out.println(e); }
                catch(InstantiationException e) { System.out.println(e); }
                catch(ClassNotFoundException e) { System.out.println(e); }                
            }
        }
        catch(SQLException e) { System.out.println(e); }
    }

    /* Retorna o proximo elemento
     *
     */   
    public RDObj next() {
       int el = elAtual;
       elAtual++;
       if (el < records.size()) 
           return (RDObj) records.get(el);
       else
           return null;
    }
    
    /* Retorna se acabou de percorrer a lista
     *
     */
    public boolean end() {
       return elAtual == records.size(); 
    }
    
}
