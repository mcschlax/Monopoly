import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
public class Board
{
 Deed[] deedList;
 ChanceCard[] chanceCards;
 CommunityChestCard[] communityChestCards;
 Bank bank;
 Token[] tokens;
 int gameType,  communityTop, chanceTop, turn, MaxTurn;
 Dimension BoardDimensions, WindowDimensions;
 MonopolyGUI GUI;
 String theme;
 String[] drawStack;
 public Board(String[] names, MonopolyGUI gui, int gameTyp, String thm)
 {
  deedList = new Deed[40];
  chanceCards = new ChanceCard[16];
  communityChestCards = new CommunityChestCard[16];
  if (thm != null) theme = thm;
  else theme = "Default";
  try
  {
   Scanner scanFile;
   for (int index = deedList.length - 1; index >= 0; index--)
   {
    String[] temp = new String[13];
    int[] vals = new int[9];
    scanFile = new Scanner(new BufferedReader(new FileReader(theme+"\\Deed" + index + ".txt")));
    for (int i = 0;  i < temp.length; i++)
     temp[i] = scanFile.nextLine().replaceAll("(\r\n)", "");
    scanFile.close();
    for (int j = 4; j < temp.length; j++)
     vals[j - 4] = Integer.parseInt(temp[j]);
    deedList[index] = new Deed(this, temp[0], temp[1], temp[2], Integer.parseInt(temp[3]), vals);
   }
   for (int index = 16 - 1; index >= 0; index--)
   {
    if (index == 15)
    {
     scanFile = new Scanner(new BufferedReader(new FileReader(theme+"\\ChanceCard" + 4 + ".txt")));
     chanceCards[index] = new ChanceCard(this, 4, scanFile.nextLine().replaceAll("(\r\n)", ""));
     scanFile.close();
    } 
    else
    {
     scanFile = new Scanner(new BufferedReader(new FileReader(theme+"\\ChanceCard" + index + ".txt")));
     chanceCards[index] = new ChanceCard(this, index, scanFile.nextLine().replaceAll("(\r\n)", ""));
     scanFile.close();
    }
    scanFile = new Scanner(new BufferedReader(new FileReader(theme+"\\CommunityChestCard" + index + ".txt")));
    communityChestCards[index] = new CommunityChestCard(this, index, scanFile.nextLine().replaceAll("(\r\n)", ""));
    scanFile.close();
   }
  }
  catch (IOException ex)
  {
   System.out.println("Notify Mark, IO Error after theme was already checked: " + ex);
  }
  gameType = gameTyp;
  tokens = new Token[8];
  if (names != null)
   for (int index = names.length - 1; index >= 0; index--){if (names[index].equalsIgnoreCase("Mark")) names[index] = "THE CREATOR"; 
    tokens[index] = new Token(names[index], this);}
  if (gameType == 1 || gameType == 2)
   MaxTurn = 2;
  bank = new Bank(this);
  shuffleCards();
  communityTop = 15;
  chanceTop = 15;
  turn = 0;
  drawStack = new String[0];
  if (gui != null)
  {
   BoardDimensions = new Dimension(gui.MaxDimensions.height, gui.MaxDimensions.height);
   WindowDimensions = gui.MaxDimensions;
   GUI = gui;
   for (int index = numberOfTokens() - 1; index >= 0; index--)
    {
     int temp = promptIntInput("What token do you want to represent " + tokens[index].name);
     if (temp > 0 && temp <= 8)
      tokens[index].image = Toolkit.getDefaultToolkit().getImage(theme+"\\token"+temp+".png");
     else
      tokens[index].image = Toolkit.getDefaultToolkit().getImage(theme+"\\token"+(int)(Math.random()*8 + 1)+".png");
    }
  }
 }
 public void nextTurn()
 {
  turn++;
  for (int index = numberOfTokens() - 1; index >= 0; index--)
  {
   while (promptBooleanInput(tokens[index].name + ", Any misc. activities?"))
   {
    int choice = promptIntInput("Trade Deed(1)/Deeds(2)/Cards(3), Buy Houses(4)/Hotels(5), or Sell Houses(6)/Hotels(7), Mortgage(7)/Unmortgage(8)\nEnter Number");
    if (choice == 1 || choice == 2)
    {
     int trader, count = 0, price = 0;
     Deed[] choices, choices2, temp = new Deed[40]; 
     do
     {
      for (int i = numberOfTokens() - 1; i >= 0; i--) if (i != index) printOut("(" + i + ")"+tokens[i]);
      trader = promptIntInput("Trade with which token?");
     } while (trader != index && (trader < 0 || trader > numberOfTokens() - 1));
     do
     {
      printOut(tokens[trader].toString());
      choice = promptIntInput("For which deeds from them? (enter a deed they don't have to stop)");
      if (choice >= 0 && choice <= deedList.length - 1 && tokens[trader].hasDeed(deedList[choice]))
      {
       temp[choice] = deedList[choice];
       count++;
      }
     } while (promptBooleanInput("Continue?"));
     choices = new Deed[count];
     for (int i = temp.length - 1; i >= 0; i--)
      if (temp[i] != null)
       choices[--count] = temp[i];
     if (count != 0){System.out.println("Logic Error with Count's decrementation");
      count = 0;}
     temp = new Deed[40];
     do
     {
      printOut(tokens[index].toString());
      choice = promptIntInput("For which deeds from you? (enter a deed you don't have to stop)");
      if (choice >= 0 && choice <= deedList.length - 1 && tokens[index].hasDeed(deedList[choice]))
      {
       temp[choice] = deedList[choice];
       count++;
      }
     } while (promptBooleanInput("Continue?"));
     choices2 = new Deed[count];
     for (int i = temp.length - 1; i >= 0; i--)
      if (temp[i] != null)
       choices2[--count] = temp[i];
     if (promptBooleanInput("Add a price tag you'll pay?(true/false 1/0)")) price = promptIntInput("How Much?");
     if (choices2.length == 0) tokens[index].trade(choices, tokens[trader], price);
     else if (choices2.length != 0 && price == 0) tokens[index].trade(choices, tokens[trader], choices2);
     else if (choices2.length != 0 && price != 0) tokens[index].trade(choices, tokens[trader], choices2, price);
    }
    else if (choice == 3)
    {
     int trader, price = 0;
     do
     {
      for (int i = numberOfTokens() - 1; i >= 0; i--) if (i != index) printOut("(" + i + ")"+tokens[i]);
      trader = promptIntInput("Trade with which token?");
     } while (trader != index && (trader < 0 || trader > numberOfTokens() - 1));
     if (promptBooleanInput("Add a price tag you'll pay?(true/false 1/0)")) price = promptIntInput("How Much?");
     tokens[index].tradeCard(tokens[trader], price);
    }
    else if (choice == 4)
    {
     do
     {
      printOut(tokens[index].toString());
      choice = promptIntInput("Which Deed/Group?"); 
     } while (choice < 0 || choice >= tokens[index].deedList.length && tokens[index].hasDeed(deedList[choice]));
     if (tokens[index].hasGroup(deedList[choice].group))
     {
      int buy;
      do buy = promptIntInput("How many for that group?"); while (buy > bank.houses);
      Deed[] temp = getGroup(tokens[index].deedList[choice].group);
      while (buy > 0)
       for (int i = temp.length - 1; i >= 0 && buy > 0; i--)
       {
        tokens[index].buyHouses(temp[i]);
        buy--;
       }
     }
    }
    else if (choice == 5)
    {
     do choice = promptIntInput("Which Deed?"); while ((choice < 0 || choice >= tokens[index].deedList.length) && tokens[index].hasDeed(deedList[choice]));
     tokens[index].buyHotels(tokens[index].deedList[choice]);
    }
    else if (choice == 6)
    {
     do choice = promptIntInput("Which Deed/Group?"); while ((choice < 0 || choice >= tokens[index].deedList.length) && tokens[index].hasDeed(deedList[choice]));
     if (tokens[index].hasGroup(tokens[index].deedList[choice].group))
     {
      int sell;
      do sell = promptIntInput("How many?"); while (sell > bank.houses);
      Deed[] temp = getGroup(tokens[index].deedList[choice].group);
      while (sell > 0)
       for (int i = temp.length - 1; i >= 0 && sell > 0; i--)
       {
        tokens[index].sellHouses(temp[i]);
        sell--;
       }
     }
    }
    else if (choice == 7)
    {
     do choice = promptIntInput("Which Deed?"); while (choice < 0 || choice >= tokens[index].deedList.length && tokens[index].hasDeed(deedList[choice]));
     tokens[index].sellHotels(tokens[index].deedList[choice]);
    }
    else if (choice == 8)
    {
     do choice = promptIntInput("Which Deed?"); while (choice < 0 || choice >= tokens[index].deedList.length && tokens[index].hasDeed(deedList[choice]));
     tokens[index].mortgageDeed(tokens[index].deedList[choice]);
    }
    else if (choice == 9)
    {
     do choice = promptIntInput("Which Deed?"); while (choice < 0 || choice >= tokens[index].deedList.length && tokens[index].hasDeed(deedList[choice]));
     tokens[index].unmortgageDeed(tokens[index].deedList[choice]);
    }
   }
   tokens[index].move();
  }
 }
 public int rollDie()
 {
  int roll = (int)(Math.random() * 6) + 1;
  if (BoardDimensions != null)
   addTodrawStack("Dice"+":"+roll);
  return roll;
 }public ChanceCard drawChanceCard()
 {
  if (chanceTop < 0)
   shuffleChanceCards();
  chanceTop--;
  if (BoardDimensions != null)
   addTodrawStack("ChanceCard"+":"+chanceCards[chanceTop+1].description);
  return chanceCards[chanceTop + 1];
 }
 public CommunityChestCard drawCommunityChestCard()
 {
  if (chanceTop < 0)
   shuffleCommunityChestCards();
  communityTop--;
  if (BoardDimensions != null)
   addTodrawStack("CommunityChestCard"+":"+communityChestCards[communityTop+1].description);
  return communityChestCards[communityTop + 1];
 }
 public void shuffleCards()
 {
  shuffleChanceCards();
  shuffleCommunityChestCards();
 }
 public void shuffleChanceCards()
 {
  int index;
  for (int i = chanceCards.length - 1; i > 0; i--)
  {
   index = (int) (Math.random() * i);
   ChanceCard temp = chanceCards[index];
   chanceCards[index] = chanceCards[i];
   chanceCards[i] = temp;
  }
  chanceTop = 15;
 }
 public void shuffleCommunityChestCards()
 {
  int index;
  for (int i = communityChestCards.length - 1; i > 0; i--)
  {  
   index = (int) (Math.random() * i);
   CommunityChestCard temp = communityChestCards[index];
   communityChestCards[index] = communityChestCards[i];
   communityChestCards[i] = temp;
  }
  communityTop = 15;
 }
 public void addToken(String name)
 {
  for (int index = 0; index < tokens.length; index++)
   if (tokens[index] == null)
    tokens[index] = new Token(name, this);
 }
 public void remove(Token token)
 {
  int length = numberOfTokens(), count = length;
  for (int index = length - 1; index >= 0; index--)
   if (tokens[index] != token)
    count--;
  Token[] temp = new Token[count];
  for (int index = temp.length - 1; index >= 0; index--)
   if (tokens[index] != null)
    temp[--count] = tokens[index];
  if (count != 0) System.out.println("Contact Mark, Count Decramation Error in remove(Token token)");
  tokens = temp;
 }
 public int numberOfTokens()
 {
  int temp = 0;
  for (int index = 0; index < tokens.length; index++)
   if (tokens[index] != null) temp++;
  return temp;
 }
 public Deed[] getGroup(String groupName)
 {
  int count = 0;
  Deed[] temp = new Deed[deedList.length], temp2;
  for (int index = temp.length - 1; index >= 0; index--)
   if (deedList[index].group.equals(groupName))
   {
    temp[index] = deedList[index];
    count++;
   }
  temp2 = new Deed[count];
  for (int index = temp.length - 1; index >= 0; index--)
   if (temp[index] != null)
    temp2[--count] = temp[index];
  return temp2;
 }
 public void prompt(String prompt)
 {
  if (GUI != null) GUI.textPrompt.setText(prompt);
  else System.out.println(prompt);
 }
 public String promptInput(String prompt)
 {
  String temp;
  prompt(prompt);
  if (GUI != null)
  {
   temp = GUI.getInput();
  }
  else
  {
   Scanner scan = new Scanner(System.in);
   temp = scan.nextLine();
  }
  return temp;
 }
 public boolean promptBooleanInput(String prompt)
 {
  String temp = promptInput(prompt);
  try
  { 
   if (temp != null)
    return Integer.parseInt(temp) == 1; 
  }
  catch(NumberFormatException e)
  {
   if (temp.equalsIgnoreCase("true") || temp.equalsIgnoreCase("yes")) return true;
  }
  return false;
 }
 public int promptIntInput(String prompt)
 {
  String temp = promptInput(prompt);
  try
  {
   if (temp != null)
    return Integer.parseInt(temp); 
  }
  catch(NumberFormatException e)
  {
   return 0;
  }
  return 0;
 }
 public boolean gameOver()
 {
  if (gameType == 0)
  {
   if (numberOfTokens() == 1) 
   {
    prompt("Winner is: " + tokens[0].name);
    return true;
   }
  }
  else if (gameType == 1 || gameType == 2)
  {
   if (turn == MaxTurn)
   {
    int winner = 0;
    for (int index = numberOfTokens() - 1; index >= 0; index--)
     if (gameType == 1 && tokens[index].totalCash < tokens[winner].totalCash) winner = index;
     else if(gameType == 2 && tokens[index].totalCash > tokens[winner].totalCash) winner = index;
    prompt("Winner is: " + tokens[winner].name);
   }
  }
  return false;
 }
 public void printOut(String string)
 {
  if (GUI != null) GUI.textOutput.append(string+"\n");
  else System.out.println(string);
 }
 public void addTodrawStack(String string)
 {
  String[] temp = drawStack;
  drawStack = new String[temp.length+1];
  drawStack[drawStack.length-1] = string;
  for (int index = drawStack.length-1-1; index >= 0; index--)
   drawStack[index] = temp[index];
 }
 public void draw(Graphics graphic)
 {
  graphic.setColor(new Color(189, 212, 186));
  graphic.fillRect((int) (.1 * BoardDimensions.width), (int) (.1 * BoardDimensions.height), (int) (.8 * BoardDimensions.width), (int) (.8 * BoardDimensions.height));
  for (int index = 0; index < deedList.length; index++)
   deedList[index].draw(graphic);
  graphic.drawImage(Toolkit.getDefaultToolkit().getImage(theme+"\\backDrop.png"), (int) (7.5/51*.8*BoardDimensions.width+.1*BoardDimensions.width), (int) (7.5/51*.8*BoardDimensions.height+.1*BoardDimensions.height), (int) ((51-15.0)/51*.8*BoardDimensions.width), (int) ((51-15.0)/51*.8*BoardDimensions.height), null);
  graphic.setFont(new Font("Britannic Bold", 0, (int) (1.0/51*.8*BoardDimensions.width)));
  graphic.setColor(Color.black);
  Graphics2D g = (Graphics2D) graphic.create(), g2 = (Graphics2D) graphic.create();
  g.rotate(Math.toRadians(135), (int) ((4 + 4 + 4 + 7.5 + 1.15)/51*BoardDimensions.width), (int) ((7.5 + 4 + 4)/51*BoardDimensions.height));
  g.drawRect((int) ((4 + 4 + 4 + 7.5 + 1.15)/51*BoardDimensions.width), (int) ((7.5 + 4 + 4)/51*BoardDimensions.height), (int) (7.5/51 * BoardDimensions.width), (int) (4.5/51 * BoardDimensions.height));
  g.drawString(deedList[7].name, (int) ((4 + 4 + 4 + 7.5 + 1.15)/51*BoardDimensions.width), (int) ((7.5 + 4 + 4)/51*BoardDimensions.height));
  g.setColor(new Color(254, 231, 55));
  g.fillRect((int) ((4 + 4 + 4 + 7.5 + 1.15)/51*BoardDimensions.width), (int) ((7.5 + 4 + 4)/51*BoardDimensions.height), (int) (7.5/51 * BoardDimensions.width), (int) (4.5/51 * BoardDimensions.height));
  g.dispose();
  g2.rotate(Math.toRadians(-45), (int) ((51 - 17.5 - 3)/51 * BoardDimensions.width), (int) ((51 - 12.0 - 3)/51 * BoardDimensions.height));
  g2.drawRect((int) ((51 - 17.5 - 3)/51 * BoardDimensions.width), (int) ((51 - 12.0 - 3)/51 * BoardDimensions.height), (int) (7.5/51 * BoardDimensions.width), (int) (4.5/51 * BoardDimensions.height));
  g2.drawString(deedList[2].name, (int) ((51 - 17.5 - 3)/51 * BoardDimensions.width), (int) ((51 - 12.0 - 3)/51 * BoardDimensions.height));
  g2.setColor(new Color(250, 89, 0));
  g2.fillRect((int) ((51 - 17.5 - 3)/51 * BoardDimensions.width), (int) ((51 - 12.0 - 3)/51 * BoardDimensions.height), (int) (7.5/51 * BoardDimensions.width), (int) (4.5/51 * BoardDimensions.height));
  g2.dispose();
  int max = numberOfTokens() - 1;
  for (int index = 0; index <= max; index++)
  {
   tokens[max - index].draw(graphic);
   tokens[max - index].drawOffScreen(graphic, index);
  }
  for (int index = drawStack.length-1; index >= 0; index--)
  {
   String[] temp = drawStack[index].split(":");
   if (temp[0].equals("Dice"))
   {
    int x = (int)(.1*BoardDimensions.width+7.5/51*.8*BoardDimensions.width+Math.random()*(36.0/51)*.8*BoardDimensions.width), y = (int)(.1*BoardDimensions.height+7.5/51*.8*BoardDimensions.height+Math.random()*(36.0/51)*.8*BoardDimensions.height);
    Graphics2D g3 = (Graphics2D) graphic.create();
    g3.rotate(Math.toRadians(Math.random()*360), x, y);
    g3.drawImage(Toolkit.getDefaultToolkit().getImage(theme+"\\die"+temp[1]+".png"), x, y, (int) (1.5/51*BoardDimensions.width), (int) (1.5/51*BoardDimensions.height), null);
    g3.dispose();
   }
   else if (temp[0].equals("ChanceCard"))
   {
    String[] temp2 = temp[1].split(";");
    for (int i = temp2.length - 1; i >= 0; i--)
    {
     Graphics2D g3 = (Graphics2D) graphic.create();
     g3.setFont(new Font("Britannic Bold", 0, (int) (.7/51*.8*BoardDimensions.width)));
     g3.rotate(Math.toRadians(135), (int) ((4+4+4+7.5+1.15-i/2.0-.5)/51*BoardDimensions.width), (int) ((7.5+4+4-i/2.0-.5)/51*BoardDimensions.height));
     g3.drawString(temp2[i], (int) ((4+4+4+7.5+1.15-i/2.0-.5)/51*BoardDimensions.width), (int) ((7.5+4+4-i/2.0-.5)/51*BoardDimensions.height));
     g3.dispose();
    }
   }
   else if (temp[0].equals("CommunityChestCard"))
   {
    String[] temp2 = temp[1].split(";");
    for (int i = temp2.length - 1; i >= 0; i--)
    {
     Graphics2D g3 = (Graphics2D) graphic.create();
     g3.setFont(new Font("Britannic Bold", 0, (int) (.7/51*.8*BoardDimensions.width)));
     g3.rotate(Math.toRadians(-45), (int) ((51-17.5-3+i/2.0+.5)/51 * BoardDimensions.width), (int) ((51-12.0-3+i/2.0+.5)/51 * BoardDimensions.height));
     g3.drawString(temp2[i], (int) ((51-17.5-3+i/2.0+.5)/51 * BoardDimensions.width), (int) ((51-12.0-3+i/2.0+.5)/51 * BoardDimensions.height));
     g3.dispose();
    }   
   }
  }
  drawStack = new String[0];
 }
}