import java.awt.Graphics;
import java.awt.Color;
public class CommunityChestCard
{
 Board board;
 int cardNumber;
 String description;
 public CommunityChestCard(Board bored, int crd, String des)
 {
  board = bored;
  cardNumber = crd;
  description = des;
 }
 public void activate(Token token)
 {
  switch (cardNumber)
  {
   case 0: token.advanceTo(board.deedList[0]); break;
   case 1: board.bank.pay(token, 200); break;
   case 2: token.payBank(50); break;
   case 3: board.bank.pay(token, 50); break;
   case 4: token.cards++; break;
   case 5: token.putInJail(); break;
   case 6: for (int index = 0; index < board.numberOfTokens(); index++) board.tokens[index].pay(token, 50); break;
   case 7: board.bank.pay(token, 100); break;
   case 8: board.bank.pay(token, 20); break;
   case 9: board.bank.pay(token, 100); break;
   case 10: token.payBank(100); break;
   case 11: token.payBank(150); break;
   case 12: board.bank.pay(token, 25); break;
   case 13: token.payBank(token.houses * 40 + token.hotels * 115); break;
   case 14: board.bank.pay(token, 10); break;
   case 15: board.bank.pay(token, 100); break;
  }
 }
 public void draw(Graphics graphic)
 {
  int width = board.BoardDimensions.width, height = board.BoardDimensions.height;
  graphic.setColor(Color.orange);
  graphic.drawRect(0, 0,(int) (7.5/51*.8*width), (int) (4.0/51*.8*height));
  graphic.drawString(description, 0, 0);
 }
 public String toString()
 {
  return "Number: " + cardNumber + "\nDescription" + description;
 }
}