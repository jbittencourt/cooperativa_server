//
//  RDUser.java
//  RDJava
//
//  Created by Maicon Brauwers on Mon May 26 2003.
//  Copyright (c) 2003 __MyCompanyName__. All rights reserved.
//

package rdjava;

public class RDUser extends RDObj {
    public int codUser,id,tempo,flaRep,flaSuper;
    public String nomUser,nomPessoa,desSenha;
    
    public RDUser() {
        super();
        String tabela = "usuario";
        String[] fields = { "codUser","id","tempo","flaRep","flaSuper","nomUser","nomPessoa","desSenha" };
        String[] primaryKeys = { "codUser" };

        addTabela(tabela);
        setCampos(tabela,fields);
        setPkFields(tabela,primaryKeys);
        setAutoIncFields(tabela,"codUser");
    }

    public RDUser(OpVal[] chaves) {
        this();
        le(chaves);
    }

}
