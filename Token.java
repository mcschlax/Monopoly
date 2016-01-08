import java.awt.Graphics;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.Image;
import java.awt.Font;
import java.awt.Graphics2D;
public class Token
{
 int[] bills;
 int totalCash, location, houses, hotels, cards, jailed;
 String name;
 Board board;
 Deed[] deedList;
 Image image;
 public Token(String nam, Board bored)
 {
  name = nam;
  board = bored;
  deedList = new Deed[board.deedList.length];
  bills = new int[7]; //[0] = $500 bills, [1] = $100 bills, [2] = $50 bills, [3] = $20 bills, [4] = $10 bills, [5] = $5 bills, [6] $1 bills.
  bills[0] = 2;
  bills[1] = 2;
  bills[2] = 2;
  bills[3] = 6;
  bills[4] = 5;
  bills[5] = 5;
  bills[6] = 5;
  totalCash = bills[0] * 500 + bills[1] * 100 + bills[2] * 50 + bills[3] * 20 + bills[4] * 10 + bills[5] * 5 + bills[6] * 1;
  location = 0;
  houses = 0;
  hotels = 0;
  cards = 0;
  jailed = 0;
 }
 public void move()
 {
  int doubles = 0;
  do
  {
   int roll1 = board.rollDie(), roll2 = board.rollDie();
   if (roll1 == roll2) doubles++;
   else doubles = 0;
   if (jailed == 0) 
   {
    location += (roll1 + roll2);
    board.printOut(name + "-Roll 1: " + roll1 + "\n-Roll 2: " + roll2);
    board.printOut(name + "-Moving: " + (roll1 + roll2));
    if (location >= 40)
    {
     location %= 40;
     board.deedList[0].activate(this);
    }
   }
   else if (jailed != 0)
   {
    if (cards > 0 && board.promptBooleanInput("Use a Get Out Of Jail Free Card?"))
    {
     cards--;
     jailed = 0;
     location += (roll1 + roll2);
     board.printOut(name + "-Moving: " + (roll1 + roll2));
     if (location >= 40) 
     {
      location %= 40;
      board.deedList[0].activate(this);
     }
    }
    else if (doubles != 0 || jailed >= 4)
    {
     if (jailed >= 4) payBank(50);
     jailed = 0;
     doubles = 0;
     location += (roll1 + roll2);
     board.printOut(name + "-Moving: " + (roll1 + roll2));
     if (location >= 40) 
     {
      location %= 40;
      board.deedList[0].activate(this);
     }
    }
    else jailed++;
   }
   board.deedList[location].activate(this);
   if (doubles >= 3) putInJail();
  } while (doubles > 0 || doubles >= 3);
 }
 public void advanceTo(Deed deed)
 {
  board.printOut(name + "-Advanding to " + deed);
  int deedLocation = deed.location;
  if (deedLocation - location > 0) move(deedLocation - location);
  else move(40 - location + deedLocation);
 }
 public void advanceTo(Deed[] deedType)
 {
  board.printOut(name + "-Advanding to " + deedType[0].type);
  int deedLocation = 40;
  for(int index = deedType.length - 1; index >= 0; index--)
   if((deedType[index].location - location) < deedLocation) deedLocation = deedType[index].location;
  if (deedLocation - location > 0) move(deedLocation - location);
  else move(40 - location + deedLocation);
 }
 public void move(int num)
 {
  board.printOut(name + "-Moving: " + num);
  location += num;
  if (location >= 40) 
  {
   location %= 40;
   board.deedList[0].activate(this);
  }
 }
 public void payBank(Deed deed)
 {
  int[] temp = breakIntoBills(deed.getCurrentValue());
  board.printOut(name + "-Paying Bank: " + deed.getCurrentValue());
  subtractBillsToBank(temp);
  board.bank.addBills(temp);
 }
 public void payBank(int value)
 {
  int[] temp = breakIntoBills(value);
  board.printOut(name + "-Paying Bank: " + value);
  subtractBillsToBank(temp);
  board.bank.addBills(temp);
 }
 public void pay(Token collector, int value)
 {
  board.printOut(name + "-Paying " + collector.name + ": " + value);
  int[] temp = breakIntoBills(value);
  subtractBills(temp, collector);
  collector.addBills(temp);
 }
 public void pay(Token collector, Deed deed)
 {
  int[] temp = breakIntoBills(deed.getCurrentValue());
  board.printOut(name + "-Paying " + collector.name + ": " + deed.getCurrentValue());
  subtractBills(temp, collector);
  collector.addBills(temp);
 }
 public void purchase(Deed deed)//The purchase method would be used for when a deed is bought for the first time.
 {
  board.printOut(name + "-Purchasing: " + deed);
  int[] temp = breakIntoBills(deed.getCurrentValue());
  subtractBillsToBank(temp);
  board.bank.addBills(temp);
  deed.owner = this;
  board.bank.subtractDeed(deed);
  addDeed(deed);
 }
 public void purchase(Deed deed, int price)//This purchase method would be used after an auction.
 {
  board.printOut(name + "-Purchasing: " + deed + " for " + price);
  int[] temp = breakIntoBills(price);
  subtractBillsToBank(temp);
  board.bank.addBills(temp);
  deed.owner = this;
  board.bank.subtractDeed(deed);
  addDeed(deed);
 }
 public void trade(Deed[] deeds, Token seller, int price)
 {
  board.printOut(name + "-Trading to " + seller.name + ": deeds[] for " + price);
  int[] temp = breakIntoBills(price);
  subtractBills(temp, seller);
  seller.addBills(temp);
  for (int index = deeds.length - 1; index >= 0; index--)
  {
   if (deeds[index].hotels > 0)
    seller.sellHotels(deeds[index]);
   if (deeds[index].houses > 0)
    seller.sellHouses(deeds[index]);
   deeds[index].owner = this;
   seller.subtractDeed(deeds[index]);
   addDeed(deeds[index]);
  }
 }
 public void trade(Deed[] deeds, Token seller, Deed[] deeds2)
 {
  board.printOut(name + "-Trading to " + seller.name + ": deeds[] for deeds[]");
  for (int index = deeds2.length - 1; index >= 0; index--)
  {
   if (deeds2[index].hotels > 0)
    sellHotels(deeds2[index]);
   while (deeds2[index].houses > 0)
    sellHouses(deeds2[index]);
   deeds2[index].owner = seller;
   subtractDeed(deeds2[index]);
   seller.addDeed(deeds2[index]);
  }
  for (int index = deeds.length - 1; index >= 0; index--)
  {
   if (deeds[index].hotels > 0)
    seller.sellHotels(deeds[index]);
   while (deeds[index].houses > 0)
    seller.sellHouses(deeds[index]);
   deeds[index].owner = this;
   seller.subtractDeed(deeds[index]);
   addDeed(deeds[index]);
  }
 }
 public void trade(Deed[] deeds, Token seller, Deed[] deeds2, int price)
 {
  board.printOut("-Trading: deeds[] for deeds[], and " + price);
  int[] temp = breakIntoBills(price);
  subtractBills(temp, seller);
  seller.addBills(temp);
  for (int index = deeds2.length - 1; index >= 0; index--)
  {
   if (deeds2[index].hotels > 0)
    sellHotels(deeds[index]);
   while (deeds2[index].houses > 0)
    sellHouses(deeds[index]);
   deeds2[index].owner = seller;
   subtractDeed(deeds2[index]);
   seller.addDeed(deeds2[index]);
  }
  for (int index = deeds.length - 1; index >= 0; index--)
  {
   if (deeds[index].hotels > 0)
    seller.sellHotels(deeds[index]);
   while (deeds[index].houses > 0)
    seller.sellHouses(deeds[index]);
   deeds[index].owner = this;
   seller.subtractDeed(deeds[index]);
   addDeed(deeds[index]);
  }
 }
 public void tradeCard(Token seller, int price)
 {
  board.printOut(name + "-Trading/buying Card from " + seller.name + ": for" + price);
  int[] temp = breakIntoBills(price);
  subtractBills(temp, seller);
  seller.addBills(temp);
  seller.cards--;
  cards++;
 }
 public void mortgageDeed(Deed deed)
 {
  if (!deed.inMortgage && deed.houses == 0 && deed.hotels == 0)
  {
   board.printOut(name + "-Mortgaging: " + deed);
   deed.inMortgage = true;
   int[] temp = breakIntoBills(deed.getCurrentValue());
   board.bank.subtractBills(temp);
   addBills(temp);
  }
  else if (deed.houses != 0 || deed.hotels != 0)
   board.printOut(name + "Sell all houses and hotels before mortgaging "+deed.name);
 }
 public void unmortgageDeed(Deed deed)
 {
  if (deed.inMortgage)
  {
   board.printOut(name + "-Unmortgaging: " + deed);
   int[] temp = breakIntoBills((int)(1.1 *deed.getCurrentValue()));
   board.bank.addBills(temp);
   subtractBillsToBank(temp);
   deed.inMortgage = false;
  }
 }
 public void buyHouses(Deed deed)
 {
  board.printOut(name + "-Buying Houses for " + deed);
  int deedHouses = deed.houses;
  if (hasGroup(deed.group) && deedHouses < 4 && board.bank.houses > 0)
  {
   Deed[] temp  = board.getGroup(deed.group);
   boolean check = false;
   for (int index = temp.length - 1; index >= 0; index--)
   {
    int tempHouses = temp[index].houses;
    if (deedHouses == tempHouses + 1 || deedHouses == tempHouses)
     check = true;
    else check = false;
   }
   if (check)
   {
    int[] tempBills = breakIntoBills(deed.getHouseCost());
    subtractBillsToBank(tempBills);
    board.bank.addBills(tempBills);
    board.bank.houses--;
    houses++;
    deed.houses++;
   }
  }
 }
 public void buyHotels(Deed deed)
 {
  board.printOut(name + "-Buying Hotel for " + deed);
  int deedHouses = deed.houses;
  if (hasGroup(deed.group) && deedHouses == 4 && deed.hotels == 0 && board.bank.hotels > 0)
  {
   Deed[] temp  = board.getGroup(deed.group);
   boolean check = false;
   for (int index = temp.length - 1; index >= 0; index--)
    if (temp[index].houses == 4 || temp[index].hotels == 1)
     check = true;
    else check = false;
   if (check)
   {
    int[] tempBills = breakIntoBills(deed.getHouseCost());
    subtractBillsToBank(tempBills);
    board.bank.addBills(tempBills);
    board.bank.houses += 4;;
    houses -= 4;
    deed.houses -= 4;;
    board.bank.hotels -= 1;
    hotels++;
    deed.hotels = 1;
   }
  }
 }
 public void sellHouses(Deed deed)
 {
  board.printOut(name + "-Selling Houses for " + deed);
  int deedHouses = deed.houses;
  if (hasGroup(deed.group) && deedHouses > 0)
  {
   Deed[] temp  = board.getGroup(deed.group);
   boolean check = false;
   for (int index = temp.length - 1; index >= 0; index--)
   {
    int tempHouses = temp[index].houses;
    if (temp[index].hotels < 0 && (deedHouses == tempHouses + 1 || deedHouses == tempHouses))
     check = true;
    else check = false;                                                                                                                         
   }
   if (check)
   {
    int[] tempBills = breakIntoBills(deed.getHouseCost()/2);
    board.bank.subtractBills(tempBills);
    addBills(tempBills);
    board.bank.houses--;
    houses--;
    deed.houses -= 1;
   }
  }
 }
 public void sellHotels(Deed deed)
 {
  board.printOut(name + "-Selling Hotel for " + deed);
  int deedHouses = deed.houses;
  if (hasGroup(deed.group) && deed.hotels == 1)
  {
   Deed[] temp  = board.getGroup(deed.group);
   boolean check = false;
   for (int index = temp.length - 1; index >= 0; index--)
    if (temp[index].houses == 4 || temp[index].hotels == 1 && board.bank.houses >= 4)
     check = true;
    else check = false;
   if (check)
   {
    int[] tempBills = breakIntoBills(deed.getHouseCost()/2);
    board.bank.subtractBills(tempBills);
    addBills(tempBills);
    board.bank.houses -= 4;
    houses += 4;
    deed.houses += 4;
    board.bank.hotels += 1;
    hotels--;
    deed.hotels = 0;
   }
  }
 }
 public void goBankrupt(Token otherToken)
 {
  if (otherToken != this)
  {
   board.printOut(name + "-Going Bankrupt! to " + otherToken.name);
   int count = 0;
   for (int index = deedList.length - 1; index >= 0; index--)
    if (deedList[index] != null)
     count++;
   Deed[] temp = new Deed[count];
   for (int index = deedList.length - 1; index >= 0; index--)
    if (deedList[index] != null)
     temp[--count] = deedList[index];
   otherToken.trade(temp, this, 0);
   for (int index = cards; index > 0;) otherToken.tradeCard(this, 0);
   otherToken.addBills(bills);
   for (int index = bills.length - 1; index >= 0; index--)
    bills[index] = 0;
   totalCash = 0;
   board.remove(this);
  }
 }
 public void goBankruptToBank()
 {
  board.printOut(name + "-Going Bankrupt! to " + board.bank.name);
  for (int index = deedList.length - 1; index >= 0; index--)
   if (deedList[index] != null)
    {
     subtractDeed(deedList[index]);
     board.bank.auction(board.deedList[index], this);
    }
  for (int index = cards; index > 0; index--) board.bank.tradeCard(this, 0);
  board.bank.addBills(bills);
  for (int index = bills.length - 1; index >= 0; index--)
   bills[index] = 0;
  totalCash = 0;
  board.remove(this);
 }
 public void addBills(int[] newBills)
 {
  board.printOut(name + "--Adding Bills: " + (newBills[0] * 500 + newBills[1] * 100 + newBills[2] * 50 + newBills[3] * 20 + newBills[4] * 10 + newBills[5] * 5 + newBills[6] * 1));
  for (int index = bills.length - 1; index >= 0; index--) bills[index] += newBills[index];
  totalCash = bills[0] * 500 + bills[1] * 100 + bills[2] * 50 + bills[3] * 20 + bills[4] * 10 + bills[5] * 5 + bills[6] * 1;
 }
 public void subtractBills(int[] newBills, Token seller)
 {
  board.printOut(name + "--Subtracting: " + (newBills[0] * 500 + newBills[1] * 100 + newBills[2] * 50 + newBills[3] * 20 + newBills[4] * 10 + newBills[5] * 5 + newBills[6] * 1) + " to " + seller.name);
  int newBillsValue = newBills[0] * 500 + newBills[1] * 100 + newBills[2] * 50 + newBills[3] * 20 + newBills[4] * 10 + newBills[5] * 5 + newBills[6] * 1;
  if ((totalCash -=  newBills[0] * 500 + newBills[1] * 100 + newBills[2] * 50 + newBills[3] * 20 + newBills[4] * 10 + newBills[5] * 5 + newBills[6] * 1) < 0)
   goBankrupt(seller);
  bills = breakIntoBills(totalCash);
 }
 public void subtractBillsToBank(int[] newBills)
 {
  board.printOut(name + "--Subtracting: " + (newBills[0] * 500 + newBills[1] * 100 + newBills[2] * 50 + newBills[3] * 20 + newBills[4] * 10 + newBills[5] * 5 + newBills[6] * 1) + " to " + board.bank.name);
  int newBillsValue = newBills[0] * 500 + newBills[1] * 100 + newBills[2] * 50 + newBills[3] * 20 + newBills[4] * 10 + newBills[5] * 5 + newBills[6] * 1;
  if ((totalCash -=  newBills[0] * 500 + newBills[1] * 100 + newBills[2] * 50 + newBills[3] * 20 + newBills[4] * 10 + newBills[5] * 5 + newBills[6] * 1) < 0)
   goBankruptToBank();
  bills = breakIntoBills(totalCash);
 }
 public int[] breakIntoBills(int value)
 {
  int[] temp = {value/500, (value%500)/100, ((value%500)%100)/50, (((value%500)%100)%50)/20, ((((value%500)%100)%50)%20)/10, (((((value%500)%100)%50)%20)%10)/5, (((((value%500)%100)%50)%20)%10)%5};
  return temp;
 }
 public void addDeed(Deed deed)
 {
  board.printOut(name + "--Adding Deed: " + deed);
  deed.owner = this;
  deedList[deed.location] = deed;
 }
 public void subtractDeed(Deed deed)
 {
  board.printOut(name + "--Subtracting Deed: " + deed);
  deedList[deed.location] = null;
 }
 public boolean hasDeed(Deed deed)
 {
  board.printOut(name + "--Checking if has Deed: " + deed);
  return (deed.owner == this);
 }
 public boolean hasGroup(String group)
 {
  String str = "Checking if has group: ";
  Deed[] temp  = board.getGroup(group);
  int check = 0;
  for (int index = temp.length - 1; index >= 0; index--)
  {
   str += temp[index] + "\n";
   if (temp[index].owner == this) check++;
  }
  board.printOut(str);
  if (check == temp.length) return true;
  return false;
 }
 public int getGroupSize(String group)
 {
  Deed[] temp = board.getGroup(group);
  int check = 0;
  for (int index = temp.length - 1; index >= 0; index--)
   if (temp[index].owner == this) check++;
  return check;
 }
 public void putInJail(){ board.printOut(name + "-Putting in jail"); location = 10; jailed = 1;}
 public void takeOutOfJail(){ board.printOut(name + "-Taking out of Jail"); jailed = 0;}
 public String toString()
 {
  String temp = "Name: " + name + "\nLocation:" + location + "\nCash: " + totalCash + "\nDeeds: ";
  for (int index = deedList.length - 1; index >= 0; index--) 
   if (deedList[index] != null) temp += deedList[index] + ", ";
  return temp;
 }
 public void draw(Graphics graphic)
 {
  int length = board.deedList.length, width = board.BoardDimensions.width, height = board.BoardDimensions.height;
  if (location > 0 && location < (int) (.25*length))
   graphic.drawImage(image, (int) ((1-(location%10)/9.0)*36.0/51.0*.8*width+(7.5+7.5-1.15)/51.0*.8*width+Math.random()*(4.0/51.0*.8*width-2/51.0*width)), (int) (.9*height-7.5/51.0*.8*height+Math.random()*(7.5/51.0*.8*width-2/51.0*width)),(int) (2/51.0*width),(int) (2/51.0*height), null);
  else if (location > (int) (.25*length) && location < (int) (.5*length))
  {
   Graphics2D g = (Graphics2D) graphic.create();
   int x = (int) (.1*width+(7.5/51.0)*.8*width-Math.random()*(7.5/51.0*.8*width - 2/51.0*width)), y = (int)  ((1-(location%10)/9.0)*36.0/51.0*.8*height+(15-1.15)/51.0*.8*height+Math.random()*(4.0/51.0*.8*height - 2/51.0*height));
   g.rotate(Math.toRadians(90), x, y);
   g.drawImage(image, x, y, (int) (2/51.0*width), (int) (2/51.0*height), null);
   g.dispose();
  }
  else if (location > (int) (.5*length) && location < (int) (.75*length))
  {
   Graphics2D g = (Graphics2D) graphic.create();
   int x = (int) ((location%10 + 1)/9.0*36.0/51.0*.8*width+(7.5+2.35)/51.0*.8*width-Math.random()*(4.0/51.0*.8*width-2/51.0*width)), y = (int) (.1*height+(7.5)/51.0*.8*height+Math.random()*(-7.5/51.0*.8*height+2/51.0*height));
   g.rotate(Math.toRadians(180), x, y);
   g.drawImage(image, x, y, (int) (2/51.0*width), (int) (2/51.0*height), null);
   g.dispose();
  }
  else if (location > (int) (.75*length) && location < length)
  {
   Graphics2D g = (Graphics2D) graphic.create();
   int x = (int) (.9*width-7.5/51.0*.8*width+Math.random()*(7.5/51.0*.8*width-2/51.0*width)), y = (int) ((location%10 + 1)/9.0*36.0/51.0*.8*height+(7.5+2.35)/51.0*.8*height+Math.random()*(-4.0/51.0*.8*height+2/51.0*height));
   g.rotate(Math.toRadians(270), x, y);
   g.drawImage(image, x, y, (int) (2/51.0*width), (int) (2/51.0*height), null);
   g.dispose();
  }
  else if (location == 0)
   graphic.drawImage(image, (int) (.9*width-2.0/51*width-Math.random()*(7.5/51*.8*width-2.0/51*width)), (int) (.9*height-7.5/51.0*.8*height+Math.random()*(7.5/51.0*.8*width-2/51.0*width)),(int) (2/51.0*width),(int) (2/51.0*height), null);
  else if (location == (int) (.25*length))
  {
   Graphics2D g = (Graphics2D) graphic.create();
   int x = (int) (.1*width+2.0/51*width+Math.random()*(7.5/51*.8*width-2.0/51*width)), y = (int)  (.9*height-7.5/51*.8*height+Math.random()*(7.5/51*.8*height-2.0/51*height));
   g.rotate(Math.toRadians(90), x, y);
   g.drawImage(image, x, y, (int) (2/51.0*width), (int) (2/51.0*height), null);
   g.dispose();
  }
  else if (location == (int) (.5*length))
  {
   Graphics2D g = (Graphics2D) graphic.create();
   int x = (int) (.1*width+2.0/51*width+Math.random()*(7.5/51*.8*width-2.0/51*width)), y = (int) (.1*height+(7.5)/51.0*.8*height+Math.random()*(-7.5/51.0*.8*height+2/51.0*height));
   g.rotate(Math.toRadians(180), x, y);
   g.drawImage(image, x, y, (int) (2/51.0*width), (int) (2/51.0*height), null);
   g.dispose();
  }
  else if (location == (int) (.75*length))
  {
   Graphics2D g = (Graphics2D) graphic.create();
   int x = (int) (.9*width-7.5/51.0*.8*width+Math.random()*(7.5/51*.8*width - 2.0/51*width)), y = (int) (.1*height+2.0/51*height+Math.random()*(7.5/51*.8*height-2.0/51*height));
   g.rotate(Math.toRadians(270), x, y);
   g.drawImage(image, x, y, (int) (2/51.0*width), (int) (2/51.0*height), null);
   g.dispose();
  }
 }
 public void drawOffScreen(Graphics graphic, int heightFromTop)
 {//Knowing that board.height is also board.width, stating there, heightFromTop will be a proptortion of the distance from the top of the scree, leaving enough space for all 8 tokens
  int xStart = (int) (.9*board.BoardDimensions.width), yStart = (int) (heightFromTop/8.0*board.BoardDimensions.height),
      width = (int) (board.WindowDimensions.width - .9*board.BoardDimensions.width - .3*board.WindowDimensions.width), height = (int)(1/8.0*board.BoardDimensions.height),
      fontSize = (int) (1.0/51*.8*board.BoardDimensions.width);
  graphic.setFont(new Font("Britannic Bold", 0, fontSize));
  graphic.setColor(Color.black);
  graphic.drawString(name+", Total Cash: "+totalCash, xStart, (int) (yStart + fontSize));
  for (int index = bills.length - 1; index >= 0; index--)
  {
   switch(index)
   {
    case 6: graphic.setColor(new Color(251, 248, 233)); break;
    case 5: graphic.setColor(new Color(240, 189, 196)); break;
    case 4: graphic.setColor(new Color(252, 244, 97)); break;
    case 3: graphic.setColor(new Color(183, 218, 185)); break;
    case 2: graphic.setColor(new Color(144, 197, 201)); break;
    case 1: graphic.setColor(new Color(244, 205, 128)); break;
    case 0: graphic.setColor(new Color(237, 172, 46)); break;
   }
   graphic.fillRect((int)(index/7.0*width + xStart), (int) (yStart+fontSize), (int) (1/7.0*width), fontSize);
   graphic.setColor(Color.black);
   graphic.drawRect((int)(index/7.0*width + xStart), (int) (yStart+fontSize), (int) (1/7.0*width), fontSize);
   switch (index)
   {
    case 6: graphic.drawString(bills[index]+" X "+1, (int)(index/7.0*width + xStart), (int) (yStart+fontSize+fontSize)); break;
    case 5: graphic.drawString(bills[index]+" X "+5, (int)(index/7.0*width + xStart), (int) (yStart+fontSize+fontSize)); break;
    case 4: graphic.drawString(bills[index]+" X "+10, (int)(index/7.0*width + xStart), (int) (yStart+fontSize+fontSize)); break;
    case 3: graphic.drawString(bills[index]+" X "+25, (int)(index/7.0*width + xStart), (int) (yStart+fontSize+fontSize)); break;
    case 2: graphic.drawString(bills[index]+" X "+50, (int)(index/7.0*width + xStart), (int) (yStart+fontSize+fontSize)); break;
    case 1: graphic.drawString(bills[index]+" X "+100, (int)(index/7.0*width + xStart), (int) (yStart+fontSize+fontSize)); break;
    case 0: graphic.drawString(bills[index]+" X "+500, (int)(index/7.0*width + xStart), (int) (yStart+fontSize+fontSize)); break;
   }
  }
  int count = 0;
  for (int index = deedList.length - 1; index >= 0; index--)
   if (deedList[index] != null)
    count++;
  Deed[] temp = new Deed[count];
  for (int index = deedList.length - 1; index >= 0; index--)
   if (deedList[index] != null)
    temp[--count] = deedList[index];
  count = temp.length;
  for (int index = count - 1; index >= 0; index--)
   temp[index].drawOffScreen(graphic, (int)(xStart + ((1.0*index)/count)*width), (int)(yStart+fontSize+fontSize), (int)((1.0/count)*width), (int)(height-fontSize-fontSize));
 }
}