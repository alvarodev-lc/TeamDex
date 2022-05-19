package es.upm.roombasic.utils;


public class CifradoCesar {

    public static final int CAESARCODE_ROT = 3;
    private static final int NUMLETTERSALPHABET = 26;

    // Método para cifrar el texto
    public String cifrar(String texto, int codigo) {
        StringBuilder cifrado = new StringBuilder();
        codigo = codigo % 26;
        for (int i = 0; i < texto.length(); i++) {
            if (texto.charAt(i) >= 'a' && texto.charAt(i) <= 'z') {
                if ((texto.charAt(i) + codigo) > 'z') {
                    cifrado.append((char) (texto.charAt(i) + codigo - NUMLETTERSALPHABET));
                } else {
                    cifrado.append((char) (texto.charAt(i) + codigo));
                }
            } else if (texto.charAt(i) >= 'A' && texto.charAt(i) <= 'Z') {
                if ((texto.charAt(i) + codigo) > 'Z') {
                    cifrado.append((char) (texto.charAt(i) + codigo - NUMLETTERSALPHABET));
                } else {
                    cifrado.append((char) (texto.charAt(i) + codigo));
                }
            }
        }
        return cifrado.toString();
    }

    // Método para descifrar el texto
    public  String descifrar(String texto, int codigo) {
        StringBuilder cifrado = new StringBuilder();
        codigo = codigo % 26;
        for (int i = 0; i < texto.length(); i++) {
            if (texto.charAt(i) >= 'a' && texto.charAt(i) <= 'z') {
                if ((texto.charAt(i) - codigo) < 'a') {
                    cifrado.append((char) (texto.charAt(i) - codigo + NUMLETTERSALPHABET));
                } else {
                    cifrado.append((char) (texto.charAt(i) - codigo));
                }
            } else if (texto.charAt(i) >= 'A' && texto.charAt(i) <= 'Z') {
                if ((texto.charAt(i) - codigo) < 'A') {
                    cifrado.append((char) (texto.charAt(i) - codigo + NUMLETTERSALPHABET));
                } else {
                    cifrado.append((char) (texto.charAt(i) - codigo));
                }
            }
        }
        return cifrado.toString();
    }
} // Fin cifrado Cesar