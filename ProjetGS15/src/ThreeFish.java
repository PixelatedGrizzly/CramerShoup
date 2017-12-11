import java.util.Scanner;

//Packages � importer afin d'utiliser les objets
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ThreeFish {
	
	public static String Lecture() {
	      // Nous d�clarons nos objets en dehors du bloc try/catch
	      FileInputStream fis = null;
	      String result = "";

	      try {
	         // On instancie nos objets :
	         // fis va lire le fichier
	         // fos va �crire dans le nouveau !
	         fis = new FileInputStream(new File("test.txt"));

	         // On cr�e un tableau de byte pour indiquer le nombre de bytes lus �
	         // chaque tour de boucle
	         byte[] buf = new byte[8];

	         // On cr�e une variable de type int pour y affecter le r�sultat de
	         // la lecture
	         // Vaut -1 quand c'est fini
	         int n = 0;

	         // Tant que l'affectation dans la variable est possible, on boucle
	         // Lorsque la lecture du fichier est termin�e l'affectation n'est
	         // plus possible !
	         // On sort donc de la boucle
	         while ((n = fis.read(buf)) >= 0) {          
	            // On affiche ce qu'a lu notre boucle au format byte et au
	            // format char
	            
	            for (byte bit : buf) {
	               result += (char)bit;
	            }
	            System.out.println("");
	            //Nous r�initialisons le buffer � vide
	            //au cas o� les derniers byte lus ne soient pas un multiple de 8
	            //Ceci permet d'avoir un buffer vierge � chaque lecture et ne pas avoir de doublon en fin de fichier
	            buf = new byte[8];

	         }
	         

	      } catch (FileNotFoundException e) {
	         // Cette exception est lev�e si l'objet FileInputStream ne trouve
	         // aucun fichier
	         e.printStackTrace();
	      } catch (IOException e) {
	         // Celle-ci se produit lors d'une erreur d'�criture ou de lecture
	         e.printStackTrace();
	      } finally {
	         // On ferme nos flux de donn�es dans un bloc finally pour s'assurer
	         // que ces instructions seront ex�cut�es dans tous les cas m�me si
	         // une exception est lev�e !
	         try {
	            if (fis != null)
	               fis.close();
	         } catch (IOException e) {
	            e.printStackTrace();
	         }
	      }
		return result;
		
	   }
	
	// Fonction qui va generer la cl�
	public static String GenerationCle(int size){
		if (size == 256){
			return "Un texte qui va faire 32 chars !";
		}
		else if(size == 512){
			return "Cette fois on va essayer d'avoir 64chars pour atteindre 512 bits";
		}
		else if(size == 1024){
			return "Aie aie cette fois il faut arriver � atteindre 128 chars pour obtenir une cl� de 1024 bits, c'est vraiment difficile par moment.";
		}
		else {
			System.out.println("La taille de la cl� demand�e est invalide");
		}
		return null;
		
	}
	
	// Fonction qui transforme un string en binaire
	static StringBuilder ChaineToBinaire(String chaine){
	    byte[] bytes = chaine.getBytes();
	    StringBuilder binary = new StringBuilder();
	    for (byte b : bytes) {
	        int val = b;
	        for (int i = 0; i < 8; i++) {
	            binary.append((val & 128) == 0 ? 0 : 1);
	            val <<= 1;
	        }
	    }
	    return binary;
	}
	
	// Fonction qui transforme du binaire en chaine
	static String BinaireTochaine(StringBuilder a2){	     
	    String res="";
        for(int i=1;i<a2.length();i++){
            if(i%8==0){
                char a=(char)Integer.parseInt(a2.substring(i-8,i),2);
                res=res+a;
            }  
        }
        res=res+(char)Integer.parseInt(a2.substring(a2.length()-8,a2.length()),2);
        return res;
    }
	
	// Fonction qui va g�n�rer les sous cl�s en s�parant la cl� principale en N morceaux et les tweaks
	public static String[] GenerationSousCles(String chaine, int N){
		String[] cle = chaine.split("");
		String[] sousCles = new String[N+1];
		String[] tweaks = new String[3];
		for (int i = 1; i < N +1; i++){
			String tampon = "";
			for (int j = 64*(i-1); j < 64*i; j++){
				tampon += cle[j];
			}
			sousCles[i-1] = tampon;
			// On choisit arbitrairement les deux dernier mots de 64 bits comme �tant les tweaks
			if(i == N-1){
				tweaks[0] = tampon;
			}
			else if (i == N){
				tweaks[1] = tampon;
			}
			//System.out.println("tab[" + (i-1)  + "] : "+ tab[i-1] );
		}
		
		
		// On initialise sousCles[N] avec la valeur de C
		sousCles[N] = "0001101111010001000110111101101010101001111111000001101000100010";
		// On calcule kN en xorant k1, k2, ...., kN-1 et C
		for(int i = 0; i < N; i++){
			sousCles[N] = xor(sousCles[i],sousCles[N]);
		}
		
		// On calcule t2 en xorant t0 et t1
		tweaks[2] = xor(tweaks[0], tweaks[1]);
		
		// Affichage des sous cl�s et des tweaks
		/*System.out.println("Les sous cl�s :");
		for (int i = 0; i < sousCles.length; i++){
			System.out.println("tab[" + (i)  + "] : "+ sousCles[i] );
		}
		System.out.println("Les 3 tweaks");
		for (int i = 0; i < tweaks.length; i++){
			System.out.println("tweak[" + (i)  + "] : "+ tweaks[i] );
		}*/
		
		GenerationClesTournees(sousCles, tweaks);
		return sousCles;		
	}
	
	// Fonction qui va g�n�rer toutes les cl�s de tourn�e et r�aliser le chiffrement / d�chiffrement
	public static String[] GenerationClesTournees (String[] sousCles, String[] tweaks){
		// sousCles est notre tableau qui contient les sous cl�s, sa taille est donc N + 1 actuellement
		int N = sousCles.length-1;
		// On cr�er un tableau [20][N] car il y'a N-1 sous cl�s par tourn�e et 20 tourn�e, on est sur le mod�le kn(i).
		String[][] clesTournees = new String[20][N];
		
		for (int i = 0; i < 20; i++){
			for(int n = 0; n < N; n++){
				
				if (n == N-3){
					clesTournees[i][n] = AdditionModulaire(sousCles[(i+n)%(N+1)],tweaks[i%3]);
				}
				else if (n == N-2){
					clesTournees[i][n] = AdditionModulaire(sousCles[(i+n)%(N+1)],tweaks[(i+1)%3]);
				}
				else if (n == N-1){
					clesTournees[i][n] = AdditionModulaire(sousCles[(i+n)%(N+1)], ChaineToBinaire(Integer.toString(i)).toString());
				}
				else {
					clesTournees[i][n] = sousCles[(i+n)%(N+1)];
				}
			}
		}
		// Affichage des cl�s de tourn�es
		/*for (int i = 0; i < 20; i++){
			System.out.println("Tourn�e n�" + i);
			for(int n = 0; n < N; n++){
				System.out.println("Cle["+n+"] : " + clesTournees[i][n]);
			}
		}*/
		
		// Message � chiffrer
		//String messageAChiffrerChaine = "J'ai l'impression que le chiffrement et le d�chiffrement marchent mais que la conversion de binaire � chaine a quelques probl�me. Je pense que tout fonctionne correctement jusqu'� maintenant, faut juste que je revois la conversion de BinaireToChaine, que j'implemente le chiffrement et d�chiffrement en CBC, impl�menter la lecture et l'�criture d'un fichier et enfin proposer un menu � l'utilisateur pour qu'il puisse r�aliser toutes ces actions. Je penserais aussi � clean le code.";
		String messageAChiffrerChaine = Lecture();
		// On le passe en binaire
		String messageAChiffrer = ChaineToBinaire(messageAChiffrerChaine).toString();
		// On le d�coupe en blocs de 64 bits
		String[] tabTempo = messageAChiffrer.split("");		
		
		// On calcule la taille de notre message � chiffrer pour savoir comment le stocker
		int tailleMessage = tabTempo.length;
		// On regarde la taille des mots qu'on souhaite obtenir (256, 512 ou 1024)
		int tailleSousMessage = N * 64;
		// On calcule le nombre de sous message que l'on va avoir
		int nombreSousMessage = tailleMessage / tailleSousMessage;
		if((tailleMessage%tailleSousMessage) != 0){
			nombreSousMessage += 1;
		}
		// On cr�er le tableau qui va stocker le message d�coup� en sousMessage et blocks de 64bits
		String[][] tabAChiffrer = new String[nombreSousMessage][N];
		// On d�clare une variable qui va compter le nombre de bits bourr�s
		int bourrage = 0;
		// On rempli le tableau avec le message � chiffrer
		// Pour chaque sous message, on d�coupe en blocs de 64bits et on fait du bourrage si necessaire
		for (int i = 0; i < nombreSousMessage; i++){
			for(int j = 0; j < N; j++){
				String tempo = "";
				for(int k = (j*64)+(i*64*N); k < 64*(j+1)+(i*64*N); k++){
					// S'il reste quelque chose dans tabTempo
					if(k <= tabTempo.length-1){
						tempo += tabTempo[k];
					}
					// Sinon on bourre avec des 0
					else{
						// On incr�mente le nombre de bits bourr�s
						bourrage ++;
						tempo += 0;
					}
				}
				tabAChiffrer[i][j] = tempo;
			}			
		}	
		
		// On affiche le tableau avec le message � chiffrer
		/*System.out.println("Avant chiffrement");
		for (int i = 0; i < tabAChiffrer.length; i ++){
			for(int j = 0; j < N; j++){
				System.out.println("Tab[" + i + "]["+ j +"] : " + tabAChiffrer[i][j]);
			}
		}*/
		
		// Boucle qui va g�rer les 76 tourn�es avec les 20 ajouts de cl�s
		// Pour chaque mot on va appliquer le chiffrement : ECB
		for (int i = 0; i < nombreSousMessage; i++){
			for(int j = 0; j < 20; j++){
				for(int k = 0; k <N; k++){
					// On xor le message avec les cl�s de tourn�es
					tabAChiffrer[i][k] = xor(tabAChiffrer[i][k],clesTournees[i][k]);
				}
				// On effectue 4 mix + Permute
				for(int l = 0; l < 4; l++){
					tabAChiffrer[i] = Substitution(tabAChiffrer[i]);
					for(int m = 0; m < N; m++){
						tabAChiffrer[i][m] = Permutation(tabAChiffrer[i][m]);
					}
				}
			}
		}
		// On affiche le tableau avec le message � chiffrer apr�s chiffrement
		/*System.out.println("Apres chiffrement");
				for (int i = 0; i < tabAChiffrer.length; i ++){
					for(int j = 0; j < N; j++){
						System.out.println("Tab[" + i + "]["+ j +"] : " + tabAChiffrer[i][j]);
					}
				}*/
		
		// On remet le tabAChiffrer sous forme de String
		messageAChiffrer = "";
		for(int i = 0; i < nombreSousMessage; i++){
			for (int j = 0; j < N; j++){				
					messageAChiffrer += tabAChiffrer[i][j];
				}
		}
		// Test pour voir si les fonction de conversion binaire fonctionnent
		/*System.out.println(messageAChiffrer);
		StringBuilder s = new StringBuilder(messageAChiffrer);
		System.out.println(BinaireTochaine(s));
		System.out.println(ChaineToBinaire(BinaireTochaine(s)));*/
		
		// On peut convertir en chaine de caract�res le message chiffr�
		StringBuilder sbChiffr� = new StringBuilder(messageAChiffrer);
		String messageChiffr� = BinaireTochaine(sbChiffr�);
		System.out.println("Message avant chiffrement : " + messageAChiffrerChaine);
		System.out.println("Le message chiffr� est : ");
		System.out.println(messageChiffr�);
	
		
		// Message � d�chiffrer
		String messageADechiffrerChaine = messageChiffr�;
		// On le passe en binaire (On a un pb avec la fonction BinaryToChaine du coup on passe directement par le binaire du messageAChiffrer
		String messageADechiffrer = messageAChiffrer;
		//String messageADechiffrer = ChaineToBinaire(messageADechiffrerChaine).toString();
		// On le d�coupe en blocs de 64 bits
		String[] tabTempo2 = messageADechiffrer.split("");		
		
		// On calcule la taille de notre message � chiffrer pour savoir comment le stocker
		int tailleMessageADechiffrer = tabTempo2.length;
		//System.out.println("Taille du message : "+ tailleMessageADechiffrer);
		// On regarde la taille des mots qu'on souhaite obtenir (256, 512 ou 1024)
		int tailleSousMessageADechiffrer = N * 64;
		//System.out.println("Taille des mots : " + tailleSousMessageADechiffrer);
		// On calcule le nombre de sous message que l'on va avoir
		int nombreSousMessageADechiffrer = tailleMessageADechiffrer / tailleSousMessageADechiffrer;
		if((tailleMessageADechiffrer%tailleSousMessageADechiffrer) != 0){
			nombreSousMessageADechiffrer += 1;
		}
		//System.out.println("Nombre de sous message n�cessaires : " + nombreSousMessageADechiffrer);
		// On cr�er le tableau qui va stocker le message d�coup� en sousMessage et blocks de 64bits
		String[][] tabADechiffrer = new String[nombreSousMessageADechiffrer][N];
		
		// On rempli le tableau avec le message � d�chiffrer
		// Pour chaque sous message, on d�coupe en blocs de 64bits et on fait du bourrage si necessaire
		for (int i = 0; i < nombreSousMessageADechiffrer; i++){
			for(int j = 0; j < N; j++){
				String tempo2 = "";
				for(int k = (j*64)+(i*64*N); k < 64*(j+1)+(i*64*N); k++){
					// S'il reste quelque chose dans tabTempo
					if(k <= tabTempo2.length-1){
						tempo2 += tabTempo2[k];
					}
					// Sinon on bourre avec des 0
					else{
						// System.out.println("On rentre dans le bourrage");
						tempo2 += "0";
					}
				}
				tabADechiffrer[i][j] = tempo2;
			}			
		}	
		
		// On affiche le tableau avec le message � d�chiffrer
		/*System.out.println("Message � d�chiffrer :");
		for (int i = 0; i < tabADechiffrer.length; i ++){
			for(int j = 0; j < N; j++){
				System.out.println("Tab[" + i + "]["+ j +"] : " + tabADechiffrer[i][j]);
			}
		}*/
		
		// Boucle qui va g�rer les 76 tourn�es avec les 20 ajouts de cl�s
		// Pour chaque mot on va appliquer le d�chiffrement : ECB
		for (int i = 0; i < nombreSousMessageADechiffrer; i++){
			for(int j = 0; j < 20; j++){				
				// On effectue 4 mix + Permute
				for(int l = 0; l < 4; l++){
					for(int m = 0; m < N; m++){
						tabADechiffrer[i][m] = Permutation(tabADechiffrer[i][m]);
					}
					tabADechiffrer[i] = AntiSubstitution(tabADechiffrer[i]);
					
				}
				
				for(int k = 0; k <N; k++){
					// On xor le message avec les cl�s de tourn�es
					tabADechiffrer[i][k] = xor(tabADechiffrer[i][k],clesTournees[i][k]);
				}
			}
		}
		// On affiche le tableau avec le message apr�s d�chiffrement
		/*System.out.println("Apres D�chiffrement");
				for (int i = 0; i < tabADechiffrer.length; i ++){
					for(int j = 0; j < N; j++){
						System.out.println("Tab[" + i + "]["+ j +"] : " + tabADechiffrer[i][j]);
					}
				}*/
		
		// On remet le tabAChiffrer sous forme de String
		messageADechiffrer = "";
		for(int i = 0; i < nombreSousMessageADechiffrer; i++){
			for (int j = 0; j < N; j++){
				messageADechiffrer += tabADechiffrer[i][j];
			}
		}
		// System.out.println("On a bourr� " + bourrage + " bits.");
		// On s'occupe d'enlever le bourrage
		messageADechiffrer = messageADechiffrer.substring(0, messageADechiffrer.length()- bourrage);
		
		// On peut convertir en chaine de caract�res le message chiffr�
		StringBuilder sbDechiffr� = new StringBuilder(messageADechiffrer);
		String messageDechiffr� = BinaireTochaine(sbDechiffr�);
		System.out.println("Le message d�chiffr� est : ");
		System.out.println(messageDechiffr�);
		System.out.println("Message d'origine :");
		System.out.println(messageAChiffrerChaine);
		return null;
	}
	
	// Fonction qui va g�rer la substitution entre 2 mots de 64 bits
	public static String[] Substitution(String[] tabMessage){
		for(int i = 0; i < tabMessage.length-1; i += 2){
			tabMessage[i] = AdditionModulaire(tabMessage[i], tabMessage[i+1]);
			tabMessage[i+1] = xor(tabMessage[i], PermutationCirculaire(tabMessage[i+1]));
		}
		return tabMessage;
	}
	
	// Fonction qui va r�aliser une permutation circulaire sur un mot
	private static String PermutationCirculaire(String str1) {
		String[] tab1 = str1.split("");
		String result = "";
		for(int i = 0; i < tab1.length; i++){
			result += tab1[(i+49)%tab1.length];
		}
		return result;
	}
	
	// Fonction de permutation qui va inverser les elements du mot ([1 ... 64] => [64 ... 1])
	private static String Permutation(String str1){
		String[] tab1 = str1.split("");
		String resultat = "";
		for (int i = 0; i < tab1.length; i++){
			resultat += tab1[tab1.length-1-i];
		}
		return resultat;
	}
	
	// Fonction qui va g�rer la substitution inverse entre 2 mots de 64 bits
	public static String[] AntiSubstitution(String[] tabMessage){
		// Pour inverser la Substitution, il faut commencer par trouvrer m2 en xorant puis appliquant l'anti permut circulaire (voir sujet)
		for(int i = 0; i < tabMessage.length-1; i += 2){
			// On trouve m2
			tabMessage[i+1] = xor(tabMessage[i], tabMessage[i+1]);
			tabMessage[i+1] = AntiPermutationCirculaire(tabMessage[i+1]);
			// Pour inverser une addition modulaire, on peut ajouter l'oppos�.
			// On dispose de m'1 = tab[i] et de m2 = tab[i+1]. Et m'1 = AdditionModulaire(m1,m2).
			// On peut retrouver m1 en faisant m1 = AdditionModulaire(m'1, not(m2))
			// On cherche l'oppos� de m2 dans notre ensemble Z
			String oppos� = CalculOppos�(tabMessage[i+1]);
			tabMessage[i] = AdditionModulaire(tabMessage[i], oppos�);
		}
		return tabMessage;
	}
	
	// Fonction qui va chercher l'oppos� d'un element
	public static String CalculOppos�(String str1){
		String[] tab1 = str1.split("");
		String[] tab2 = new String[tab1.length];
		boolean retenue = false;
		int taille = tab1.length - 1;
		// On fait au cas pas cas pour calcul� l'oppos�
		for (int i = taille; i >= 0 ; i--){
			if(Integer.parseInt(tab1[i]) == 0 && retenue){
				tab2[taille-i] = "1";
				retenue = true;
			}
			else if (Integer.parseInt(tab1[i]) == 0 && !retenue){
				tab2[taille-i] = "0";
				retenue = false;
			}
			else if (Integer.parseInt(tab1[i]) == 1 && !retenue){
				tab2[taille-i] = "1";
				retenue = true;
			}
			else{
				tab2[taille-i] = "0";
				retenue = true;
			}			
		}
		String str2 = "";
		for(int i = tab2.length-1; i >=  0; i--){
			str2 += tab2[i];
		}
		// On va v�rifier que AdditionModulaire(tab1, tab2) = 0
		String test = AdditionModulaire(str1, str2);
		//System.out.println("Oppos� trouv� : " + str2);
		//System.out.println("R�sultat de l'addition modulaire : " + test);
		if (Integer.parseInt(test) == 0){
			return str2;
		}
		return null;
	}
	
	// Fonction qui va r�aliser une permutation circulaire sur un mot, cette permutation s'opposera � la fct PermutationCirculaire
	private static String AntiPermutationCirculaire(String str1){
		String[] tab1 = str1.split("");
		String result = "";
		for(int i = 0; i < tab1.length; i++){
			result += tab1[(i-49+tab1.length)%tab1.length];
		}
		return result;
	}
	
	private static String AdditionModulaire(String str1, String str2) {
		// TODO Auto-generated method stub
		boolean retenue = false;
		// On stocke le string le plus long dans str1
		if(str1.length() < str2.length()){
			String tampon = str1;
			str1 = str2;
			str2 = tampon;
		}
		String[] tab1 = str1.split("");
		String[] tab2 = str2.split("");
		String str3 = "";
		// On boucle sur le plus grand tableau tab1
		for (int i = 0; i < tab1.length; i++){
			// On v�rifie qu'on soit toujours dans le tableau tab2
			if(i < tab2.length){
				// Si les deux tableaux contiennent un 1
				if(Integer.parseInt(tab1[tab1.length-1-i]) == Integer.parseInt(tab2[tab2.length-1-i]) && Integer.parseInt(tab1[tab1.length-1-i])==1){
					// Si y'avait d�j� une retenue, la case vaudra 1
					if (retenue){
						str3 +="1";
					}
					// Sinon elle vaudra 0
					else {
						str3 +="0";
					}
					// On retient
					retenue = true;				
				}
				// Si les deux tableaux contiennent 0
				else if (Integer.parseInt(tab1[tab1.length-1-i]) == Integer.parseInt(tab2[tab2.length-1-i]) && Integer.parseInt(tab1[tab1.length-1-i])==0){
					// Si y'avait d�j� une retenue, la case vaudra 1
					if (retenue){
						str3 +="1";
					}
					// Sinon elle vaudra 0
					else {
						str3 +="0";
					}
					// On retient pas
					retenue = false;
				}
				// Le cas o� on a un 1 et un 0
				else{
					// Si y'avait d�j� une retenue, la case vaudra 0 et on retiendra
					if (retenue){
						str3 +="0";
						retenue = true;
					}
					// Sinon elle vaudra 1 et on ne retiendra pas
					else {
						str3 +="1";
						retenue = false;
					}
				}
			}
			// Si le tab2 est plus petit alors le r�sultat dependra seulement de tab1
			else {
				if (retenue){
					switch(Integer.parseInt(tab1[tab1.length-1-i])){
					case 0: str3 += "1";
						retenue = false;
						break;
					case 1: str3 += "0";
						retenue = true;
						break;
					default: System.out.println("Error ?");
					}
				}
				else {
					str3 += tab1[tab1.length-1-i];
				}				
			}
		}
		// Il faut retourner le r�sultat obtenu
		String [] tab3 = str3.split("");
		String resultat = "";
		for(int i = tab3.length-1; i >=  0; i--){
			resultat += tab3[i];
		}
		//System.out.println("Str1 :" + str1 + "\nStr2 :" + str2 + "\nR�sultat :" + resultat);
		return resultat;
	}

	public static String xor(String str1, String str2){
		try {
			String[] tab1 = str1.split("");
			String[] tab2 = str2.split("");
			String str3 = "";
			for (int i = 0; i < tab1.length; i++){
				if(Integer.parseInt(tab1[i]) == Integer.parseInt(tab2[i])){
					str3 += "0";
				}
				else{
					str3 += "1";
				}
			}
			//System.out.println(str1 + "\n" + str2 + "\n" + str3);
			return str3;
			
		}
		catch(ArrayIndexOutOfBoundsException exception){
			System.out.println("Les chaines envoy�es n'ont pas la meme taille, impossible de xorer");
			return null;
		}
		
	}

	public static void main(String[] args) {
		System.out.println("Bonjour et bienvenue dans le chiffrement sym�trique ThreeFish.\nVous souhaitez utiliser une cl� de 256, 512 ou 1024 bits ?");
		Scanner scan = new Scanner( System.in );
		int user_input = scan.nextInt();
		// On choisi ici entre 256, 512 ou 1024 pour la g�n�ration de la cl�
		String cle = GenerationCle(user_input);
		// Le nombre de d�coupage qu'on va faire sur la cl�
		int N = 0;
		// En fonction de la taille de la cl� on en d�duit le nombre de d�coupage de la cl�
		switch(cle.length()){
		case 32: N=4;
		break;
		case 64: N=8;
		break;
		case 128: N=16;
		break;
		default: System.out.println("La taille de la cl� n'est pas correcte");
		break;
		}
		// On rend la cl� binaire
		String cleBinary = ChaineToBinaire(cle).toString();
		// On g�n�re toutes les cl�s
		GenerationSousCles(cleBinary,N);
		// On ferme le scanner
		scan.close();
	}

}
