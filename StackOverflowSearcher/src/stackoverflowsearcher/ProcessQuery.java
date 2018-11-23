package stackoverflowsearcher;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class ProcessQuery {
    private static final String INDEX_DIRECTORY = "./index";
    private String line;
    public BooleanQuery query;
    
    public ProcessQuery(String line) {
        this.line = line;
    }
    
    public BooleanQuery procQuery() throws ParseException {
        Map<String,Query> queries = new HashMap<String,Query>();
        
        // Parseamos título de la pregunta
        QueryParser parser = new QueryParser("Title_q", new StandardAnalyzer());
        
        Query query = parser.parse(line);
        
        queries.put("Title_q", query);
        
        // Parseamos cuerpo de la pregunta
        parser = new QueryParser("Body_q", new StandardAnalyzer());
        
        query = parser.parse(line);
        
        queries.put("Body_q", query);
        
        // Parseamos código de la pregunta
        parser = new QueryParser("Code_q", new StandardAnalyzer());
        
        query = parser.parse(line);
        
        queries.put("Code_q", query);
        
        // Parseamos título de la respuesta
        parser = new QueryParser("Title_a", new StandardAnalyzer());
    
        query = parser.parse(line);
        
        queries.put("Title_a", query);
        
        // Parseamos cuerpo de la respuesta
        parser = new QueryParser("Body_a", new StandardAnalyzer());
        
        query = parser.parse(line);
        
        queries.put("Body_a", query);
        
        // Parseamos código de la pregunta
        parser = new QueryParser("Code_a", new StandardAnalyzer());
        
        query = parser.parse(line);
        
        queries.put("Code_a", query);
        
        BooleanQuery.Builder bqbuilder = new BooleanQuery.Builder();
        
        Iterator it = queries.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            String field = (String) e.getKey();
            Query q = (Query) e.getValue();
            
            BooleanClause bc = new BooleanClause(q, BooleanClause.Occur.SHOULD);
            bqbuilder.add(bc);
        }
        this.query = bqbuilder.build();
        
        return this.query;
    }
}
