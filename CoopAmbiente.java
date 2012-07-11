//
//  CoopAmbiente.java
//  CoopServer
//
//  Created by Maicon Brauwers on Tue Aug 26 2003.
//  Copyright (c) 2003 __MyCompanyName__. All rights reserved.
//

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CoopAmbiente {

    public CoopAmbiente() {
	

    }

    /** Autentica o usuario
     *  @param nomUser String  Nome do usuario
     *  @param senha String Senha
     */
    public boolean autentica(String nomUser,String senha) {

        CoopUser user = new CoopUser(nomUser); //le usuario baseado no seu nome

	if (!user.isNew()) {
	    //compara as senhas, fazendo md5
	    return user.desSenha.equals(md5(senha));
	}
	else {
	    System.out.println("Nao consegui ler usuario");
	    return false;
	}
    }

    /* Devolve o md5 de um string
     *
     */
    
    private String md5(String str) {
        byte[] byteArray = str.getBytes();
        try {
            //cria o  engine de encriptacao
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteArray);
            byte hash[] = md5.digest();
		
            StringBuffer hexString = new StringBuffer(); //string dos bytes da senha
            for (int i=0;i<hash.length;i++) {
                String strReprOfByte = Integer.toHexString(0xFF & hash[i]);
                //cada byte eh convertido em dois caracteres
                //se o byte comecar por um caracter 0 este eh suprimido
                //logo deve-se acrecentar este catacter
                if (strReprOfByte.length() == 1) {
                  strReprOfByte = "0" + strReprOfByte;
                }
                hexString.append(strReprOfByte);
             }
            
            return hexString.toString();
        }
        catch(NoSuchAlgorithmException e) {
            System.out.println(e);
            return null;
        }
    }
    
    /** Cria um novo usuario.
     *  Retorna falso se houve algum erro na criacao do usuario
     */
    public boolean createUser(String nomUser,String password,String nomPessoa,int codAvatar) {
        CoopUser user = new CoopUser(nomUser);
        //verifica se nao existe ja um usuario com este nome
        if (user.isNew()) {
            user.nomUser = nomUser;
            user.desSenha = md5(password);
            user.desSenhaPlain = password;
            user.nomPessoa = nomPessoa;
            user.codAvatar = codAvatar;
            user.codHomeCenario = 1;
            user.salva();
            return true;
        }
        else 
           return false;
    }

}
