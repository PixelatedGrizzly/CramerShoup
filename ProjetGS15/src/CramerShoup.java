
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

public class CramerShoup {

	private static int bitNumber = 128;

	public CramerShoup(){}

	public static ArrayList<Long> genElementGenerateurSurNonFriable(BigInteger bi){
		BigInteger ordreRecherche = new BigInteger(bi.toByteArray());
		ordreRecherche = ordreRecherche.subtract(BigInteger.valueOf(1));
		BigInteger secondFacteur = new BigInteger(ordreRecherche.toByteArray());
		secondFacteur = secondFacteur.divide(BigInteger.valueOf(2));

		BigInteger test;
		ArrayList<Long> result = new ArrayList<Long>();

		for (long i = 2 ; i <= 101 ; i++) {
			test = BigInteger.valueOf(i);
			if(test.modPow(secondFacteur, bi).compareTo(BigInteger.ONE) != 0 && test.modPow(BigInteger.valueOf(2), bi).compareTo(BigInteger.ONE) != 0){
				result.add(i);
			}
		}

		return result;
	}

	public static void generationClePubliquePrivee(){
		Random rnd = new Random(); //Creation generateur de nombres pseudo al�toire

		BigInteger p = new BigInteger(bitNumber*2, 100, rnd); //p est cr�� 2 fois plus grand pour �tre de mani�re sur plus grand que toutes les autres variables
		BigInteger deriv = new BigInteger(p.toByteArray()); //deriv permet de v�rifier que p est non friable
		deriv = deriv.subtract(BigInteger.valueOf(1)).divide(BigInteger.valueOf(2));
		boolean friable = true;

		//ici, on va tester si p est friable, et s'il l'est, alors on reg�n�re jusqu'� ce qu'il soit non friable
		if (deriv.isProbablePrime(100)) {
			friable = false;
		}
		else{
			while(friable){
				p = new BigInteger(bitNumber*2, 100, rnd);
				deriv = new BigInteger(p.toByteArray());
				deriv = deriv.subtract(BigInteger.valueOf(1)).divide(BigInteger.valueOf(2));
				if(deriv.isProbablePrime(100)){
					friable = false;
				}
			}
		}

		System.out.println("p = "+p.toString());

		//Ici on va g�n�rer les autres �l�ments pour nos cl�s priv�e et publique
		ArrayList<Long> premElemGenerateur = genElementGenerateurSurNonFriable(p);

		BigInteger a1 = new BigInteger(premElemGenerateur.get(premElemGenerateur.size()-1)+"");
		BigInteger a2 = new BigInteger(premElemGenerateur.get(premElemGenerateur.size()-2)+"");

		BigInteger x1 = new BigInteger(bitNumber, rnd);
		BigInteger x2 = new BigInteger(bitNumber, rnd);
		BigInteger y1 = new BigInteger(bitNumber, rnd);
		BigInteger y2 = new BigInteger(bitNumber, rnd);
		BigInteger w = new BigInteger(bitNumber, rnd);

		BigInteger majX = a1.modPow(x1, p).multiply(a2.modPow(x2, p)).mod(p);
		BigInteger majY = a1.modPow(y1, p).multiply(a2.modPow(y2, p)).mod(p);
		BigInteger majW = a1.modPow(w, p);

		System.out.println("a1 = "+a1.toString());
		System.out.println("a2 = "+a2.toString());
		System.out.println("X = "+majX.toString());
		System.out.println("Y = "+majY.toString());
		System.out.println("W = "+majW.toString());

		//On �crit ici le fichier de cl� public (key.pub dans le r�pertoire courant), puis le fichier de cl� priv�e ()

		Utilitaires.Ecriture(
		p.toString()+" "+a1.toString()+" "+a2.toString()+" "+majX.toString()+" "+majY.toString()+" "+majW.toString()+" ",
		"./key.pub"
		);

		Utilitaires.Ecriture(
		p.toString()+" "+a1.toString()+" "+a2.toString()+" "+x1.toString()+" "+x2.toString()+" "+y1.toString()+" "+y2.toString()+" "+w.toString()+" ",
		"./key.prv"
		);
	}

	public static void chiffrementCramerShoup(){
		Random rnd = new Random(); //Creation generateur de nombres pseudo al�toire

		//on r�cup�re ici les diff�rentes variables de cl� publique
		System.out.println("S�lectionnez le fichier de cl�s publiques !");
		String[] variables = Utilitaires.Lecture().split(" ");
		BigInteger p = new BigInteger(variables[0]);
		BigInteger a1 = new BigInteger(variables[1]);
		BigInteger a2 = new BigInteger(variables[2]);
		BigInteger majX = new BigInteger(variables[3]);
		BigInteger majY = new BigInteger(variables[4]);
		BigInteger majW = new BigInteger(variables[5]);

		//on r�cup�re ici le message � chiffrer
		System.out.println("S�lectionnez le fichier � chiffrer");
		String messageBrut = Utilitaires.Lecture();
		byte[] messageClair = messageBrut.getBytes();

		System.out.println(messageBrut);
		for (int i = 0 ; i<messageClair.length ; i++) {
			System.out.println(messageBrut.charAt(i)+" ["+messageClair[i]+"]");
		}

		BigInteger messageChiffrable = new BigInteger(messageClair);

		BigInteger b = new BigInteger(bitNumber, rnd);
		BigInteger majB1 = a1.modPow(b, p);
		BigInteger majB2 = a2.modPow(b, p);
		BigInteger messageChiffre = majW.modPow(b, p).multiply(messageChiffrable).mod(p);

		System.out.println("p = "+p.toString());
		System.out.println("mes clair = "+messageChiffrable.toString());
		System.out.println("m chiffre = "+messageChiffre.toString());

		//G�n�ration des variables de v�rification du chiffr�
		BigInteger beta = Hashage.getHashFromSHA512(majB1.toString()+majB2.toString()+messageChiffre.toString());
		BigInteger v_verif = majX.modPow(b, p).multiply(majY.modPow(b.multiply(beta), p)).mod(p);

		Utilitaires.Ecriture(
			majB1.toString()+" "+majB2.toString()+" "+messageChiffre.toString()+" "+v_verif.toString()+" ",
			"messageChiffreCS.txt"
		);
	}

	public static void dechiffrementCramerShoup(){
		//On r�cup�re ici les diff�rentes variables qui composent la cl� priv�e
		System.out.println("S�lectionnez le fichier de cl�s priv�es !");
		String[] variables = Utilitaires.Lecture().split(" ");
		BigInteger p = new BigInteger(variables[0]);
		BigInteger a1 = new BigInteger(variables[1]);
		BigInteger a2 = new BigInteger(variables[2]);
		BigInteger x1 = new BigInteger(variables[3]);
		BigInteger x2 = new BigInteger(variables[4]);
		BigInteger y1 = new BigInteger(variables[5]);
		BigInteger y2 = new BigInteger(variables[6]);
		BigInteger w = new BigInteger(variables[7]);
		
		//On r�cup�re ici les diff�rentes variables qui composent notre message chiffr�
		System.out.println("S�lectionnez le fichier � d�chiffrer!");
		variables = Utilitaires.Lecture().split(" ");
		BigInteger majB1 = new BigInteger(variables[0]);
		BigInteger majB2 = new BigInteger(variables[1]);
		BigInteger messageChiffre = new BigInteger(variables[2]);
		BigInteger v_verif = new BigInteger(variables[3]);

		// On recalcule ici le hash pour ensuite v�rifier l'int�grit� du message chiffr� et ensuite le d�chiffrer s'il n'a pas �t� alt�r�
		BigInteger betaPrime = Hashage.getHashFromSHA512(majB1.toString()+majB2.toString()+messageChiffre.toString());
		BigInteger v_verifPrime = majB1.modPow(x1, p).multiply(majB2.modPow(x2, p)).multiply(majB1.modPow(y1, p).modPow(betaPrime, p)).mod(p).multiply(majB2.modPow(y2, p).modPow(betaPrime, p)).mod(p);

		if(v_verif.equals(v_verifPrime)){
			System.out.println("Verif juste");
			BigInteger messageDechiffre = new BigInteger(messageChiffre.toByteArray());
			BigInteger invPower = p.subtract(BigInteger.ONE).subtract(w);
			BigInteger invB1poww = majB1.modPow(invPower, p);
			messageDechiffre = messageDechiffre.multiply(invB1poww).mod(p);
			
			//On affiche le message d�chiffr� dans l'invit� de commandes
			System.out.println("m dechiffre = "+messageDechiffre.toString());
	
			byte[] messageDechiffreByte = messageDechiffre.toByteArray();
			for (byte b :messageDechiffreByte ) {
				System.out.print((char)b);
			}
		}
		else {
			System.out.println("Verif fausse");
		}

		
		System.out.println("\nFin message");
	}
	//Utilis� pour des besoins de tests
	public static void main(String[] args) {
		generationClePubliquePrivee();
		chiffrementCramerShoup();
		dechiffrementCramerShoup();
	}
}
