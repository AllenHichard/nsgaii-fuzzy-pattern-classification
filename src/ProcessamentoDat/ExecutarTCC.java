package ProcessamentoDat;

import SistemaGenético.NSGAII_main;
import java.io.IOException;
import jmetal.metaheuristics.nsgaII.NSGAII;

/**
 *
 * @author allen
 */
public class ExecutarTCC {
 
    /*X                                                    X
    "iris", "tae", "hepatitis","newthyroid", "haberman", "bupa", "heart", 
               X
    "wine", "balance", "wisconsin", "ecoli" , "glass", "cleveland", 
    "pima", "australian", "contraceptive",
                                                    18            19           21         16
    "titanic", "banana","phoneme", "page-blocks",  "vehicle",     "segment",  "thyroid", "penbased",
        36        20       20         57         40          85          13
    "satimage", "ring","twonorm", "spambase", "texture", "coil2000", "marketing"
    */
    
    
    
    public static void main(String[] args) throws IOException, SecurityException, ClassNotFoundException, Throwable {
        String nomeArq[] = {"heart"};
        String nome;
        //System.out.println(nomeArq.length);
        for(int i = 0; i < 1; i++){
            nome = nomeArq[i];
            //new distribuirPercentuais(nome).distribuindo();
            new NSGAII_main(nome);
        }
    }
}
