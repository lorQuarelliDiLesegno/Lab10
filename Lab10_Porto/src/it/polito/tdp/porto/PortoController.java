package it.polito.tdp.porto;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.porto.model.Author;
import it.polito.tdp.porto.model.Model;
import it.polito.tdp.porto.model.Paper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

public class PortoController {
	
	private Model model; 
	private List<Author> autori;
	private List<Author> autoriDue; 

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<Author> boxPrimo;

    @FXML
    private ComboBox<Author> boxSecondo;

    @FXML
    private TextArea txtResult;

    @FXML
    void handleCoautori(ActionEvent event) {
    	txtResult.clear();
    	Author autore = boxPrimo.getValue(); 
    	if (autore == null) {
			txtResult.setText("Non hai selezionato un'autore\n");
			return; 
		}
    	model.createGraph();
    	List <Author> listaCoautori = new ArrayList<>(model.trovaVicini(autore)); 
    	if (listaCoautori.size()!=0) {
    		txtResult.appendText("Elenco coautori di "+autore.toString()+"\n");
    		for (Author a: listaCoautori) {
    			txtResult.appendText(a.toString()+"\n");
    		}
    	}
    	else {
    		txtResult.appendText(autore.toString()+" non ha coautori\n");
    		return; 
    	}
    	setComboDueItems(listaCoautori, autore);
    }


	@FXML
    void handleSequenza(ActionEvent event) {
    	
    	if (model.getGrafo()== null) {
    		txtResult.appendText("Devi prima selezionare il primo autore\n");
    		return; 
    	}
    	Author autoreUno = boxPrimo.getValue(); 
    	Author autoreDue = boxSecondo.getValue(); 
    	if (autoreDue == null) {
			txtResult.setText("Non hai selezionato un'autore\n");
			return; 
		}
    	List <Paper> articoli = model.calcolaSequenza(autoreUno, autoreDue); 
    	txtResult.appendText("Ci sono "+model.getEdges().size()+" autori che collegano quelli selezionati \n");
    	txtResult.appendText("Sequenza di articoli da "+autoreUno.toString()+" a "+autoreDue.toString()+"\n");
    	for (Paper p : articoli) {
    		txtResult.appendText(p.toString()+"\n");
    	}
    	txtResult.appendText(articoli.size()+" articoli");
    }

    @FXML
    void initialize() {
        assert boxPrimo != null : "fx:id=\"boxPrimo\" was not injected: check your FXML file 'Porto.fxml'.";
        assert boxSecondo != null : "fx:id=\"boxSecondo\" was not injected: check your FXML file 'Porto.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Porto.fxml'.";
    }

	private void setComboUnoItems() {
		autori = new ArrayList<>(model.getAutori());
		
		// Aggiungi tutti i corsi alla ComboBox
		Collections.sort(autori, new Comparator<Author>(){
    		public int compare(Author a, Author b){
    			return a.getLastname().compareTo(b.getLastname());
    		};
    	});
		
		//Ricordarsi di andare dove è stata definita la combo box
		//e parametrizzare --> <Corso>
		boxPrimo.getItems().addAll(autori);
	}
	
	private void setComboDueItems(List<Author> listaCoautori, Author autore) {
		autoriDue = model.getNonCoautori(listaCoautori);
		autoriDue.remove(autore);
		// Aggiungi tutti i corsi alla ComboBox
		Collections.sort(autoriDue, new Comparator<Author>(){
    		public int compare(Author a, Author b){
    			return a.getLastname().compareTo(b.getLastname());
    		};
    	});
		
		//Ricordarsi di andare dove è stata definita la combo box
		//e parametrizzare --> <Corso>
		boxSecondo.getItems().addAll(autoriDue);
	}

	public void setModel(Model model) {
		this.model = model;
		setComboUnoItems(); 
	}
}
