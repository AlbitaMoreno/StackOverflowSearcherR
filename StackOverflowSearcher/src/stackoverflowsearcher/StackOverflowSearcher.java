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
        
        // Extraer etiquetas
        
        // Abro csv
        Reader reader = Files.newBufferedReader(Paths.get("./rquestions/Tags.csv"));
        CSVReader csvreader = new CSVReader(reader);
        
        List<String[]> etiquetas = csvreader.readAll(); // Leo archivo
        etiquetas.remove(0); // Elimino la primera fila que informa del nombre de las columnas
        
        csvreader.close(); // Cerramos csv
        
        // Índice
        IndexBuilder i = new IndexBuilder("./rquestions/Questions.csv", 
                "./rquestions/Answers.csv", etiquetas);
    
        // Buscador
        //Search s = new Search(i.analyzerPerField);
    }
    
}
