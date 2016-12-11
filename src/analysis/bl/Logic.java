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

package analysis.bl;

import analysis.db.*;
import analysis.dsp.Heatmap;
import java.awt.image.BufferedImage;
import java.sql.Timestamp;
import java.util.ArrayList;
import tools.FileSystem;
import static java.lang.Math.toIntExact;


/**
 * A class with static methods used to process the clients requests.
 * 
 * @author Artur Antunes
 * @author Rubens Figueiredo
 */
public class Logic {
    
    
    private static String imgFolder;
//    private static String vidFolder;
    private static String url;
    
    /**
     * Gets the reqeusted heatmap path, creating it if it doesn't exist.
     * 
     * @author Rubens Figueiredo
     * @param date1
     * @param date2
     * @return
     */
    public static String getHeatmap(Long date1, Long date2){
        
        String fileName = MD5.crypt(date1.toString().concat(date2.toString()));
        String filePath = System.getenv("$HOME") + "/public_html/" + imgFolder + "/" + fileName + ".png";
        String fileURL = url + "/" + imgFolder + "/" + fileName + ".png";
        
        if (FileSystem.fileExists(filePath)) 
            return fileURL;
        
        // wait if being processed
        
        if (null == Heatmap.getBackground())
            return "ERROR : heatmap background null!!!";
        
        Double[][] values = new Double
            [Heatmap.getBackground().getWidth()]
            [Heatmap.getBackground().getHeight()];
        
        for (int i = 0; i < Heatmap.getBackground().getWidth(); i++)
            for (int j = 0; j < Heatmap.getBackground().getHeight(); j++)
                values[i][j] = (i + j) * 1.0/(Heatmap.getBackground().getWidth()+Heatmap.getBackground().getHeight());
        
        
        // Somehow get the values from the database
        // Convert them from coordenates
        
        Heatmap heatmap = new Heatmap(values);
        heatmap.generate();
        BufferedImage image = heatmap.toBufferedImage();
        
        FileSystem.saveImage(filePath, image);
        
        // update database
        // wake up waiting threads
        
        return fileURL;
    }
    
    private static Double[][] Smooth(Double[][] data,Integer w, Integer h){
        double[][] matriz= {
            {1,2,3,2,1},
            {2,3,4,3,2},
            {3,4,5,4,3},
            {2,3,4,3,2},
            {1,2,3,2,1}
        };
        double total = 73;
    
        Double[][] res= new Double[w][h];

        for (int i=0;i<w;i++){
            for (int j=0;j<h;j++){ 
                res[i][j]=0.0;
                for (int t=0;t<5;t++)
                    for (int l=0;l<5;l++)
                      res[i][j] += getMirroredValue(data, i+t-2, j+t-2, w, h) * matriz[t][l];
                res[i][j] /= total;
            }
        }
        return res;
    }
    
    private static Double getMirroredValue(Double[][]data, Integer i, Integer j, Integer w, Integer h){
        if (i<0)
            return getMirroredValue(data, i+1, j, w, h);
        if (j<0)
            return getMirroredValue(data, i, j+1, w, h);
        if (i>=w)
            return getMirroredValue(data, i-1, j, w, h);
        if (j>=h)
            return getMirroredValue(data, i, j-1, w, h);
        
        return data[i][j];
    }
    
    public static void setup() {
        Heatmap.setup();
        MySQL.setup();
    }
    
    public static int[] getNumberOfLocationsByHour (){
        
        int i, auxi;
        Long aux;
        int[] num = new int[24]; //Array com pessoas/hora
        Timestamp la, iniciots, fimts; //Variavel para converter string para timestamp
        
        String inicio = "2016-03-01 00:00:00"; //Data inicio para pesquisa(quary) na DB
        iniciots = Timestamp.valueOf(inicio);
        Long inicionum = iniciots.getTime()/1000; //Converte timestamp para inteiro e elimina 0s extra
        //System.out.println(inicionum);
        
        Long finnum = (inicionum+86399)*1000;  //Data final calculado (00:00:00+23:59:59) para pesquisa(quary) na DB
        fimts = new Timestamp(finnum);
        
        //String fin = "2016-03-01 23:59:59"; //Data final para pesquisa(quary) na DB
        //fimts = Timestamp.valueOf(fin);
        //Long finnum = fimts.getTime()/1000; //Converte timestamp para inteiro e elimina 0s extra
        //System.out.println(finnum);
        
        ArrayList<String> all = new ArrayList<String>(); //Inicializacao lista de timestamps(strings)
        ArrayList<Long> nmr = new ArrayList<Long>();    //Inicializacao lista de timestamps(long)
      /*  
       // --------------------------------------------------------------------------------------------
        all.add("2016-03-01 13:15:15");
        all.add("2016-03-01 13:00:00");
        all.add("2016-03-01 00:00:00");
        all.add("2016-03-01 16:00:00");         //Substituir por funcao da base de dados
        all.add("2016-03-01 20:10:15");         //Pode-se aproveitar a lista all na mesma
        all.add("2016-03-01 22:45:00");
        all.add("2016-03-01 08:30:00");
        all.add("2016-03-01 10:15:15");
        all.add("2016-03-01 23:59:59");
      // ---------------------------------------------------------------------------------------------
      */
        
        Locations loc = new Locations();
        //all = loc.getTimeLocation(iniciots, fimts);    //Busca a DB
        
        
        for (i=0; i<all.size(); i++){           //Converte lista de strings para long
            System.out.println(all.get(i));
            la = Timestamp.valueOf(all.get(i));
            nmr.add(la.getTime()/1000);         //Elimina 0s a mais
        }
        
        for (i=0; i<nmr.size(); i++){           //Contagem das pessoas/hora
            //System.out.println(nmr.get(i));
            aux=nmr.get(i)-inicionum;
            aux=aux/3600;
            auxi=aux.intValue();
            num[auxi]++;
        }
        return num;
    }
    
    public static int[] getNumberOfLocationsByDay (){ //Dias da semana
        
        int i, auxi;
        Long aux;
        int[] num = new int[7]; //Array com pessoas/hora
        Timestamp la, iniciots, fimts; //Variavel para converter string para timestamp
        
        String inicio = "2016-03-01 00:00:00"; //Data inicio para pesquisa(quary) na DB
        iniciots = Timestamp.valueOf(inicio);
        Long inicionum = iniciots.getTime()/1000; //Converte timestamp para inteiro e elimina 0s extra
        //System.out.println(inicionum);
        
        Long finnum = (inicionum+604799)*1000;  //Data final calculado (7 dias) para pesquisa(quary) na DB
        fimts = new Timestamp(finnum);
        
        //String fin = "2016-03-01 23:59:59"; //Data final para pesquisa(quary) na DB
        //fimts = Timestamp.valueOf(fin);
        //Long finnum = fimts.getTime()/1000; //Converte timestamp para inteiro e elimina 0s extra
        //System.out.println(finnum);
        
        ArrayList<String> all = new ArrayList<String>(); //Inicializacao lista de timestamps(strings)
        ArrayList<Long> nmr = new ArrayList<Long>();    //Inicializacao lista de timestamps(long)
    
        
        Locations loc = new Locations();
        //all = loc.getTimeLocation(iniciots, fimts);    //Busca a DB
        
        
        for (i=0; i<all.size(); i++){           //Converte lista de strings para long
            System.out.println(all.get(i));
            la = Timestamp.valueOf(all.get(i));
            nmr.add(la.getTime()/1000);         //Elimina 0s a mais
        }
        
        for (i=0; i<nmr.size(); i++){           //Contagem das pessoas/hora
            //System.out.println(nmr.get(i));
            aux=nmr.get(i)-inicionum;
            aux=aux/86400;
            auxi=aux.intValue();
            num[auxi]++;
        }
        return num;
    }
    
    public static int[] getNumberOfLocationsByInterval (Timestamp init, Timestamp fin, long step){
        
        int i, j, auxi;
        Long aux;
        long l_init=init.getTime();
        long l_fin=fin.getTime();
        //long l_step=step.getTime();
        int vectorsize = toIntExact((l_init-l_fin)/step);
        int[] num = new int[vectorsize]; //Array com pessoas/hora
        Timestamp la, iniciots, fimts; //Variavel para converter string para timestamp
        
        
        Long inicionum = init.getTime()/1000; //Converte timestamp para inteiro e elimina 0s extra
        //System.out.println(inicionum);
        
        /*Long finnum = (inicionum+86399)*1000;  //Data final calculado (00:00:00+23:59:59) para pesquisa(quary) na DB
        fimts = new Timestamp(finnum);*/
        
        //String fin = "2016-03-01 23:59:59"; //Data final para pesquisa(quary) na DB
        //fimts = Timestamp.valueOf(fin);
        //Long finnum = fimts.getTime()/1000; //Converte timestamp para inteiro e elimina 0s extra
        //System.out.println(finnum);
        
        ArrayList<String> all = new ArrayList<String>(); //Inicializacao lista de timestamps(strings)
        ArrayList<Long> nmr = new ArrayList<Long>();    //Inicializacao lista de timestamps(long)
        
        Locations loc = new Locations();
        
        
        //all = loc.getTimeLocation(init, fin);    //Busca a DB
        
        
        for (i=0; i<all.size(); i++){           //Converte lista de strings para long
            System.out.println(all.get(i));
            la = Timestamp.valueOf(all.get(i));
            nmr.add(la.getTime()/1000);         //Elimina 0s a mais
        }
        
        
        
        for (i=0; i<nmr.size(); i++){           //Contagem das pessoas/hora
            //System.out.println(nmr.get(i));
            aux=nmr.get(i)-inicionum;
            aux=aux/step;
            auxi=aux.intValue();
            num[auxi]++;
        }
        
        return num;
    }
    
}
