//
//  Avatar.java
//  CoopServer
//
//  Created by Maicon Brauwers on Tue Aug 26 2003.
//  Copyright (c) 2003 __MyCompanyName__. All rights reserved.
//

/** Avatar Class
*  This is a persistent class
*  @author Maicon Brauwers
*
*/

import rdjava.*;

public class Avatar extends RDObj {

    public int codAvatar;
    public String nomeAvatar;
    public String flashFile;    //flash file of this scene

    public Avatar() {
        super();
        String tabela = "avatar";
        String[] fields = { "codAvatar","nomeAvatar","flashFile" };
        String[] primaryKeys = { "codAvatar" };

        addTabela(tabela);
        setCampos(tabela,fields);
        setPkFields(tabela,primaryKeys);
        setAutoIncFields(tabela,"codAvatar");
    }

    public Avatar(OpVal chaves[]) {
        this();
        le(chaves);
    }

}
