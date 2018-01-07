import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Hashage {

	//Permet de g�n�rer une chaine de caract�res correspondant au Hash d'un fichier dig�r� en utilisant SHA512

	public static BigInteger getHashFromSHA512(String message){
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512"); //instanciation de l'algorithme SHA-512
			md.update(message.getBytes());
			BigInteger hash = new BigInteger(md.digest());
			System.out.println(hash.toString());

			byte[] byteData = hash.toByteArray();
			return hash; //retourne le Hash du message sous la forme d'un BigInteger pour des raisons pratiques (utilis� dans CramerShoup)

		}
		catch (NoSuchAlgorithmException e){
			e.printStackTrace();
			return null;
		}
	}


	//Genere le hash d'un fichier en entr�e, puis �crit le hash dans le fichier hash_sha512.txt du r�pertoire courant
	public static void generationHashSHA512fromFileBIformat(){
		String messageBrut = Utilitaires.Lecture();
		BigInteger hash = getHashFromSHA512(messageBrut);

		Utilitaires.Ecriture(
			hash.toString()+"\n",
			"./hash_sha512.txt"
		);
	}
	
	//Genere le hash d'un fichier en entr�e, puis �crit le hash dans le fichier hash_sha512.txt du r�pertoire courant
		public static String generationHashThreeFish(Scanner scan, int tailleCle){
			System.out.println("Rentrez la cle que vous voulez utiliser pour chifffrer ou d�chiffrer !");
			scan.nextLine();
			String messageBrut = scan.nextLine();
			BigInteger hash = getHashFromSHA512(messageBrut);
			String hashString = hash.toString();
			String hashBinary = ThreeFish.StringToBinary(hashString);
			hashBinary = hashBinary.substring(0,tailleCle/8);
			return hashBinary;
		}

	//Permet de comparer le Hash g�n�r� d'un fichier, par rapport � un hash enregistr� dans un fichier
	//Et renvoie dans le terminal si les deux Hash sont les m�mes

	public static void verificationHashSHA512fromFileBIformat(){
		String messageBrut = Utilitaires.Lecture();
		BigInteger hash = getHashFromSHA512(messageBrut);

		BigInteger hashTeste = new BigInteger(Utilitaires.Lecture().split("\n")[0]);

		if(hash.equals(hashTeste))
			System.out.println("Hash valide");
		else
			System.out.println("Hash non valide");

	}

	
	//Utilis� pour des besoins de tests
	public static void main(String[] args) {
		generationHashSHA512fromFileBIformat();
		verificationHashSHA512fromFileBIformat();
	}

}
