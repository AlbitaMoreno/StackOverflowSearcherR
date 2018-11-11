package stackoverflowsearcher;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LetterTokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public final class IndexBuilder {
    private PerFieldAnalyzerWrapper ana;
    public Analyzer RcodeAnalyzer;
    private Similarity similarity;
    public static final String INDEX_DIRECTORY = "./index"; 
    private IndexWriter writer;
    private Map<String, Analyzer> analyzerPerField = new HashMap<String, Analyzer>();
    private List<String> palabras_codigo_r = Arrays.asList("for","if","else","function","while","case","break","do","try","catch","return",
    "objects","rm","assign","order","sort","numeric","character","integer");
    
    public IndexBuilder(List<String[]> preguntas, List<String[]> respuestas, List<String[]> etiquetas) throws IOException {
        this.similarity = new ClassicSimilarity();
        
        // Creamos un analizador de código R 
        this.RcodeAnalyzer = new Analyzer(){
            @Override
            protected Analyzer.TokenStreamComponents createComponents(String string) {
                // Extraemos caracteres alfanuméricos
                final Tokenizer source = new LetterTokenizer();
                
                // Convertimos todo a minúscula
                TokenStream result = new LowerCaseFilter(source);
                
                // Elimino palabras vacías
                result = new StopFilter(result, new CharArraySet(palabras_codigo_r, false));
                
                return new TokenStreamComponents(source, result);
            }
        };
        // Creamos analizador por campo       
        analyzerPerField.put("Title", new StandardAnalyzer());
        analyzerPerField.put("Body", new StandardAnalyzer());
        
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
            doc.add(new TextField("Title", d[4],Field.Store.YES));
            doc.add(new TextField("Body", d[5],Field.Store.YES));
            
            org.jsoup.nodes.Document code = Jsoup.parse(d[5]);
            
            for (Element e : code.getAllElements()){
              if(e.tagName().equals("code")){
                  doc.add(new TextField("Code", e.text(),Field.Store.YES));
                  analyzerPerField.put("Code", this.RcodeAnalyzer);
              }  
            }

            try {
                writer.addDocument(doc);
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
