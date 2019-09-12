public class Location
{
  //the game board is labeled under the Cartesian coordinate system where (0,0) in the study is the top left corner and (23,24) is the bottom right corner
  public int xCoordinate;
  public int yCoordinate;
  
  public Location(int x, int y)
  {
    xCoordinate = x;
    yCoordinate = y;
  }
 
  public int getX()
  {
    return xCoordinate;
  }
  
  public int getY()
  {
    return yCoordinate;
  }
  
  public String toString()
  {
    return "(" + xCoordinate + ", " + yCoordinate + ")";
  }
 
  public boolean closeTo(Location loc)
  {
    return Math.abs(loc.getX()-getX())<25 && Math.abs(loc.getY()-getY())<15;
  }
}