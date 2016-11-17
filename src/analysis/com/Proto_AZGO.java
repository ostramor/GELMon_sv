/*
 * FEUP / MIEEC / SETEC / 2016 / Group B
 * http://fe.up.pt/
 *
 * 201202877 / Artur Antunes
 * 200907504 / Bruno Gonçalves
 * 201106784 / Eugenio Carvalhido
 * 201105402 / Fábio Cunha
 * 201206114 / Filipe Rocha
 * 201105621 / José Carvalho
 * 201100603 / Luís Pinto
 * 201200617 / Pedro Fonseca
 * 201201704 / Raquel Ribeiro
 * 201202703 / Rubens Figueiredo
 * 201109265 / Vânia Vieira
 */

package analysis.com;

import tools.*;
import java.io.BufferedReader;
import java.io.PrintWriter;


/**
 * The protocol used to comunicate with the AZGO mobile client.
 * 
 * @author Artur Antunes
 * @author Rubens Figueiredo
 */
public class Proto_AZGO extends Protocol {

    /**
     * Simple Constructor. Just calls the superclass' constructor.
     * 
     * @param out the uplink channel (PrintWriter)
     * @param in the downlink channel (BufferedReader)
     */
    public Proto_AZGO(PrintWriter out, BufferedReader in) {
        super(out, in);
    }

    @Override
    public String decode(String received){
        String[] tokens = received.split("@");
        switch (tokens[0]){
            case "Login": return handlerLogin(tokens);
            //case "Logout": return handlerLogout(tokens);
            default: return received.concat("_OK");
        }
    }
    
    private String handlerLogin(String[] tokens){
        
        return "ok";
    }

}
