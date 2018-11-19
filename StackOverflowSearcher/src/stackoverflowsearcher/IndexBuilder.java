package stackoverflowsearcher;

import com.opencsv.CSVReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
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
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public final class IndexBuilder {
    private PerFieldAnalyzerWrapper ana;
    public Analyzer RcodeAnalyzer;
    private Similarity similarity;
    private static final String INDEX_DIRECTORY = "./index"; 
    private static final String FACET_DIRECTORY = "./facet"; 
    private IndexWriter writer;
    public Map<String, Analyzer> analyzerPerField = new HashMap<String, Analyzer>();
    private List<String> palabras_codigo_r = Arrays.asList("for","if","else","function","while","case","break","do","try","catch","return",
    "objects","rm","assign","order","sort","numeric","character","integer");
    private FacetsConfig fconfig;
    private DirectoryTaxonomyWriter fwriter;
    private String[] meses;
    
    public IndexBuilder(String ruta_preguntas, String ruta_respuestas, List<String[]> etiquetas) throws IOException {
        this.meses = new String[]{"ENERO","FEBRERO","MARZO","ABRIL","MAYO","JUNIO",
            "JULIO","AGOSTO","SEPTIEMBRE","OCTUBRE","NOVIEMBRE","DICIEMBRE"};
        this.similarity = new BM25Similarity();
        
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
        analyzerPerField.put("Code", this.RcodeAnalyzer);

        this.ana = new PerFieldAnalyzerWrapper( new WhitespaceAnalyzer(), analyzerPerField);
        
        // Creamos el índice 
        this.createIndex();
        
        // Añadimos los documentos
        this.indexDocuments(ruta_preguntas, ruta_respuestas, etiquetas);
        
        // Cerramos el índice
        this.close();
    }
    
    public void createIndex() throws IOException {
        // Abrimos directorio de índices
        FSDirectory dir = FSDirectory.open(Paths.get(INDEX_DIRECTORY));

        // Abrimos el directorio de facetas
        FSDirectory factDir = FSDirectory.open(Paths.get(FACET_DIRECTORY));
        
        // Configuramos índice al cual le vamos a pasar el StandardAnalyzer
        IndexWriterConfig config = new IndexWriterConfig(ana);
        config.setSimilarity(similarity); // Usamos como medida de similitud el Okapi BM25
        config.setOpenMode(OpenMode.CREATE); // Configuramos el índice de forma que se cree un índice cada vez que se ejecute esta función
        
        // Configuramos facetas
        fconfig = new FacetsConfig(); 
        
        fconfig.setIndexFieldName("pregunta","facet_pregunta"); // La faceta pregunta la gestiona dentro del facet_pregunta
        fconfig.setIndexFieldName("usuario","facet_usuario"); // La faceta usuario la gestiona dentro del facet_usuario
        fconfig.setIndexFieldName("fecha","facet_fecha"); // La faceta fecha la gestiona dentro del facet_fecha
        fconfig.setIndexFieldName("pregunta","facet_pregunta"); // La faceta pregunta la gestiona dentro del facet_pregunta
        fconfig.setIndexFieldName("respuesta_aceptada","facet_respuesta_aceptada"); // La faceta respuesta_aceptada la gestiona dentro del facet_respuesta_aceptada
        fconfig.setIndexFieldName("puntuacion", "facet_puntuacion"); // La faceta puntuacion la gestiona dentro del facet_puntuacion
        
        fconfig.setHierarchical("fecha", true); // Creamos una jerarquía para la faceta fecha
        
        // Construimos IndexWriter con los parámetros dados en config
        writer = new IndexWriter(dir, config); 
        
        // Construimos DirectoryTaxonomyWriter pásandole el directorio de facetas
        fwriter = new DirectoryTaxonomyWriter(factDir);
    }
    
    public void indexDocuments(String ruta_preguntas, String ruta_respuestas, List<String[]> etiquetas) throws IOException {   
        // PREGUNTAS
        
        // Abro csv para extraer preguntas
        Reader reader = Files.newBufferedReader(Paths.get(ruta_preguntas));
        CSVReader csvreader = new CSVReader(reader);
        
        csvreader.readNext(); // Evito hacer un documento con la columna de los nombres de los campos
        
        String [] d;
        while((d = csvreader.readNext()) != null) {
            Document doc = new Document();
            
            // Incluimos los campos a indexar
            doc.add(new StringField("Id_q", d[0],Field.Store.YES));
            doc.add(new StringField("OwnerUserId_q", d[1],Field.Store.YES));
            doc.add(new StringField("CreationDate_q", d[2],Field.Store.YES));
            doc.add(new StringField("Score_q", d[3],Field.Store.YES));
            doc.add(new TextField("Title_q", d[4],Field.Store.YES));
            doc.add(new TextField("Body_q", d[5],Field.Store.YES));
            
            org.jsoup.nodes.Document code = Jsoup.parse(d[5]);
            
            for (Element e : code.getAllElements()){
              if(e.tagName().equals("code")){
                  doc.add(new TextField("Code_q", e.text(),Field.Store.YES));
              }  
            }
            
            // Incluimos las facetas
            doc.add(new FacetField("pregunta", "true"));
            doc.add(new FacetField("usuario", d[1]));
            
            // Extraemos el mes y el año
            String [] fecha;
            fecha = d[2].split("-");
            doc.add(new FacetField("fecha", fecha[0], meses[Integer.parseInt(fecha[1]) - 1]));
            
            doc.add(new FacetField("puntuacion", this.categorizeScore(Integer.parseInt(d[3]))));
            
            writer.addDocument(fconfig.build(fwriter, doc));
        }
        
        csvreader.close(); // Cerramos csv
        
        // RESPUESTAS
        
        // Abro csv para extraer preguntas
        reader = Files.newBufferedReader(Paths.get(ruta_respuestas));
        csvreader = new CSVReader(reader);
        
        csvreader.readNext(); // Evito hacer un documento con la columna de los nombres de los campos
        
        while((d = csvreader.readNext()) != null) {  
            Document doc = new Document();
                      
            doc.add(new StringField("Id_a", d[0],Field.Store.YES));
            doc.add(new StringField("OwnerUserId_a", d[1],Field.Store.YES));
            doc.add(new StringField("CreationDate_a", d[2],Field.Store.YES));
            doc.add(new TextField("ParentId_a", d[4],Field.Store.NO));
            doc.add(new StringField("Score_a", d[3],Field.Store.YES));
            doc.add(new TextField("IsAcceptedAnswer_a", d[5],Field.Store.YES));            
            doc.add(new TextField("Body_a", d[6],Field.Store.YES));
            
            org.jsoup.nodes.Document code = Jsoup.parse(d[5]);
            
            for (Element e : code.getAllElements()){
              if(e.tagName().equals("code")){
                  doc.add(new TextField("Code_a", e.text(),Field.Store.YES));
              }  
            }          
            
            // Incluimos las facetas
            doc.add(new FacetField("pregunta", "false"));
            doc.add(new FacetField("respuesta_aceptada", d[5]));
            doc.add(new FacetField("usuario", d[1]));
            
            // Extraemos el mes y el año
            String [] fecha;
            fecha = d[2].split("-");
            doc.add(new FacetField("fecha", fecha[0], meses[Integer.parseInt(fecha[1]) - 1]));
            
            doc.add(new FacetField("puntuacion", this.categorizeScore(Integer.parseInt(d[3]))));
            
            writer.addDocument(fconfig.build(fwriter, doc));
        }
        
        csvreader.close(); // Cerramos csv
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
    
    private String categorizeScore(int score) {
        if(score < 50) return "Bajo";
        else if(score < 100) return "Medio";
        else return "Alto";
    }
}
