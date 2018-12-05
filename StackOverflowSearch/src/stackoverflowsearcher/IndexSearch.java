package stackoverflowsearcher;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.DrillDownQuery;
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
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.store.FSDirectory;

public class IndexSearch {
    private static final String INDEX_DIRECTORY = "./../../index";
    private final String FACET_DIRECTORY = "./../../facet";
    private ProcessQuery query;
    private TaxonomyReader txReader;
    public List<Pair<String, String>> resultsSearch = new ArrayList<>();
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
        this.txReader = new DirectoryTaxonomyReader(FSDirectory.open(Paths.get(FACET_DIRECTORY)));
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
        
        // Creamos el criterio de ordenación
        SortField sf = new SortField("Score", SortField.Type.INT, true); // Ordenamos los documentos por score decreciente
        sf.setMissingValue(0);
        Sort orden = new Sort(sf);
        
        // Obtenemos documentos
        TopFieldDocs results = searcher.search(this.query.procQuery(),20, orden);
       
        for(ScoreDoc hit : results.scoreDocs){
            Document doc = searcher.doc(hit.doc);
            String title = doc.get("Title");
            String body = doc.get("Body");
            String code = doc.get("Code");
            String score = doc.get("Score");
           
            resultsSearch.add(new Pair<>("Title",title));
            resultsSearch.add(new Pair<>("Body",body));
            resultsSearch.add(new Pair<>("Code",code));
            resultsSearch.add(new Pair<>("Score",score)); 
        }      
    } 
   
    public List<Pair<String, String>> _facetSearch(String campo, String v1, String v2) throws IOException {
        // Abrimos el directorio donde se ubica el índice creado
        IndexReader reader = DirectoryReader.open(
                FSDirectory.open(Paths.get(INDEX_DIRECTORY)));
    
        // Instanciamos la clase IndexSearcher, que se encarga de realizar
        // la búsqueda sobre el índice creado
        // Por defecto, le vamos a asignar la métrica de similitud BM25Similarity
        IndexSearcher searcher = new IndexSearcher(reader);
        
        FacetsConfig fconfig = new FacetsConfig();
        fconfig.setMultiValued("etiqueta", true); // Indicamos que la faceta etiqueta puede contener varios valores
        fconfig.setHierarchical("fecha", true); // Creamos una jerarquía para la faceta fecha
        
        DrillDownQuery q = new DrillDownQuery(fconfig, this.query.query);
        if(v2 == null) q.add(campo, v1);
        else q.add(campo, v1, v2);
        
        FacetsCollector fc = new FacetsCollector();
        TopDocs tdc = FacetsCollector.search(searcher, q, 10, fc);
        System.out.println(tdc.totalHits);
        
        // Retrieve results
        Facets facets = new FastTaxonomyFacetCounts(txReader, fconfig, fc);
        List<FacetResult> result = facets.getAllDims(100);
        
        List<Pair<String, String>> resultsFacet = new ArrayList<>();
        
        for(ScoreDoc hit : tdc.scoreDocs){
            Document doc = searcher.doc(hit.doc);
            String title = doc.get("Title");
            String body = doc.get("Body");
            String code = doc.get("Code");
            String score = doc.get("Score");
           
            resultsFacet.add(new Pair<>("Title",title));
            resultsFacet.add(new Pair<>("Body",body));
            resultsFacet.add(new Pair<>("Code",code));
            resultsFacet.add(new Pair<>("Score",score)); 
        }
        
        reader.close();
        return resultsFacet;
    }
    
    public List<Pair<String, String>> getResultSearch() throws IOException, ParseException{
        _indexSearch();
        return this.resultsSearch;
    }

}