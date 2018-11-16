package stackoverflowsearcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.FSDirectory;

public class Search {
    private static final String INDEX_DIRECTORY = "./index";
    private Map<String, Analyzer> analyzerPerField = new HashMap<String, Analyzer>();
    
    public Search(Map<String, Analyzer> m) {
        this.analyzerPerField = m;
    }
    
    public void indexSearch() throws IOException, ParseException {
        // Abrimos el directorio donde se ubica el índice creado
        IndexReader reader = DirectoryReader.open(
                FSDirectory.open(Paths.get(INDEX_DIRECTORY)));
    
        // Instanciamos la clase IndexSearcher, que se encarga de realizar
        // la búsqueda sobre el índice creado
        // Por defecto, le vamos a asignar la métrica de similitud BM25Similarity
        IndexSearcher searcher = new IndexSearcher(reader);
        
        // Preparo entrada
        BufferedReader in = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));
        
        // Para cada campo
        Iterator it = this.analyzerPerField.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            QueryParser parser = new QueryParser((String) e.getKey(), (Analyzer) e.getValue());
            
            while(true) {
                String linea = in.readLine();

                if(linea == null || linea.length() == -1) break;

                // Eliminamos los espacios en blanco al inicio y al final
                linea = linea.trim();

                // Comprobamos que la consulta no este vacía después de quitar los
                // espacios en blanco
                if(linea.length() == 0) break;

                Query query;
                query = parser.parse(linea);
                    
                /** CÓDIGO ALBA **/

                // No hay más consultas
                if(linea.equals("")) break;
            }
            
        }
        
        // Cerramos el directorio de índices
        reader.close();
    }
}