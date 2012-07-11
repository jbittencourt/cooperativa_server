//
//  RDConnection.java
//  RDJava2
//
//  Created by Maicon Brauwers on Tue Jun 10 2003.
//  Copyright (c) 2003 __MyCompanyName__. All rights reserved.
//

package rdjava;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.*;
import java.util.Properties;

public class RDConnection {
    static private Connection conn;
    static private Statement stmt;

    public static void connect(String propFile) {
            //le o arquivo que contem as propriedades
        try {
            FileInputStream fileProp = new FileInputStream(propFile);
            Properties prop = new Properties();
            try {
                prop.load(fileProp);

                //le os dados da tabela de propriedades
                String driverClass = (String) prop.get("driverClass");
                String subProtocol = (String) prop.get("subProtocol");
                String host = (String) prop.get("host");
                String db = (String) prop.get("database");
                String user = (String) prop.get("user");
                String password = (String) prop.get("password");

                //instancia a classe do driver
                Class.forName(driverClass).newInstance();
                
                //estabelece a conexao com o banco de dados
                String url = "jdbc:" + subProtocol + "://" + host + "/" + db + "?user=" + user + "&password=" + password + "&db=" + db + "useUnicode=true&characterEncoding=utf-8";
                conn = DriverManager.getConnection(url);
                stmt = conn.createStatement();
            }
            catch (ClassNotFoundException cnfex) {
                System.out.println("Classe do driver para o banco de dados nao existe");
            }
            catch (InstantiationException ie) {
                System.out.println(ie);
            }
            catch(IllegalAccessException iae) {
                System.out.println(iae);
            }
            catch (SQLException sqlex) {
                System.out.println("Nao foi possivel conectar ao banco de dados ");
                System.out.println(sqlex);
            }
        }
        catch(FileNotFoundException e) {
            System.out.println("Arquivo de propriedade nao encontrado");
        }
        catch(IOException e) {
            System.out.println(e);
        }

    }	

/*        catch (ClassNotFoundException cnfex) {
            System.out.println(cnfex);
        }
        catch (SQLException sqlex) {
            System.out.println(sqlex);
        }
        catch (InstantiationException ie) {
            System.out.println(ie);
        }
        catch(IllegalAccessException iae) {
            System.out.println(iae);
        }*/
        
    
    public static Connection getConnection() {
        return conn;
    }

    public static Statement getStatement() {
        return stmt;
    }


    
}
