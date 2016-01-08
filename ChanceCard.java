import java.awt.Graphics;
import java.awt.Color;
public class ChanceCard
{
 Board board;
 int cardNumber;
 String description;
 public ChanceCard(Board bored, int crd, String des)
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
   case 1: token.advanceTo(board.deedList[24]); board.deedList[token.location].activate(token); break;
   case 2: token.advanceTo(board.deedList[11]); board.deedList[token.location].activate(token); break;
   case 3: token.advanceTo(board.getGroup("Utilities")); if (board.deedList[token.location].owner == null) board.deedList[token.location].activate(token); else token.pay(board.deedList[token.location].owner, (board.rollDie() + board.rollDie()) * 10); break;
   case 4: token.advanceTo(board.getGroup("Railroad")); board.deedList[token.location].activate(token); board.deedList[token.location].activate(token); break;
   case 5: board.bank.pay(token, 50); break;
   case 6: token.cards++; break;
   case 7: token.move(-3); board.deedList[token.location].activate(token); break;
   case 8: token.putInJail(); break;
   case 9: token.payBank(token.houses * 25 + token.hotels * 100); break;
   case 10: token.payBank(15); break;
   case 11: token.advanceTo(board.deedList[5]); board.deedList[token.location].activate(token); break;
   case 12: token.advanceTo(board.deedList[39]); board.deedList[token.location].activate(token); break;
   case 13: for(int index = 0; index < board.numberOfTokens(); index++) token.pay(board.tokens[index], 50); break;
   case 14: board.bank.pay(token, 150); break;
  }
 }
 public String toString()
 {
  return "Number: " + cardNumber + "\nDescription" + description;
 }
}