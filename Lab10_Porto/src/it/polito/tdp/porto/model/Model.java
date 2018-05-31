package it.polito.tdp.porto.model;

import java.util.ArrayList;



import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import it.polito.tdp.porto.db.PortoDAO;

public class Model {
	
	private PortoDAO pdao;
	private List <Author> autori; 
	private List <Paper> articoli; 
	private List <DefaultEdge> edges;
	
	private AuthorIdMap authorIdMap; 
	private PaperIdMap paperIdMap;
	
	private Graph<Author, DefaultEdge> grafo; 
	
	public Model() {
		pdao = new PortoDAO();
		
		authorIdMap = new AuthorIdMap();
		paperIdMap = new PaperIdMap();
		
		autori = pdao.getAutori(authorIdMap);
		articoli = pdao.getArticoli(paperIdMap); 
		
		System.out.println("Lista degli autori: \n");
		for (Author a: autori) {
			System.out.println(a.toString());
		}
		
		//popolo le liste incrociate di articoli e autori 
		
		pdao.getCreatori(authorIdMap, paperIdMap);         
	} 
	
	public void createGraph() {
		
		grafo = new SimpleGraph<>(DefaultEdge.class); 
		
		Graphs.addAllVertices(grafo, autori); 
		
		for (Author a: grafo.vertexSet()) {
			List <Author> coautori = ricercaCoautori(a); 
			if (coautori.size() != 0) {
				for (Author c : coautori) {
					grafo.addEdge(a, c); 
				}
			}
		}
		
		System.out.println(grafo.vertexSet().size());
		System.out.println(grafo.edgeSet().size());

	}

	private List<Author> ricercaCoautori(Author a) {
		List <Author> coautori = new ArrayList<>(); 
		for (Paper p: articoli) {
			if (p.getAutori().size()>1 && p.getAutori().contains(a)) {
				for (Author c: p.getAutori()) {
					if(!c.equals(a)) {
						coautori.add(c); 
					}
				}
				
			}
		}
		return coautori;
	}

	public List<Author> trovaVicini(Author autore) {
		List<Author> coautori = new ArrayList<Author>(); 
		coautori = Graphs.neighborListOf(grafo, autore); 
		System.out.println(coautori.toString());
		Collections.sort(coautori, new Comparator<Author>(){
    		public int compare(Author a, Author b){
    			return a.getLastname().compareTo(b.getLastname());
    		};
    	});
		return coautori;
	}

	public List<Author> getNonCoautori(List<Author> listaCoautori) {
		List <Author> nonCoautori = new ArrayList<>(this.autori); 
		for (Author a: listaCoautori) {
			if(nonCoautori.contains(a)) {
				nonCoautori.remove(a); 
			}
		}
		return nonCoautori;
	}

	public List<Paper> calcolaSequenza(Author autoreUno, Author autoreDue) {
		List <Paper> articoli = new ArrayList<>();  
		
		//trovo cammino minimo tra autoreUno e autoreDue
		
		ShortestPathAlgorithm<Author, DefaultEdge> spa = new DijkstraShortestPath<Author, DefaultEdge>(grafo);
		GraphPath<Author, DefaultEdge> gp = spa.getPath(autoreUno, autoreDue);
		
		//PRENDO LA LISTA DI ARCHI E NON DI VERTICI!

		edges = gp.getEdgeList();		
		for (DefaultEdge e : edges) {
				Author uno =  grafo.getEdgeSource(e);
				Author due = grafo.getEdgeTarget(e); 
				Paper p = this.trovaComuni(uno, due); 
				if (p==null) {
					throw new InternalError("Paper not found..."); 
				}
				articoli.add(p); 
		}
		System.out.println("Ci sono "+edges.size()+" autori che collegano quelli specificati");
		System.out.println(edges.toString()); 
		System.out.println("Lista articoli: "+articoli.toString());
		System.out.println(articoli.size());

		return articoli;
	}

	private Paper trovaComuni(Author uno, Author due) {
		Paper p = null; 
		for (Paper p1: uno.getArticoli()) {
			if (due.getArticoli().contains(p1)) {
				p=p1; 
				break; 
			}
		} 
		return p;
	}
	
	public Graph<Author, DefaultEdge> getGrafo() {
		return grafo;
	}
	
	public List<Author> getAutori() {
		return autori;
	}

	public List<DefaultEdge> getEdges() {
		return edges;
	}
	

}
