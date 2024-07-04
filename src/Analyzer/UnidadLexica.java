package Analyzer;

public class UnidadLexica {
    public int token = 0;
    public String description;
    public String lexeme;

    public UnidadLexica(){
    }

    @Override
    public String toString() {
        return String.format("%-8s%-24s%-8s", token, description, lexeme);
    }
}

