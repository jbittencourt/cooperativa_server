//
//  RDJava2.java
//
package rdjava;

import java.util.*;

public class RDJava2 {

    public static void main (String args[]) {

        String propFile = "/desenvolvimento/rdjava/db.ini";
        RDConnection.connect(propFile);

        /*
        RDUser userNovo = new RDUser();
        userNovo.nomUser = "teste1";
        userNovo.nomPessoa = "pessoaTeste1";
        userNovo.salva();
        System.out.println(userNovo.codUser + " : " + userNovo.nomUser);
         */
         
        /* Testes do RDLista */


        OpVal[] chaves = new OpVal[1];
        chaves[0] = new OpVal("codUser","0",">");

        RDLista lista = new RDLista("RDUser",chaves,"nomUser ASC");

        System.out.println("Lista.size() : " + lista.records.size());

        for(int i=0; i<lista.records.size(); i++) {
            RDUser obj = (RDUser) lista.records.get(i);
            System.out.println("codUser : " + obj.codUser + "  , nomUser: " + obj.nomUser);
        }
         
    }
}
