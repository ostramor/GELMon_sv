/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analysis.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Class that handles the storing and retrieving of data from the users table.
 * @author Bruno, Fábio Cunha
 */
public class Users extends MySQL{
            
    PreparedStatement st1;
    ResultSet rs;
    
    /**
    * Method that logs in a user and creates one if it doesn't exist in the database.
    * @param name User's name.
    * @param email User's email.
    * @param number User's phone number.
    * @return Return true if successful and false otherwise.
    */
    public boolean vfLogin(String name, String email, int number){      
             
        try{
           /* String query = "SELECT * FROM users WHERE email = ?";
            st1 = con.prepareStatement(query);
            st1.setString(1,email);
            rs=st1.executeQuery();
            
            if(rs.next()){
                System.out.println("Login OK");
                return true;
               
            } else {
                System.out.println("User não existe. Adicionado");
                String query2= "INSERT INTO users (email, name, number) values(?, ?, ?) ";
                st1 = con.prepareStatement(query2);
                st1.setString(1,email);
                st1.setString(2, name);
                st1.setInt(3, number);
                st1.executeUpdate();
                return true;*/        
           
    
            String query = "INSERT INTO users (email, name, number) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE name=?, number=?";
            st1 = con.prepareStatement(query);
            st1.setString(1,email);
            st1.setString(2,name);
            st1.setInt(3, number);
            st1.setString(4,name);
            st1.setInt(5, number);
            
            st1.executeUpdate();
                  
            
        } catch(SQLException ex) {
            System.out.println("vfLogin error:" + ex);
        }
        
     return true;   
    }

    /**
    * Method that tests if a string represents an integer.
    * @param s String to test.
    * @return Return true if s is an integer and false otherwise.
    */
    private static boolean isInteger(String s){
        
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException | NullPointerException e){
            return false;
        }
        
        return true;
    }
    
    /**
    * Method that retrieves the users' friends.
    * @param lAmigos The ArrayList<String> of users of which the friends are to be retrieved.
    * @return Returns the friends in a ArrayList<String> form.
    */
    public ArrayList<String> getFriendsInfo(ArrayList<String> lAmigos){
        
        ArrayList<String> sInfo = new ArrayList<String>();
        int j;
        
        for(j = 0; j < lAmigos.size(); j++){
            if (isInteger(lAmigos.get(j))){
                try{ //pesquisa SQL para extrair dados caso seja numero de telefone
                    String query = "SELECT * FROM users WHERE number = ?";
                    st1 = con.prepareStatement(query);
                    st1.setInt(1, Integer.parseInt(lAmigos.get(j)));
                    rs = st1.executeQuery();
                    if(rs.next()) {
                        String la1 = rs.getString("email"); //busca sql para obter do email do user
                        sInfo.add(la1);
                    }
                }catch (SQLException e) {
                    e.getMessage();
                    e.printStackTrace();
                }
            }
            else sInfo.add(lAmigos.get(j));
        }
        return sInfo;
    }
}
