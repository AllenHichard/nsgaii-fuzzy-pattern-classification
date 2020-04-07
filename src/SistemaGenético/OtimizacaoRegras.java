
package SistemaGenético;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.ArrayRealSolutionType;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.encodings.variable.ArrayReal;
import jmetal.util.JMException;
import jmetal.util.wrapper.XReal;

/**
 * Class representing problem OtimizacaoRegras
 */
public class OtimizacaoRegras extends Problem {
    public static boolean codSemente;
    public  double  maiorPercentual;
    public static int controle;
    public  double interpretabilidade;
    public  String melhorArquivo;
    public static Cromossomo cromossomo;
    public static int[] vetorEntrada;
    public static double[] vetorPontosMedios;
    public static int numeroDeVariaveis;
    public double[] vetorPontosIniciais;
    private int limiteInferiorSaida;
    private int limiteSuperiorSaida;
    public int TamanhoRegraTotal;
    public static int TamanhoRegraEntrada;
    public static ArquivoTestObjetivo gerenciamentoArquivo;
    public String nomeArquivo;
    public TestFuzzy testFuzzy;
    public String conteudoArquivoFis;
    public int MaxRegras;
    public int qtdSaida;
    public List melhorRegra;
    public static double TotalAcuraciaGeracao;
    public static double TotalIntGeracao;
    
    public static double atualAGeral;
    public static double atualIGeral;
    
    public boolean primeiroConjuntoRegra;
    public  List conjuntoRegraInicio;
    public String arquivoFLCinicio;
    
    public static String auxSolucao;

    public double getMaiorPercentual() {
        return maiorPercentual;
    }

    public double getInterpretabilidade() {
        return interpretabilidade;
    }

    public String getMelhorArquivo() {
        return melhorArquivo;
    }
    
    public String getMelhorArquivoInicio() {
        return arquivoFLCinicio;
    }

    /**
     * Constructor. Creates default instance of problem ZDT3 (30 decision
     * variables.
     *
     * @param solutionType The solution type must "Real", "BinaryReal, and
     * "ArrayReal".
     */
     // OtimizacaoRegras

    public void inicializarVariaveis(String nomeArq) throws Throwable{     
        codSemente = true;
        controle = 0;
        atualAGeral = 0.0;
        atualIGeral = 0.0;
        TotalAcuraciaGeracao = 0.0;
        primeiroConjuntoRegra = true;
        TotalIntGeracao = 0.0;
        cromossomo = new ExecutavelCromossomo().getCromossomo(nomeArq);
        maiorPercentual = 0;
        interpretabilidade = 0;
        this.MaxRegras = cromossomo.getMaxRegras();
        this.vetorEntrada = cromossomo.getRegras();
        this.vetorPontosMedios = cromossomo.getPontosMedios();
        this.limiteInferiorSaida = cromossomo.getLimiteInferiorSaida();
        this.limiteSuperiorSaida = cromossomo.getLimiteSuperiorSaida();
        //System.out.println(limiteInferiorSaida);
        this.TamanhoRegraTotal = cromossomo.getTamanhoRegra();
        this.vetorPontosIniciais = cromossomo.getPontosIniciais();
        this.TamanhoRegraEntrada = this.TamanhoRegraTotal-1;
        this.nomeArquivo = cromossomo.getNomeArquivo();
        this.qtdSaida = cromossomo.getSaida();
        this.gerenciamentoArquivo = new ArquivoTestObjetivo(nomeArquivo);
        System.out.println("Tamanho Máximo de regras - " + MaxRegras);
        
    }
    /**
     * Constructor. Creates a instance of ZDT3 problem.
     *
     * @param numberOfVariables Number of variables.
     * @param solutionType The solution type must "Real", "BinaryReal, and
     * "ArrayReal".
     */
    public OtimizacaoRegras(String nomeArq) throws Throwable {
        inicializarVariaveis(nomeArq);
        double[] semente = cromossomo.getSemente();
        numberOfVariables_ = vetorEntrada.length + vetorPontosMedios.length;
        numeroDeVariaveis = numberOfVariables_;
        numberOfObjectives_ = 2;
        numberOfConstraints_ = 0;
        problemName_ = "PontosMedios";
        upperLimit_ = new double[numberOfVariables_];
        lowerLimit_ = new double[numberOfVariables_];
        int entrada = 0;
        for (int var = 0; var < vetorEntrada.length; var++) {
            if (entrada < TamanhoRegraEntrada) {
                lowerLimit_[var] = new Integer(1);
                upperLimit_[var] = new Integer(3);
                entrada++;
            } else{
                lowerLimit_[var] = semente[var];
                upperLimit_[var] = semente[var];
                //lowerLimit_[var] = new Integer(this.limiteInferiorSaida);
               // upperLimit_[var] = new Integer(this.limiteSuperiorSaida);
                entrada = 0;
            }
        } 
        entrada = 0;
        
        for (int var = vetorEntrada.length; var < numberOfVariables_; var++) {
            lowerLimit_[var] = calculoPontosMediosInf(vetorPontosMedios[entrada], vetorPontosIniciais[entrada]);
            upperLimit_[var] = calculoPontosMediosSup(vetorPontosMedios[entrada], vetorPontosIniciais[entrada]);
            entrada ++;
        }
        
        solutionType_ = new ArrayRealSolutionType(this,vetorEntrada.length,semente);
        
    } //ZDT3

   
    public int[] converterDoubleInt(Solution solution) throws JMException{
        int [] doubleInt = new int[vetorEntrada.length];
        for(int i = 0; i < vetorEntrada.length; i++){
            ArrayReal variavel = (ArrayReal)solution.getDecisionVariables()[0];
            doubleInt[i] = (int) variavel.getValue(i) ;
        }
        
        /*
        for(int i = 0; i < vetorEntrada.length; i++){
            System.out.print(doubleInt[i] + " ");
        }
        System.out.println("");
        */
        return doubleInt;
    }
    
    public double[] aproximacaoDouble(Solution solution) throws JMException{
        double [] real = new double[vetorPontosMedios.length];
        int posicao = 0;
        for(int i = vetorEntrada.length; i < numberOfVariables_ ; i++){
            ArrayReal variavel = (ArrayReal)solution.getDecisionVariables()[0];
            double valor = variavel.getValue(i);
            BigDecimal valorExato = new BigDecimal(valor).setScale(2, RoundingMode.HALF_DOWN);
            //real[posicao] = valorExato.doubleValue();
            real[posicao] = valor;
            posicao++;
        }
        /*
        for(int i = 0; i < vetorPontosMedios.length ; i++){
            System.out.print(real[i] + " ");
        }
        System.out.println("");
        */
        return real;
    }
    
   
    
    public double calculoPontosMediosInf(double pontoMedio, double ponto){ 
        double pontoDiferenca = (pontoMedio - ponto)/2;
        double pontoM = pontoMedio - pontoDiferenca;
        BigDecimal valorExato = new BigDecimal(pontoM).setScale(3, RoundingMode.HALF_DOWN);
        System.out.println("Ponto Min " + valorExato );
        return pontoM;
    }
    
    public double calculoPontosMediosSup(double pontoMedio, double ponto){ 
        double pontoDiferenca = (pontoMedio - ponto)/2;
        double pontoM = pontoMedio + pontoDiferenca;
        BigDecimal valorExato = new BigDecimal(pontoM).setScale(3, RoundingMode.HALF_DOWN);
        System.out.println("Ponto MAX " + valorExato);
        return pontoM;
    }
    
    /**
     * Evaluates a solution
     *
     * @param solution The solution to evaluate
     * @throws JMException
     */
    public void evaluate(Solution solution) throws JMException {
        
        //if(controleTest < 1000){
            double valorP, tempInterpretabilidade;
            double[] pontosMedios = aproximacaoDouble(solution);
            int [] regrasInt = converterDoubleInt(solution);
            Interpretabilidade inter = new Interpretabilidade(TamanhoRegraEntrada, regrasInt);
            regrasInt = inter.regrasSemelhantes();
            String conjuntoRegras = cromossomo.converterInverso(regrasInt,1);
            //System.out.println(conjuntoRegras);
            gerenciamentoArquivo.alterarPontosMediosERegras(pontosMedios, conjuntoRegras);
            conteudoArquivoFis = gerenciamentoArquivo.getConteudoArquivoFis();
            //System.out.println(conteudoArquivoFis);
            testFuzzy = new TestFuzzy(conteudoArquivoFis, nomeArquivo, cromossomo.getRegrasAGAtualizada());
            valorP = testFuzzy.testSimulado();
            tempInterpretabilidade = 1 - ((double)cromossomo.qtdRegrasAtual() / MaxRegras);
            //System.out.println(valorP);
            //System.out.println(tempInterpretabilidade);
            //System.out.println(cromossomo.qtdRegrasAtual());
            //System.out.println(qtdSaida);
            //ArrayReal variavel = (ArrayReal)solution.getDecisionVariables()[0];
            //variavel.redefinindoIntervalo(regrasInt);
            
            auxSolucao = conteudoArquivoFis;
            //System.out.println(cromossomo.qtdRegrasAtual());
            //System.out.println(qtdSaida);
            //System.out.println("");
            
            if(cromossomo.qtdRegrasAtual() >= qtdSaida && valorP  > maiorPercentual   /*|| (valorP == maiorPercentual && tempInterpretabilidade > interpretabilidade)*/){
                //System.out.println("Atualização AG");
                //System.out.println("Antigo Maior " + maiorPercentual);
                maiorPercentual = valorP;
                interpretabilidade = tempInterpretabilidade;
                ArrayReal variavel = (ArrayReal)solution.getDecisionVariables()[0];
                variavel.redefinindoIntervalo(regrasInt);
                melhorArquivo = conteudoArquivoFis;
                //System.out.println(conteudoArquivoFis);
                System.out.println("Acurácia           " + maiorPercentual);
                System.out.println("Interpretabilidade " + interpretabilidade);
                System.out.println("Otimização: " + controle);
                System.out.println("");
                melhorRegra = cromossomo.getRegrasAGAtualizada();
                //System.out.println("");
                //System.out.println("");
                //valorP = 0;
                controle = 0;
                if(primeiroConjuntoRegra){
                    conjuntoRegraInicio = melhorRegra;
                    arquivoFLCinicio = conteudoArquivoFis;
                    primeiroConjuntoRegra = false;
                }
            } else if(cromossomo.qtdRegrasAtual() >= qtdSaida && valorP == maiorPercentual && tempInterpretabilidade > interpretabilidade){
                maiorPercentual = valorP;
                interpretabilidade = tempInterpretabilidade;
                ArrayReal variavel = (ArrayReal)solution.getDecisionVariables()[0];
                variavel.redefinindoIntervalo(regrasInt);
                melhorArquivo = conteudoArquivoFis;
                melhorRegra = cromossomo.getRegrasAGAtualizada();
                controle = 0;
                System.out.println("Acurácia           " + maiorPercentual);
                System.out.println("Interpretabilidade " + interpretabilidade);
                System.out.println("Otimização: " + controle);
                System.out.println("");
            }
            //controleTest++;
            solution.setObjective(0, -valorP);
            solution.setObjective(1, -tempInterpretabilidade);
            //solution.setObjective(1, -tempInterpretabilidade);
            //solution.setObjective(1, -maiorPercentual);
         //System.out.println(controleTest);
            //System.out.println(controle);
            controle++;
            atualAGeral+=valorP;
            atualIGeral+=tempInterpretabilidade;
            
       // }
        
        
    } //evaluate
    
    public String getSolucaoAux(){
        return auxSolucao;
    }
    
    public int getControle(){
        return controle;
    }
    public List getMelhorRegra(){
        return melhorRegra;
    }
     public List getMelhorRegraPrimeira(){
        return conjuntoRegraInicio;
    }
}