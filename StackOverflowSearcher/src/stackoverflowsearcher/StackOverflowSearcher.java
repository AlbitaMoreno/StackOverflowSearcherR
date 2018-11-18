package stackoverflowsearcher;

import com.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 *
 * @author Alba, Alberto
 */
public class StackOverflowSearcher {
    
    public static void main(String[] args) throws Exception { 
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        
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
    
        // Búsqueda
        while(true){
            System.out.println("Consulta?: ");
            String line = in.readLine();
            
            IndexSearch iS = new IndexSearch(line);
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
