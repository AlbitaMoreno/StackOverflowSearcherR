package stackoverflowsearcher;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Alba, Alberto
 */
public class StackOverflowSearcher {
    
    public static void main(String[] args) throws Exception { 
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        
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
            
            System.out.println(iS.getResultSearch().toString());
            System.out.println("-------------------------------------------------------------------------");
            System.out.println(iS.getResultFacet());
            
            if(line.equals("")){
                break;
            }
        }
    }  
}
