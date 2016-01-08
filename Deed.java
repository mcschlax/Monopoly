import java.awt.Toolkit;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Font;
import java.awt.Color;
public class Deed
{
 Board board;
 Token owner;
 int[] values;
 int location, houses, hotels, currentValue;
 String name, type, group;
 boolean inMortgage = false;
 public Deed(Board bored, String nam, String typ, String grp, int loc, int[] vals)
 {
  board = bored;
  name = nam;
  type = typ;
  group = grp;
  owner = null;
  location = loc;
  values = vals;//[0] is "purchase" price, [1] is mortgage, [2] is value of a house, [3-7] is value with 0-4 houses, [8] is value with hotel
  houses = 0;
  hotels = 0;
  currentValue = vals[0];
 }
 public void activate(Token token)
 {
  board.printOut(token.name+" is activating: "+name);
  if (type.equals("Property") || type.equals("Railroad") || type.equals("Utilities"))
  {
   if (owner == null)
   {
    if (board.promptBooleanInput(name + ", Buy or Auction")) token.purchase(this);
    else board.bank.auction(this);
   }
   else token.pay(owner, this);
  }
  else if (type.equals("Luxury Tax")) token.payBank(this);
  else if (type.equals("Income Tax"))
  {
   if (board.promptBooleanInput(name + ", pay 200 or 10%")) token.payBank(200);
   else token.payBank((int) (token.totalCash*.1));
  }
  else if (type.equals("Community Chest")) 
  {
   CommunityChestCard temp = board.drawCommunityChestCard(); 
   board.printOut(name+", "+temp.description);
   temp.activate(token);
  }
  else if (type.equals("Chance")) 
  {
   ChanceCard temp = board.drawChanceCard();
   board.printOut(name+", "+temp.description);
   temp.activate(token);
  }
  else if (type.equals("Go To Jail")) token.putInJail();
  else if (type.equals("GO")) board.bank.pay(token, 200);  
 }
 public void updateValue()
 {
  if (owner == null) currentValue = values[0];
  else if (inMortgage) currentValue = values[1];
  else if (type.equals("Property"))
  {
   if (houses == 0 && hotels == 0)
   {
    if (owner.hasGroup(group)) currentValue = values[3]*2;
    else currentValue = values[3];
   }
   else if (houses == 1) currentValue = values[4];
   else if (houses == 2) currentValue = values[5];
   else if (houses == 3) currentValue = values[6];
   else if (houses == 4) currentValue = values[7];
   else if (hotels == 1) currentValue = values[8];
  }
  else if (type.equals("Railroad"))
  {
   int temp = owner.getGroupSize(group);
   if (temp == 1) currentValue = values[3];
   else if (temp == 2) currentValue = values[4];
   else if (temp == 3) currentValue = values[5];
   else if (temp == 4) currentValue = values[6];
  }
  else if (type.equals("Luxury Tax")) currentValue = values[0];
  else if (type.equals("Utilities"))
  {
   int diceRoll = board.rollDie()+board.rollDie();
   if (owner.hasGroup("Utilities"))
    currentValue = diceRoll*10;
   else currentValue = diceRoll*4;
  }
  
 }
 public int getCurrentValue()
 {
  updateValue();
  return currentValue;
 }
 public int getHouseCost(){ return values[2];}
 public void draw(Graphics graphic)
 {
  int length = board.deedList.length, width = board.BoardDimensions.width, height = board.BoardDimensions.height;
  graphic.setFont(new Font("Britannic Bold", 0, (int) (.55/51*.8*width)));
  if (location > 0 && location < .25*length)
  {
   graphic.setColor(new Color(189, 212, 186));
   graphic.fillRect((int) ((1-(location%10)/9.0)*36.0/51*.8*width+(7.5+7.5-1.15)/51*.8*width), (int) (.9*height-7.5/51*.8*height), (int) (4.0/51*.8*width), (int) (7.5/51*.8*height));
   if (type.equals("Property"))
   {
    if (location < .125*length) graphic.setColor(new Color(113, 61, 47));
    else graphic.setColor(new Color(142, 202, 228));
    graphic.fillRect((int) ((1-(location%10)/9.0)*36.0/51*.8*width+(7.5+ 7.5-1.15)/51*.8*width), (int) (.9*height-7.5/51*.8*height), (int) (4.0/51*.8*width), (int) (1.0/51*.8*height));
    graphic.setColor(Color.black);
    graphic.drawRect((int) ((1-(location%10)/9.0)*36.0/51*.8*width+(7.5+ 7.5-1.15)/51*.8*width), (int) (.9*height-7.5/51*.8*height), (int) (4.0/51*.8*width), (int) (1.0/51*.8*height)); 
    if (houses > 0)
    {
     graphic.setColor(new Color(38, 96, 56));
     for (int temp = houses; temp > 0; temp--)
      graphic.fillRect((int) ((1-(location%10)/9.0)*36.0/51*.8*width+(7.5+ 7.5-1.15+temp-1+.125)/51*.8*width), (int) (.9*height-(7.5-.125)/51*.8*height), (int) (.75/51*.8*width), (int) (.75/51*.8*height));
    }
    if (hotels > 0)
    {
     graphic.setColor(new Color(235, 57, 59));
     graphic.fillRect((int) ((1-(location%10)/9.0)*36.0/51*.8*width+(7.5+ 7.5-1.15+4.0/3)/51*.8*width), (int) (.9*height-(7.5-.125)/51*.8*height), (int) (1.5/51*.8*width), (int) (.75/51*.8*height));
    }
    graphic.setColor(Color.black);
    String[] temp = name.split(" ");
    for (int index = 0; index < temp.length; index++)    
     graphic.drawString(temp[index], (int) ((1-(location%10)/9.0)*36.0/51*.8*width+(7.5+ 7.5-1.15 + .5)/51*.8*width), (int) (.9*height-(7.5-(index/2.0 + 1.5))/51*.8*height));
    graphic.drawString("Price $"+values[0], (int) ((1-(location%10)/9.0)*36.0/51*.8*width+(7.5+ 7.5-1.15 + .5)/51*.8*width), (int) (.9*height-(7.5-7)/51*.8*height));
   }
   else 
   {
    graphic.setColor(Color.black);
    if (type.equals("Railroad"))
    {
     graphic.drawImage(Toolkit.getDefaultToolkit().getImage(board.theme+"\\railroad"+0+".png"), (int) ((1-(location%10)/9.0)*36.0/51*.8*width+(7.5+7.5-1.15)/51*.8*width), (int) (.9*height-7.5/51*.8*height), (int) (4.0/51*.8*width), (int) (7.5/51*.8*height), null);
     graphic.drawString("Price $"+values[0], (int) ((1-(location%10)/9.0)*36.0/51*.8*width+(7.5+ 7.5-1.15 + .5)/51*.8*width), (int) (.9*height-(7.5-7)/51*.8*height));
    }
    else if (type.equals("Chance"))
     graphic.drawImage(Toolkit.getDefaultToolkit().getImage(board.theme+"\\chance"+0+".png"), (int) ((1-(location%10)/9.0)*36.0/51*.8*width+(7.5+7.5-1.15)/51*.8*width), (int) (.9*height-7.5/51*.8*height), (int) (4.0/51*.8*width), (int) (7.5/51*.8*height), null);
    else if (type.equals("Community Chest"))
     graphic.drawImage(Toolkit.getDefaultToolkit().getImage(board.theme+"\\communityChest"+0+".png"), (int) ((1-(location%10)/9.0)*36.0/51*.8*width+(7.5+7.5-1.15)/51l*.8*width), (int) (.9*height-7.5/51*.8*height), (int) (4.0/51*.8*width), (int) (7.5/51*.8*height), null);
    else if (type.equals("Utilities"))
    {
     graphic.drawImage(Toolkit.getDefaultToolkit().getImage(board.theme+"\\utilities"+0+".png"), (int) ((1-(location%10)/9.0)*36.0/51*.8*width+(7.5+7.5-1.15)/51l*.8*width), (int) (.9*height-7.5/51*.8*height), (int) (4.0/51*.8*width), (int) (7.5/51*.8*height), null);
     graphic.drawString("Price $"+values[0], (int) ((1-(location%10)/9.0)*36.0/51*.8*width+(7.5+ 7.5-1.15 + .5)/51*.8*width), (int) (.9*height-(7.5-7)/51*.8*height));
    }
    else if (type.equals("Luxury Tax"))
    {
     graphic.drawImage(Toolkit.getDefaultToolkit().getImage(board.theme+"\\luxuryTax"+0+".png"), (int) ((1-(location%10)/9.0)*36.0/51*.8*width+(7.5+7.5-1.15)/51l*.8*width), (int) (.9*height-7.5/51*.8*height), (int) (4.0/51*.8*width), (int) (7.5/51*.8*height), null);
     graphic.drawString("Price $"+values[0], (int) ((1-(location%10)/9.0)*36.0/51*.8*width+(7.5+ 7.5-1.15 + .5)/51*.8*width), (int) (.9*height-(7.5-7)/51*.8*height));
    }
    else if(type.equals("Income Tax"))
    {
     String[] temp = {"Pay 10%", "Or", "$200"};
     for (int index = 0; index < temp.length; index++)    
      graphic.drawString(temp[index], (int) ((1-(location%10)/9.0)*36.0/51*.8*width+(7.5+ 7.5-1.15 + .5)/51*.8*width), (int) (.9*height-(7.5-(index/2.0 + 6))/51*.8*height));
    }
    String[] temp = name.split(" ");
    for (int index = 0; index < temp.length; index++)    
     graphic.drawString(temp[index], (int) ((1-(location%10)/9.0)*36.0/51*.8*width+(7.5+ 7.5-1.15 + .5)/51*.8*width), (int) (.9*height-(7.5-(index/2.0 + 1.0))/51*.8*height));
   }
   graphic.drawRect((int) ((1-(location%10)/9.0)*36.0/51*.8*width+(7.5+7.5-1.15)/51*.8*width), (int) (.9*height-7.5/51*.8*height), (int) (4.0/51*.8*width), (int) (7.5/51*.8*height));
  }
  else if (location > .25*length && location < .5*length)
  {
   graphic.setColor(new Color(189, 212, 186));
   graphic.fillRect((int) (.1*width), (int) ((1-(location%10)/9.0)*36.0/51*.8*height+(7.5+7.5-1.15)/51*.8*height), (int) (7.5/51*.8*width), (int) (4.0/51*.8*height));
   if (type.equals("Property"))
   {
    if (location < .375*length) graphic.setColor(new Color(197, 59, 136));
    else graphic.setColor(new Color(225, 113, 29));
    graphic.fillRect((int) (.1*width+(7.5-1)/51*.8*width), (int) ((1-(location%10)/9.0)*36.0/51*.8*height+(7.5+7.5-1.15)/51*.8*height), (int) (1.0/51*.8*width), (int) (4.0/51*.8*height));
    graphic.setColor(Color.black);
    graphic.drawRect((int) (.1*width+(7.5-1)/51*.8*width), (int) ((1-(location%10)/9.0)*36.0/51*.8*height+(7.5+7.5-1.15)/51*.8*height), (int) (1.0/51*.8*width), (int) (4.0/51*.8*height));
    if (houses > 0)
    {
     graphic.setColor(new Color(38, 96, 56));
     for (int temp = houses; temp > 0; temp--)
      graphic.fillRect((int) (.1*width+(7.5-1+.125)/51*.8*width), (int) ((1-(location%10)/9.0)*36.0/51*.8*height+(7.5+7.5-1.15+temp-1+.125)/51*.8*height), (int) (.75/51*.8*width), (int) (.75/51*.8*height));
    }
    if (hotels > 0)
    {
     graphic.setColor(new Color(235, 57, 59));
     graphic.fillRect((int) (.1*width+(7.5-1+.125)/51*.8*width), (int) ((1-(location%10)/9.0)*36.0/51*.8*height+(7.5+7.5-1.15+4.0/3)/51*.8*height), (int) (.75/51*.8*width), (int) (1.5/51*.8*height));
    }
    graphic.setColor(Color.black);
    Graphics2D g = (Graphics2D) graphic.create();
    g.rotate(Math.toRadians(90), (int) (.1*width+(7.5-1)/51*.8*width), (int) ((1-(location%10)/9.0)*36.0/51*.8*height+(7.5+7.5-1.15)/51*.8*height));
    String[] temp = name.split(" ");
    for (int index = 0; index < temp.length; index++)
     g.drawString(temp[index], (int) (.1*width+(7.5-1+.5)/51*.8*width), (int) ((1-(location%10)/9.0)*36.0/51*.8*height+(7.5+7.5-1.15+index/2.0 + .5)/51*.8*height));
    g.drawString("Price $"+values[0], (int) (.1*width+(7.5-1+.5)/51*.8*width), (int) ((1-(location%10)/9.0)*36.0/51*.8*height+(7.5+7.5-1.15+6)/51*.8*height));
    g.dispose();
   }
   else
   {
    graphic.setColor(Color.black);
    Graphics2D g = (Graphics2D) graphic.create();
    g.rotate(Math.toRadians(90), (int) (.1*width+(7.5-1)/51*.8*width), (int) ((1-(location%10)/9.0)*36.0/51*.8*height+(7.5+7.5-1.15)/51*.8*height));
    if (type.equals("Railroad"))
    {
     graphic.drawImage(Toolkit.getDefaultToolkit().getImage(board.theme+"\\railroad"+90+".png"), (int) (.1*width), (int) ((1-(location%10)/9.0)*36.0/51*.8*height+(7.5+7.5-1.15)/51*.8*height), (int) (7.5/51*.8*width), (int) (4.0/51*.8*height), null);
     g.drawString("Price $"+values[0], (int) (.1*width+(7.5-1+.5)/51*.8*width), (int) ((1-(location%10)/9.0)*36.0/51*.8*height+(7.5+7.5-1.15+6)/51*.8*height));
    }
    else if (type.equals("Chance"))
     graphic.drawImage(Toolkit.getDefaultToolkit().getImage(board.theme+"\\chance"+90+".png"), (int) (.1*width), (int) ((1-(location%10)/9.0)*36.0/51*.8*height+(7.5+7.5-1.15)/51*.8*height), (int) (7.5/51*.8*width), (int) (4.0/51*.8*height), null);
    else if (type.equals("Community Chest"))
     graphic.drawImage(Toolkit.getDefaultToolkit().getImage(board.theme+"\\communityChest"+90+".png"), (int) (.1*width), (int) ((1-(location%10)/9.0)*36.0/51*.8*height+(7.5+7.5-1.15)/51*.8*height), (int) (7.5/51*.8*width), (int) (4.0/51*.8*height), null);
    else if (type.equals("Utilities"))
    {
     graphic.drawImage(Toolkit.getDefaultToolkit().getImage(board.theme+"\\utilities"+90+".png"), (int) (.1*width), (int) ((1-(location%10)/9.0)*36.0/51*.8*height+(7.5+7.5-1.15)/51*.8*height), (int) (7.5/51*.8*width), (int) (4.0/51*.8*height), null);
     g.drawString("Price $"+values[0], (int) (.1*width+(7.5-1+.5)/51*.8*width), (int) ((1-(location%10)/9.0)*36.0/51*.8*height+(7.5+7.5-1.15+6)/51*.8*height));
    }else if (type.equals("Luxury Tax"))
    {
     graphic.drawImage(Toolkit.getDefaultToolkit().getImage(board.theme+"\\luxuryTax"+90+".png"), (int) (.1*width), (int) ((1-(location%10)/9.0)*36.0/51*.8*height+(7.5+7.5-1.15)/51*.8*height), (int) (7.5/51*.8*width), (int) (4.0/51*.8*height), null);
     g.drawString("Price $"+values[0], (int) ((1-(location%10)/9.0)*36.0/51*.8*width+(7.5+ 7.5-1.15 + .5)/51*.8*width), (int) (.9*height-(7.5-7)/51*.8*height));
    }
    else if(type.equals("Income Tax"))
    {
     String[] temp = {"Pay 10%", "Or", "$200"};
     for (int index = 0; index < temp.length; index++)    
      g.drawString(temp[index], (int) (.1*width+(7.5-1+.5)/51*.8*width), (int) ((1-(location%10)/9.0)*36.0/51*.8*height+(7.5+7.5-1.15+index/2.0 + 5)/51*.8*height));
    }
    String[] temp = name.split(" ");
    for (int index = 0; index < temp.length; index++)
     g.drawString(temp[index], (int) (.1*width+(7.5-1+.5)/51*.8*width), (int) ((1-(location%10)/9.0)*36.0/51*.8*height+(7.5+7.5-1.15+index/2.0 + .5)/51*.8*height));
    g.dispose();
   }
   graphic.drawRect((int) (.1*width), (int) ((1-(location%10)/9.0)*36.0/51*.8*height+(7.5+7.5-1.15)/51*.8*height), (int) (7.5/51*.8*width), (int) (4.0/51*.8*height));
  }
  else if (location > .5*length && location < .75*length)
  {
   graphic.setColor(new Color(189, 212, 186));
   graphic.fillRect((int) ((location%10)/9.0*36.0/51*.8*width+(7.5+2.35)/51*.8*width), (int) (.1*height), (int) (4.0/51*.8*width), (int) (7.5/51*.8*height));
   if (type.equals("Property"))
   {
    if (location < .625*length) graphic.setColor(new Color(215, 29, 32));
    else graphic.setColor(new Color(249, 221, 0)); 
    graphic.fillRect((int) ((location%10)/9.0*36.0/51*.8*width+(7.5+2.35)/51*.8*width), (int) (.1*height+(7.5-1.0)/51*.8*height), (int) (4.0/51*.8*width), (int) (1.0/51*.8*height));
    graphic.setColor(Color.black);
    graphic.drawRect((int) ((location%10)/9.0*36.0/51*.8*width+(7.5+2.35)/51*.8*width), (int) (.1*height+(7.5-1.0)/51*.8*height), (int) (4.0/51*.8*width), (int) (1.0/51*.8*height));
    if (houses > 0)
    {
     graphic.setColor(new Color(38, 96, 56));
     for (int temp = houses; temp > 0; temp--)
      graphic.fillRect((int) ((location%10)/9.0*36.0/51*.8*width+(7.5+2.35+temp-1+.125)/51*.8*width), (int) (.1*height+(7.5-1.0+.125)/51*.8*height), (int) (.75/51*.8*width), (int) (.75/51*.8*height));
    }
    if (hotels > 0)
    {
     graphic.setColor(new Color(235, 57, 59));
     graphic.fillRect((int) ((location%10)/9.0*36.0/51*.8*width+(7.5+2.35+4.0/3)/51*.8*width), (int) (.1*height+(7.5-1.0+.125)/51*.8*height), (int) (1.5/51*.8*width), (int) (.75/51*.8*height));
    }
    graphic.setColor(Color.black);
    Graphics2D g = (Graphics2D) graphic.create();
    g.rotate(Math.toRadians(180), (int) ((location%10)/9.0*36.0/51*.8*width+(7.5+2.35)/51*.8*width), (int) (.1*height+(7.5-1.0)/51*.8*height));
    String[] temp = name.split(" ");
    for (int index = 0; index < temp.length; index++)
     g.drawString(temp[index],(int) ((location%10)/9.0*36.0/51*.8*width+(7.5+2.35-3.5)/51*.8*width), (int) (.1*height+(7.5-1.0+index/2.0+.5)/51*.8*height));
    g.drawString("Price $"+values[0], (int) ((location%10)/9.0*36.0/51*.8*width+(7.5+2.35-3.5)/51*.8*width), (int) (.1*height+(7.5-1.0+6)/51*.8*height));
    g.dispose();
   }
   else 
   {
    graphic.setColor(Color.black);
    Graphics2D g = (Graphics2D) graphic.create();
    g.rotate(Math.toRadians(180), (int) ((location%10)/9.0*36.0/51*.8*width+(7.5+2.35)/51*.8*width), (int) (.1*height+(7.5-1.0)/51*.8*height));
    if (type.equals("Railroad"))
    {
     graphic.drawImage(Toolkit.getDefaultToolkit().getImage(board.theme+"\\railroad"+180+".png"), (int) ((location%10)/9.0*36.0/51*.8*width+(7.5+2.35)/51*.8*width), (int) (.1*height), (int) (4.0/51*.8*width), (int) (7.5/51*.8*height), null);
     g.drawString("Price $"+values[0], (int) ((location%10)/9.0*36.0/51*.8*width+(7.5+2.35-3.5)/51*.8*width), (int) (.1*height+(7.5-1.0+6)/51*.8*height));
    }
    else if (type.equals("Luxury Tax"))
    {
     graphic.drawImage(Toolkit.getDefaultToolkit().getImage(board.theme+"\\luxuryTax"+180+".png"), (int) ((location%10)/9.0*36.0/51*.8*width+(7.5+2.35)/51*.8*width), (int) (.1*height), (int) (4.0/51*.8*width), (int) (7.5/51*.8*height), null);
     g.drawString("Price $"+values[0], (int) ((location%10)/9.0*36.0/51*.8*width+(7.5+2.35-3.5)/51*.8*width), (int) (.1*height+(7.5-1.0+6)/51*.8*height));
    }
    else if (type.equals("Income Tax"))
    {
     String[] temp = {"Pay 10%", "Or", "$200"};
     for (int index = 0; index < temp.length; index++)    
       g.drawString(temp[index],(int) ((location%10)/9.0*36.0/51*.8*width+(7.5+2.35-3.5)/51*.8*width), (int) (.1*height+(7.5-1.0+index/2.0+5)/51*.8*height));
    }
    else if (type.equals("Chance"))
     graphic.drawImage(Toolkit.getDefaultToolkit().getImage(board.theme+"\\chance"+180+".png"), (int) ((location%10)/9.0*36.0/51*.8*width+(7.5+2.35)/51*.8*width), (int) (.1*height), (int) (4.0/51*.8*width), (int) (7.5/51*.8*height), null);
    else if (type.equals("Community Chest"))
     graphic.drawImage(Toolkit.getDefaultToolkit().getImage(board.theme+"\\communityChest"+180+".png"), (int) ((location%10)/9.0*36.0/51*.8*width+(7.5+2.35)/51*.8*width), (int) (.1*height), (int) (4.0/51*.8*width), (int) (7.5/51*.8*height), null);
    else if (type.equals("Utilities"))
    {
     graphic.drawImage(Toolkit.getDefaultToolkit().getImage(board.theme+"\\utilities"+180+".png"), (int) ((location%10)/9.0*36.0/51*.8*width+(7.5+2.35)/51*.8*width), (int) (.1*height), (int) (4.0/51*.8*width), (int) (7.5/51*.8*height), null);
     g.drawString("Price $"+values[0], (int) ((location%10)/9.0*36.0/51*.8*width+(7.5+2.35-3.5)/51*.8*width), (int) (.1*height+(7.5-1.0+6)/51*.8*height));
    }
    String[] temp = name.split(" ");
    for (int index = 0; index < temp.length; index++)
     g.drawString(temp[index],(int) ((location%10)/9.0*36.0/51*.8*width+(7.5+2.35-3.5)/51*.8*width), (int) (.1*height+(7.5-1.0+index/2.0)/51*.8*height));
    g.dispose();
   }
   graphic.drawRect((int) ((location%10)/9.0*36.0/51*.8*width+(7.5+2.35)/51*.8*width), (int) (.1*height), (int) (4.0/51*.8*width), (int) (7.5/51*.8*height));
  }
  else if (location > .75*length && location < length)
  {
   graphic.setColor(new Color(189, 212, 186));
   graphic.fillRect((int) (.9*width-7.5/51*.8*width), (int) ((location%10)/9.0*36.0/51*.8*height+(7.5+2.35)/51*.8*height), (int) (7.5/51*.8*width), (int) (4.0/51*.8*height));
   if (type.equals("Property"))
   {
    if (location < .875*length) graphic.setColor(new Color(0, 151, 74));
    else graphic.setColor(new Color(0, 87, 158));
    graphic.fillRect((int) (.9*width-7.5/51*.8*width), (int) ((location%10)/9.0*36.0/51*.8*height+(7.5+2.35)/51*.8*height), (int) (1.0/51*.8*width), (int) (4.0/51*.8*height));
    graphic.setColor(Color.black);
    graphic.drawRect((int) (.9*width-7.5/51*.8*width), (int) ((location%10)/9.0*36.0/51*.8*height+(7.5+2.35)/51*.8*height), (int) (1.0/51*.8*width), (int) (4.0/51*.8*height));
    if (houses > 0)
    {
     graphic.setColor(new Color(38, 96, 56));
     for (int temp = houses; temp > 0; temp--)
      graphic.fillRect((int) (.9*width-(7.5-.125)/51*.8*width), (int) ((location%10)/9.0*36.0/51*.8*height+(7.5+2.35+temp-1 +.125)/51*.8*height), (int) (.75/51*.8*width), (int) (.75/51*.8*height));
    }
    if (hotels > 0)
    {
     graphic.setColor(new Color(235, 57, 59));
     graphic.fillRect((int) (.9*width-(7.5-.125)/51*.8*width), (int) ((location%10)/9.0*36.0/51*.8*height+(7.5+2.35+4.0/3)/51*.8*height), (int) (.75/51*.8*width), (int) (1.5/51*.8*height));
    }
    graphic.setColor(Color.black);
    Graphics2D g = (Graphics2D) graphic.create();
    g.rotate(Math.toRadians(270), (int) (.9*width-7.5/51*.8*width), (int) ((location%10)/9.0*36.0/51*.8*height+(7.5+2.35)/51*.8*height));
    String[] temp = name.split(" ");
    for (int index = 0; index < temp.length; index++)
     g.drawString(temp[index], (int) (.9*width-(7.5+3.5)/51*.8*width), (int) ((location%10)/9.0*36.0/51*.8*height+(7.5+2.35+index/2.0+1.5)/51*.8*height));
    g.drawString("Price $"+values[0], (int) (.9*width-(7.5+3.5)/51*.8*width), (int) ((location%10)/9.0*36.0/51*.8*height+(7.5+2.35+7)/51*.8*height));
    g.dispose();
   }
   else
   {
    graphic.setColor(Color.black);
    Graphics2D g = (Graphics2D) graphic.create();
    g.rotate(Math.toRadians(270), (int) (.9*width-7.5/51*.8*width), (int) ((location%10)/9.0*36.0/51*.8*height+(7.5+2.35)/51*.8*height));
    if (type.equals("Railroad"))
    {
     graphic.drawImage(Toolkit.getDefaultToolkit().getImage(board.theme+"\\railroad"+270+".png"),(int) (.9*width-7.5/51*.8*width), (int) ((location%10)/9.0*36.0/51*.8*height+(7.5+2.35)/51*.8*height), (int) (7.5/51*.8*width), (int) (4.0/51*.8*height), null);
     g.drawString("Price $"+values[0], (int) (.9*width-(7.5+3.5)/51*.8*width), (int) ((location%10)/9.0*36.0/51*.8*height+(7.5+2.35+7)/51*.8*height));
    }
    else if (type.equals("Utilities"))
    {
     graphic.drawImage(Toolkit.getDefaultToolkit().getImage(board.theme+"\\utilities"+270+".png"),(int) (.9*width-7.5/51*.8*width), (int) ((location%10)/9.0*36.0/51*.8*height+(7.5+2.35)/51*.8*height), (int) (7.5/51*.8*width), (int) (4.0/51*.8*height), null);
     g.drawString("Price $"+values[0], (int) (.9*width-(7.5+3.5)/51*.8*width), (int) ((location%10)/9.0*36.0/51*.8*height+(7.5+2.35+7)/51*.8*height));
    }
    else if (type.equals("Community Chest"))
     graphic.drawImage(Toolkit.getDefaultToolkit().getImage(board.theme+"\\communityChest"+270+".png"),(int) (.9*width-7.5/51*.8*width), (int) ((location%10)/9.0*36.0/51*.8*height+(7.5+2.35)/51*.8*height), (int) (7.5/51*.8*width), (int) (4.0/51*.8*height), null);
    else if (type.equals("Chance"))
     graphic.drawImage(Toolkit.getDefaultToolkit().getImage(board.theme+"\\chance"+270+".png"),(int) (.9*width-7.5/51*.8*width), (int) ((location%10)/9.0*36.0/51*.8*height+(7.5+2.35)/51*.8*height), (int) (7.5/51*.8*width), (int) (4.0/51*.8*height), null);
    else if (type.equals("Luxury Tax"))
    {
     graphic.drawImage(Toolkit.getDefaultToolkit().getImage(board.theme+"\\luxuryTax"+270+".png"),(int) (.9*width-7.5/51*.8*width), (int) ((location%10)/9.0*36.0/51*.8*height+(7.5+2.35)/51*.8*height), (int) (7.5/51*.8*width), (int) (4.0/51*.8*height), null);
     g.drawString("Price $"+values[0], (int) (.9*width-(7.5+3.5)/51*.8*width), (int) ((location%10)/9.0*36.0/51*.8*height+(7.5+2.35+7)/51*.8*height));
    }
    else if (type.equals("Income Tax"))
    {
     String[] temp = {"Pay 10%", "Or", "$200"};
     for (int index = 0; index < temp.length; index++)
      g.drawString(temp[index], (int) (.9*width-(7.5+3.5)/51*.8*width), (int) ((location%10)/9.0*36.0/51*.8*height+(7.5+2.35+index/2.0+6)/51*.8*height));
    }
    String[] temp = name.split(" ");
    for (int index = 0; index < temp.length; index++)
     g.drawString(temp[index], (int) (.9*width-(7.5+3.5)/51*.8*width), (int) ((location%10)/9.0*36.0/51*.8*height+(7.5+2.35+index/2.0+1)/51*.8*height));
    g.dispose();
   }
   graphic.drawRect((int) (.9*width-7.5/51*.8*width), (int) ((location%10)/9.0*36.0/51*.8*height+(7.5+2.35)/51*.8*height), (int) (7.5/51*.8*width), (int) (4.0/51*.8*height));
  }
  else if (location == 0)
  {
   graphic.setColor(new Color(189, 212, 186));
   graphic.fillRect((int) ((1-(location%10)/10)*.8*width+.1*width-7.5/51*.8*width), (int) (.9*height-7.5/51*.8*height), (int) (7.5/51*.8*width), (int) (7.5/51*.8*height));
   graphic.drawImage(Toolkit.getDefaultToolkit().getImage(board.theme+"\\go.png"), (int) ((1-(location%10)/10)*.8*width+.1*width-7.5/51*.8*width), (int) (.9*height-7.5/51*.8*height), (int) (7.5/51*.8*width), (int) (7.5/51*.8*height), null);
   graphic.setColor(Color.black);
   graphic.drawRect((int) ((1-(location%10)/10)*.8*width+.1*width-7.5/51*.8*width), (int) (.9*height-7.5/51*.8*height), (int) (7.5/51*.8*width), (int) (7.5/51*.8*height));
   Graphics2D g = (Graphics2D) graphic.create();
   g.rotate(Math.toRadians(-45), (int) ((1-(location%10)/10)*.8*width+.1*width-(7.5-1.5)/51*.8*width), (int) (.9*height-(7.5-3.5)/51*.8*height));
   g.drawString("Collect $200", (int) ((1-(location%10)/10)*.8*width+.1*width-(7.5-1.5)/51*.8*width), (int) (.9*height-(7.5-3.5)/51*.8*height));
   g.dispose();
  }
  else if (location == (int) (.25*length)) 
  {
   graphic.setColor(new Color(189, 212, 186));
   graphic.fillRect((int) (.1*width), (int) ((1-(location%10)/10.0)*.8*height+.1*height-7.5/51*.8*height), (int) (7.5/51*.8*width), (int) (7.5/51*.8*height));
   graphic.drawImage(Toolkit.getDefaultToolkit().getImage(board.theme+"\\inJail.png"), (int) (.1*width), (int) (.9*height-7.5/51*.8*height), (int) (7.5/51 * .8 * width), (int) (7.5/51 * .8 * height), null);
   graphic.setColor(Color.black);
   Graphics2D g = (Graphics2D) graphic.create();
   g.rotate(Math.toRadians(90), (int) (.1*width+1.5/51*.8*width), (int) (.9*height-(7.5-2.0)/51*.8*height));
   g.drawString("Just", (int) (.1*width+1.5/51*.8*width), (int) (.9*height-(7.5-2.0)/51*.8*height));
   g.dispose();
   graphic.drawString("Visiting", (int) (.1*width+3.5/51*.8*width), (int) (.9*height-1.5/51*.8*height));
   graphic.drawRect((int) (.1*width), (int) ((1-(location%10)/10.0)*.8*height+.1*height-7.5/51*.8*height), (int) (7.5/51*.8*width), (int) (7.5/51*.8*height));
   String[] temp = {"In", name};
   for (int index = temp.length - 1; index >= 0; index--)
   {
    Graphics2D g2 = (Graphics2D) graphic.create();
    g2.rotate(Math.toRadians(45), (int) (.1*width+(7.5-4*index-1.0)/51*.8*width), (int) (.9*height-(7.5-3*index-1.0)/51*.8*height));
    g2.drawString(temp[index], (int) (.1*width+(7.5-4*index-1.0)/51*.8*width), (int) (.9*height-(7.5-3*index-1.0)/51*.8*height));
    g2.dispose();
   }
  }
  else if (location == (int) (.5*length))
  {
   graphic.setColor(new Color(189, 212, 186));
   graphic.fillRect((int) ((location%10)/10.0*.8*width+.1*width), (int) (.1*height), (int) (7.5/51*.8*width), (int) (7.5/51*.8*height));
   graphic.drawImage(Toolkit.getDefaultToolkit().getImage(board.theme+"\\freeParking.png"), (int) ((location%10)/10.0*.8*width+.1*width), (int) (.1*height), (int) (7.5/51*.8*width), (int) (7.5/51*.8*height), null);
   graphic.setColor(Color.black);
   graphic.drawRect((int) ((location%10)/10.0*.8*width+.1*width), (int) (.1*height), (int) (7.5/51*.8*width), (int) (7.5/51*.8*height));
   String[] temp = name.split(" ");
   for (int index = 0; index < temp.length; index++)
   {
    Graphics2D g = (Graphics2D) graphic.create();
    g.rotate(Math.toRadians(135), (int) ((7.5 + 1.15 + 4*index)/51*.8*width), (int) ((7.5+4*index)/51*.8*height));
    g.drawString(temp[temp.length-1-index], (int) ((7.5 + 1.15 + 4*index)/51*.8*width), (int) ((7.5+4*index)/51*.8*height));
    g.dispose();
   }
  }
  else if (location == (int) (.75*length))
  {
   graphic.setColor(new Color(189, 212, 186));
   graphic.fillRect((int) (.9*width-7.5/51*.8*width), (int) ((location%10)/10.0*.8*height+.1*height), (int) (7.5/51*.8*width), (int) (7.5/51*.8*height));
   graphic.drawImage(Toolkit.getDefaultToolkit().getImage(board.theme+"\\goToJail.png"), (int) (.9*width-7.5/51*.8*width), (int) (.1*height), (int) (7.5/51*.8*width), (int) (7.5/51*.8*height), null);
   graphic.setColor(Color.black);
   graphic.drawRect((int) (.9*width-7.5/51*.8*width), (int) (.1*height), (int) (7.5/51*.8*width), (int) (7.5/51*.8*height));
   String[] temp = {"Go To", board.deedList[10].name};
   for (int index = temp.length - 1; index >= 0; index--)
   {
    Graphics2D g = (Graphics2D) graphic.create();
    g.rotate(Math.toRadians(225), (int) (.9*width-(7.5-4.5*index-2)/51*.8*width), (int) ((7.5-4.5*index-1)/51*.8*height+.1*height));
    g.drawString(temp[index], (int) (.9*width-(7.5-4.5*index-2)/51*.8*width), (int) ((7.5-4.5*index-1)/51*.8*height+.1*height));
    g.dispose();
   }
  }
 }
  public void drawOffScreen(Graphics graphic, int xStart, int yStart, int width, int height)
 {
  if (width > height) width = height;//everyone knows that deed cards are square, 
  else if (height > width) height = width;// there won't be any rectangles that don't fit
  graphic.setColor(Color.white);
  graphic.fillRect(xStart, yStart, width, height);
  graphic.setColor(Color.black);
  graphic.drawRect(xStart, yStart, width, height);
  graphic.setFont(new Font("Britannic Bold", 0, (int)(.5/6.5*width)));
  if (!inMortgage)
  {
   if (type.equals("Property"))
   {
    int length = board.deedList.length;
    if (location > 0 && location < .25*length)
    {
     if (location < .125*length) graphic.setColor(new Color(113, 61, 47));
     else graphic.setColor(new Color(142, 202, 228));
    }
    else if (location > .25*length && location < .5*length)
    {
    if (location < .375*length) graphic.setColor(new Color(197, 59, 136));
     else graphic.setColor(new Color(225, 113, 29));
    }
    else if (location > .5*length && location < .75*length)
    {
     if (location < .625*length) graphic.setColor(new Color(215, 29, 32));
     else graphic.setColor(new Color(249, 221, 0));
    }
    else if (location > .75*length && location < length)
    {
     if (location < .875*length) graphic.setColor(new Color(0, 151, 74));
     else graphic.setColor(new Color(0, 87, 158));
    }//[0] is "purchase" price, [1] is mortgage, [2] is value of a house, [3-7] is value with 0-4 houses, [8] is value with hotel
    graphic.fillRect((int) (xStart + .5/6.5*width), (int) (yStart + .5/7.5*height), (int) (5.5/6.5*width), (int) (1.0/6.5*height));
    graphic.setColor(Color.black);
    graphic.drawRect((int) (xStart + .5/6.5*width), (int) (yStart + .5/7.5*height), (int) (5.5/6.5*width), (int) (1.0/6.5*height));
    graphic.drawString(name, (int) (xStart + .5/6.5*width), (int) (yStart + (1.0+.5)/7.5*height));
    graphic.drawString("Rent: $" + values[3], (int) (xStart + .5/6.5*width), (int) (yStart + (1.0+2*.5)/7.5*height));
    graphic.drawString("With 1 House: $" + values[4], (int) (xStart + .5/6.5*width), (int) (yStart + (1.0+3*.5)/7.5*height));
    graphic.drawString("With 2 Houses: $" + values[5], (int) (xStart + .5/6.5*width), (int) (yStart + (1.0+4*.5)/7.5*height));
    graphic.drawString("With 3 Houses: $" + values[6], (int) (xStart + .5/6.5*width), (int) (yStart + (1.0+5*.5)/7.5*height));
    graphic.drawString("With 4 Houses: $" + values[7], (int) (xStart + .5/6.5*width), (int) (yStart + (1.0+6*.5)/7.5*height));
    graphic.drawString("With 1 Hotel: $" + values[8], (int) (xStart + .5/6.5*width), (int) (yStart + (1.0+7*.5)/7.5*height)); 
    graphic.drawString("Mortgage Value: $" + values[1], (int) (xStart + .5/6.5*width), (int) (yStart + (1.0+8*.5)/7.5*height));
    graphic.drawString("Houses Cost: $" + values[4], (int) (xStart + .5/6.5*width), (int) (yStart + (1.0+9*.5)/7.5*height));
    graphic.drawString("Hotels, $" + values[4]+". Plus 4 Houses", (int) (xStart + .5/6.5*width), (int) (yStart + (1.0+10*.5)/7.5*height));
   }
   else if (type.equals("Railroad"))
   {
    graphic.setColor(Color.black);
    graphic.drawRect((int) (xStart + .5/6.5*width), (int) (yStart + .5/7.5*height), (int) (5.5/6.5*width), (int) (1.0/6.5*height));
    graphic.drawString(name, (int) (xStart + .5/6.5*width), (int) (yStart + (1.0+.5)/7.5*height)); 
    graphic.drawString("Rent: $" + values[3], (int) (xStart + .5/6.5*width), (int) (yStart + (1.0+2*.5)/7.5*height));
    graphic.drawString("With 2 "+group+": $" + values[4], (int) (xStart + .5/6.5*width), (int) (yStart+(1.0+3*.5)/7.5*height));
    graphic.drawString("With 3 "+group+": $" + values[5], (int) (xStart + .5/6.5*width), (int) (yStart + (1.0+4*.5)/7.5*height));
    graphic.drawString("With 4 "+group+": $" + values[6], (int) (xStart + .5/6.5*width), (int) (yStart + (1.0+5*.5)/7.5*height));
    graphic.drawString("Mortgage Value: $" + values[1], (int) (xStart + .5/6.5*width), (int) (yStart + (1.0+8*.5)/7.5*height));
   }
   else if (type.equals("Utilities"))
   {
    graphic.setColor(Color.black);
    graphic.drawRect((int) (xStart + .5/6.5*width), (int) (yStart + .5/7.5*height), (int) (5.5/6.5*width), (int) (1.0/6.5*height));
    graphic.drawString(name, (int) (xStart + .5/6.5*width), (int) (yStart + (1.0+.5)/7.5*height));
    graphic.drawString("Rent: 4 X Dice Roll", (int) (xStart + .5/6.5*width), (int) (yStart + (1.0+2*.5)/7.5*height));
    graphic.drawString("Rent if 2 "+group+" Owned:", (int) (xStart + .5/6.5*width), (int) (yStart + (1.0+3*.5)/7.5*height));
    graphic.drawString("10 X Dice Roll", (int) (xStart + .5/6.5*width), (int) (yStart + (1.0+4*.5)/7.5*height));
    graphic.drawString("Mortgage Value: $" + values[1], (int) (xStart + .5/6.5*width), (int) (yStart + (1.0+8*.5)/7.5*height));
   }
  }
  else if (inMortgage)
  {
   graphic.drawString(name, (int) (xStart + .5/6.5*width), (int) (yStart + (1.0+.5)/7.5*height));
   graphic.drawString("Mortgage For: $" + values[1], (int) (xStart + .5/6.5*width), (int) (yStart + (1.0+3*.5)/7.5*height));
  }
 }
 public String toString()
 {
  return name+"("+location+")" + "(" + group + ")";
 }
}