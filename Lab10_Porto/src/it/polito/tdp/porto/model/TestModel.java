package it.polito.tdp.porto.model;

import java.util.ArrayList;
import java.util.List;

public class TestModel {

	public static void main(String[] args) {
		
		Model model = new Model();
		model.createGraph();
		List<Author> autori =  new ArrayList<>(model.getAutori());
		Author atemp1 = null; 
		Author atemp2 = null; 
		for (Author a: autori) {
			if (a.getId()==18415) {
				atemp1 = a; 
			}
			if (a.getId() ==28028) {
				atemp2 = a;
			}
		}
		model.trovaVicini(atemp1);
		
		model.calcolaSequenza(atemp1, atemp2); 
	}

}
