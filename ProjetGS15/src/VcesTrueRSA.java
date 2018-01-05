import java.util.InputMismatchException;
import java.util.Scanner;
public class VcesTrueRSA {

	public static void main (String[] args){
		Scanner scan = new Scanner( System.in );
		System.out.println("Selectionner votre fonction de chiffrement : \t");
		System.out.println("->1<- Chiffrement sym�trique ThreeFish\t");
		System.out.println("->2<- Chiffrement de Cramer-Shoup\t");
		System.out.println("->3<- Hashage d'un message\t");
		System.out.println("->4<- D�chiffrement de Cramer-Shoup\t");
		System.out.println("->5<- V�rification d'un hash\t");
		System.out.println("->6<- G�n�ration de cl�s pour Cramer-Shoup\t");
		System.out.println("->7<- Fin du programme");

		int iChoixUser = scan.nextInt();
		switch (iChoixUser) {
		case 1: System.out.println("Appel de la fonction de Chiffrement sym�trique ThreeFish");
		ThreeFish.Initialisation(scan);
		break;
		case 2: System.out.println("Appel de la fonction de Chiffrement Cramer-Shoup \nVeuillez selectionnez le message � chiffrer");
		CramerShoup.chiffrementCramerShoup();
		break;
		case 3: System.out.println("Appel de la fonction Hashage d'un message \nVeuillez s�lectionner le fichier � Hasher");
		Hashage.generationHashSHA512fromFileBIformat();
		break;
		case 4: System.out.println("Appel de la fonction de d�chiffrement de Cramer-Shoup \nVeuillez selectionnez le fichier de cl� privée, puis le message à déchiffrer");
		CramerShoup.dechiffrementCramerShoup();
		break;
		case 5: System.out.println("Appel de la fonction de v�rification d'un hash \nVeuillez selectioner le fichier � v�rifier, et le fichier de Hash");
		Hashage.verificationHashSHA512fromFileBIformat();
		break;
		case 6: System.out.println("Appel de la fonction de g�n�ration de cl� pour Cramer-Shoup");
		CramerShoup.generationClePubliquePrivee();
		break;
		case 7: System.out.println("Fin du programme, bonne journ�e");
		break;			
		default: System.out.println("Entr�e invalide veuillez recommencer !");
		break;
		}
		scan.close();

	}

}
