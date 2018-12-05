package stackoverflowsearcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

public class ProcessQuery {
    private static final String INDEX_DIRECTORY = "./../../index";
    private String line;
    public BooleanQuery query;
    
    public ProcessQuery(String line) {
        this.line = line;
    }
    
    public BooleanQuery procQuery() throws ParseException {
        List<Query> queries = new ArrayList<>();
        
        Query query;
        QueryParser parser;
        for(String t : this.line.split(" ")) {
            // Parseamos título
            parser = new QueryParser("Title", new StopAnalyzer());
            query = parser.parse(t);
            queries.add(query);
            
            // Parseamos cuerpo
            parser = new QueryParser("Body", new StopAnalyzer());
            query = parser.parse(t);
            queries.add(query);
            
            // Parseamos código 
            parser = new QueryParser("Code", new StopAnalyzer());
            query = parser.parse(t);
            queries.add(query);
        }
        
        // Construimos la consulta
        BooleanQuery.Builder bqbuilder = new BooleanQuery.Builder();
        
        for(Query q : queries) {
            BooleanClause bc = new BooleanClause(q, BooleanClause.Occur.SHOULD);
            bqbuilder.add(bc);
        }
        this.query = bqbuilder.build();
        System.out.println(this.query.toString());
        
        return this.query;
    }
}
