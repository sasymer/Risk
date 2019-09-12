import java.awt.*;  //for Graphics
import javax.swing.*;  //for JComponent, JFrame
import java.net.*;  //for URL
import java.util.*;

import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class Place
{
  public String name;
  public int index;
  public Player owner;
  public Location point;
  public int armies;
  public ArrayList<Integer> canAttack;
  
  public Place(String n, Location pt, ArrayList<Integer> attack)
  {
    name = n;
    owner = null;
    point = pt;
    armies = 0;
    canAttack = attack;
    //parse the string into numbers
  }
  
  public String getName()
  {
    return name;
  }
  
  public ArrayList<Integer> canAttack()
  {
    return canAttack;
  }
  
  public Player getOwner()
  {
    return owner;
  }
  
  public void setPlayer(Player p)
  {
    owner = p;
  }
  
  public Location getLoc()
  {
    return point;
  }
  
  public void addArmies(int add)
  {
    armies+=add;
  }
  
  public int getArmies()
  {
    return armies;
  }
  
}