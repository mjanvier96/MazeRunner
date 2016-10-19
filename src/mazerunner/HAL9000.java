/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mazerunner;

/**
 *
 * @author Michael Janvier
 */
public class HAL9000 
{
    HAL9000 pointerBelow;
    private int lookDirection = 0;
    private int xPosition;
    private int yPosition;
    private int lookLeft = 0;
    private int lookRight = 0;
    private int lookStraight = 0;
    
    public void setLookDirection(int look)
    {
        lookDirection = look;
    }
    
    public int getLookDirection()
    {
        return lookDirection;
    }
    
    public void setPositionX(int x)
    {
        xPosition = x;
    }
    
    public int getPositionX()
    {
        return xPosition;
    }
    
    public void setPositionY(int y)
    {
        yPosition = y;
    }
    
    public int getPositionY()
    {
        return yPosition;
    }
    
    public void setLookLeft(int look)
    {
        lookLeft = look;
    }
    
    public int getLookLeft()
    {
        return lookLeft;
    }
    
    public void setLookStraight(int look)
    {
        lookStraight = look;
    }
    
    public int getLookStraight()
    {
        return lookStraight;
    }
    
    public void setLookRight(int look)
    {
        lookRight = look;
    }
    
    public int getLookRight()
    {
        return lookRight;
    }
}
