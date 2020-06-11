/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aps_automato;

import com.singularsys.jep.EvaluationException;
import com.singularsys.jep.Jep;
import com.singularsys.jep.JepException;
import com.singularsys.jep.ParseException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 *
 * @author mathe
 */
public class APS_Automato {

    static final Pattern identificador = Pattern.compile("[a-z]([a-z]|[0-9]|[_])");
    static final Pattern op_atrib = Pattern.compile("=");
    static final Pattern numero = Pattern.compile("([0-9]([0-9])*)|([0-9]([0-9])*’.’[0-9]([0-9])*)");
    static final Pattern op_arit = Pattern.compile("(‘+’|‘-‘|‘*’|‘/’)");
    static final Pattern pv = Pattern.compile(";");
    static final Pattern expressao = Pattern.compile("identificador op_atrib (identificador | numero)(op_arit(identificador | numero) )* pv");

    static final List<String> variaveisLista = new ArrayList<String>();
    static final  ArrayList< Atribuicoes > atribuicoes = new ArrayList< Atribuicoes >();
    static final  Jep jep = new Jep();
    
    public static void main(String[] args) {
        APS_Automato aps = new APS_Automato();
        aps.leArquivo();
        
    }

    public void leArquivo() {
        String nome = JOptionPane.showInputDialog(null, "Digite o arquivo a ser processado");

        // Try e Catch para pegar o arquivo verificar se ele existe e preencher um vetor com os operadores.
        try {
            FileReader arq = new FileReader(nome); //home/sawa/NetBeansProjects/teste.txt                   
            Path path = Paths.get(nome);
            List<String> lista = Files.readAllLines(path);

            System.out.println("Conteúdo do arquivo:\n" + lista); //pra saber que leu  
            System.out.println("\n ==================================================");
            separaAtribuicaoDeExpressao(lista);

        } catch (IOException e) {
            System.err.printf("Erro ao abrir arquivo\n");
            e.getMessage();
             paraProgramaQueTemCoisaErradaNoArquivo();

        }

    }

    public void separaAtribuicaoDeExpressao(List<String> lista) {
        String listString = String.join(",", lista);
        String[] separaLinhas = listString.split(",,");

        leLinhas(separaLinhas[0]);
        leExpressao(separaLinhas[1]);
    }

    public void leLinhas(String lista) {

        String[] separaLinhas = lista.split(","); //separa em linhas

        int numItens = separaLinhas.length;

        for (int i = 0; i < numItens; i++) {
            String linha = separaLinhas[i];
            String[] caractere = linha.split(" ");
            validaAtribuicoes(caractere);
        }
        System.out.println("==================================================");
        //System.out.println(Arrays.toString(separaLinhas));
    }

    public void validaAtribuicoes(String[] caractere) {

        boolean identifVA = validarIdentificador(caractere[0]);
        boolean igualVA = validarOpAtrib(caractere[1]);
        boolean numeroVa = validarNumero(caractere[2]);

        if (identifVA == true && igualVA == true && numeroVa == true) {
            String montaAtribuicao = caractere[0] + " " + caractere[1] + " " + caractere[2];
            System.out.println("A atribuição '" + montaAtribuicao + "' é válida!\n");
            variaveisLista.add(caractere[0]);
            guardaVariaveis(caractere);
        } else {
            System.err.printf("O arquivo possui alguma atribuição errada!!!\n");
             paraProgramaQueTemCoisaErradaNoArquivo();
        }

    }
    
     public void guardaVariaveis(String[] caractere){
        String nomeVariavel = caractere[0];
        int variavel = Integer.parseInt(caractere[2]);
        
        Atribuicoes atrib = new Atribuicoes();
        atrib.setNomeVariavel(nomeVariavel);
        atrib.setVariavel(variavel);
        atribuicoes.add(atrib);
        
        try {
            jep.addVariable(nomeVariavel, variavel);
        } catch (JepException ex) {
            Logger.getLogger(APS_Automato.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     
      public void fazConta(String expressao){
        try {
            jep.parse(expressao);
           
            try {
                 Object result = jep.evaluate();
                 System.out.println("E o resultado dela é: " + result);
                 System.out.println("==================================================");
            } catch (EvaluationException ex) {
                Logger.getLogger(APS_Automato.class.getName()).log(Level.SEVERE, null, ex);
            }

            
           

        } catch (ParseException ex) {
            Logger.getLogger(APS_Automato.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void leExpressao(String expressao) {
       
        String[] caractere = expressao.split(" ");

        int numItens = caractere.length;
        int ultimoElementoTemQueSerPontoEVirgula = numItens -1;
        int penultimoTemQueSerNumeroOuVariavel = numItens - 2;

        validaExpressao(caractere, ultimoElementoTemQueSerPontoEVirgula, penultimoTemQueSerNumeroOuVariavel);

        for (int i = 2; i >= penultimoTemQueSerNumeroOuVariavel ; i++) {

            boolean enumero = validarNumero(caractere[i]);

            if (variaveisLista.contains(caractere[i]) || enumero == true) {
                if (caractere[i + 1].equals("+") || caractere[i + 1].equals("-")
                        || caractere[i + 1].equals("/") || caractere[i + 1].equals("*")) {

                    boolean operadores = validarOpArit(caractere[i + 1]);
                    if (operadores = false) {
                        System.err.printf("\nDepois da variável, é necessário vir um operador. Corriga sua expressão!!!\n");
                         paraProgramaQueTemCoisaErradaNoArquivo();
                    } else {
                        System.err.printf("\nDepois da variável, é necessário vir um operador. Corriga sua expressão!!!\n");
                         paraProgramaQueTemCoisaErradaNoArquivo();
                    }
                }
            } else {
                System.err.printf("\n Corriga sua expressão!!!\n");
                paraProgramaQueTemCoisaErradaNoArquivo();
            }

        }
       
         System.out.println("\nA expressão " + expressao + " é válida!\n");
          fazConta(expressao);
    }

    
   
   
    
    public void validaExpressao(String[] caractere, int ultimo, int penultimo) {
        boolean identifVA = validarIdentificador(caractere[0]);
        boolean igualVA = validarOpAtrib(caractere[1]);
        boolean pontoeVirgula = validarPv(caractere[ultimo]);
        boolean enumero = validarNumero(caractere[penultimo]);

        if (enumero == true || variaveisLista.contains(caractere[penultimo])) {
            if (identifVA == false || igualVA == false || pontoeVirgula == false) {

                System.err.printf("\nA expressão está com erro de sintaxe!!!\n");
                paraProgramaQueTemCoisaErradaNoArquivo();
            }
        }

    }
    
    public void paraProgramaQueTemCoisaErradaNoArquivo(){
        System.exit(0);
    }
    
    //////////////////////////////////////////////////////////////////////
    public static boolean validarIdentificador(String palavra) {
        boolean iden = false;
        Matcher matcher = identificador.matcher(palavra);
        if (matcher.find()) {
            iden = true;
        }
        return iden;
    }

    public static boolean validarNumero(String palavra) {
        boolean iden = false;
        Matcher matcher = numero.matcher(palavra);
        if (matcher.find()) {
            iden = true;
        }
        return iden;
    }

    public static boolean validarOpArit(String palavra) {
        boolean iden = false;
        Matcher matcher = op_arit.matcher(palavra);
        if (matcher.find()) {
            iden = true;
        }
        return iden;

    }

    public static boolean validarOpAtrib(String palavra) {
        boolean iden = false;
        Matcher matcher = op_atrib.matcher(palavra);
        if (matcher.find()) {
            iden = true;
        }
        return iden;

    }

    public static boolean validarPv(String palavra) {
        boolean iden = false;
        Matcher matcher = pv.matcher(palavra);
        if (matcher.find()) {
            iden = true;
        }
        return iden;
    }
}


