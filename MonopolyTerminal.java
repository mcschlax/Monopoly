import java.util.Scanner;
public class MonopolyTerminal
{
 public static void main(String[] args)
 {
  Scanner scan = new Scanner(System.in);
  System.out.print("Welcome!\nHow many players?");
  int length = Integer.parseInt(scan.nextLine());
  String[] names = new String[length];
  for (int i =  length - 1; i >= 0; i--)
  {
   System.out.print("Name: ");
   names[i] = scan.nextLine();
  }
  Board monopoly = new Board(names, null, 0, null);
  while (!monopoly.gameOver()){System.out.println("Turn: " + monopoly.turn);
   for (int i = 0; i < length; i++) System.out.println(monopoly.tokens[i]);
   System.out.println(monopoly.bank);
   monopoly.nextTurn();}
 }
}