//Some of the colors aren�t popping right to the corresponding properties
import java.awt.*;  //for Graphics
import javax.swing.*;  //for JComponent, JFrame
import java.net.*;  //for URL
import java.util.*;

import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class RiskBoard extends JComponent implements MouseListener,ActionListener
{
  private Image board;
  private Image[] whiteDieImages;
  private boolean start;
  private Risk game;
  private JLabel infoLabel;
  private JLabel infoLabel2;
  private JLabel infoLabel3;
  private JLabel turnLabel;
  private String labelInfo;
  private Image red;
  private Image blue;
  private Image green;
  private Image black;
  private Image purple;
  private Image pink;
  private Image click;
  private ArrayList<Place> placesWArmies;
  
 // private JButton mortgageButton;

  private ArrayList<String> playerInfos;
  private ArrayList<String> textColors;
  JFrame frame;
  
  private Location lastLocationClicked;
  
  public RiskBoard(Risk g)
  {
    game = g;
    int numPlayers = game.getNumPlayers();
    playerInfos = new ArrayList<String>();
    start = true;
    frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.LINE_AXIS));

    setPreferredSize(new Dimension(1000, 592));
    frame.getContentPane().setBackground(new Color(205, 230, 208));
    frame.getContentPane().add(this);
    addMouseListener(this);

    turnLabel = new JLabel("", SwingConstants.RIGHT);
    turnLabel.setPreferredSize(new Dimension(145,200));
    turnLabel.setText("<html>line 1<br/>line 2</html>");
    turnLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
    frame.getContentPane().add(turnLabel);
    
    infoLabel = new JLabel("", SwingConstants.RIGHT);
    infoLabel.setPreferredSize(new Dimension(145, 200));
    infoLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
  
    infoLabel2 = new JLabel("", SwingConstants.RIGHT);
    infoLabel2.setPreferredSize(new Dimension(145, 200));
    infoLabel2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
    
    infoLabel3 = new JLabel("", SwingConstants.RIGHT);
    infoLabel3.setPreferredSize(new Dimension(145, 200));
    infoLabel3.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
    
    infoLabel.setText("<html>line 1<br/>line 2</html>");
    frame.getContentPane().add(infoLabel);
    if (numPlayers>2)
     frame.getContentPane().add(infoLabel2);
    if (numPlayers>4)
      frame.getContentPane().add(infoLabel3);
    
    frame.pack();
    URL url = getClass().getResource("board final.jpg");
    if (url == null)
      throw new RuntimeException("file not found:  " + "board final.jpg");
    board = new ImageIcon(url).getImage();
    
    URL urlC = getClass().getResource("click.png");
    if (urlC == null)
      throw new RuntimeException("file not found:  " + "click.png");
    click = new ImageIcon(urlC).getImage();
   
    frame.setVisible(true);
    
    int players = 0;
    
    red = null;
    black = null;
    blue = null;
    green = null;
    purple = null;
    pink = null;
    
    while (numPlayers > 0)
    {
      if (players == 0)
      {
        URL url1 = getClass().getResource("green.jpg");
        if (url1 == null)
          throw new RuntimeException("file not found:  " + "green.jpg");
        green = new ImageIcon(url1).getImage();
      }
      else if (players == 1)
      {
        URL url2 = getClass().getResource("red.jpg");
        if (url2 == null)
          throw new RuntimeException("file not found:  " + "red.jpg");
        red = new ImageIcon(url2).getImage();
      }
      else if (players == 2)
      {
        URL url3 = getClass().getResource("blue.jpg");
        if (url3 == null)
          throw new RuntimeException("file not found:  " + "blue.jpg");
        blue = new ImageIcon(url3).getImage();
      }
      else if (players == 3)
      {
        URL url4 = getClass().getResource("black.jpg");
        if (url4 == null)
          throw new RuntimeException("file not found:  " + "black.jpg");
        black = new ImageIcon(url4).getImage();
      }
      else if (players == 4)
      {
        URL url5 = getClass().getResource("purple.jpg");
        if (url5 == null)
          throw new RuntimeException("file not found:  " + "purple.jpg");
        purple = new ImageIcon(url5).getImage();
      }
      else if (players == 5)
      {
        URL url6 = getClass().getResource("pink.jpg");
        if (url6 == null)
          throw new RuntimeException("file not found:  " + "pink.jpg");
        pink = new ImageIcon(url6).getImage();
      }
      players++;
      numPlayers--;
      frame.setVisible(true);
    }   
    
    
    whiteDieImages = new Image[7];
    for (int i = 1; i <= 6; i++)
    {
      String fileName = i + ".png";
      url = getClass().getResource(fileName);
      if (url == null)
        throw new RuntimeException("file not found:  " + fileName);
      whiteDieImages[i] = new ImageIcon(url).getImage();
    }
    
    
    textColors = new ArrayList<String>();
    //blue
    for (int i = 0; i<2; i++)
      textColors.add("<font color=#226ecc>");
    //green
    for (int i = 0; i<3; i++)
      textColors.add("<font color=#27a31b>");
    //yellow
    for (int i = 0; i<3; i++)
      textColors.add("<font color=#fffb72>");
    //red
    for (int i = 0; i<3; i++)
      textColors.add("<font color=#ff0000>");
    //orange
    for (int i = 0; i<3; i++)
      textColors.add("<font color=#ff7700>");
    //pink
    for (int i = 0; i<3; i++)
      textColors.add("<font color=#ff00bb>");
    //light blue
    for (int i = 0; i<3; i++)
      textColors.add("<font color=#63d7ff>");
    //brown
    for (int i = 0; i<2; i++)
      textColors.add("<font color=#6d3b18>");
    
    placesWArmies = game.getPlacesWithArmies();
  }
  
  
  //magically called whenever java feels like it
  public void paintComponent(Graphics g)
  {  
    g.drawImage(board, 0, 0, null); 
    //ArrayList<Location> locations = game.getPropertyLocations();
    
    int numPlayers = game.getNumPlayers();
  

//    int locationP1 = game.getPlayer(1).getLocation();
//  //  Location p1 = locations.get(locationP1);
//  //  g.drawImage(green, p1.getX(), p1.getY(), null);
//    
//    if (numPlayers >= 2)
//    {
//      int locationP2 = game.getPlayer(2).getLocation();
//  //    Location p2 = locations.get(locationP2);
//  //    g.drawImage(red, p2.getX(), p2.getY(), null);
//    }
//    
//    if (numPlayers >= 3)
//    {
//      int locationP3 = game.getPlayer(3).getLocation();
// //     Location p3 = locations.get(locationP3);
//   //   g.drawImage(blue, p3.getX(), p3.getY(), null);
//    }
//    
//    if (numPlayers == 4)
//    {
//      int locationP4 = game.getPlayer(4).getLocation();
//   //   Location p4 = locations.get(locationP4);
//   //   g.drawImage(black, p4.getX(), p4.getY(), null);
//    }
    turnLabel.setText("<html> Player " + (game.getTurn()+1) + "'s turn </html>");
    updateLabel();
    
    placesWArmies = game.getPlacesWithArmies();
    if (placesWArmies!=null)
    {
      for (Place place : placesWArmies)
      {
        int num = place.getArmies();
        String color = place.getOwner().getColor();
        if (color.equals("green"))
          g.drawImage(green,place.getLoc().getX() -10,place.getLoc().getY()-10,null);
        if (color.equals("red"))
          g.drawImage(red,place.getLoc().getX() -10,place.getLoc().getY()-10,null);
        if (color.equals("blue"))
          g.drawImage(blue,place.getLoc().getX() -10,place.getLoc().getY()-10,null);
        if (color.equals("black"))
          g.drawImage(black,place.getLoc().getX() -10,place.getLoc().getY()-10,null);
        if (color.equals("purple"))
          g.drawImage(purple,place.getLoc().getX() -10,place.getLoc().getY()-10,null);
        if (color.equals("pink"))
          g.drawImage(pink,place.getLoc().getX() -10,place.getLoc().getY()-10,null);
        updateLabel();
      }
    }
    
    if (game.selecting())
    {
      ArrayList<Place> places = game.getPlaces();
      for (Place pp : places)
        g.drawImage(click,pp.getLoc().getX(),pp.getLoc().getY(),null);
    }
    
    updateLabel();  
    
    int[] whites = game.getWhiteDice();
    int[] reds = game.getRedDice();
    int r1 = reds[0];
    int r2 = reds[1];
    int r3 = reds[2];
    int w1 = whites[0];
    int w2 = whites[1];
    
//    //have to make separate dieImages for red and white
    g.drawImage(whiteDieImages[w1], 300, 350, null);
    g.drawImage(whiteDieImages[w2], 425, 350, null);
    
  }
  
  
  public void updateLabel()
  {
    int numPlayers = game.getNumPlayers();
    ArrayList<Player> players = game.getPlayers();
    
    int k = 0;
    while (k < 5)
    {
      playerInfos = new ArrayList<String>();
      for (int i = k; i<numPlayers; i++)
      {
        if (i < k+2)
        {
          playerInfos.add("<br/> Player " + (i+1));
          if (players!=null)
          {
            Player p = players.get(i);
            ArrayList<Place> places = p.getPlaces();
            if (places!=null)
            {
              for (Place place : places)
                playerInfos.add("<br/> " + place.getName() + "    " + place.getArmies() + " armies" + "</font>");
            }
          }
        }
      }
      labelInfo = "<html>";
      for (int j = 0; j<playerInfos.size(); j++)
        labelInfo+=playerInfos.get(j);
      labelInfo += "</html>";
      if (k==0)
      {
        infoLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        infoLabel.setText(labelInfo);
      }
      else if (k==2)
      {
        infoLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
        infoLabel2.setText(labelInfo);
      }
      else if (k==4)
      {
        infoLabel3.setHorizontalAlignment(SwingConstants.RIGHT);
        infoLabel3.setText(labelInfo);
      }
      k+=2;
    }
  }

  
  public void drawPlayer(Graphics g, int x, int y)
  {
    
  }

  public void showMessageDialog(String message)
  {
    JOptionPane.showMessageDialog(this, message);
  }
  
  public void getClick()
  {
    
  }
  
  public void update()
  {
    repaint();  //java:  "when i have time, call paintComponent"
    try{Thread.sleep(100);}catch(Exception e){}
  }
  
  public void labelToString()
  {
    for (int i = 0; i<playerInfos.size(); i++)
    {
      System.out.println(i+ ": " + playerInfos.get(i));
    }
  }
  
  public void mouseExited(MouseEvent e)
  {
  }
  
  public void mouseEntered(MouseEvent e)
  {
  }
  
  public void mouseReleased(MouseEvent e)
  {
  }
  
  public void mousePressed(MouseEvent e)
  {
    lastLocationClicked = new Location(e.getX(), e.getY());
  }
  
  public void mouseClicked(MouseEvent e)
  {
  }
  
  //waits for user to click on screen, and returns coordinates
  public Location getClickedLocation()
  {
    lastLocationClicked = null;
    while (lastLocationClicked == null)
    {
      try{Thread.sleep(100);}catch(Exception e){};
    }
    return lastLocationClicked;
  }
  
  public void actionPerformed(ActionEvent e)
  {
    String command = e.getActionCommand();
    final Player player = game.getCurrentPlayer();
//    if (command.equals("house"))
//    {
//      
//      new Thread()
//      {
//        public void run()
//        {
//          game.buyHouses(player);
//        }
//      }.start();
//    }
 
//    else if (command.equals("new"))
//    {
//      new Thread()
//      {
//        public void run()
//        {
//          game.newGame();
//        }
//      }.start();
//    }
  }
}

