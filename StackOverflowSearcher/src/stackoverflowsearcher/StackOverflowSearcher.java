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
        boolean indexNotDefine = false;
        // Extraer etiquetas
        if(indexNotDefine){
            // Índice
            IndexBuilder i = new IndexBuilder("./rquestions/Questions.csv", 
            "./rquestions/Answers.csv", "./rquestions/Tags.csv");
        }
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
