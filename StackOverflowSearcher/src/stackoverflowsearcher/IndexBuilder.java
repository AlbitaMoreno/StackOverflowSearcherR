package stackoverflowsearcher;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
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
        for ( String[] d : preguntas){
            Document doc = new Document();
            //El ID y el OWNERID no lo almaceno, ya que es una número de usuario y no se estiman búsquedas por el campo
            doc.add(new StringField("Id", d[0],Field.Store.NO));
            doc.add(new StringField("OwnerUserId", d[1],Field.Store.NO));
            doc.add(new StringField("CreationDate", d[2],Field.Store.YES));
            doc.add(new StringField("Score", d[3],Field.Store.YES));
            doc.add(new StringField("Title", d[4],Field.Store.YES));
            doc.add(new TextField("Body", d[5],Field.Store.YES));

            try {
                writer.addDocument(doc);
                //System.out.println(" d[0] " + d[0] + " d[1] " + d[1] + " d[2] " +d[2] + " d[3] " + d[3] + " d[4] " +d[4] + " d[5] " + d[5] );
            } catch (IOException ex) {
                System.out.println("Error writting document " + ex);
            }
        }   
    }

    public void close(){
        try{
            // Ejecutamos todos los cambios pendientes en el índice
            writer.commit();
            // Cerramos el IndexWriter
            writer.close();
        } catch (IOException e){
            System.out.println("Error closing index " + e);
        }
       
    }  
}
