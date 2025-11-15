package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;

public abstract class BaseRepository<T> {

    protected final File arquivoBD;
    protected final ObjectMapper objectMapper;
    protected List<T> cache; // Cache em memória dos dados do arquivo
    private final TypeReference<List<T>> typeReference;

    public BaseRepository(String nomeArquivo, TypeReference<List<T>> typeReference) {
        this.arquivoBD = new File("data/" + nomeArquivo);
        this.typeReference = typeReference;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        criarInfraestruturaArquivo();
        this.cache = carregarDoArquivo();
    }

    /**
     * Carrega a lista completa de <T> do arquivo JSON para o cache em memória.
     */
    protected List<T> carregarDoArquivo() {
        try {
            if (arquivoBD.length() == 0) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(arquivoBD, this.typeReference);

        } catch (IOException e) {
            System.err.println("Erro ao carregar dados de '" + arquivoBD.getName() + "': " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Salva a lista inteira do cache em memória de volta no arquivo JSON.
     * (Sobrescreve o arquivo)
     */
    protected void salvarNoArquivo() {
        try {
            // Escreve a lista inteira (this.cache) de volta no arquivo
            objectMapper.writeValue(arquivoBD, this.cache);
        } catch (IOException e) {
            System.err.println("Erro ao salvar dados em '" + arquivoBD.getName() + "': " + e.getMessage());
        }
    }

    // --- Método de Inicialização (Privado) ---

    /**
     * Garante que a pasta "data/" e o arquivo .json existam.
     */
    private void criarInfraestruturaArquivo() {
        try {
            File pastaData = new File("data");
            if (!pastaData.exists()) {
                pastaData.mkdirs(); // Cria a pasta "data"
            }
            if (!arquivoBD.exists()) {
                arquivoBD.createNewFile(); // Cria o arquivo "exemplo.json"
            }
        } catch (IOException e) {
            System.err.println("Erro ao criar infraestrutura de arquivos: " + e.getMessage());
        }
    }

    /**
     * Helper genérico para gerar o próximo ID.
     * Ele recebe uma função que sabe como extrair o 'int' (ID) do objeto 'T'.
     * @param idGetter A função que extrai o ID (ex: Endereco::getId)
     * @return O próximo ID disponível
     */
    protected int gerarProximoId(ToIntFunction<T> idGetter) {
        return this.cache.stream()
                .mapToInt(idGetter)
                .max()
                .orElse(0) + 1;
    }
}