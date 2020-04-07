/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import SistemaFuzzy.ConversaoValor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.core.SolutionSet;
import net.sourceforge.jFuzzyLogic.FIS;

public class TESTE2 {

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
    private String nomeArquivoTest;
    private int varEntrada;

    public TESTE2(String fold, String nomeArquivo, String test, double[] Acuracia, double[] Interpretabilidade, int posicao) throws IOException {
        classificacao = new LinkedList();
        atributos = new LinkedList();
        linhaEntrada = new LinkedList();
        TestEntrada = new LinkedList();
        TestSimulado = new LinkedList();
        this.nomeArquivo = nomeArquivo;
        this.nomeArquivoTest = test;

        Carregarregras(fold, posicao);
        iniciaInferencia(fold, posicao);
        dadosEntrada();
        calculaInferencia();
        testSimulado(Acuracia, Interpretabilidade, posicao);
    }

    private void Carregarregras(String fold, int i) throws FileNotFoundException, IOException {
        regras = new ArrayList();
        BufferedReader ler = new BufferedReader(new FileReader("saida\\" + fold + "\\" + "TREINAMENTO" + "\\" + "WM - "+fold+"-"+(i+1)+ " - Regras.txt"));
        String regra = ler.readLine();
        regra = regra.substring(1, regra.length() - 1);
        String[] r = regra.split(", ");
        for (int j = 0; j < r.length; j++) {
            regras.add(r[j]);
        }
        ler.close();
    }

    private void iniciaInferencia(String fold, int i) {
        try {
            BufferedReader ler = new BufferedReader(new FileReader("saida\\" + fold + "\\" + "TREINAMENTO" + "\\" + "WM - "+fold+"-"+(i+1)+ ".FLC"));
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
            Logger.getLogger(TESTE.class.getName()).log(Level.SEVERE, "Erro ao abrir o arquivo", ex);
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
        BufferedReader ler = new BufferedReader(new FileReader("teste\\" + nomeArquivoTest + ".DAT"));
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
                //System.out.println(var);
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
                //System.out.println(atributos.get(controle).toString() + " " + regras.get(i).toString());
                double grau = fis.getVariable(atributos.get(controle).toString()).getMembership(regras.get(i).toString());
                //System.out.println(grau);
                tnorma = tnorma + grau;
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
        System.out.println(nome);
        TestSimulado.add(nome);
    }

    public double testSimulado(double[] Acuracia, double[] Interpretabilidade, int posicao) {
        int count = 0;
        linhaEntrada.size();
        //System.out.println("Novo Teste");
        for (int i = 0; i < TestEntrada.size(); i++) {
            //System.out.println(TestSimulado.get(i).toString());
            if (TestSimulado.get(i).toString().equals("")) {
                System.out.println("ERRO");
            }
            if (TestEntrada.get(i).toString().equals(ConversaoValor.nomeValor(TestSimulado.get(i).toString()))) {
                count++;
            }
        }
        //System.out.println(TestEntrada.get(0).toString());
        //System.out.println(TestSimulado.get(0).toString());
        //System.out.println("Nome do Arquivo: " + nomeArquivo);
        //System.out.println("Total de dados entrada: " + TestEntrada.size());
        //System.out.println("Test FUZZY");
        //System.out.println("Total de acertos do sistema: " + count);
        //System.out.println("Percentual de Acertos: " + ((double) count / TestEntrada.size()));
        //System.out.println("");
        Acuracia[posicao] = (double) count / TestEntrada.size();
        Interpretabilidade[posicao] = 1 - ((double) (regras.size() / (varEntrada + 1)) / TestEntrada.size());
        return (double) count / TestEntrada.size();
    }

    public int MaxRegras() {
        return TestEntrada.size();
    }

    public static void main(String[] args) throws IOException {
        int tam = 10;
        double[] Interpretabilidade = new double[tam];
        double[] Acuracia = new double[tam];
        String nomeArq[] = new String[tam];
        String nomeArqTest[] = new String[tam];
        
        String fold = "hepatitis";
        long initTime = System.currentTimeMillis();
        for (int i = 0; i < Acuracia.length; i++) {
            //System.out.println(fold + "-10-" + (i % 10 + 1) + "tra");
            nomeArq[i] = fold + "-10-" + (i % 10 + 1) + "tra";
            nomeArqTest[i] = fold + "-10-" + (i % 10 + 1) + "tst";
            new TESTE2(fold, nomeArq[i], nomeArqTest[i], Acuracia, Interpretabilidade, i);
        }
        long estimatedTime = System.currentTimeMillis() - initTime;
        desvioPadrão(fold, Acuracia, Interpretabilidade, fold, estimatedTime);
    }

    public static void desvioPadrão(String fold, double[] acuracias, double[] interpretabilidades, String nome, long estimatedTime) throws IOException {
        double somaAcuracia = 0, mediaAcuracia = 0, somaInterpretabilidade = 0, mediaInterpretabilidade = 0;
        double somatorioDPA = 0;
        double somatorioDPI = 0;
        for (int i = 0; i < acuracias.length; i++) {
            somaAcuracia += acuracias[i];
            somaInterpretabilidade += interpretabilidades[i];
        }
        mediaAcuracia = somaAcuracia / acuracias.length;
        mediaInterpretabilidade = somaInterpretabilidade / acuracias.length;
        for (int i = 0; i < acuracias.length; i++) {
            somatorioDPA += Math.pow(acuracias[i] - mediaAcuracia, 2);
            somatorioDPI += Math.pow(interpretabilidades[i] - mediaInterpretabilidade, 2);
        }
        double dpA = Math.sqrt(somatorioDPA / acuracias.length);
        double dpI = Math.sqrt(somatorioDPI / acuracias.length);
       // File diretorio = new File("saida\\" + fold + "\\" + "TESTE" + T);
        //diretorio.mkdir();
        BufferedWriter gravar = new BufferedWriter(new FileWriter("saida\\" + fold + "\\WM - Teste AG Resultados.txt"));
        for (int i = 0; i < acuracias.length; i++) {
            gravar.write("Arquivo " + i + " - Acurácia = " + acuracias[i] + " +/- " + dpA + "\r\n");
            //gravar.write("Arquivo " + (i + 1) + " - Acurácia = " + acuracias[i] + " +/- " + dpA + " - Interpretabilidade = " + interpretabilidades[i] + " +/- " + dpI + "\r\n");
        }
        gravar.write("\r\nAcurácia Média " + mediaAcuracia + "\r\n");
        //gravar.write("Média Interpretabilidade = " + mediaInterpretabilidade + "\r\n");
        gravar.write("\r\nTempo Execução Total " + estimatedTime + " ms\r\n");
        gravar.close();
    }

}
