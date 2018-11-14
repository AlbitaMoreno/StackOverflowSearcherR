package stackoverflowsearcher;

import stackoverflowsearcher.IndexBuilder;
import com.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

/**
 *
 * @author Alba, Alberto
 */
public class StackOverflowSearcher {
    public static void main(String[] args) throws Exception { 
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        // Extraer preguntas
        // Abro csv
        Reader reader = Files.newBufferedReader(Paths.get("./rquestions/Questions.csv"));
        CSVReader csvreader = new CSVReader(reader);

        List<String[]> preguntas = csvreader.readAll(); // Leo archivo
        preguntas.remove(0); // Elimino la primera fila que informa del nombre de las columnas

        csvreader.close(); // Cerramos csv

        // Extraer respuestas
        // Abro csv
        reader = Files.newBufferedReader(Paths.get("./rquestions/Answers.csv"));
        csvreader = new CSVReader(reader);

        List<String[]> respuestas = csvreader.readAll(); // Leo archivo
        respuestas.remove(0); // Elimino la primera fila que informa del nombre de las columnas

        csvreader.close(); // Cerramos csv

        // Extraer etiquetas
        // Abro csv
        reader = Files.newBufferedReader(Paths.get("./rquestions/Tags.csv"));
        csvreader = new CSVReader(reader);

        List<String[]> etiquetas = csvreader.readAll(); // Leo archivo
        etiquetas.remove(0); // Elimino la primera fila que informa del nombre de las columnas

        csvreader.close(); // Cerramos csv

        // Índice
        IndexBuilder iB = new IndexBuilder(preguntas, respuestas, etiquetas);
        while(true){
            System.out.println("Consulta?: ");
            String line = in.readLine();
            //Falla pq hay inicializar la query
            Query query;
            IndexSearch iS = new IndexSearch(query);
            if(line == null || line.length() == -1){
                break;
            }
            line = line.trim();
            if(line.length() == 0){
                break;
            }
            iS.indexSearch();
            if(line.equals("")){
                break;
            }
        }
    }  
}
