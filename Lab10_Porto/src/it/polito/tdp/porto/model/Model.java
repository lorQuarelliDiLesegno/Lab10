package it.polito.tdp.porto.model;

import java.util.ArrayList;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.*;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import it.polito.tdp.porto.db.PortoDAO;

public class Model {
	
	private PortoDAO pdao;
	private List <Author> autori; 
	private List <Paper> articoli; 
	
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
	
	public Graph<Author, DefaultEdge> getGrafo() {
		return grafo;
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

	public List<Author> getAutori() {
		return autori;
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
		DijkstraShortestPath<Author, DefaultEdge> spa = new DijkstraShortestPath<Author, DefaultEdge>(grafo, autoreUno, autoreDue);
		GraphPath<Author, DefaultEdge> gp = spa.getPath();
		Set <Author> temp = gp.getGraph().vertexSet();
		List <Author> visitati = new ArrayList<>(temp);
		for (int i = 0; i<visitati.size()-1; i=i+2) {
				Author uno =  visitati.get(i);
				Author due = visitati.get(i+1); 
				for (Paper p: uno.getArticoli()) {
					if (p.getAutori().contains(due) && !articoli.contains(p)) {
						articoli.add(p); 
						break; 
					}
				} 
		}
		System.out.println("Ci sono "+temp.size()+" autori che collegano quelli specificati");
		System.out.println(temp.toString()); 
		System.out.println("Lista articoli: "+articoli.toString());
		System.out.println(articoli.size());

		return articoli;
	}


}
