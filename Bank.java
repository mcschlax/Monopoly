
public class Bank
{
 int[] bills;
 int totalCash, houses, hotels, cards;
 String name;
 Deed[] deedList;
 Board board;
 public Bank(Board bored) 
 {
  name = "Bank";
  board = bored;
  deedList = new Deed[40];
  bills = new int[7]; //[0] = $500 bills, [1] = $100 bills, [2] = $50 bills, [3] = $20 bills, [4] = $10 bills, [5] = $5 bills, [6] $1 bills.
  int temp = board.numberOfTokens();
  bills[0] = 20 - temp;
  bills[1] = 20 - temp;
  bills[2] = 30 - temp;
  bills[3] = 50 - temp;
  bills[4] = 40 - temp;
  bills[5] = 40 - temp;
  bills[6] = 40 - temp;
  totalCash = bills[0] * 500 + bills[1] * 100 + bills[2] * 50 + bills[3] * 20 + bills[4] * 10 + bills[5] * 5 + bills[6] * 1;
  houses = 32;
  hotels = 12;
  cards = 2;
  for (int index = 0; index < deedList.length; index++) addDeed(board.deedList[index]);
 }
 public void pay(Token collector, int value)
 {
  int[] temp = breakIntoBills(value);
  printOut(name + "-Paying " + collector.name + ": " + value);
  subtractBills(temp);
  collector.addBills(temp);
 }
 public void pay(Token collector, Deed deed)
 {
  int[] temp = breakIntoBills(deed.getCurrentValue());
  printOut(name + "-Paying " + collector.name + ": " + deed.getCurrentValue());
  subtractBills(temp);
  collector.addBills(temp);
 }
 public void tradeCard(Token seller, int price)
 {
  int[] temp = breakIntoBills(price);
  printOut(name + "-Trading/buying Card from " + seller.name + ": for" + price);
  subtractBills(temp);
  seller.addBills(temp);
  seller.cards--;
  cards++;
 }
 public void addBills(int[] newBills)
 {
  printOut(name + "--Adding Bills: " + (newBills[0] * 500 + newBills[1] * 100 + newBills[2] * 50 + newBills[3] * 20 + newBills[4] * 10 + newBills[5] * 5 + newBills[6] * 1));
  for (int index = 0; index < bills.length; index++) bills[index] += newBills[index];
  totalCash = bills[0] * 500 + bills[1] * 100 + bills[2] * 50 + bills[3] * 20 + bills[4] * 10 + bills[5] * 5 + bills[6] * 1;
 }
 public void subtractBills(int[] newBills)
 {
  printOut(name + "--Subtracting: " + (newBills[0] * 500 + newBills[1] * 100 + newBills[2] * 50 + newBills[3] * 20 + newBills[4] * 10 + newBills[5] * 5 + newBills[6] * 1) + " to <LOOK ABOVE>");
  if ((totalCash -= newBills[0] * 500 + newBills[1] * 100 + newBills[2] * 50 + newBills[3] * 20 + newBills[4] * 10 + newBills[5] * 5 + newBills[6] * 1)  < 0)
   addBills(newBills);
  for (int index = newBills.length - 1; index >= 0; index--) bills[index] -= newBills[index];
 }
 public void auction(Deed deed)
 {
  printOut(name + "-Auctioning: " + deed);
  int length = board.numberOfTokens() - 1, lastBidder = -1, currentBidder = 0, lastBid = 0;
  while (lastBidder != currentBidder || lastBid == 0)
  {
   if (lastBidder == -1 && board.promptBooleanInput("Last Bid: " + lastBid + " by no-one, " + board.tokens[currentBidder].name  + " place a bid or pass"))
   {
    int nextBid;
    do nextBid = board.promptIntInput("Bid : (Greater than last Bid)"); while (nextBid <= lastBid);
    lastBid = nextBid;
    lastBidder = currentBidder;
   }
   if (lastBidder != -1 && lastBidder != currentBidder &&board.promptBooleanInput("Last Bid: " + lastBid + " by " + board.tokens[lastBidder].name + ", " + board.tokens[currentBidder].name  + " place a bid or pass"))
   {
    int nextBid;
    do nextBid = board.promptIntInput("Bid: (Greater than last Bid)"); while (nextBid <= lastBid);
    lastBid = nextBid;
    lastBidder = currentBidder;
   }
   if (++currentBidder > length) currentBidder = 0;
  }
  board.tokens[lastBidder].purchase(deed, lastBid);
 }
 public void auction(Deed deed, Token dontBidTo)
 {
  printOut(name + "-Auctioning: " + deed);
  int length = board.numberOfTokens() - 1, lastBidder = -1, currentBidder = 0, lastBid = 0;
  while (lastBidder != currentBidder || lastBid == 0)
  {
   if (board.tokens[currentBidder] != dontBidTo && lastBidder == -1 && board.promptBooleanInput("Last Bid: " + lastBid + " by no-one, " + board.tokens[currentBidder].name  + " place a bid or pass"))
   {
    int nextBid;
    do nextBid = board.promptIntInput("Bid : (Greater than last Bid)"); while (nextBid <= lastBid);
    lastBid = nextBid;
    lastBidder = currentBidder;
   }
   if (board.tokens[currentBidder] != dontBidTo && lastBidder != -1 && lastBidder != currentBidder && board.promptBooleanInput("Last Bid: " + lastBid + " by " + board.tokens[lastBidder].name + ", " + board.tokens[currentBidder].name  + " place a bid or pass"))
   {
    int nextBid;
    do nextBid = board.promptIntInput("Bid: (Greater than last Bid)"); while (nextBid <= lastBid);
    lastBid = nextBid;
    lastBidder = currentBidder;
   }
   if (++currentBidder > length) currentBidder = 0;
  }
  board.tokens[lastBidder].purchase(deed, lastBid);
 }
 public int[] breakIntoBills(int value)
 {
  printOut(name + "---BreakingIntoBills: " + value);
  int[] temp = {value/500, (value%500)/100, ((value%500)%100)/50, (((value%500)%100)%50)/20, ((((value%500)%100)%50)%20)/10, (((((value%500)%100)%50)%20)%10)/5, (((((value%500)%100)%50)%20)%10)%5};
  return temp;
 }
 public void addDeed(Deed deed)
 {
  printOut(name + "--Adding Deed: " + deed);
  deed.owner = null;
  deedList[deed.location] = deed;
 }
 public void subtractDeed(Deed deed)
 {
  printOut(name + "--Subtracting Deed: " + deed);
  deedList[deed.location] = null;
 }
 public String toString()
 {
  return name + "\nTotal Cash: " + totalCash;
 }
 public void printOut(String string)
 {
  if (board.GUI != null) board.GUI.textOutput.append(string+"\n");
  else System.out.println(string);
 }
}