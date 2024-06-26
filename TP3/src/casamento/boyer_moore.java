package casamento;

public class boyer_moore {

    public static final int NO_OF_CHARS = 256;

    // Função auxiliar para retornar o maior de dois números
    private static int max(int a, int b) {
        return (a > b) ? a : b;
    }

    // Função para preencher o vetor de ocorrência do caractere ruim
    private static void caractereRuim(char[] str, int size, int[] caractereRuim) {

        // Inicializa todas as ocorrências com -1
        for (int i = 0; i < NO_OF_CHARS; i++)
            caractereRuim[i] = -1;

        // Preenche o valor da última ocorrência de cada caractere
        for (int i = 0; i < size; i++)
            caractereRuim[(int) str[i]] = i;
    }

    // Função de busca utilizando a regra do caractere ruim
    public static void pesquisaCaractereRuim(char[] txt, char[] pat) {
        int m = pat.length; // Comprimento do padrão
        int n = txt.length; // Comprimento do texto

        int[] caractereRuim = new int[NO_OF_CHARS];

        // Preprocessamento para encontrar as últimas ocorrências dos caracteres do
        // padrão
        caractereRuim(pat, m, caractereRuim);

        int s = 0; // Posição atual do padrão no texto, começa do início do texto
        while (s <= (n - m)) {
            int j = m - 1;

            // Reduz j enquanto os caracteres do padrão forem iguais aos do texto
            while (j >= 0 && pat[j] == txt[s + j])
                j--;

            // Se o padrão está no texto
            if (j < 0) {
                System.out.println("Padrão ocorre em = " + s);
                s += (s + m < n) ? m - caractereRuim[txt[s + m]] : 1;
                // Se o padrão não está no texto, pula para a posição indicada pelo vetor de
                // caractere ruim, ou pula 1
            } else
                s += max(1, j - caractereRuim[txt[s + j]]);
        }
    }

    // Função para calcular o próximo sufixo bom do padrão e atualizar o vetor bpos
    // com as posições dos sufixos
    private static void sufixoBom(int[] shift, int[] bpos, char[] pat, int m) {
        int i = m;
        int j = m + 1;
        bpos[i] = j;

        while (i > 0) {
            // Se o caractere na posição i-1 não é equivalente ao caractere em j-1, então
            // continua procurando
            while (j <= m && pat[i - 1] != pat[j - 1]) {
                // Se shift[j] ainda é 0, atualiza com a diferença de posições
                if (shift[j] == 0)
                    shift[j] = j - i;

                j = bpos[j];
            }
            // Se pat[i-1] = pat[j-1], semelhança encontrada, guarda a posição de início da
            // semelhança
            i--;
            j--;
            bpos[i] = j;
        }
    }

    // Implementação do caso 2 da regra de sufixo bom
    private static void sufixoBom2(int[] shift, int[] bpos, char[] pat, int m) {
        int i, j;
        j = bpos[0];
        for (i = 0; i <= m; i++) {
            // Define a posição da borda do primeiro caractere do padrão para todos os
            // índices no array shift que têm shift[i] = 0
            if (shift[i] == 0)
                shift[i] = j;

            // Se o sufixo se torna mais curto que bpos[0], usa a posição da próxima borda
            // mais ampla como valor de j
            if (i == j)
                j = bpos[j];
        }
    }

    // Função de busca utilizando a regra de sufixo bom do algoritmo Boyer-Moore
    public static void pesquisaSufixoBom(char[] text, char[] pat) {
        int s = 0, j;
        int m = pat.length; // Comprimento do padrão
        int n = text.length; // Comprimento do texto

        int[] bpos = new int[m + 1];
        int[] shift = new int[m + 1];

        // Inicializa todas as ocorrências de shift com 0
        for (int i = 0; i < m + 1; i++)
            shift[i] = 0;

        // Faz o pré-processamento
        sufixoBom(shift, bpos, pat, m);
        sufixoBom2(shift, bpos, pat, m);

        while (s <= n - m) {
            j = m - 1;

            // Reduz j enquanto os caracteres do padrão forem iguais aos do texto
            while (j >= 0 && pat[j] == text[s + j])
                j--;

            // Se o padrão está no texto
            if (j < 0) {
                System.out.printf("Padrão ocorre em  = %d\n", s);
                s += shift[0];
            } else
                // Se pat[j] != text[s+j], então desloca o padrão de shift[j+1] posições
                s += shift[j + 1];
        }
    }
}
