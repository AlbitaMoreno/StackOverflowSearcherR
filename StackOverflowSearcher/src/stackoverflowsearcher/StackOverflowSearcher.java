package stackoverflowsearcher;

import com.opencsv.CSVReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 *
 * @author Alba, Alberto
 */
public class StackOverflowSearcher {

    public static void main(String[] args) throws Exception {
       
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
        IndexBuilder i = new IndexBuilder(preguntas, respuestas, etiquetas);
    
        // Buscador
        Search s = new Search(i.analyzerPerField);
    }
    
}
