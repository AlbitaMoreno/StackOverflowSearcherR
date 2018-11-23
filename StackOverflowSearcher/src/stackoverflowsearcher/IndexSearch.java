package stackoverflowsearcher;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.LabelAndValue;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class IndexSearch {
    private static final String INDEX_DIRECTORY = "./index";
    private final String INDEX_DIRECTORY_FACETS = "./index/facet";
    private ProcessQuery query;
    private TaxonomyReader txReader;
    public Map<String, String> resultsMap = new HashMap<>();
    public Map<String, Number> resultsFacetMap = new HashMap<>();
    public List <FacetResult> resFacet;
    private FacetsCollector fc = new FacetsCollector();

    //==========================================================================
    //  Los índices tienen los siguientes campos:
    //      Questions:
    //          1. Title
    //          2. Body
    //          3. CreationDate
    //          4. Score
    //          5. Code
    //      Answers:
    //          1. IsAcceptedAnswer
    //          2. Body
    //          3. CreationDate
    //          4. Score
    //          5. Code
    //======================================================================
    public IndexSearch(String line) throws IOException {
        this.txReader = new DirectoryTaxonomyReader((Directory) Paths.get(INDEX_DIRECTORY_FACETS));
        this.query = new ProcessQuery(line);
    }
    
    private void _indexSearch() throws IOException, ParseException {
        // Abrimos el directorio donde se ubica el índice creado
        IndexReader reader = DirectoryReader.open(
                FSDirectory.open(Paths.get(INDEX_DIRECTORY)));
    
        // Instanciamos la clase IndexSearcher, que se encarga de realizar
        // la búsqueda sobre el índice creado
        // Por defecto, le vamos a asignar la métrica de similitud BM25Similarity
        IndexSearcher searcher = new IndexSearcher(reader);
        
        TopDocs results = searcher.search(this.query.procQuery(),100);
        ScoreDoc [] hits = results.scoreDocs;
                
        for(int j=0; j < hits.length; j++){
            Document doc = searcher.doc(hits[j].doc);
            String title_q = doc.get("Title_q");
            String body_q = doc.get("Body_q");
            String code_q = doc.get("Code_q");
            String title_a = doc.get("Title_a");
            String body_a = doc.get("Body_a");
            String code_a = doc.get("Code_a");

            //Integer id = doc.getField("Score").numericValue().intValue();
            if(title_q != null)
                resultsMap.put("Title_q",title_q);
            if(body_q != null)
                resultsMap.put("Body_q",body_q);
            if(code_q != null)
                resultsMap.put("Code_q",code_q);
            if(title_a != null)
                resultsMap.put("Title_a",title_a);
            if(body_a != null)
                resultsMap.put("Body_a",body_a);
            if(code_a != null)
                resultsMap.put("Code_a",code_a);
        }      
        // Cerramos el directorio de índices
        reader.close();
        _facetSearch(searcher);
    }
    
    private Map<String, Number> _facetSearch(IndexSearcher searcher) throws IOException {
        TopDocs tdc = FacetsCollector.search(searcher,query.query,10,fc);
        FacetsConfig fconfig = new FacetsConfig();
        Facets facetas = new FastTaxonomyFacetCounts(txReader,fconfig,fc);
        this.resFacet = facetas.getAllDims(100);
        
        for(FacetResult fr : resFacet) {      
            for( LabelAndValue lv : fr.labelValues){
                resultsFacetMap.put(lv.label,lv.value);
            }
        }        
        return this.resultsFacetMap;
    }
    public Map<String, String> getResultSearch() throws IOException, ParseException{
        _indexSearch();
        return this.resultsMap;
    }
    
    public Map<String, Number> getResultFacet(){
        return this.resultsFacetMap;
    }
}