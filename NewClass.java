//  NSGAII_main.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.
package Test;

import ProcessamentoDat.distribuirPercentuais;
import SistemaFuzzy.Controlador;
import static SistemaGenético.OtimizacaoRegras.TamanhoRegraEntrada;
import static SistemaGenético.OtimizacaoRegras.vetorEntrada;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.encodings.variable.ArrayReal;
import jmetal.metaheuristics.nsgaII.*;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.ProblemFactory;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.Configuration;
import jmetal.util.JMException;

/**
 * Class to configure and execute the NSGA-II algorithm.
 *
 * Besides the classic NSGA-II, a steady-state version (ssNSGAII) is also
 * included (See: J.J. Durillo, A.J. Nebro, F. Luna and E. Alba "On the Effect
 * of the Steady-State Selection Scheme in Multi-Objective Genetic Algorithms"
 * 5th International Conference, EMO 2009, pp: 183-197. April 2009)
 */
public class NewClass {

    public static Logger logger_;      // Logger object
    //public static FileHandler fileHandler_; // FileHandler object

    /**
     * @param args Command line arguments.
     * @throws JMException
     * @throws IOException
     * @throws SecurityException Usage: three options -
     * jmetal.metaheuristics.nsgaII.NSGAII_main -
     * jmetal.metaheuristics.nsgaII.NSGAII_main problemName -
     * jmetal.metaheuristics.nsgaII.NSGAII_main problemName paretoFrontFile
     */
    public NewClass(String fold) throws JMException, SecurityException, IOException, ClassNotFoundException, Throwable {
        String nomeArq[] = new String[10];
        double acuraciaWM[] = new double[10];
        double interpretabilidadesWM[] = new double[10];
        double acuracias[] = new double[10];
        int geracoes[] = new int[10];
        double interpretabilidades[] = new double[10];
        //String fold = nome;
        File diretorio = new File("saida\\" + fold);
        diretorio.mkdir();
        for (int t = 1; t <= 1; t++) {

            diretorio = new File("saida\\" + fold + "\\" + "TREINAMENTO" + t);
            diretorio.mkdir();

            for (int i = 1; i <= 10; i++) {
                nomeArq[i - 1] = fold + "-10-" + i + "tra";
                //nomeArq[i-1] = fold;
            }
            String nomeArquivo = null;
            for (int i = 0; i < nomeArq.length; i++) {
                nomeArquivo = nomeArq[i];
                new distribuirPercentuais(fold, nomeArquivo).distribuindo();

                Problem problem; // The problem to solve
                Algorithm algorithm; // The algorithm to use
                Operator crossover; // Crossover operator
                Operator mutation; // Mutation operator
                Operator selection; // Selection operator

                HashMap parameters; // Operator parameters

                QualityIndicator indicators; // Object to get quality indicators

                // Logger object and file to store log messages
                logger_ = Configuration.logger_;
                //fileHandler_ = new FileHandler("NSGAII_main.log");
                //logger_.addHandler(fileHandler_);

                indicators = null;

                problem = new OtimizacaoRegras(nomeArquivo);
                algorithm = new NSGAII(problem);
    //algorithm = new ssNSGAII(problem);

                // Algorithm parameters
                algorithm.setInputParameter("populationSize", 100);
                algorithm.setInputParameter("maxEvaluations", 25000);

                // Mutation and Crossover for Real codification 
                parameters = new HashMap();
                parameters.put("probability", 0.9);
                parameters.put("distributionIndex", 20.0);
                crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);

                parameters = new HashMap();
                //parameters.put("probability", 1.0 / problem.getNumberOfVariables());
                parameters.put("probability", 0.05);
                parameters.put("distributionIndex", 20.0);
                mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);

                // Selection Operator 
                parameters = null;
                selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters);

                // Add the operators to the algorithm
                algorithm.addOperator("crossover", crossover);
                algorithm.addOperator("mutation", mutation);
                algorithm.addOperator("selection", selection);

                // Add the indicator object to the algorithm
                algorithm.setInputParameter("indicators", indicators);

                // Execute the Algorithm
                long initTime = System.currentTimeMillis();
                SolutionSet population = algorithm.execute();
                long estimatedTime = System.currentTimeMillis() - initTime;

                // Result messages 
                /*
                 logger_.info("Total execution time: " + estimatedTime + "ms");
                 logger_.info("Variables values have been writen to file VAR");
                 population.printVariablesToFile("VAR");
                 logger_.info("Objectives values have been writen to file FUN");
                 population.printObjectivesToFile("FUN");

                 if (indicators != null) {
                 logger_.info("Quality indicators");
                 logger_.info("Hypervolume: " + indicators.getHypervolume(population));
                 logger_.info("GD         : " + indicators.getGD(population));
                 logger_.info("IGD        : " + indicators.getIGD(population));
                 logger_.info("Spread     : " + indicators.getSpread(population));
                 logger_.info("Epsilon    : " + indicators.getEpsilon(population));

                 int evaluations = ((Integer) algorithm.getOutputParameter("evaluations")).intValue();
                 logger_.info("Speed      : " + evaluations + " evaluations");
                 } // if
                 */
                logger_.info("Total execution time: " + estimatedTime + "ms");
                logger_.info("Variables values have been writen to file VAR");
                population.printVariablesToFile("saida\\" + fold + "\\" + "TREINAMENTO" + t + "\\VAR" + i);
                logger_.info("Objectives values have been writen to file FUN");
                population.printObjectivesToFile("saida\\" + fold + "\\" + "TREINAMENTO" + t + "\\FUN" + i);

                double acuraciaM = 0;
                double interM = 0;
                double MelhorAcur = 0;
                double MelhorInter = 0;
                int index = 0;
                //System.out.println("Início Verificação");
                for (int w = 0; w < population.size(); w++) {
                    acuraciaM = population.get(w).getObjective(0);
                    interM = population.get(w).getObjective(1);
                    if (w == 0) {
                        MelhorAcur = acuraciaM;
                        MelhorInter = interM;
                        index = w;
                        //System.out.println("Primeiro");
                        //System.out.println("Acurácia  -  " + MelhorAcur);
                        //System.out.println("Inter  -  " + MelhorInter);
                    } else if (Math.abs(acuraciaM - interM) < Math.abs(MelhorAcur - MelhorInter)) {
                        //System.out.println("Continuação");
                        MelhorAcur = population.get(w).getObjective(0);
                        MelhorInter = population.get(w).getObjective(1);
                        index = w;
                    }
                }

                //System.out.println(population.get(index).getDecisionVariables()[0]);

                double[] pontosMedios = new double[OtimizacaoRegras.vetorPontosMedios.length];
                int posicao = 0;
                for (int c = OtimizacaoRegras.vetorEntrada.length; c < OtimizacaoRegras.numeroDeVariaveis; c++) {
                    ArrayReal variavel = (ArrayReal) population.get(index).getDecisionVariables()[0];
                    double valor = variavel.getValue(c);
                    BigDecimal valorExato = new BigDecimal(valor).setScale(2, RoundingMode.HALF_DOWN);
                    pontosMedios[posicao] = valorExato.doubleValue();
                    posicao++;
                }

                int[] regrasInt = new int[vetorEntrada.length];
                for (int c = 0; c < vetorEntrada.length; c++) {
                    ArrayReal variavel = (ArrayReal) population.get(index).getDecisionVariables()[0];
                    regrasInt[c] = (int) variavel.getValue(c);
                }

                
                //Interpretabilidade inter = new Interpretabilidade(TamanhoRegraEntrada, regrasInt);
                //regrasInt = inter.regrasSemelhantes();
                String conjuntoRegras = OtimizacaoRegras.cromossomo.converterInverso(regrasInt, 1);
                OtimizacaoRegras.gerenciamentoArquivo.alterarPontosMediosERegras(pontosMedios, conjuntoRegras);
                String conteudoArquivoFis = OtimizacaoRegras.gerenciamentoArquivo.getConteudoArquivoFis();

                //System.out.println(MelhorAcur);
                //System.out.println(MelhorInter);
                //System.out.println("Final Verificação");
                String nome = nomeArquivo;
                BufferedWriter gravar = new BufferedWriter(new FileWriter("saida\\" + fold + "\\" + "TREINAMENTO" + t + "\\" + nome + ".FLC"));
                OtimizacaoRegras o = (OtimizacaoRegras) problem;
                //gravar.write(o.getMelhorArquivo());
                gravar.write(conteudoArquivoFis);
                gravar.close();
                //gravar = new BufferedWriter(new FileWriter("saida\\" + nome + " - Desenpenho.txt"));
                //gravar.write("Acurácia - " + o.getMaiorPercentual() + "\r\n");
                //gravar.write("Interpretabilidade - " + o.getInterpretabilidade());
                //gravar.close();
                gravar = new BufferedWriter(new FileWriter("saida\\" + fold + "\\" + "TREINAMENTO" + t + "\\" + "WM - " + nome + ".FLC"));
                gravar.write(o.getMelhorArquivoInicio());
                gravar.close();
                gravar = new BufferedWriter(new FileWriter("saida\\" + fold + "\\" + "TREINAMENTO" + t + "\\" + nome + " - Regras.txt"));
                gravar.write(o.getMelhorRegra().toString());
                gravar.close();
                gravar = new BufferedWriter(new FileWriter("saida\\" + fold + "\\" + "TREINAMENTO" + t + "\\" + "WM - " + nome + " - Regras.txt"));
                gravar.write(o.conjuntoRegraInicio.toString());
                gravar.close();
                acuracias[i] = MelhorAcur;
                interpretabilidades[i] = MelhorInter;
                acuraciaWM[i] = Controlador.acuraciaWM;
                interpretabilidadesWM[i] = Controlador.interpretabilidadeWM;
                geracoes[i] = NSGAII.evolucao;
                System.out.println("Acurácia" + acuracias[i]);
                System.out.println("Interpretabilidade" + interpretabilidades[i]);
            }
            desvioPadrão("Genetico", geracoes, acuracias, interpretabilidades, fold, t);
            desvioPadrão("WangMendel", geracoes, acuraciaWM, interpretabilidadesWM, fold, t);
        }
    } //main

    public static void desvioPadrão(String type, int[] geracoes, double[] acuracias, double[] interpretabilidades, String nome, int t) throws IOException {
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
        BufferedWriter gravar = new BufferedWriter(new FileWriter("saida\\" + nome + "\\" + "TREINAMENTO" + t + "\\" + nome + " - " + type + " - Resultados.txt"));
        for (int i = 0; i < acuracias.length; i++) {
            gravar.write("Arquivo " + i + " - Acurácia = " + acuracias[i] + " +/- " + dpA + " - Interpretabilidade = " + interpretabilidades[i] + " +/- " + dpI + "\r\n");
        }
        gravar.write("Média Acurácia = " + mediaAcuracia + "\r\n");
        gravar.write("Média Interpretabilidade = " + mediaInterpretabilidade + "\r\n");
        if (type.equals("Genetico")) {
            for (int m = 0; m < 10; m++) {
                gravar.write("Arquivo " + m + " = " + geracoes[m] + "\r\n");
            }
        }
        gravar.close();
    }

} // NSGAII_main

