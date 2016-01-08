import java.io.File;
import javax.swing.JFrame;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
//import javax.swing.JTextField;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.GraphicsEnvironment;
public class MonopolyGUI extends JComponent
{
 JTextArea textOutput, textPrompt;
 JScrollPane outputScroll;
 //JTextField textInput;
 //String lastInput;
 final Dimension MaxDimensions;
 Board board;
 Boolean start;
 public static void main(String[] args)
 {
  javax.swing.SwingUtilities.invokeLater(new Runnable()
   {
    public void run()
    {
     createAndShowGUI();
    }
   }
  );
 }
 public static void createAndShowGUI()
 {
  JFrame frame = new JFrame("Monopoly");
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  MonopolyGUI game = new MonopolyGUI();
  frame.add(game);
  frame.pack();
  frame.setVisible(true);
  while (game.start)
   game.runStart();
  while (!game.board.gameOver())
   game.board.nextTurn();
 }
 public MonopolyGUI()
 {
  Dimension temp = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getSize();
  MaxDimensions = new Dimension((int) (.9 *temp.width), (int) (.9 * temp.height));
  addMouseListener(new MouseAdapter()
   {
    public void mousePressed(MouseEvent event)
    {
     if (event.getX() <= 5 && event.getY() <= 5) board.theme = "Smile";
     if (event.getX() <= 10 && event.getY() <= 10) textOutput.append("There's nothing over here\n");
     repaint();
    }
   }
  );   
  textOutput = new JTextArea(1, 1);
  textOutput.setEditable(false);
  textOutput.setLineWrap(true);
  textOutput.setFont(new Font("Britannic Bold", 0, (int) (1.0/51*.8*MaxDimensions.height)));
  outputScroll = new JScrollPane(textOutput);
  outputScroll.setBounds((int)((.75-.05/2)*MaxDimensions.width), (int)((.9-.25)*MaxDimensions.height), (int)(.25*MaxDimensions.width), (int)(.25*MaxDimensions.height));
  /*textInput = new JTextField(1);
  textInput.setBounds((int)((.75-.05/2)*MaxDimensions.width), (int)((.9-.35)*MaxDimensions.height), (int)(.25*MaxDimensions.width), (int)(.05*MaxDimensions.height));
  textInput.setFont(new Font("Britannic Bold", 0, (int) (1.0/51*.8*MaxDimensions.height)));
  textInput.addActionListener(new ActionListener()
   {
    public void actionPerformed(ActionEvent e)
    {
     lastInput = textInput.getText();
     textOutput.append("LastInput: "+lastInput+"\n");
     textInput.setText("");
     repaint();
    }
   }
  );*/
  textPrompt = new JTextArea(1, 1);
  textPrompt.setBounds((int)((.75-.05/2)*MaxDimensions.width), (int)((.9-.45)*MaxDimensions.height), (int)(.25*MaxDimensions.width), (int)(.05*MaxDimensions.height));
  textPrompt.setEditable(false);
  textPrompt.setLineWrap(true);  
  textPrompt.setFont(new Font("Britannic Bold", 0, (int) (1.0/51*.8*MaxDimensions.height)));
  add(outputScroll);
  //add(textInput);
  add(textPrompt);
  start = true;
  board = new Board(null, this, 0, null);
 }
 public Dimension getPreferredSize()
 {
  return MaxDimensions;
 }
 public String getInput()
 {
  repaint();
  return (String)JOptionPane.showInputDialog(this, textPrompt.getText());
 }
 public void runStart()
 {
  String[] names = new String[0];
  String thm = null;
  int gameTyp = 0, length;
  boolean flag = true;
  do 
  {
   thm = board.promptInput("Welcome!\nEnter the theme of the board"); 
   if (thm != null)
   {
    File file = new File(thm);
    if (file.exists()) flag = false;
   }
  }
  while (flag);
  do gameTyp = board.promptIntInput("What game type?\n 0 is normal, 1 is Flop-oly, 2 is Rich-oply"); while (gameTyp < 0 || gameTyp > 2);
  do length = board.promptIntInput("How many players?"); while (length < 2 || length > 8);
  names = new String[length];
  for (int index = length - 1; index >= 0; index--)
   names[index] = board.promptInput("Name of player " + (length-index));
  board = new Board(names, this, gameTyp, thm);
  start = false;
 }
 protected void paintComponent(Graphics graphic)
 {
  board.draw(graphic);
  graphic.setColor(new Color(189, 212, 186));
  graphic.fillRect((int)((.75-.05)*MaxDimensions.width), (int)((.9-.25-.05/2)*MaxDimensions.height), (int)((.25+.05)*MaxDimensions.width), (int)((.25+.05)*MaxDimensions.height));
  //graphic.fillRect((int)((.75-.05)*MaxDimensions.width), (int)((.9-.35-.05/2)*MaxDimensions.height), (int)((.25+.05)*MaxDimensions.width), (int)(.1*MaxDimensions.height));
  graphic.fillRect((int)((.75-.05)*MaxDimensions.width), (int)((.9-.45-.05/2)*MaxDimensions.height), (int)((.25+.05)*MaxDimensions.width), (int)(.1*MaxDimensions.height));
  graphic.setColor(Color.black);
  graphic.drawRect((int)((.75-.05)*MaxDimensions.width), (int)((.9-.25-.05/2)*MaxDimensions.height), (int)((.25+.05)*MaxDimensions.width), (int)((.25+.05)*MaxDimensions.height));
  //graphic.drawRect((int)((.75-.05)*MaxDimensions.width), (int)((.9-.35-.05/2)*MaxDimensions.height), (int)((.25+.05)*MaxDimensions.width), (int)(.1*MaxDimensions.height));
  graphic.drawRect((int)((.75-.05)*MaxDimensions.width), (int)((.9-.45-.05/2)*MaxDimensions.height), (int)((.25+.05)*MaxDimensions.width), (int)(.1*MaxDimensions.height));
  Graphics2D g = (Graphics2D) graphic.create();
  g.rotate(Math.toRadians(270), (int)(.7*MaxDimensions.width), (int)(.85*MaxDimensions.height));
  g.drawString("History", (int)(.725*MaxDimensions.width), (int)(.875*MaxDimensions.height));
  //g.drawString("Input", (int)(.84*MaxDimensions.width), (int)(.875*MaxDimensions.height));
  g.drawString("Prompt", (int)(.89*MaxDimensions.width), (int)(.875*MaxDimensions.height));
  g.dispose();
 }
}