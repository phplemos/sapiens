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
    protected List<T> cache;
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

    protected void salvarNoArquivo() {
        try {
            objectMapper.writeValue(arquivoBD, this.cache);
        } catch (IOException e) {
            System.err.println("Erro ao salvar dados em '" + arquivoBD.getName() + "': " + e.getMessage());
        }
    }

    private void criarInfraestruturaArquivo() {
        try {
            File pastaData = new File("data");
            if (!pastaData.exists()) {
                pastaData.mkdirs();
            }
            if (!arquivoBD.exists()) {
                arquivoBD.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("Erro ao criar infraestrutura de arquivos: " + e.getMessage());
        }
    }

    protected int gerarProximoId(ToIntFunction<T> idGetter) {
        return this.cache.stream()
                .mapToInt(idGetter)
                .max()
                .orElse(0) + 1;
    }
}