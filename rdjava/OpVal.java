//
//  OpVal.java
//  RDJava
//
//  Created by Maicon Brauwers on Wed May 21 2003.
//  Copyright (c) 2003 __MyCompanyName__. All rights reserved.
//

package rdjava;

public class OpVal {
    private String field,field2,value,op,table1,table2;
    private boolean join;

    //opval sobre uma tabela
    public OpVal(String campo,String valor) {
        field = campo;
        value = valor;
        op = "=";
        join = false;
    }

    public OpVal(String campo, int valor) {
        this(campo, String.valueOf(valor));
    }
    
    //opval sobre uma tabela
    public OpVal(String campo,String valor,String oper) {
        this(campo,valor);
        op = oper;
    }

    //opval sobre duas tabelas, campos de comparacao diferentes (para fazer join)
    public OpVal(String tab1,String campo,String tab2,String campo2) {
        field = campo;
        value = "";
        table1 = tab1;
        table2 = tab2;
        //se o campo2 for vazio entao eh o mesmo nome do campo da primeira tabela
        if (campo2=="") {
            field2 = campo;
        }
        else {
            field2 = campo2;
        }
        join = true;
    }
    
    public boolean isJoin() {
        return join;
    }


    public String getTable1() {
        return table1;
    }

    public String getTable2() {
        return table2;
    }

    public String getField() {
        return field;
    }

    public String getField2() {
        return field2;
    }

    public String getOp() {
        return op;
    }

    public String getValue() {
        return value;
    }
    
    
}
