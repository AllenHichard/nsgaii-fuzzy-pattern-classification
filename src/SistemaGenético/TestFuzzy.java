/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SistemaGenético;

import SistemaFuzzy.Controlador;
import SistemaFuzzy.ConversaoValor;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.jFuzzyLogic.FIS;
import org.antlr.runtime.RecognitionException;

/**
 *
 * @author allen
 */
public class TestFuzzy {

    private FIS fis;
    private List atributos;
    private List classificacao;
    private String saida;
    private List regras;
    private List linhaEntrada;
    private List colunaEntrada;
    private List TestEntrada; // pega os valores do arquivo para teste
    private List TestSimulado; // pega os valores fuzzy
    private String nomeArquivo;
    private int varEntrada;

    

    public TestFuzzy(String conteudoArquivoFis, String nomeArquivo, List regras) {
        try {
            this.regras = regras;
            classificacao = new LinkedList();
            atributos = new LinkedList();
            linhaEntrada = new LinkedList();
            TestEntrada = new LinkedList();
            TestSimulado = new LinkedList();
            this.nomeArquivo = nomeArquivo;
            fis = FIS.createFromString(conteudoArquivoFis, true);
            dadosEntrada();
            calculaInferencia();
            //testSimulado();
            
        } catch (RecognitionException ex) {
            Logger.getLogger(TestFuzzy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestFuzzy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    public void classificacao(String linhatual) {

        String tipoclasse = linhatual.substring(linhatual.indexOf("{") + 1, linhatual.lastIndexOf("}"));
        String aux[] = tipoclasse.split(",");
        for (int i = 0; i < aux.length; i++) {
            classificacao.add(ConversaoValor.nomeValor(aux[i]));
        }
    }

    public void dadosEntrada() throws FileNotFoundException, IOException {
        BufferedReader ler = new BufferedReader(new FileReader("entrada\\" + nomeArquivo + "AG.DAT"));
        String linha = ler.readLine();
        while (!linha.contains("{")) {
            linha = ler.readLine();
        }
        classificacao(linha);
        linha = ler.readLine(); // input
        String[] var = linha.split(" ");
        for (int i = 1; i < var.length; i++) {
            atributos.add(var[i].split(",")[0]);
        }
        this.varEntrada = atributos.size();
        linha = ler.readLine(); // output
        saida = linha.split(" ")[1];
        ler.readLine();
        linha = ler.readLine();
        while (linha != null) {
            String aux[];
            if (linha.contains(", ")) {
                aux = linha.split(", ");
            } else {
                aux = linha.split(",");
            }
            colunaEntrada = new LinkedList();
            for (int i = 0; i < aux.length; i++) {
                /*
                 if (aux[i].contains("-")) {
                    aux[i] = aux[i].split("-")[0] + aux[i].split("-")[1];
                }
                */
                
                colunaEntrada.add(aux[i]);
            }
            linhaEntrada.add(colunaEntrada);
            linha = ler.readLine();
        }

    }
    
    public void calculaInferencia() {
        //linhaEntrada.size()
        Deffuzificacao();
    }
   /*
    public void calculaInferencia() {
        //linhaEntrada.size()
        for (int i = 0; i < linhaEntrada.size(); i++) {
            List coluna = (List) linhaEntrada.get(i); // coluna é a população de treinamento
            for (int j = 0; j < atributos.size(); j++) {
                this.fis.setVariable(atributos.get(j).toString(), Double.parseDouble((String) coluna.get(j)));
            }
            String classificacaoSaida = ConversaoValor.nomeValor(coluna.get(coluna.size() - 1).toString());
            TestEntrada.add(classificacaoSaida);
            //System.out.println(coluna.get(coluna.size()-1));
            this.fis.evaluate();
            double classe = this.fis.getVariable(saida).getLatestDefuzzifiedValue();
        }
    }
    */
    

    public void Deffuzificacao() {
        Iterator i = linhaEntrada.iterator();
        Iterator j;
        int controle;
        //int c = 1;
        while (i.hasNext()) {
            //System.out.println(c++);
            List coluna = (List) i.next();
            j = atributos.iterator();
            controle = 0;
            while (j.hasNext()) {
                String var = (String) j.next();
                this.fis.setVariable(var, Double.parseDouble((String) coluna.get(controle)));
                controle++;
            }
            String classificacaoSaida = ConversaoValor.nomeValor(coluna.get(coluna.size() - 1).toString());
            TestEntrada.add(classificacaoSaida);
            DeffuzificacaoFuzzyAtivados();
        }

    }

    private void DeffuzificacaoFuzzyAtivados() {
        //int c = 1;
        double melhorTNorma = 0;
        double tnorma = 0; // calculo para encontrar a melhor regra    
        int controle = 0;
        String melhorClassificacao = "";
        //System.out.println(regras.size());
        for (int i = 0; i < regras.size(); i++) {
           // System.out.println(c++);
            if ((i + 1) % (varEntrada + 1) != 0) {
                //System.out.println(atributos.get(controle).toString() + " --- " + regras.get(i).toString());
                double grau = fis.getVariable(atributos.get(controle).toString()).getMembership(regras.get(i).toString());
                //System.out.println(grau);
                tnorma = tnorma+grau;
            } else {
                //System.out.println(tnorma);
                if(tnorma > melhorTNorma){
                     melhorTNorma = tnorma;
                     tnorma = 0;
                     melhorClassificacao = regras.get(i).toString();
                     
                 } 
                 else {
                    tnorma = 0;
                 } 
                //System.out.println(" ");  System.out.println(regras.get(i)); System.out.println(" ");
            }
            if (controle < varEntrada) {
                controle++;
            } else {
                 controle = 0;
            }
        }
        //System.out.println(melhorClassificacao);
        //System.out.println(melhorTNorma);
        TestSimulado.add(melhorClassificacao);
    }

    //public static void main(String[] args) throws IOException {
    //Controlador c = new Controlador();
    //}
    
    /*
    private void conjuntosFuzzyAtivados() {
        double valorM = 0;
        String nome = "";
        for (int i = 0; i < classificacao.size(); i++) {
            double atual = fis.getVariable(saida).getMembership(classificacao.get(i).toString());
            if (atual > valorM) {
                valorM = atual;
                nome = classificacao.get(i).toString();
            }
        }
        TestSimulado.add(nome);
    }
    */

    public double testSimulado() {
        int count = 0;
        linhaEntrada.size();
       // System.out.println("Inicio\n\n");
        for (int i = 0; i < TestEntrada.size(); i++) {
           // System.out.println(TestEntrada.get(i).toString() + " --- "+ ConversaoValor.nomeValor(TestSimulado.get(i).toString()));
            if (TestEntrada.get(i).toString().equals(ConversaoValor.nomeValor(TestSimulado.get(i).toString()))) {
                count++;
            }
        }
       // System.out.println("Fim \n\n");
        //System.out.println(TestEntrada.get(0).toString());
        //System.out.println(TestSimulado.get(0).toString());
        //System.out.println("Nome do Arquivo: " + nomeArquivo);
        //System.out.println("Total de dados entrada: " + TestEntrada.size());
        //System.out.println("Test FUZZY");
        //System.out.println("Total de acertos do sistema: " + count);
        //System.out.println("Percentual de Acertos: " + ((double) count / TestEntrada.size()));
        //System.out.println("");
        return (double) count / TestEntrada.size();
    }

    public int MaxRegras() {
        return TestEntrada.size();
    }

}
