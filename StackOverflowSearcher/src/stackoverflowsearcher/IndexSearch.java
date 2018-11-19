package stackoverflowsearcher;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

public class IndexSearch {
    private static final String INDEX_DIRECTORY = "./index";
    private ProcessQuery query;
    //==========================================================================
    //  Los índices tienen los siguientes campos:
    //      Questions:
    //          1. Title
    //          2. Body
    //          3. CreationDate
    //          4. Score
    //          5. Code
    //      Answers:
    //          1. IsAcceptedAnswer
    //          2. Body
    //          3. CreationDate
    //          4. Score
    //          5. Code
    //======================================================================
    public IndexSearch(String line) {
        this.query = new ProcessQuery(line);
    }
    
    public void indexSearch() throws IOException, ParseException {
        // Abrimos el directorio donde se ubica el índice creado
        IndexReader reader = DirectoryReader.open(
                FSDirectory.open(Paths.get(INDEX_DIRECTORY)));
    
        // Instanciamos la clase IndexSearcher, que se encarga de realizar
        // la búsqueda sobre el índice creado
        // Por defecto, le vamos a asignar la métrica de similitud BM25Similarity
        IndexSearcher searcher = new IndexSearcher(reader);
        
        TopDocs results = searcher.search(this.query.procQuery(),100);
        ScoreDoc [] hits = results.scoreDocs;
        
        System.out.println("--------------------RESULTADOS--------------------");
        
        for(int j=0; j < hits.length; j++){
            Document doc = searcher.doc(hits[j].doc);
            String title_q = doc.get("Title_q");
            String body_q = doc.get("Body_q");
            String code_q = doc.get("Code_q");
            String title_a = doc.get("Title_a");
            String body_a = doc.get("Body_a");
            String code_a = doc.get("Code_a");

            //Integer id = doc.getField("Score").numericValue().intValue();
            if(title_q != null)
                System.out.println("Título pregunta: \n" + title_q);
            if(body_q != null)
                System.out.println("Cuerpo: \n" + body_q);
            if(code_q != null)
                System.out.println("Código relacionado con la pregunta \n" + code_q);
            if(title_a != null)
                System.out.println("Título respuesta: \n" + title_a);
            if(body_a != null)
                System.out.println("Cuerpo: \n" + body_a);
            if(code_a != null)
                System.out.println("Código relacionado con la pregunta \n" + code_a);
            
        }
              
        // Cerramos el directorio de índices
        reader.close();
    }
}