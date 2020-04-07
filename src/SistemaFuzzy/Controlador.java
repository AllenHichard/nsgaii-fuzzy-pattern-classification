/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SistemaFuzzy;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.jFuzzyLogic.FIS;

/**
 *
 * @author allen
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.jFuzzyLogic.FIS;

public class Controlador {

    private FIS fis;
    private List atributos;
    private List classificacao;
    private List regras;
    private String saida;
    private List linhaEntrada;
    private List colunaEntrada;
    private List TestEntrada; // pega os valores do arquivo para teste
    private List TestSimulado; // pega os valores fuzzy
    private String nomeArquivo;
    public static double acuraciaWM;
    public static double interpretabilidadeWM;
    private int varEntrada;

    public Controlador(String nomeArquivo, List regras) throws IOException {
        this.regras = regras;
        classificacao = new LinkedList();
        atributos = new LinkedList();
        linhaEntrada = new LinkedList();
        TestEntrada = new LinkedList();
        TestSimulado = new LinkedList();
        this.nomeArquivo = nomeArquivo;
        iniciaInferencia();
        dadosEntrada();
        calculaInferencia();
        testSimulado();
    }

    private void iniciaInferencia() {
        try {
            BufferedReader ler = new BufferedReader(new FileReader("semente\\" + nomeArquivo + ".FLC"));
            String linha = ler.readLine();
            String conteudoArquivoFis = "";
            while (linha != null) {
                conteudoArquivoFis += linha + "\n";
                linha = ler.readLine();
            }
            ler.close();
            fis = FIS.createFromString(conteudoArquivoFis, true);
            //System.out.println("Instancia de inferencias carregada com sucesso");
        } catch (Exception ex) {
            Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, "Erro ao abrir o arquivo", ex);
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
        BufferedReader ler = new BufferedReader(new FileReader("entrada\\" + nomeArquivo + ".DAT"));
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
            //System.out.println("Nova Entrada");
            List coluna = (List) i.next();
            j = atributos.iterator();
            controle = 0;
            while (j.hasNext()) {
                String var = (String) j.next();
                this.fis.setVariable(var, Double.parseDouble((String) coluna.get(controle)));
                //System.out.print((String) coluna.get(controle) + " ");
                controle++;
            }
            String classificacaoSaida = ConversaoValor.nomeValor(coluna.get(coluna.size() - 1).toString());
            //System.out.println(classificacaoSaida);
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
            //System.out.println(c++);
            if ((i + 1) % (varEntrada + 1) != 0) {
                //System.out.println(atributos.get(controle).toString() + " " + regras.get(i).toString());
                double grau = fis.getVariable(atributos.get(controle).toString()).getMembership(regras.get(i).toString());
                //System.out.println(grau);
                tnorma = tnorma + grau;
                //System.out.print(grau + " ");
            } else {
                //System.out.println(tnorma);
                //System.out.println(regras.get(i).toString());
                if(tnorma > melhorTNorma){
                     //System.out.println("Atualizando melhor tnorma - " + tnorma );
                     melhorTNorma = tnorma;
                     tnorma = 0;
                     melhorClassificacao = regras.get(i).toString();
                     
                 } else {
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
        //System.out.println("Melhor Classificacao " +  melhorClassificacao);
        //System.out.println("Melhor T Norma " + melhorTNorma);
        //System.out.println("");
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

    private void testSimulado() {
        
        
        int count = 0;
        linhaEntrada.size();
        for (int i = 0; i < TestEntrada.size(); i++) {
            //System.out.println("Execucao - " + i);
            //System.out.println(TestEntrada.get(i).toString() + " - " + TestSimulado.get(i).toString());
            if (TestEntrada.get(i).toString().equals(ConversaoValor.nomeValor(TestSimulado.get(i).toString()))) {
                count++;
            }
        }
        //System.out.println(TestEntrada.get(0).toString());
        //System.out.println(TestSimulado.get(0).toString());
        System.out.println("Nome do Arquivo: " + nomeArquivo);
        System.out.println("Total de dados entrada: " + TestEntrada.size());
        System.out.println("Total de acertos do sistema: " + count);
        acuraciaWM = ((double) count / TestEntrada.size());
        System.out.println("Percentual de Acertos: " + acuraciaWM);
        double regrasqtd = regras.size() / (varEntrada + 1);
        System.out.println("Quantidade de Regras: " + regrasqtd );
        interpretabilidadeWM = 1 - regrasqtd / TestEntrada.size();
        System.out.println("Interpretabilidade " + interpretabilidadeWM);
    }

    public int MaxRegras() {
        return TestEntrada.size();
    }
}
