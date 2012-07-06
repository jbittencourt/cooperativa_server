//  RDRecBuilder.java
//  RDJava
//
//  Created by Maicon Brauwers on Wed May 21 2003.
//  Copyright (c) 2003 __MyCompanyName__. All rights reserved.
//

package rdjava;


import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class RDRecBuilder {
    private StringBuffer sql;
    private String tipoJoin,order;
    private String[] fields,tables;
    private boolean boolDistinct;
    private Statement sqlStatement;

    public RDRecBuilder() {
        //se nao foi passado nenhum statement para fazer as consultas sql
        //entao usa o padrao ( o que esta  na classe RDConnection)
        //sqlStatament = RDConnection.getStatement();
    }
    
    public RDRecBuilder(String[] tabelas, String[] campos,Statement stmt) {
        tables = tabelas;
        fields = campos;
        sqlStatement = stmt;
        tipoJoin = "INNER JOIN";
        boolDistinct = false;
        order = "";
        


    }

    public RDRecBuilder(String[] tabelas, String[] campos,Statement stmt,String join) {
        this(tabelas,campos,stmt);
        tipoJoin = join;
    }
    
    public String buildSelect(OpVal chaves[]) {
        StringBuffer str,strWhere;
        sql = new StringBuffer(35);
        
        
        sql.append("SELECT ");

        if (isDistinct()) {
            sql.append(" DISTINCT ");
        }
        
        for (int i=0;i<tables.length;i++) {
            sql.append(" " + tables[i] + ".*,");
        }
        //tira a ultima virgula desnecessaria
        sql.deleteCharAt(sql.length()-1);

        sql.append(" FROM " + tables[0] + " ");

        str = new StringBuffer(10);
        strWhere = new StringBuffer(10);

        if (chaves != null) {

            for (int i=0; i<chaves.length; i++) {
                if (chaves[i] != null) {
                    if (chaves[i].isJoin()) {
                        //chave join
                        str.append(tipoJoin + " " + chaves[i].getTable2() + " ON ");
                        str.append(chaves[i].getTable1() + "." + chaves[i].getField() + "=");
                        str.append(chaves[i].getTable2() + "." + chaves[i].getField2() + " ");
                    }
                    else {
                        //chave where
                        if (strWhere.length() > 0) {
                            strWhere.append(" AND ");
                        }
                        else {
                            strWhere.append(" WHERE ");
                        }
                        strWhere.append(chaves[i].getField() + " " + chaves[i].getOp());
                        strWhere.append(" '" + chaves[i].getValue() + "'");

                    }

                }
            }
            sql.append(str.toString() + strWhere.toString());
        }
        
        String sqlOrder = getOrder();
        if (sqlOrder.length() > 0) {
            sql.append(" ORDER BY " + sqlOrder);
        }
        
        return sql.toString();

    }

    /** Retorna uma linha apenas
     *  Se a select construida retornar mais linhas, devolver null
     */
    
    public ResultSet getRecord(OpVal[] chaves) {
        buildSelect(chaves);
        try {
            ResultSet rs = sqlStatement.executeQuery(sql.toString());
            //verifica se ha apenas uma linha
            boolean hasOne = rs.next();
            boolean hasNext = rs.next();
            if (!hasOne || hasNext) {
                return null;
            }
            else {
                //ja posiciona no primeiro ( e unico) registro
                rs.first();
                return rs;
            }
        }
        catch (SQLException sqle) {
            System.out.println(sqle);
            return null;
        }

    }

    /**  Retorna uma lista de registros
     *
     */
    
    public ResultSet getListOfRecords(OpVal chaves[]) {
        buildSelect(chaves);
        try {
            ResultSet rs = sqlStatement.executeQuery(sql.toString());
            System.out.println(sql.toString());
            return rs;
        }
        catch(SQLException sqle) {
            System.out.println(sqle);
            return null;
        }
    }

    public int insertRecord(String[] fieldNames, String[] fieldValues) {
        sql = new StringBuffer(35);
        sql.append("INSERT INTO " + tables[0] + " SET ");
        if (fieldNames.length == fieldValues.length) {
            for (int i=0; i<fieldNames.length; i++) {
                if (fieldValues[i] != null) {
                    sql.append(fieldNames[i] + "='" + fieldValues[i] + "', ");
                }
            }
            sql.deleteCharAt(sql.length()-2);
            System.out.println(sql.toString());
            try {
                int rowAffected = sqlStatement.executeUpdate(sql.toString());
                return rowAffected;
            }
            catch (SQLException sqle) {
                System.out.println(sqle);
                return 0;
            }
        }
        else {
            return 0;
        }
        
    }

    public int updateRecord(String tabela,OpVal[] chaves,String[] fieldNames, String[] fieldValues) {
        sql = new StringBuffer(35);
        sql.append("UPDATE " + tabela + " SET ");
        for (int i=0; i<fieldNames.length;i++) {
            if (fieldValues[i] != null) {
                sql.append(fieldNames[i] + "='" + fieldValues[i] + "', ");
            }
        }
        sql.deleteCharAt(sql.length()-2);

        StringBuffer strWhere = new StringBuffer(15);
        for (int i=0;i<chaves.length;i++) {
            if (strWhere.length() > 0) {
                strWhere.append(" AND ");
            }
            else {
                strWhere.append(" WHERE ");
            }
            strWhere.append(chaves[i].getField() + " " + chaves[i].getOp());
            strWhere.append(" '" + chaves[i].getValue() + "'");
        }
        sql.append(strWhere);

        System.out.println(sql.toString());
        try {
            int rowAffected = sqlStatement.executeUpdate(sql.toString());
            return rowAffected;
        }
        catch (SQLException sqle) {
            System.out.println(sqle);
            return 0;
        }
    }
    
    public int deleteRecord(String tabela,OpVal[] chaves) {

        StringBuffer sql = new StringBuffer(20);
        sql.append("DELETE FROM " + tabela);
        
        StringBuffer strWhere = new StringBuffer(15);
        for (int i=0;i<chaves.length;i++) {
            if (strWhere.length() > 0) {
                strWhere.append(" AND ");
            }
            else {
                strWhere.append(" WHERE ");
            }
            strWhere.append(chaves[i].getField() + " " + chaves[i].getOp());
            strWhere.append(" '" + chaves[i].getValue() + "'");
        }
        sql.append(strWhere);

        System.out.println(sql.toString());
        try {
            int rowAffected = sqlStatement.executeUpdate(sql.toString());
            return rowAffected;
        }
        catch (SQLException sqle) {
            System.out.println(sqle);
            return 0;
        }
        
    }

    public void setOrder(String rec_order) {
        order = rec_order;
    }

    public String getOrder() {
        return order;
    }

    public void setDistinct() {
        boolDistinct = true;
    }

    public boolean isDistinct() {
        return boolDistinct;
    }

    public void setSqlStatement(Statement stmt) {
        sqlStatement = stmt;
    }
    
    
}
