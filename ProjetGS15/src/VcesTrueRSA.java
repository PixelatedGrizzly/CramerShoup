import java.util.Scanner;
public class VcesTrueRSA {
	public static void main (String[] args){
		Scanner user_input = new Scanner( System.in );
		System.out.println("Selectionner votre fonction de chiffrement : \t");
		System.out.println("->1<- Chiffrement sym�trique ThreeFish\t");
		System.out.println("->2<- Chiffrement de Cramer-Shoup\t");
		System.out.println("->3<- Hashage d'un message\t");
		System.out.println("->4<- D�chiffrement sym�trique ThreeFish\t");
		System.out.println("->5<- D�chiffrement de Cramer-Shoup\t");
		System.out.println("->6<- V�rification d'un hash\t");
		String sChoixUser = user_input.nextLine();
		int iChoixUser = Integer.parseInt(sChoixUser);
		switch (iChoixUser) {
		case 1: System.out.println("Appel de la fonction de Chiffrement sym�trique ThreeFish");
		break;
		case 2: System.out.println("Appel de la fonction de Chiffrement Cramer-Shoup");
		break;
		case 3: System.out.println("Appel de la fonction Hashage d'un message");
		break;
		case 4: System.out.println("Appel de la fonction de d�chiffrement sym�trique ThreeFish");
		break;
		case 5: System.out.println("Appel de la fonction de d�chiffrement de Cramer-Shoup");
		break;
		case 6: System.out.println("Appel de la fonction de v�rification d'un hash");
		break;
		default: System.out.println("Entr�e invalide veuillez recommencer !");
		break;
		}
		
	}
}
