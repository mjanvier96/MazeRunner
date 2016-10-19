/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor

********************** MAZEBOARD VALUE REFERENCE **********************
0 - path
1 - wall 
2 - current bot 
3 - end point
4 - fastest path (correct path)
5 - current path
6 - incorrect path
7 - start point 
 */
package mazerunner;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
/**
 *
 * @author Michael Janvier
 */
public class MazeBoard extends JPanel implements MouseListener, MouseMotionListener, KeyListener
{

    /************* Variable Dictionary *************
     * boardData - 
     * currentBot - 
     * image - *top secret*
     * faceDirection - the direction the bot faces 
     * mazeSize - side length maze is square 
     * player - used in multi-player and is info for second bot
     */

    private HAL9000 currentBot;
    private Player player;
    private int faceDirection = 0;
    private final int mazeSize;
    private final int boardData[][];
    private BufferedImage image;
    
    public void updatePlayer(Player setPlayer)
    {
        player = setPlayer;
        // sets new info for second bot in multi-player
        repaint();
    }
    
    public void setCurrentBot(HAL9000 bot)
    {
        /************* Variable Dictionary *************
         * bot - updated bot info 
         */
        //sets new bot info for local player in multiplayer user controled (not AI controlled)
    	currentBot = bot;
    }
    
    public HAL9000 getCurrentBot()
    {
    	return currentBot;
        //sends current bot info that is updated if user controlled
    }
    
    public void setFaceDirection(int direction)
    {
        /************* Variable Dictionary *************
         * direction - updated bot face direction
         */
        faceDirection = direction;
        repaint();
    }
    
    public int getFaceDirection()
    {
        return faceDirection;
    }
    
    public MazeBoard(int[][] multiplayerBoard)
    {
        /************* Variable Dictionary *************
         * multiplayerBoard - board received from server
         */
        super();
        mazeSize = multiplayerBoard.length;
        boardData = multiplayerBoard;
        currentBot = new HAL9000();
        try {                
            image = ImageIO.read(new File("src/ThePimp.jpg"));
         } catch (IOException ex) {
              // handle exception...
         }
    }
    
    public MazeBoard(int x, int y, int size)
    {
        /************* Variable Dictionary *************
         * creator - new maze generator 
         * size - board size
         */
        super();
        mazeSize = size;
        MazeGenerator creator = new MazeGenerator();
        boardData = creator.RandomMaze(x, y, mazeSize);
        try {                
            image = ImageIO.read(new File("src/ThePimp.jpg"));
         } catch (IOException ex) {
              // handle exception...
         }
    }
    
    public void setPosition(int x, int y, int value)
    {
        /************* Variable Dictionary *************
         * value - maze spot value refer to table at top of program
         * x - x position of bot
         * y - y position of bot
         */
        if(getPosition(x, y, mazeSize) != 6 && getPosition(x, y, mazeSize) != 7)
        {
            boardData[x][y] = value;
            //sets the mazeboard value 
            //refer to top of program for table
        }
        repaint();
        //refresh the board
    }
    
    public int getPosition(int x, int y, int mazeSize)
    {
        
        if(x >= 0 && x <= (mazeSize) - 1 && y >= 0 && y <= (mazeSize) - 1)
        {
            //gets board value
            return boardData[x][y];
        }
        else
        {
            //if out of bounds returns a wall
            return 1;
        }
    }
    

      
    @Override
    public void paint(Graphics g)
    {
        /************* Variable Dictionary *************
         * space - width and height of each tile
         */
        System.out.println("Repainting");
        //debugging
        super.paint(g);
        setBackground(Color.BLACK);
        //paints walls
        double space = (double)getWidth()/(double)mazeSize;
        for(int i = 1; i <= mazeSize; i++)
        {
            //draws vertivle lines
            g.drawLine((int)(space*i), 0, (int)space*(i), getHeight());
        }
        for(int x = 1; x <= mazeSize; x++)
        {
            //draws horizontal lines
            g.drawLine(0, (int)space*x, getWidth(), (int)space*x);
        }
        for(int x = 0; x <= mazeSize; x++)
        {
            for(int y = 0; y <= mazeSize; y++)
            {
                //iterates through every tile
                if(getPosition(x, y, mazeSize) == 0)
                {
                    //draws path
                    g.setColor(Color.WHITE);
                    g.fill3DRect((int)((x) * space), (int)((y) * space), getWidth()/mazeSize, getHeight()/mazeSize, true);
                }
                else if(getPosition(x, y, mazeSize) == 2)
                {
                    //draws current bot 
                    System.out.println("Bot is at " + x + ",  " + y);
                    g.drawImage(image, (int)(x * space), (int)(y * space), getWidth()/mazeSize, getHeight()/mazeSize, null);
                    g.setColor(Color.BLUE);
                    //g.fill3DRect((int)(x * space), (int)(y * space), getWidth()/mazeSize, getHeight()/mazeSize, true);
                    g.setColor(Color.GREEN);
                    if(faceDirection == 1)
                    {
                        //draws faces as 1/5 slabs of a differnt color
                        g.fill3DRect((int)(x*space), (int)((y + 1) * space - getHeight()/(mazeSize * 5)), getWidth()/mazeSize, getHeight()/(mazeSize * 5), true);
                    }
                    else if(faceDirection == 2)
                    {
                        g.fill3DRect((int)(((x + 1) * space - getWidth()/(mazeSize * 5))), (int)(y * space), getWidth()/(mazeSize * 5), getHeight()/mazeSize, true);
                    }
                    else if(faceDirection == 3)
                    {
                        g.fill3DRect((int)(x*space), (int)(y * space), getWidth()/mazeSize, getHeight()/(mazeSize * 5), true);
                    }
                    else
                    {
                        g.fill3DRect((int)(x*space), (int)(y * space), getWidth()/(mazeSize * 5), getHeight()/mazeSize, true);
                    }       
                }
                else if(getPosition(x, y, mazeSize) == 3)
                {
                    //draws end point
                    g.setColor(Color.CYAN);
                    g.fill3DRect((int)(x * space), (int)(y * space), getWidth()/mazeSize, getHeight()/mazeSize, true);
                }
                else if(getPosition(x, y, mazeSize) == 4)
                {
                    //draws fastest path 
                    g.setColor(Color.YELLOW);
                    g.fill3DRect((int)(x * space), (int)(y * space), getWidth()/mazeSize, getHeight()/mazeSize, true);
                }
                else if(getPosition(x, y, mazeSize) == 5)
                {
                    //draws current path
                    g.setColor(Color.DARK_GRAY);
                    g.fill3DRect((int)(x * space), (int)(y * space), getWidth()/mazeSize, getHeight()/mazeSize, true);
                }
                else if(getPosition(x, y, mazeSize) == 6)
                {
                    //draws incorrect path
                    g.setColor(Color.RED);
                    g.fill3DRect((int)(x * space), (int)(y * space), getWidth()/mazeSize, getHeight()/mazeSize, true);
                }
                else if(getPosition(x, y, mazeSize) == 7)
                {
                    //draws start point
                    g.setColor(Color.MAGENTA);
                    g.fill3DRect((int)(x * space), (int)(y * space), getWidth()/mazeSize, getHeight()/mazeSize, true);
                }
                if(player != null)
                {
                    //draws optional player 2 for multi-player
                    g.setColor(Color.GREEN);
                    g.fill3DRect((int)(player.getXPosition() * space), (int)(player.getYPosition() * space), getWidth()/mazeSize, getHeight()/mazeSize, true);
                    g.setColor(Color.BLUE);
                    if(player.getFaceDirection() == 1)
                    {
                        g.fill3DRect((int)(player.getXPosition()*space), (int)((player.getYPosition() + 1) * space - getHeight()/(mazeSize * 5)), getWidth()/mazeSize, getHeight()/(mazeSize * 5), true);
                    }
                    else if(player.getFaceDirection() == 2)
                    {
                        g.fill3DRect((int)(((player.getXPosition() + 1) * space - getWidth()/(mazeSize * 5))), (int)(player.getYPosition() * space), getWidth()/(mazeSize * 5), getHeight()/mazeSize, true);
                    }
                    else if(player.getFaceDirection() == 3)
                    {
                        g.fill3DRect((int)(player.getXPosition()*space), (int)(player.getYPosition() * space), getWidth()/mazeSize, getHeight()/(mazeSize * 5), true);
                    }
                    else
                    {
                        g.fill3DRect((int)(player.getXPosition()*space), (int)(player.getYPosition() * space), getWidth()/(mazeSize * 5), getHeight()/mazeSize, true);
                    }  
                }
            }  
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //allows user to edit maze
        
        int x = (int)((double)e.getX()/((double)getWidth()/(double)mazeSize));
                int y = (int)((double)e.getY()/((double)getHeight()/(double)mazeSize));    
                if(getPosition(x, y, mazeSize) == 1 || getPosition(x, y, mazeSize) == 0)
                {
                    //cant change paths that havent been edited yet
                    if(getPosition(x, y, mazeSize) == 0)
                    {
                        setPosition(x, y, 1);
                    }
                    else if(getPosition(x, y, mazeSize) == 1)
                    {
                        setPosition(x, y, 0);
                    }
                } 
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        //cast as double originally so no precision is lost
        int x = (int)((double)e.getX()/((double)getWidth()/(double)mazeSize));
                int y = (int)((double)e.getY()/((double)getHeight()/(double)mazeSize));    
                if(getPosition(x, y, mazeSize) != 3)
                {
                    //sames as click
                    if(getPosition(x, y, mazeSize) == 0)
                    {
                        setPosition(x, y, 1);
                    }
                    else if(getPosition(x, y, mazeSize) == 1)
                    {
                        setPosition(x, y, 0);
                    }
                } 
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
                //translates arrow keys into correct movement of bot
                //only used in multiplater user controlled 
		if(e.getKeyCode() == KeyEvent.VK_UP)
		{
			if(isMove(currentBot.getPositionX(), currentBot.getPositionY() - 1))
			{
				setPosition(currentBot.getPositionX(), currentBot.getPositionY(), 0);
				currentBot.setPositionY(currentBot.getPositionY() - 1);
				faceDirection = 3;
				setPosition(currentBot.getPositionX(), currentBot.getPositionY(), 2);
			}
		}
		else if(e.getKeyCode() == KeyEvent.VK_LEFT)
		{
			if(isMove(currentBot.getPositionX() - 1, currentBot.getPositionY()))
			{
				setPosition(currentBot.getPositionX(), currentBot.getPositionY(), 0);
				currentBot.setPositionX(currentBot.getPositionX() - 1);
				faceDirection = 4;
				setPosition(currentBot.getPositionX(), currentBot.getPositionY(), 2);
			}
		}
		else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
		{
			if(isMove(currentBot.getPositionX() + 1, currentBot.getPositionY()))
			{
				setPosition(currentBot.getPositionX(), currentBot.getPositionY(), 0);
				currentBot.setPositionX(currentBot.getPositionX() + 1);
				faceDirection = 2;
				setPosition(currentBot.getPositionX(), currentBot.getPositionY(), 2);
			}
		}
		else if(e.getKeyCode() == KeyEvent.VK_DOWN)
		{
			if(isMove(currentBot.getPositionX(), currentBot.getPositionY() + 1))
			{
				setPosition(currentBot.getPositionX(), currentBot.getPositionY(), 0);
				currentBot.setPositionY(currentBot.getPositionY() + 1);
				faceDirection = 1;
				setPosition(currentBot.getPositionX(), currentBot.getPositionY(), 2);
			}
		}
		repaint();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean isMove(int x, int y)
	{
		if(getPosition(x, y, mazeSize) < 1 || getPosition(x, y, mazeSize) == 3 || getPosition(x, y, mazeSize) == 7)
                    //only allows certain moves
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
