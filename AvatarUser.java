//
//  AvatarUser.java
//  CoopServer
//
//  Created by Maicon Brauwers on Tue Aug 26 2003.
//  Copyright (c) 2003 __MyCompanyName__. All rights reserved.
//

/** Stores de avatar that the user is using
 *  This is a persistent class
 *  @author Maicon Brauwers
 *
 */
import rdjava.*;

public class AvatarUser extends RDObj {

    public int codAvatar;
    public int codUser;
    
    public AvatarUser() {
        super();
        String tabela = "avatarUser";
        String[] fields = { "codAvatar","codUser" };
        String[] primaryKeys = { "codAvatar","codUser" };

        addTabela(tabela);
        setCampos(tabela,fields);
        setPkFields(tabela,primaryKeys);
    }
    
    public AvatarUser(OpVal chaves[]) {
        this();
        le(chaves);
    }

}