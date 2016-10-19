package mazerunner;


public class Player implements java.io.Serializable
{
	/**
	 * used for multiplayer object that is sent and received by server and local 
	 */
	private int faceDirection;
	private int xPosition;
	private int yPosition;
	
	public Player(int y, int x, int face)
	{
		yPosition = y;
		xPosition = x;
		faceDirection = face;
	}

	public int getFaceDirection()
	{
		return faceDirection;
	}
	
	public int getXPosition()
	{
		return xPosition;
	}
	
	public int getYPosition()
	{
		return yPosition;
	}
	
}

	


