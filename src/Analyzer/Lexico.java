package Analyzer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Lexico {
    public ArrayList<String> palabrasReservadas = new ArrayList<>() {
        {
            add("INICIAR");
            add("cts");
            add("clase");
            add("metod");
            add("proced");
            add("entero");
            add("real");
            add("cadena");
            add("carac");
            add("verfal");
            add("devolver");
            add("si");
            add("sino");
            add("para");
            add("mientras");
            add("mostrar");
            add("leer");
        }
    };
    public ArrayList<String> operadores = new ArrayList<>() {
        {
            add("{");
            add("}");
            add("(");
            add(")");
            add("=");
            add("<<");
            add(">>");
            add(",");
            add(".");
            add(";");
            add("+");
            add("-");
            add("*");
            add("/");
            add("%");
            add("^");
            add("<");
            add(">");
            add("==");
            add("/=");
            add("<=");
            add(">=");
            add("&");
            add("º");
            add("~");

        }
    };

    public ArrayList<String> vaLogic = new ArrayList<>() {
        {
            add("true");
            add("false");
        }
    };   
    public ArrayList<UnidadLexica> TablaScanner = new ArrayList<>();
    public ArrayList<UnidadLexica> TablaSimbolos = new ArrayList<>();

    public void scanner() {
        try {
            FileReader fileReader = new FileReader("ProgramaFuente.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            int lineNum = 0;
            boolean enComent = true;
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    lineNum++;
                    int posAtLine = 0;
                    while (posAtLine < line.length()) {
                        char charAtPos = line.charAt(posAtLine);
                        StringBuilder lexeme = new StringBuilder();
                        UnidadLexica lexicalUnit = new UnidadLexica();
                        if (charAtPos == '#' && posAtLine + 1 < line.length() && line.charAt(posAtLine + 1) == '*') {
                            while (enComent) {
                                int finalComent = line.indexOf("*#");
                                if (finalComent == -1) {
                                    posAtLine = line.length();
                                    line = bufferedReader.readLine();
                                    lineNum++;
                                } else {
                                    posAtLine = finalComent + 2;
                                    enComent = false;
                                }
                            }
                            lexicalUnit.token = 0;
                            lexicalUnit.description = "Comentario en bloque";
                            lexicalUnit.lexeme = "Hay un comentario en bloque";// Añade al lexema lo que sigue después del # hasta que salte de línea
                            TablaScanner.add(lexicalUnit);
                            posAtLine = line.length();
                        } else if (charAtPos == '#') {
                            lexicalUnit.token = 0;
                            lexicalUnit.description = "Comentario";
                            lexicalUnit.lexeme = line.substring(posAtLine + 1);// Añade al lexema lo que sigue después del # hasta que salte de línea
                            TablaScanner.add(lexicalUnit);
                            posAtLine = line.length();// Si hay un comentario de línea, la posición pasa al último valor de la fila
                        } else if (charAtPos == 'C') {
                            if (posAtLine + 1 < line.length() && isLetter(line.charAt(posAtLine + 1))) {
                                lexeme.append(charAtPos);
                                while (++posAtLine < line.length() && isAlphanumeric(line.charAt(posAtLine))) {
                                    lexeme.append(line.charAt(posAtLine));
                                }
                                lexicalUnit.token = 3001 + TablaSimbolos.size();
                                lexicalUnit.description = "Id Clase";
                                lexicalUnit.lexeme = lexeme.toString();
                                if (isNotRegisteredId(lexeme.toString())) {
                                    TablaSimbolos.add(lexicalUnit);
                                }
                                TablaScanner.add(lexicalUnit);
                            } 
                        } else if (charAtPos == 'O') {
                            if (posAtLine + 1 < line.length() && isLetter(line.charAt(posAtLine + 1))) {
                                lexeme.append(charAtPos);
                                while (++posAtLine < line.length() && isAlphanumeric(line.charAt(posAtLine))) {
                                    lexeme.append(line.charAt(posAtLine));
                                }
                                lexicalUnit.token = 3001 + TablaSimbolos.size();
                                lexicalUnit.description = "Id Objeto";
                                lexicalUnit.lexeme = lexeme.toString();
                                if (isNotRegisteredId(lexeme.toString())) {
                                    TablaSimbolos.add(lexicalUnit);
                                }
                                TablaScanner.add(lexicalUnit);
                            }
                        }else if (isLetter(charAtPos)) {
                            lexeme.append(charAtPos);
                            while (++posAtLine < line.length() && isAlphanumeric(line.charAt(posAtLine))) {
                                lexeme.append(line.charAt(posAtLine));
                            }
                            if (isReservedWord(lexeme.toString())) {
                                lexicalUnit.token = 1001 + palabrasReservadas.indexOf(lexeme.toString());
                                lexicalUnit.description = "Palabra Reservada";
                            } else if (isValLogic(lexeme.toString())) {
                                lexicalUnit.token = 8001 + vaLogic.indexOf(lexeme.toString());
                                lexicalUnit.description = "Logico";
                            } else {
                                lexicalUnit.token = 3001 + TablaSimbolos.size();
                                lexicalUnit.description = "Id";
                                if (isNotRegisteredId(lexeme.toString())) {
                                    TablaSimbolos.add(lexicalUnit);
                                }
                            }
                            lexicalUnit.lexeme = lexeme.toString();
                            TablaScanner.add(lexicalUnit);
                        }  else if (isOperator(String.valueOf(charAtPos))) {
                            lexeme.append(charAtPos);
                            while (++posAtLine < line.length() && isOperator(lexeme.toString() + line.charAt(posAtLine))) {
                                lexeme.append(line.charAt(posAtLine));
                            }
                            if (operadores.contains(lexeme.toString())) {
                                lexicalUnit.token = 2001 + operadores.indexOf(lexeme.toString());
                                lexicalUnit.description = "Operador";
                                lexicalUnit.lexeme = lexeme.toString();
                                TablaScanner.add(lexicalUnit);
                            } else {
                                String errorCause = lexeme.toString() + " no es un operador válido en el lenguaje";
                                String errorMessage = "Error línea " + lineNum + ": " + errorCause;
                                throw new Exception(errorMessage);
                            }
                        } else if (isDigit(charAtPos)) {
                            lexeme.append(charAtPos);
                            while (++posAtLine < line.length() && isDigit(line.charAt(posAtLine))) {
                                lexeme.append(line.charAt(posAtLine));
                            }
                            if (posAtLine + 1 < line.length() && isReal(line, posAtLine)) {
                                lexeme.append('.');
                                lexeme.append(line.charAt(++posAtLine));
                                while (++posAtLine < line.length() && isDigit(line.charAt(posAtLine))) {
                                    lexeme.append(line.charAt(posAtLine));
                                }
                                lexicalUnit.token = 5000;
                                lexicalUnit.description = "Real";
                                lexicalUnit.lexeme = lexeme.toString();
                            } else {
                                lexicalUnit.token = 4000;
                                lexicalUnit.description = "Entero";
                            }
                            lexicalUnit.lexeme = lexeme.toString();
                            TablaScanner.add(lexicalUnit);
                        } else if (charAtPos == '\'') {
                            if (posAtLine + 2 < line.length() && line.charAt(posAtLine + 2) == '\'') {
                                lexicalUnit.token = 6000;
                                lexicalUnit.description = "Caracter";
                                lexicalUnit.lexeme = String.valueOf(line.charAt(posAtLine + 1));
                                TablaScanner.add(lexicalUnit);
                            } else {
                                String errorCause = "' no existe en el lenguaje por si solo";
                                String errorMessage = "Error linea " + lineNum + ": " + errorCause;
                                throw new Exception(errorMessage);
                            }
                            posAtLine += 3;
                        } else if (charAtPos == '"') {
                            int posLastQuotationMark = line.indexOf("\"", posAtLine + 1);
                            if (posLastQuotationMark != -1) {
                                lexicalUnit.token = 7000;
                                lexicalUnit.description = "Cadena";
                                lexicalUnit.lexeme = line.substring(posAtLine + 1, posLastQuotationMark);
                                TablaScanner.add(lexicalUnit);
                                posAtLine = posLastQuotationMark + 1;
                            } else {
                                String errorCause = "\" no existe en el lenguaje por si solo";
                                String errorMessage = "Error linea " + lineNum + ": " + errorCause;
                                throw new Exception(errorMessage);
                            }
                        } else if (charAtPos == ' ') {
                            posAtLine++;
                        } else {
                            String errorCause = charAtPos + " no existe en el lenguaje";
                            String errorMessage = "Error linea " + lineNum + ": " + errorCause;
                            lexicalUnit.token = 1;
                            lexicalUnit.description = "Error";
                            lexicalUnit.lexeme = String.valueOf(errorCause);
                            throw new Exception(errorMessage);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            } finally {
                printTables();
            }
            bufferedReader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isValLogic(String cadena) {
        return vaLogic.contains(cadena);
    }

    public boolean isOperator(String cadena) {
        return operadores.contains(cadena);
    }

    public boolean isReservedWord(String cadena) {
        return palabrasReservadas.contains(cadena);
    }

    public boolean isLetter(char caracter) {
        return Character.isLetter(caracter);
    }

    public boolean isDigit(char caracter) {
        return Character.isDigit(caracter);
    }

    public boolean isAlphanumeric(char caracter) {
        return Character.isLetterOrDigit(caracter);
    }

    public boolean isReal(String line, int posAtLine) {
        return line.charAt(posAtLine) == '.' && isDigit(line.charAt(posAtLine + 1));
    }

    public boolean isNotRegisteredId(String lexeme) {
        boolean registrado = false;
        for (UnidadLexica l : TablaSimbolos) {
            if (l.lexeme.equals(lexeme)) {
                registrado = true;
                break;
            }
        }
        return !registrado;
    }

    public void printTable(ArrayList<UnidadLexica> table) {
        for (UnidadLexica lexicalUnit : table) {
            System.out.println(lexicalUnit);
        }
    }

    public void printTables() {
        System.out.println("\nANALIZADOR LEXICO");
        String header = String.format("%-8s%-24s%-8s\n", "TOKEN", "TIPO", "SIMBOLO");
        System.out.print(header);
        String border = "--------------------------------------------";
        System.out.println(border);
        printTable(TablaScanner);
    }
}
