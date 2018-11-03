package stackoverflowsearcher;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;

public final class IndexBuilder {
    private PerFieldAnalyzerWrapper ana;
    private Similarity similarity;
    public static final String INDEX_DIRECTORY = "./index"; 
    private IndexWriter writer;
    
    public IndexBuilder(List<String[]> preguntas, List<String[]> respuestas, List<String[]> etiquetas) throws IOException {
        this.similarity = new ClassicSimilarity();
        
        // Creamos analizador por campo
        Map<String, Analyzer> analyzerPerField = new HashMap<String, Analyzer>();
        analyzerPerField.put("campo", new StandardAnalyzer());
        
        this.ana = new PerFieldAnalyzerWrapper( new WhitespaceAnalyzer(), analyzerPerField);
        
        // Creamos el índice 
        this.createIndex();
        
        // Añadimos los documentos
        this.indexDocuments(preguntas, respuestas, etiquetas);
        
        // Cerramos el índice
        this.close();
    }
    
    public void createIndex() throws IOException {
        // Abrimos directorio de índices
        FSDirectory dir = FSDirectory.open(Paths.get(INDEX_DIRECTORY));

        // Configuramos índice al cual le vamos a pasar el StandardAnalyzer
        IndexWriterConfig config = new IndexWriterConfig(ana);
        config.setSimilarity(similarity); // Usamos como medida de similitud el Okapi BM25
        
        // Creamos un nuevo índice
        config.setOpenMode(OpenMode.CREATE);
        
        // Construimos IndexWriter con los parámetros dados en config
        writer = new IndexWriter(dir, config); 
    }
    
    public void indexDocuments(List<String[]> preguntas, List<String[]> respuestas, List<String[]> etiquetas) {
        // .......
        // PARTE ALBA
    }

    public void close() throws IOException {
        // Ejecutamos todos los cambios pendientes en el índice
        writer.commit();
        
        // Cerramos el IndexWriter
        writer.close();
    }  
}
