package stackoverflowsearcher;

import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

public class ProcessQuery {
    private static final String INDEX_DIRECTORY = "./../../index";
    private String line;
    public BooleanQuery query;
    
    public ProcessQuery(String line) {
        this.line = line;
    }
    
    public BooleanQuery procQuery() throws ParseException {
        // Construimos la consulta
        BooleanQuery.Builder bqbuilder = new BooleanQuery.Builder();
        BooleanClause bc;
        
        Query query;
        QueryParser parser;
        
        // Para cada término del resultado
        for(String t : this.line.split(" ")) {
            // Parseamos título
            parser = new QueryParser("Title", new StopAnalyzer());
            query = parser.parse(t);
            
            // Añadimos el titulo como cláusula que debe aparecer forzosamente
            bc = new BooleanClause(query, BooleanClause.Occur.SHOULD);
            bqbuilder.add(bc);
            
            // Parseamos cuerpo
            parser = new QueryParser("Body", new StopAnalyzer());
            query = parser.parse(t);
            
            // Añadimos el cuerpo como cláusula que puede aparecer
            bc = new BooleanClause(query, BooleanClause.Occur.SHOULD);
            bqbuilder.add(bc);
            
            // Parseamos código 
            parser = new QueryParser("Code", new StopAnalyzer());
            query = parser.parse(t);
            
            // Añadimos el código como cláusula que puede aparecer
            bc = new BooleanClause(query, BooleanClause.Occur.SHOULD);
            bqbuilder.add(bc);
        }
        
        this.query = bqbuilder.build();
        
        return this.query;
    }
}
