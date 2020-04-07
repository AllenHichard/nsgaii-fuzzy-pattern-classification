/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SistemaFuzzy;

/**
 *
 * @author allen
 */
public class ConversaoValor {
    
    public static String nomeValor(String valor){
    
        valor = valor.trim();
        
        switch (valor) {
            case "0":
                return "ZERO";
            case "1":
                return "UM";
            case "2":
                return "DOIS";
            case "3":
                return "TRES";
            case "4":
                return "QUATRO";
            case "5":
                return "CINCO";
            case "6":
                return "SEIS";
            case "7":
                return "SETE";
            case "8":
                return "OITO";
            case "9":
                return "NOVE";
            case "10":
                return "DEZ";
            case "11":
                return "ONZE";
            case "12":
                return "DOZE";
            case "13":
                return "TREZE";
            case "14":
                return "TORZE";
            case "15":
                return "QUINZE";
            case "16":
                return "DSEIS";
            case "17":
                return "DSETE";
            case "18":
                return "DOITO";
            case "19":
                return "DNOVE";
            case "20":
                return "VINTE";
            case "21":
                return "VUM";
            case "22":
                return "VDOIS";
            case "23":
                return "VTRES";
            case "24":
                return "VQUATRO";
            case "25":
                return "VCINCO";
            case "26":
                return "VSEIS";
            case "27":
                return "VSETE";
            case "28":
                return "VOITO";
            case "29":
                return "VNOVE";
            case "-1.0":
                return "NUMD"; 
            case "1.0":
                return "UMD"; 
        } 
        return valor;
        
    }
    
}
