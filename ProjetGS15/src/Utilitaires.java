import java.io.BufferedReader;
import java.io.BufferedWriter;
//Packages � importer afin d'utiliser les objets
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JFileChooser;

public class Utilitaires {
	public static String SelectionFichier() { 
	    // Bo�te de s�lection de fichier � partir du r�pertoire courant
	    File repertoireCourant = null;
	    try {
	        // obtention d'un objet File qui d�signe le r�pertoire courant. Le
	        // "getCanonicalFile" n'est pas absolument n�cessaire mais permet
	        // d'�viter les /Truc/./Chose/ ...
	        repertoireCourant = new File(".").getCanonicalFile();
	        //System.out.println("R�pertoire courant : " + repertoireCourant);
	    } catch(IOException e) {}
     
	    // cr�ation de la bo�te de dialogue dans ce r�pertoire courant
	    // (ou dans "home" s'il y a eu une erreur d'entr�e/sortie, auquel
	    // cas repertoireCourant vaut null)
	    JFileChooser dialogue = new JFileChooser(repertoireCourant);
	     
	    // affichage
	    dialogue.showOpenDialog(null);
	     
	    // r�cup�ration du fichier s�lectionn�
	    System.out.println("\nFichier choisi : " + dialogue.getSelectedFile());
		return dialogue.getSelectedFile().toString();

	}
	
	// Fonction de lecture sans entr�e
	public static String Lecture() {
	    	String cible = SelectionFichier();
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(cible));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String line;
			String resultat = "";
			try {
				while ((line = br.readLine()) != null) {
				   // process the line.
					resultat += line;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return resultat;
		}

	// Fonction de lecture avec en entr�e le fichier � lire
	public static String Lecture(String cible){
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(cible));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String line;
		String resultat = "";
		try {
			while ((line = br.readLine()) != null) {
			   // process the line.
				resultat += line;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultat;
	}
	
	
	// Fonction qui va permettre d'�crire dans un fichier
		public static void Ecriture(String texte, String cible) {	      
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(new File(cible));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		       byte[] texteAEcrire = texte.getBytes();
		       try {
					fos.write(texteAEcrire);
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		       
		 }
	

}
