import java.awt.*;  //for Graphics
import javax.swing.*;  //for JComponent, JFrame
import java.net.*;  //for URL
import java.util.*;
import java.util.HashMap;

import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class Risk extends JComponent 
{
  public int numPlayers;
  public ArrayList<Player> players; 
  public static RiskBoard board; //the board
  public ArrayList<Place> places; 
  public ArrayList<Place> placesWithArmies;
  public ArrayList<Location> spots;
  public int turn; // 0 1 2 
  public int roll;
  public int red1;
  public int red2;
  public int red3;
  public int white1; //also die used to start game
  public int white2; //these are the numbers each die is showing
  public JFrame frame;
  public boolean dice;
  public boolean selecting;
  public int totalArmies;
  
  public Risk() 
  {
    turn = 0;
    selecting = false;
    totalArmies = 0;
    
    Integer[] choices = {2, 3, 4, 5, 6};
    frame = new JFrame("Test");
    Integer selected = (Integer)JOptionPane.showInputDialog(frame, "How many players?","Players", JOptionPane.QUESTION_MESSAGE,null,choices,choices[0]);   
    numPlayers = selected;
    board = new RiskBoard(this);
    dice = false;
    
    players = new ArrayList<Player>();
    for (int i = 0; i < numPlayers; i++)
      players.add(new Player(i, false));
    
    if (players.size() >= 1)
      players.get(0).setColor("green");
    if (players.size() >= 2)
      players.get(1).setColor("red");
    if (players.size() >= 3)
      players.get(2).setColor("blue");
    if (players.size() >= 4)
      players.get(3).setColor("black");
    if (players.size() >= 5)
      players.get(4).setColor("purple");
    if (players.size() >= 6)
      players.get(5).setColor("pink");
    
    places = new ArrayList<Place>();
    placesWithArmies = new ArrayList<Place>();
    addPlaces();
  }
  
  public static void main(String[] args)
  {
    Risk g = new Risk();
    g.play();
  }
  
  public void play()
  {
    start();
    //setArmiesRandom();
    board.update();
    while (true)
    {
      turn(players.get(turn));
      getClick();
    }
  }
  
  public Player getCurrentPlayer()
  {
    return players.get(turn);
  }
  
  public ArrayList<Player> getPlayers()
  {
    return players;
  }
  
  public int getNumPlayers()
  {
    return numPlayers;
  }
  
  public int getTurn()
  {
    return turn;
  }
 
  //check if this method is right
  public Player getPlayer(int num)
  {
    return players.get(turn);
  }
  
  public void nextTurn()
  {
    System.out.println(turn);
    if (turn < players.size()-1)
      turn++;
    else
      turn = 0;
    System.out.println(turn);
  }
  
  public int[] getRedDice()
  {
    int[] dice = new int[3];
    dice[0] = red1;
    dice[1] = red2;
    dice[2] = red3;
    return dice;
  }
  
  public int[] getWhiteDice()
  {
    int[] dice = new int[2];
    dice[0] = white1;
    dice[1] = white2;
    return dice;
  }
  
  
 
  public void turn(Player p)
  { 
    board.updateLabel(); 
    
    //get armies
    if (p.getPlaces()!=null && p.getPlaces().size()>=3) //player has territories
      p.addArmies(p.getPlaces().size()/3);
    else if (p.getPlaces()!=null && p.getPlaces().size()<3)
      p.addArmies(1);
    
    //add armies for continent ownership
    if (continent(p,0))
      p.addArmies(5);
    if (continent(p,1))
      p.addArmies(2);
    if (continent(p,2))
      p.addArmies(5);
    if (continent(p,3))
      p.addArmies(3);
    if (continent(p,4))
      p.addArmies(7);
    if (continent(p,5))
      p.addArmies(2);
    
    placeArmiesTurn();
    
    //attack
    attack(p);
    
    nextTurn();
  }
  
  public void attack(Player p)
  {
    int attackYN = JOptionPane.showConfirmDialog(frame, "Do you want to attack a country?","Confirm",JOptionPane.YES_NO_OPTION);
    while (attackYN==0)
    {
      JOptionPane.showMessageDialog(frame, "Choose the country you want to attack with.");
      selecting = true;
      board.update();
      Place place = getPlace(getClick());
      selecting = false;
      board.update();
      if (place!=null && place.getOwner().equals(p) && place.getArmies() >= 2)
      {
        ArrayList<Integer> canAttack = place.canAttack();
        for (int i = 0; i < canAttack.size(); i++) //take out places that the person already owns
        {
          System.out.println("canattack " + canAttack.get(i));
          if (places.get(canAttack.get(i)).getOwner().equals(p))
          {
            canAttack.remove(i);
            i--;
          }
        }
        
        ArrayList<String> names = new ArrayList<String>();
        HashMap<String, Integer> nameIndex = new HashMap<String,Integer>();
        
        for (int index : canAttack)
        {
          String name = places.get(index).getName();
          names.add(name);
          nameIndex.put(name, index);
        }

        //choose which one you want to attack
        Object[] objs = names.toArray();
        String[] choices = Arrays.copyOf(objs, objs.length, String[].class);
        String selected = (String)JOptionPane.showInputDialog(frame, "Which country do you want to attack?","Attack", JOptionPane.QUESTION_MESSAGE,null,choices,choices[0]); 
        int chosen = 0;
        if (selected!=null)
          chosen = nameIndex.get(selected);
        
        //choose number of armies to attack with
        ArrayList<Integer> numCanAttack = numCanAttack(place);
        Integer[] cs = Arrays.copyOf(numCanAttack.toArray(), numCanAttack.toArray().length, Integer[].class);
        Integer sel = (Integer)JOptionPane.showInputDialog(frame, "How many do you want to attack with?","Attack", JOptionPane.QUESTION_MESSAGE,null,cs,cs[0]); 
      }
      else
      {
        attackYN = JOptionPane.showConfirmDialog(frame, "You can't attack with that place. \n YES to choose another place or NO to change your mind.","Confirm",JOptionPane.YES_NO_OPTION);
      }
    }
    
  }
 
  public ArrayList<Integer> numCanAttack(Place p)
  {
    ArrayList<Integer> x = new ArrayList<Integer>();
    int armies = p.getArmies();
    if (armies>1)
      x.add(1);
    if (armies>2)
      x.add(2);
    if (armies>3)
      x.add(3);
    return x;
  }
  
  public void setup()
  {
    int armies = 40; //for 2 players
    int i = 3;
    while(i<=numPlayers)
    {
      armies-=5;
      i++;
    }
    for (Player p : players)
      p.addArmies(armies);
    totalArmies = armies * numPlayers;
    JOptionPane.showMessageDialog(frame, "Each player has " + armies + " armies. Roll to see who starts.");
    
    //roll and max roll starts
    int max = 0;
    Player best = players.get(0);
    for (Player p : players)
    {
      white1 = (int)(Math.random() * 6) + 1;
      board.update();
      if (white1>max)
      {
        max = white1;
        best = p;
      }
    }
    turn = best.getPlayer(); //turn goes to the player who rolled the most
    white1 = 0;
    board.update();
  }
  
  public void start()
  {
    setup();
    //place armies
    placeArmies(totalArmies);
    //all armies are placed
    JOptionPane.showMessageDialog(frame, "All armies have been placed.");
  }
  
  public ArrayList<Place> getPlacesWithArmies()
  {
    return placesWithArmies;
  }
 
  public ArrayList<Place> getPlaces()
  {
    return places;
  }
  
  public void placeArmies(int totArmies)
  { 
    int t = totArmies;
    //still armies left, so they must be placed
    while (totArmies > 0)
    {
      int n = numPlayers; //all players
      while (n>0 && totArmies > 0) //loop through players, have them choose an unclaimed territory until all are occupied
      {
        Player p = players.get(turn);
        int playersArmies = p.getArmies();
        while (p.getArmies()==playersArmies)
        {
          JOptionPane.showMessageDialog(frame, "Player " + (turn+1) + " please choose a territory for your army.");
          selecting = true;
          board.update();
          Place place = getPlace(getClick());
          if (place!=null) //they clicked on an actual place
          {
            int yesNo = JOptionPane.showConfirmDialog(frame, "Would you like to place an army on " + place.getName() + "?","Confirm",JOptionPane.YES_NO_OPTION);
            if (yesNo==0)//yes?
            {
              System.out.println("t-total " + (t-totArmies));
              if ((p.getArmies()>=1 && place.getOwner()==null) || (p.getArmies()>=1 && t-totArmies>=42 && place.getOwner().equals(p))) //allowed to place an army
              {
                if (place.getOwner()==null)
                {
                  place.setPlayer(p);
                  p.addPlace(place);
                  placesWithArmies.add(place);
                }
                place.addArmies(1);
                p.addArmies(-1);
                totArmies--;
                board.update();
              }
              else if (p.getArmies()==0) //not enough armies
                JOptionPane.showMessageDialog(frame, "Sorry, you do not have any armies left.");
              else if (p.getArmies()>=1 && place.getOwner().equals(p))//if they own it
                JOptionPane.showMessageDialog(frame, "You already placed an army here.\nChoose another place to place an army.");
              else if (!place.getOwner().equals(p))
                JOptionPane.showMessageDialog(frame, "Someone else already has armies here. \nChoose another place.");
              else{}
            }
            else //no?
              JOptionPane.showMessageDialog(frame, "Choose another place to place an army.");
          }
          else
            JOptionPane.showMessageDialog(frame, "Please click on the finger pointer in your chosen location.");
          selecting = false;
          board.update();
        }
        nextTurn();
        n--;
        board.update();
      }
    }
  }
  
  public void placeArmiesTurn()
  {
    Player p = players.get(turn);
    while (p.getArmies()>0) 
    {
      JOptionPane.showMessageDialog(frame, "Player " + (turn+1) + " please choose a territory for your army.");
      selecting = true;
      board.update();
      Place place = getPlace(getClick());
      if (place!=null) //they clicked on an actual place
      {
        int yesNo = JOptionPane.showConfirmDialog(frame, "Would you like to place an army on " + place.getName() + "?","Confirm",JOptionPane.YES_NO_OPTION);
        if (yesNo==0)//yes?
        {
          if (p.getArmies()>=1 && place.getOwner().equals(p)) //allowed to place an army
          {
            place.addArmies(1); 
            p.addArmies(-1);
            board.update();
          }
          else if (p.getArmies()==0) //not enough armies
            JOptionPane.showMessageDialog(frame, "Sorry, you do not have any armies left.");
          else if (!place.getOwner().equals(p))
            JOptionPane.showMessageDialog(frame, "Someone else already has armies here. \nChoose another place.");
          else{}
        }
        else //no?
        {}
      }
      else
        JOptionPane.showMessageDialog(frame, "Please click on the finger pointer in your chosen location.");
      selecting = false;
      board.update();
    }
  }
  
  public boolean selecting()
  {
    return selecting;
  }
  
  public void battle(Player one, Player two) //also have to know the place
  {
    //roll dice
    red1 = (int)(Math.random() * 6) + 1;
    red2 = (int)(Math.random() * 6) + 1;
    red3 = (int)(Math.random() * 6) + 1;
    white1 = (int)(Math.random() * 6) + 1;
    white2 = (int)(Math.random() * 6) + 1;
    board.update();
    
    //move soldiers to the new country and away from the old country
  }
  
  public Location getClick()
  {
    return board.getClickedLocation();
  }
 
  public Place getPlace(Location loc)
  {
    int i = 0;
    while (i <= places.size()-1 && !places.get(i).getLoc().closeTo(loc)) //loop through places to find which place is closest to loc
      i++;
    if (i > places.size()-1)
      return null;
    else
      return places.get(i);
  }
  
  public void setArmiesRandom() 
  { 
    setup();
    while (players.get(players.size()-1).getArmies()>0)
    {
      for (Player p : players)
      {
        Place place = places.get((int)(Math.random()*places.size()));
        while (place.getOwner() != null && !place.getOwner().equals(p))
          place = places.get((int)(Math.random()*places.size()));
        if (place.getArmies()==0)
        {
          place.setPlayer(p);
          place.addArmies(1);
          p.addArmies(-1);
          p.addPlace(place);
          placesWithArmies.add(place);
        }
        else if (place.getOwner().equals(p) && placesWithArmies.size()==places.size())
        {
          place.addArmies(1);
          p.addArmies(-1);
        }
      }
    }
  }
  
  //0 NA, 1 SA, 2 Eur, 3 Af, 4 Asia, 5 Aus
  public boolean continent(Player p, int cont)
  {
    int start = 0;
    int end = 0;
    if (cont == 0)
    {
      start = 1; end = 10;
    }
    if (cont == 1)
    {
      start = 10; end = 14;
    }
    if (cont == 2)
    {
      start = 14; end = 21;
    }
    if (cont == 3)
    {
      start = 21; end = 27;
    }
    if (cont == 4)
    {
      start = 27; end = 39;
    }
    if (cont == 5)
    {
      if (places.get(0).getOwner()==null || (places.get(0).getOwner()!=null && !places.get(0).getOwner().equals(p)))
        return false;
      else
        start = 39; end = 41;
    }
    

    for (int i=start; i<end; i++)
      {
        if (places.get(0).getOwner()==null || (places.get(i).getOwner()!=null && !places.get(i).getOwner().equals(p)))
          return false;
      }
    return true;
  }
  
  public void addPlaces()
  {
    places.add(new Place("Eastern Australia", new Location(847,531), new ArrayList<Integer>(Arrays.asList(39,41)))); //0

    places.add(new Place("Alaska", new Location(25,103), new ArrayList<Integer>(Arrays.asList(2,3,37)))); //1
    places.add(new Place("Northwest Territory", new Location(133,88), new ArrayList<Integer>(Arrays.asList(1,3,5,9)))); //2
    places.add(new Place("Alberta", new Location(122,140), new ArrayList<Integer>(Arrays.asList(1,2,5,4)))); //3
    places.add(new Place("Western United States", new Location(110,220), new ArrayList<Integer>(Arrays.asList(7,3,5,8)))); //4
    places.add(new Place("Ontario", new Location(170,126), new ArrayList<Integer>(Arrays.asList(2,3,6,9,4,7)))); //5
    places.add(new Place("Quebec", new Location(239,118), new ArrayList<Integer>(Arrays.asList(9,7,5)))); //6
    places.add(new Place("Eastern United States", new Location(208,201), new ArrayList<Integer>(Arrays.asList(6,5,4,8)))); //7
    places.add(new Place("Central America", new Location(130,284), new ArrayList<Integer>(Arrays.asList(7,4,10)))); //8
    places.add(new Place("Greenland", new Location(293,77), new ArrayList<Integer>(Arrays.asList(2,5,6,14)))); //9
    
    places.add(new Place("Venezuela", new Location(175,340),new ArrayList<Integer>(Arrays.asList(9,12,11)))); //10
    places.add(new Place("Brazil", new Location(264,412), new ArrayList<Integer>(Arrays.asList(10,12,13,21)))); //11
    places.add(new Place("Peru", new Location(180,404), new ArrayList<Integer>(Arrays.asList(10,11,13)))); //12
    places.add(new Place("Argentina", new Location(210,495), new ArrayList<Integer>(Arrays.asList(12,11))));
    
    places.add(new Place("Iceland", new Location(368,123), new ArrayList<Integer>(Arrays.asList(9,15,16)))); //14
    places.add(new Place("Great Britain", new Location(355,198), new ArrayList<Integer>(Arrays.asList(14,16,17,18)))); //15
    places.add(new Place("Scandanavia", new Location(420,133), new ArrayList<Integer>(Arrays.asList(14,15,17,20)))); //16
    places.add(new Place("Northern Europe", new Location(442,173), new ArrayList<Integer>(Arrays.asList(16,19,18,15,20)))); //17
    places.add(new Place("Western Europe", new Location(375,286), new ArrayList<Integer>(Arrays.asList(15,17,19,21))));
    places.add(new Place("Southern Europe", new Location(425,272), new ArrayList<Integer>(Arrays.asList(18,17,15,21,20,27))));
    places.add(new Place("Ukraine", new Location(509,176), new ArrayList<Integer>(Arrays.asList(16,17,19,27,28,29)))); //20
    
    places.add(new Place("North Africa", new Location(410,402), new ArrayList<Integer>(Arrays.asList(11,18,19,22,23)))); //21
    places.add(new Place("Egypt", new Location(490,350), new ArrayList<Integer>(Arrays.asList(21,25,19,27))));
    places.add(new Place("Congo", new Location(470,465), new ArrayList<Integer>(Arrays.asList(21,25,24)))); //23
    places.add(new Place("South Africa", new Location(465,553),new ArrayList<Integer>(Arrays.asList(23,25,26))));
    places.add(new Place("East Africa", new Location(512,446), new ArrayList<Integer>(Arrays.asList(22,21,23,24,26,27)))); //25
    places.add(new Place("Madagascar", new Location(540,540), new ArrayList<Integer>(Arrays.asList(24,25))));
    
    places.add(new Place("Middle East", new Location(542,329),new ArrayList<Integer>(Arrays.asList(20,28,32,19,22,25)))); //27
    places.add(new Place("Afghanistan", new Location(596,236), new ArrayList<Integer>(Arrays.asList(20,29,31,32,27))));
    places.add(new Place("Ural", new Location(612,161), new ArrayList<Integer>(Arrays.asList(20,30,31,28)))); //29
    places.add(new Place("Siberia", new Location(658,111),new ArrayList<Integer>(Arrays.asList(36,35,34,31,29)))); //30
    places.add(new Place("China", new Location(720,277),new ArrayList<Integer>(Arrays.asList(34,30,33,32,28,29))));
    places.add(new Place("India", new Location(640,339), new ArrayList<Integer>(Arrays.asList(33,31,28,27)))); //32
    places.add(new Place("Siam", new Location(720,350), new ArrayList<Integer>(Arrays.asList(31,32,40))));
    places.add(new Place("Mongolia", new Location(720,217),new ArrayList<Integer>(Arrays.asList(38,37,35,30,31))));
    places.add(new Place("Inkatsk", new Location(697,160),new ArrayList<Integer>(Arrays.asList(37,36,30,34)))); //35
    places.add(new Place("Yakutsk", new Location(705,81), new ArrayList<Integer>(Arrays.asList(30,35,37))));
    places.add(new Place("Kamchuka", new Location(775,85), new ArrayList<Integer>(Arrays.asList(1,36,35,34,38)))); //37
    places.add(new Place("Japan", new Location(795,231), new ArrayList<Integer>(Arrays.asList(37,34))));
    
    places.add(new Place("New Guinea", new Location(814,433), new ArrayList<Integer>(Arrays.asList(40,0,41))));
    places.add(new Place("Indonesia", new Location(716,460), new ArrayList<Integer>(Arrays.asList(33,39,41)))); //40
    places.add(new Place("Western Australia", new Location(757,552), new ArrayList<Integer>(Arrays.asList(0,40,39))));
  }
  
}
  