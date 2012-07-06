//
//  RDObj.java
//  RDJava
//
//  Created by Maicon Brauwers on Mon May 26 2003.
//  Copyright (c) 2003 __MyCompanyName__. All rights reserved.
//

package rdjava;

import java.util.ArrayList;
import java.util.HashMap;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.lang.reflect.*;
import java.lang.reflect.Field;

public class RDObj {

    private ArrayList tabelas;
    private HashMap campos,pkFields;	        
    private short novo;
    private OpVal[] chaves;
    private String tipoJoin;
    private RDRecBuilder recBuilder;
    private Statement sqlStatement;
    private HashMap autoIncFields;
    
    /** Inicializacao das propriedades
     *   
     */
    public RDObj() {
        tabelas = new ArrayList();
        campos = new HashMap();
        pkFields = new HashMap();
        novo = 1;
        tipoJoin = "INNER JOIN";
        recBuilder = null;
        //statement da conexao default
        sqlStatement = RDConnection.getStatement();
        autoIncFields = new HashMap();
    }

    /*  Sao setadas a tabela, seus campos e seus campos chave primaria
     *
     */
    public RDObj(String tabela,String[] fields,String[] primaryKeys) {
        this();
        addTabela(tabela);
        setCampos(tabela,fields);
        setPkFields(tabela,primaryKeys);
    }    

    /* Eh setado tambem os campos auto increment
     *
     */
    
    public RDObj(String tabela,String[] fields,String[] primaryKeys,String[] autoIncF) {
        this(tabela,fields,primaryKeys);
        autoIncFields.put(tabela,autoIncF);
    }

    /*  Le o registro do banco de dados e seta os campos do objeto
     *
     */
    
    public void le(OpVal[] chaves) {
        boolean sucesso = parseFieldsFromQuery(chaves);
        if (sucesso) {
            novo = 0;
            setKeys();
        }
        else {
            novo = 1;
        }
    }

    /** Retorna se o objeto eh novo
     *
     */
    public boolean isNew() {
	if (novo==0)
	    return false;
	else
	    return true;
    }

    /*  Adiciona uma tabela
     *
     */
    
    protected void addTabela(String tabName) {
        tabelas.add(tabName);
    }

    /**  Retorna o nome da tabela mais hierarquica
     *
     */    
    private String getHigherTableName() {
        return (String) tabelas.get(tabelas.size()-1);
    }	

    /*  Retorna o nome de todas as tabelas do objeto num array de strings
     *
     */
    
    public String[] getTables() {
        String[] tables = new String[tabelas.size()];
        tabelas.toArray(tables);
        return tables; 
    }
    
    /**  Seta o tipo de join
     *
     */
    
    private void setTipoJoin(String join) {
        tipoJoin = join;
    }
    
    private boolean setSqlStatement(Connection conn) {
        try {
            sqlStatement = conn.createStatement();
            return true;
        }
        catch (SQLException e) {
            return false;
        }
    }
    
    /** Retorna o nome de todos os campos de todas as tabelas
     *
     */
    
    protected String[] getFieldNamesOfDb() {
        ArrayList camposDb = new ArrayList();
        for (int i=0; i<tabelas.size(); i++) {
            String[] camposDaTabela = (String[]) campos.get(tabelas.get(i));
            for (int j=0; j<camposDaTabela.length; j++) {
                if (!camposDb.contains(camposDaTabela[j])) {
                    camposDb.add(camposDaTabela[j]);
                }
            }
        }


        String[] fieldsDb = new String[camposDb.size()];
        camposDb.toArray(fieldsDb);
        return fieldsDb;
        
    }

    /**  Seta os nomes dos campos de determinada tabela
     *
     */
    
    protected void setCampos(String tabName,String[] fields) {
        campos.put(tabName,fields);
    }

    /**  Seta os nomes dos campos chave primaria de determinada tabela
     *
     */
    
    protected void setPkFields(String tabName,String[] fields) {
        pkFields.put(tabName,fields);
    }

    protected void setAutoIncFields(String tabName,String[] fields) {
        autoIncFields.put(tabName,fields);
    }

    protected void setAutoIncFields(String tabName,String field) {
        String[] fieldsAutoInc = new String[1];
        fieldsAutoInc[0] = field;
        autoIncFields.put(tabName,fieldsAutoInc);
    }
    
    /* Retorna se o campo de determinada tabela eh um campo do tipo auto incremento
     *
     */
    
    private boolean isAutoIncField(String tabela,String campo) {
        boolean isAutoInc = false;
        String[] autoInc = (String[]) autoIncFields.get(tabela);
        if (autoInc != null) {
            for (int i=0;i < autoInc.length; i++) {
                if (autoInc[i]==campo) {
                    isAutoInc = true;
                }
            }
        }
        return isAutoInc;
    }


    /**  Seta o objeto como nao sendo novo
     *
     */

    protected void setExisting() {
        novo = 0;
    }
    
    /** Retorna os nomes dos campos chave de uma determinada tabela
     *
    */
    
    private String[] getPkFieldsOfTable(String tab) {
        return (String[]) pkFields.get(tab);
    }


    /* Retorna os nomes dos campos chave de todas as tabelas
     *
     */
    private ArrayList getPkFields() {
        ArrayList fields = new ArrayList();
        String nomeTabela;
        String[] pkFieldsOfTable;
        for (int i=0; i<tabelas.size(); i++) {
            nomeTabela = (String) tabelas.get(i);
            pkFieldsOfTable = getPkFieldsOfTable(nomeTabela);
            for (int j=0; j < pkFieldsOfTable.length; j++) {
                if (!fields.contains(pkFieldsOfTable[j])) {
                    fields.add(pkFieldsOfTable[j]);
                }
            }
        }
        return fields;
    }

    /** Retorna o nome das tabelas onde o campo eh chave
     *	
     */

    private ArrayList fieldIsKey(String campo) {
        ArrayList tabs = new ArrayList();
        String[] pkFieldsOfTable;
        for (int i=0; i<tabelas.size(); i++) {
            String nomeTabela = (String) tabelas.get(i);
            pkFieldsOfTable = getPkFieldsOfTable(nomeTabela);
            for (int j=0; j < pkFieldsOfTable.length; j++) {
                if (pkFieldsOfTable[j] == campo) {
                    tabs.add(nomeTabela);
                }
            }
        }

        return tabs;
    }

    /*  Retorna as chaves estrangeiras
     *
     */
     
    protected ArrayList getForeingKeys() {
        ArrayList fields = getPkFields();
        ArrayList tabs;
        ArrayList foreingKeys = new ArrayList();
        
        for (int i=0; i< fields.size(); i++) {
            tabs = fieldIsKey((String) fields.get(i));
            if (tabs.size() > 1) {
                String tab1 = (String) tabs.get(0);
                for (int j=1; j< tabs.size(); j++) {
                    OpVal chave = new OpVal(tab1,(String) fields.get(i), (String) tabs.get(j));
                    foreingKeys.add(chave);
                }
            }
        }

        return foreingKeys;        
    }

    /** Seta dinamicamente os campos do objeto em base num ResultSet
     * 
     */
    
    protected boolean parseFieldsFromResultSetRow(ResultSet record) {

        if (record != null) {
            /** usa reflection para setar dinamicamente os valores dos campos do
            *  objeto de acordo com o valor retornado pelo banco de dados
            */

	    //            System.out.println("Registro nao nulo");
            //System.out.println("Comecando a setar dinamicamente os campos do objeto");

            Class classe = this.getClass();
            Field[] fields = classe.getFields();

            Class typeClass;
            Field campo;

            //System.out.println("Campos da classe");
            for (int i=0;i<fields.length;i++) {
                String nomeCampo = fields[i].getName();
                typeClass = fields[i].getType();
                String tipoCampo = typeClass.getName();
		
                try {
                    campo = classe.getField(nomeCampo);
		    
                    if (tipoCampo=="java.lang.String") {
                        try {
                            String valorCampo = record.getString(nomeCampo);
                            //System.out.println("valor campo string : " + valorCampo);
                            try { campo.set(this,valorCampo); }
                            catch (IllegalAccessException e) { System.out.println(e); }
			}
			catch(SQLException e) { 
			    try { campo.set(this,""); }
                            catch (IllegalAccessException e2) { System.out.println(e2); }
			}
                    }
		    
		    
                    if (tipoCampo=="int") {
			try {
                            int valorCampo = record.getInt(nomeCampo);
			    try { campo.setInt(this,valorCampo); }
                            catch (IllegalAccessException e) { System.out.println(e); }
                        }
                        catch(SQLException e) {
			    try { campo.setInt(this,0); }
                            catch (IllegalAccessException e2) { System.out.println(e2); }
                        }
		    }
		    
                    if (tipoCampo=="float") {
                        try {
                            float valorCampo = record.getFloat(nomeCampo);
                            try { campo.setFloat(this,valorCampo); }
                            catch (IllegalAccessException e) { System.out.println(e); }
                        }
                        catch(SQLException e) { 
			    try { campo.setFloat(this,0); }
			    catch (IllegalAccessException e2) { System.out.println(e2); }
			}
                    }
		    
                    if (tipoCampo=="double") {
                        try {
                            double valorCampo = record.getDouble(nomeCampo);
                            try { campo.setDouble(this,valorCampo); }
                            catch (IllegalAccessException e) { System.out.println(e); }
                        }
                        catch(SQLException e) { 
			    try { campo.setDouble(this,0); }
			    catch (IllegalAccessException e2) { System.out.println(e2); }
			}
                    }

                    if (tipoCampo=="boolean") {
                        try {
                            boolean valorCampo = record.getBoolean(nomeCampo);
                            try { campo.setBoolean(this,valorCampo); }
                            catch (IllegalAccessException e) { System.out.println(e); }
                        }
                        catch(SQLException e) { 
			    try { campo.setBoolean(this,false); }
			    catch (IllegalAccessException e2) { System.out.println(e2); }
			}
                    }
		}
                catch(NoSuchFieldException e) {
                    System.out.println(e);
                }

            }            

            return true;
        }

        else {
            System.out.println("Registro nulo");
            return false;
        }

    }
    
    protected boolean parseFieldsFromQuery(OpVal[] chaves) {
        ArrayList keys = new ArrayList();

        //coloca as chaves num array dinamico para ficar mais facil de manipular

        if (chaves != null) {
            for (int i=0; i<chaves.length; i++) {
                keys.add(chaves[i]);
            }
        }

        //pega as chaves estrangeiras (se existirem)
        if (tabelas.size() > 1) {
            ArrayList fgKeys = getForeingKeys();
            if (fgKeys != null) {
                keys.addAll(fgKeys);
            }
        }
        
        //reconverte chaves para array
        OpVal[] keysArray = new OpVal[keys.size()];
        keys.toArray(keysArray);
        String[] tabs = new String[tabelas.size()]; //tabelas
        tabelas.toArray(tabs);
        String[] camposDb = getFieldNamesOfDb();    //campos

        if (sqlStatement == null) {
            sqlStatement = RDConnection.getStatement();
        }
        else {
            
        }
        

        //faz a query
        recBuilder = new RDRecBuilder(tabs,camposDb,sqlStatement,tipoJoin);
        ResultSet record = recBuilder.getRecord(keysArray);

        //seta os campos do objeto de acordo com o resultSet
        boolean sucesso = parseFieldsFromResultSetRow(record);
        return sucesso;
    }

    /* Gera um array de todos os valores dos campos relativos ao bd
     *	
     */
    
    public String[] toArray() {
        String[] camposDb = getFieldNamesOfDb();
        String[] fieldValues = new String[camposDb.length];

        Class classe = this.getClass();
        Field[] fields = classe.getFields();
        Class typeClass;
        Field campo;

        for (int i=0; i<camposDb.length; i++) {
            try {
                campo = classe.getField(camposDb[i]);
                try {
                    Object valor = campo.get(this);
                    if (valor != null) {
                        fieldValues[i] = (String) valor.toString();
                    }
                }
                catch(IllegalAccessException e) { System.out.println(e); };
            }
            catch(NoSuchFieldException e) { System.out.println(e); };
        }

        return fieldValues;
        
    }

    protected void setKeys() {

        String tabela = getHigherTableName();
        String[] pkFields = getPkFieldsOfTable(tabela);

        Class classe = this.getClass();
        Class typeClass;
        Field campo;        

        chaves = new OpVal[pkFields.length];
        
        for (int i=0; i < pkFields.length; i++) {
            try {
                campo = classe.getField(pkFields[i]);
                Object valor = campo.get(this);
                String valorCampo = (String) valor.toString();
                if (valorCampo != null) {
                    chaves[i] = new OpVal(pkFields[i],valorCampo);
                }
            }
            catch (NoSuchFieldException e) {System.out.println(e); }
            catch (IllegalAccessException e) {System.out.println(e); }
        }
        
    }

    public OpVal[] getKeys() {
        return chaves;
    }
    
    /* Faz uma leitura apos a insercao do objeto
     *
     */
    private void readAfterInsert() {
        //faz um select com base nos valores dos campos
        String tabela = getHigherTableName();
        String[] fields = (String[]) campos.get(tabela);  //array dos campos da tabela mais alta
        ArrayList fgKeys = getForeingKeys();
        OpVal[] keys = new OpVal[fields.length+fgKeys.size()];  //chaves estrangeiras mais as chaves com os valores dos campos
        fgKeys.toArray(keys);

        Class classe = this.getClass();
        Class typeClass;
        Field campo;
        int index = fgKeys.size();
        for (int i=0; i<fields.length;i++) {
            if (!isAutoIncField(tabela,fields[i])) {
                try {
                    campo = classe.getField(fields[i]);
                    Object valor = campo.get(this);
                    if (valor != null) {
                        String valorCampo = (String) valor.toString();
                        keys[index] = new OpVal(fields[i],valorCampo);
                        index++;
                    }
                }
                catch (NoSuchFieldException e) { System.out.println(e); }
                catch (IllegalAccessException e) {System.out.println(e); }

            }
        }
        parseFieldsFromQuery(keys);
        setKeys();
    }
    
    private void readAfterUpdate() {
        OpVal[] keys = getKeys();
        parseFieldsFromQuery(keys);
    }

    /** Insere um registro novo no banco de dados
        *
        */
    private void salvaNovo() {
        String[] camposDb = getFieldNamesOfDb();
        String[] fieldValues = toArray();

        if (sqlStatement == null) {
            //pega o statement default
            sqlStatement = RDConnection.getStatement();
        }

        String[] tabs = new String[tabelas.size()];
        tabelas.toArray(tabs);
        
        RDRecBuilder recBuilder = new RDRecBuilder(tabs,camposDb,sqlStatement,tipoJoin);
        recBuilder.insertRecord(camposDb,fieldValues);
        setKeys();
        readAfterInsert();

    }	
    
    private void salvaExistente() {

        String[] fieldValues = toArray();

        if (sqlStatement == null) {
            sqlStatement = RDConnection.getStatement();
        }

        String[] tabs = new String[tabelas.size()];
        tabelas.toArray(tabs);

        String tabela = getHigherTableName();
        String[] camposTabela = (String[]) campos.get(tabela);
        String[] camposDb = getFieldNamesOfDb();

        OpVal[] keys = getKeys();
        
        RDRecBuilder recBuilder = new RDRecBuilder(tabs,camposDb,sqlStatement,tipoJoin);
        recBuilder.updateRecord(tabela,keys,camposTabela,fieldValues);
        setKeys();
        readAfterUpdate();
    
    }

    public void salva() {
        if (novo==1) {	
            salvaNovo();
            //objeto eh novo, fazer insert
        }			
        else {
            //objeto ja existe, fazer update
            salvaExistente();
        }        
    }

    public void deleta() {

        if (sqlStatement == null) {
            sqlStatement = RDConnection.getStatement();
        }

        String[] tabs = new String[tabelas.size()];
        tabelas.toArray(tabs);
        String tabela = getHigherTableName();
        String[] camposDb = getFieldNamesOfDb();
        OpVal[] keys = getKeys();
        
        RDRecBuilder recBuilder = new RDRecBuilder(tabs,camposDb,sqlStatement,tipoJoin);
        recBuilder.deleteRecord(tabela,keys);
    }
    
    public void print() {
        System.out.println("Imprimindo objeto");
        Class classe = this.getClass();
        Field[] fields = classe.getFields();

        Class typeClass;
        Field campo;

        System.out.println("Campos da classe");
        for (int i=0;i<fields.length;i++) {
            String nomeCampo = fields[i].getName();
            typeClass = fields[i].getType();
            String tipoCampo = typeClass.getName();
            System.out.print("Campo : " + nomeCampo + " Tipo : " + tipoCampo);
            try {
                campo = classe.getField(nomeCampo);
            }
            catch(NoSuchFieldException e) {
                System.out.println(e);
            }
        }            
        
        System.out.println("Final impressao objeto");
    }

}
