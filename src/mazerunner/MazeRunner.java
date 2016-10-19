/*
 * The purpose of the program is to create a random maze and then have a 
 * travel through the maze and find the quickest path to the exit by implementing 
 * stack based memory.  The bot then finds its way back out the maze by using the 
 * memory of the stack.  Multiplayer Also and User control!! YAY
 */
package mazerunner;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
/**
 * @author Michael Janvier
 */




/************** SUPER IMPORTANT LOOK HERE *************
 * BECAUSE OF THE WAY JFRAME PRINTS LEFT AND RIGHT ARE 
 * FLIPPED ON THE SCREEN BUT FOR MEMORY AND PROGRAMMING 
 * PURPOSES LEFT IS STILL LEFT AND SAME WITH RIGHT
 * @author Michael Janvier
 */ 
public class MazeRunner //implements MouseListener//implements Runnable
{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        
        
        //just runs it a 100 to make sure no errors
        for(int i = 1; i <= 100; i++)
        {
            run();
            //(new Thread(new MazeRunner())).start();
        }        
        
    }
    
    //@Override
    public static void run()
    {
        /************* Variable Dictionary *************
         * board - board to receive from server and generate maze eventually
         * currentX - the current x value 
         * currentY - the current y value
         * faceDirection - the direction the bot is current facing
         * inFromServer - the information received from the server about player2
         * ip - IP of the server
         * mazeFrame - the graphics window
         * mazePanel - component to hold frame
         * mazeSize - maze is square and this is length of the sides
         * outToServer - the player info being sent to the server
         * port - IP port to forward connection
         * socket - connection to the server
         * top - the top of the stack
         * xStart - starts all the way on left side of maze as entrance
         * yStart - random start of the y position on the left wall of the maze
         */
        
        // 1 = face is up
        // 2 = face is right
        // 3 = face is down
        // 4 = face is left

        //turning 
        // 1 = left
        // 2 = straight
        // 3 = right
        
        String choice = JOptionPane.showInputDialog("Single player AI (1) \n BOT RACING(2) \n USER RACING (3)");
        
        MazeBoard mazePanel;
        int mazeSize;
        int yStart = 0;
        int xStart = 0;
        int yFinal = 0;
        int xFinal = 0;
        //initialize fields incase need to be pulled from server
        
        ObjectOutputStream outToServer = null;
        ObjectInputStream inFromServer = null;
        Socket socket;
        //server connections
        
        if(choice.equals("2") || choice.equals("3"))
        {
        
        String ip= "10.224.135.234";
        int port = 27000;
        int board[][] = null;
        //IP config
		try {
			socket = new Socket(ip, port);
			outToServer= new ObjectOutputStream(socket.getOutputStream());
			inFromServer= new ObjectInputStream(socket.getInputStream());
                        //sets up in and out connections
		} catch (IOException e) {
			e.printStackTrace();
		}
	try {
                board = (int[][])inFromServer.readObject();
                yStart = (int)inFromServer.readObject();
                xStart = (int)inFromServer.readObject();
                xFinal = (int)inFromServer.readObject();
                yFinal = (int)inFromServer.readObject();
                //requests board from server
            } catch (ClassNotFoundException e) {
			
		e.printStackTrace();
            } catch (IOException e) {
			
                    e.printStackTrace();
            }
        mazePanel = new MazeBoard(board);
        mazeSize = board.length;
        //generates board from server
        }
        else
        {
            //mazeSize = (int)(Math.random()*97) + 4;
            mazeSize = 750;
            //range = 0 to 800 
            //have to add extra to make true value 801 because of the way mazeboard is generated
            xStart = 0;
            yStart = (int)(Math.random()*mazeSize);
            mazePanel = new MazeBoard(xStart, yStart, mazeSize);
            //generates new random maze
            for(int x = 0; x <= mazeSize; x++)
                for(int y = 0; y <= mazeSize; y++)
                {
                    if(mazePanel.getPosition(x, y, mazeSize) == 3)
                    {
                        //finds the end position
                        xFinal = x;
                        yFinal = y;
                    }
                }
        }
        int faceDirection = 2;
        JFrame mazeFrame = new JFrame();
        
        mazeFrame.setSize(800, 800);
        mazeFrame.add(mazePanel);
        mazePanel.addMouseListener(mazePanel);
        mazePanel.addMouseMotionListener(mazePanel);
        //allows user to draw on maze
        mazeFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mazePanel.setPosition(xStart, yStart, 2);
        mazePanel.setFaceDirection(faceDirection);
        //sets starting bot point and always starts facing right
        mazeFrame.setVisible(true);
        
        mazePanel.setPosition(xStart, yStart, 7);
        //sets maze start block and color
   
        HAL9000 top = new HAL9000();
        top.setPositionX(xStart);
        top.setPositionY(yStart);
        top.setLookDirection(faceDirection);
        //creates first item on the stack
        if(choice.equals("1") || choice.equals("2"))
        {
        	top = findExit(top, mazePanel, xFinal, yFinal, faceDirection, mazeSize, outToServer, inFromServer, choice);
        }
        else if(choice.equals("3"))
        {
        	mazeFrame.addKeyListener(mazePanel);
                //adds arrow control of bot
        	mazePanel.setCurrentBot(top);
        	userSolve(mazePanel, choice, outToServer, inFromServer, xFinal, yFinal);
        }
        returnHome(top, xStart, yStart, mazePanel, mazeFrame, mazeSize, choice, outToServer, inFromServer);
        //returns to start
    }
    
    public static HAL9000 findExit(HAL9000 top, MazeBoard mazePanel, int xFinal, int yFinal, int faceDirection,
            int mazeSize, ObjectOutputStream outToServer, ObjectInputStream inFromServer, String choice)
    {
        /************* Variable Dictionary *************
         * temp - the current robot object being operated on - has position and possible moves
         */
        // 1 = face is up
        // 2 = face is right
        // 3 = face is down
        // 4 = face is left
    	int currentX = top.getPositionX();
        int currentY = top.getPositionY();
        HAL9000 temp = new HAL9000();
        temp = setPossibleMoves(temp, mazePanel, faceDirection, currentX, currentY, mazeSize);
        while(temp.getPositionX() != xFinal || temp.getPositionY() != yFinal)
        {
            if(choice.equals("2"))
            {
                Player sendPlayer = new Player(temp.getPositionY(), temp.getPositionX(), temp.getLookDirection());
                Player recievedPlayer;
                try {
                    outToServer.writeObject(sendPlayer);
                    recievedPlayer = (Player)inFromServer.readObject();
                    mazePanel.updatePlayer(recievedPlayer);
                } catch (IOException | ClassNotFoundException e) {
				// TODO Auto-generated catch block
                e.printStackTrace();
                }
            }
            //6 is maze exit
            if(temp.getLookLeft() < 1 || temp.getLookStraight() < 1 || temp.getLookRight() < 1
                    || temp.getLookLeft() == 3 || temp.getLookStraight() == 3 || temp.getLookRight() == 3)
                //Either left, right, and straight is a moves or the end node
            {
                
                //iterates by always following left, straight, right, back algorithm for each node
                //turning 
                // 1 = left
                // 2 = straight
                // 3 = right
            	if(temp.getLookStraight() < 1 || temp.getLookStraight() == 3)
                {
                    faceDirection = changeDirection(faceDirection, 2);
                    //checks straight if it is possible and moves left if so
                }
            	else if(temp.getLookRight() < 1 || temp.getLookRight() == 3)
                {
                    faceDirection = changeDirection(faceDirection, 3);
                    //checks right if it is possible and moves left if so
                }   
            	else if(temp.getLookLeft() < 1 || temp.getLookLeft() == 3)
                {
                    faceDirection = changeDirection(faceDirection, 1);
                    //checks left if it is possible and moves left if so
                }
                /*if(temp.getLookLeft() < 1 || temp.getLookLeft() == 3)
                {
                    faceDirection = changeDirection(faceDirection, 1);
                    //checks left if it is possible and moves left if so
                }
                else if(temp.getLookStraight() < 1 || temp.getLookStraight() == 3)
                {
                    faceDirection = changeDirection(faceDirection, 2);
                    //checks straight if it is possible and moves left if so
                }   
                else if(temp.getLookRight() < 1 || temp.getLookRight() == 3)
                {
                    faceDirection = changeDirection(faceDirection, 3);
                    //checks right if it is possible and moves left if so
                }*/
                
                
                if(mazePanel.getPosition(currentX, currentY, mazeSize) < 7)
                {
                    mazePanel.setPosition(currentX, currentY, 5);
                    //sets previous spot to dark grey to show current path taken
                }
                
                if(faceDirection == 1 || faceDirection == 3)
                {
                    currentY = moveVertically(faceDirection, currentY);    
                    // left/right movement
                }
                else
                {
                    currentX = moveHorizontally(faceDirection, currentX);
                    // up/down movement
                }
                temp = new HAL9000();
                temp = setPossibleMoves(temp, mazePanel, faceDirection, currentX, currentY, mazeSize);
                mazePanel.setFaceDirection(faceDirection);
                temp.setPositionX(currentX);
                temp.setPositionY(currentY);
                temp.setLookDirection(faceDirection);
                //sets new bot position
                top = push(top, temp);
                //adds node to stack       
            }
            else
            {
                //the else if there is no possible move left, straight, or right
                mazePanel.setPosition(currentX, currentY, 6);
                //sets node to be wrong move to exit 
                top = pull(top);
                //removes top node from stack
                temp = top;
                currentX = temp.getPositionX();
                currentY = temp.getPositionY();
                faceDirection = temp.getLookDirection();
                if(temp.getLookStraight() < 1)
                {
                	temp.setLookStraight(1);
                }
                else if(temp.getLookRight() < 1)
                {
                    temp.setLookRight(1);
                }
                else if(temp.getLookLeft() < 1)
                {
                    temp.setLookLeft(1);
                    //since bot is returning to previous spot it always checks in left, straight right order
                    //and 1 means impossible move to make
                }
                
                /*if(temp.getLookLeft() < 1)
                {
                    temp.setLookLeft(1);
                    //since bot is returning to previous spot it always checks in left, straight right order
                    //and 1 means impossible move to make
                }
                else if(temp.getLookStraight() < 1)
                {
                    temp.setLookStraight(1);
                }
                else if(temp.getLookRight() < 1)
                {
                    temp.setLookRight(1);
                }*/
                //this logic works by the when the bot goes to a previous spot 
                //it looks at that previous spots possible moves 
                //in left, straight, right order and elimnates in that order
                //EX: the bot moves straight and hits dead end and 
                //then when it returns it checks left and sees its already a wall
                // but straight is marked possible still so it must be coming back 
                //from straight and marks it as a wall according
                // to the bot.  
                
            }
            mazePanel.setFaceDirection(faceDirection);
            mazePanel.setPosition(currentX, currentY, 2);
            //sets new direction and position for bot
            pause(mazeSize);
        }
        return top;
    }
    
    public static void userSolve(MazeBoard mazePanel, String choice, ObjectOutputStream outToServer, 
            ObjectInputStream inFromServer, int xFinal, int yFinal)
    {
        /************* Variable Dictionary *************
         * currentBot - the current info for the local player
         * recievedPlayer - current player2 bot
         * sendPlayer - the info to be sent to server for the current local bot
         */
    	HAL9000 currentBot = mazePanel.getCurrentBot();
    	while(currentBot.getPositionX() != yFinal || currentBot.getPositionY() != xFinal)
    	{
            //sends and recieves positions which are contantly upadating until maze is solved
            Player sendPlayer = new Player(currentBot.getPositionY(), currentBot.getPositionX(), currentBot.getLookDirection());
            Player recievedPlayer;
            try {
                outToServer.writeObject(sendPlayer);
                recievedPlayer = (Player)inFromServer.readObject();
                mazePanel.updatePlayer(recievedPlayer);
                //send and recieving player positions
            } catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
            e.printStackTrace();
            }
            currentBot = mazePanel.getCurrentBot();
            //updates the current mazebot
    	}
    	
    }
    
    public static void returnHome(HAL9000 top, int xStart, int yStart, MazeBoard mazePanel, JFrame mazeFrame, 
            int mazeSize, String choice, ObjectOutputStream outToServer, ObjectInputStream inFromServer)
    {
        mazePanel.setPosition(top.getPositionX(), top.getPositionY(), 3);
        top = pull(top);
        //sets first return move
        while(top.getPositionX() != xStart || top.getPositionY() != yStart)
        {
        	if(choice.equals("2"))
            {
                Player sendPlayer = new Player(top.getPositionY(), top.getPositionX(), top.getLookDirection());
                Player recievedPlayer;
                try {
                    outToServer.writeObject(sendPlayer);
                    recievedPlayer = (Player)inFromServer.readObject();
                    mazePanel.updatePlayer(recievedPlayer);
                } catch (IOException | ClassNotFoundException e) {
				// TODO Auto-generated catch block
                e.printStackTrace();
                }
            }
            //iterates through all positions left on stack
            mazePanel.setPosition(top.getPositionX(), top.getPositionY(), 4);
            top = pull(top);
            mazePanel.setFaceDirection(top.getLookDirection());
            mazePanel.setPosition(top.getPositionX(), top.getPositionY(), 2);
            pause(mazeSize);
        }
        try
            {
                Thread.sleep(500);
            }
            catch(InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
        //pauses so use can see path before frame is disposed
        mazeFrame.dispose();
    }
    
    public static void pause(int mazeSize)
    {
        /************* Variable Dictionary *************
         * pauseTime - length of pause between moves in milliseconds 
         */
        // scales the pause time for the size of the maze
        // shorter pause for bigger mazes and longer pause for smaller mazes
        int pauseTime;
        if (mazeSize > 150)
        {
            pauseTime = 1;
        }
        else if(mazeSize > 100)
        {
            pauseTime = 1;
        }
        else if(mazeSize > 50)
        {
            pauseTime = 1;
        }
        else
        {
            pauseTime = 200 - mazeSize * 2;
        }
        try
            {
                Thread.sleep(pauseTime);
            }
            catch(InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
    }
    
    public static int changeDirection (int faceDirection, int turn)
    {
        // faces
        // 1 = face is up
        // 2 = face is right
        // 3 = face is down
        // 4 = face is left
        //turning
        // 1 = left
        // 2 = straight
        // 3 = right
        if(turn == 1 && faceDirection == 1)
            {
                faceDirection = 4;
                //facing up and turning left makes it face right
                //so set to 4
            }
            else if(turn == 1)
            {
                faceDirection--;
                //turning left is just going down 1 number in the sequence
            }
            else if(turn == 3 && faceDirection == 4)
            {
                faceDirection = 1;
                //facing left and turning right makes up so set to 1
            }
            else if(turn == 3)
            {
                faceDirection++;  
                //turning right goes 1 up the sequence of face directions 
            }
        return faceDirection;
    }
    
    public static int moveVertically(int faceDirection, int currentY)
    {
        //moving up or down
        if(faceDirection == 1)
        {
            currentY++;
        }
        else
        {
            currentY--;
        }
        return currentY;
    }
    
    public static int moveHorizontally(int faceDirection, int currentX)
    {
        //moving left or right
        if(faceDirection == 2)
        {
            currentX++;
        }
        else
        {
            currentX--;
        }
        return currentX;
    }
    
    public static HAL9000 setPossibleMoves(HAL9000 top, MazeBoard mazePanel, int faceDirection, int x, int y, int mazeSize)
    {
        // 1 = face is up
        // 2 = face is right
        // 3 = face is down
        // 4 = face is left
        
        //sets left, straight, and right according to the way HAL is facing
        if(faceDirection == 1)
        {
            top.setLookLeft(mazePanel.getPosition(x - 1, y, mazeSize));
            top.setLookStraight(mazePanel.getPosition(x, y + 1, mazeSize));
            top.setLookRight(mazePanel.getPosition(x + 1, y, mazeSize));
        }
        else if(faceDirection == 2)
        {
            top.setLookLeft(mazePanel.getPosition(x, y + 1, mazeSize));
            top.setLookStraight(mazePanel.getPosition(x + 1, y, mazeSize));
            top.setLookRight(mazePanel.getPosition(x, y - 1, mazeSize));
        }
        else if(faceDirection == 3)
        {
            top.setLookLeft(mazePanel.getPosition(x + 1, y, mazeSize));
            top.setLookStraight(mazePanel.getPosition(x, y - 1, mazeSize));
            top.setLookRight(mazePanel.getPosition(x - 1, y, mazeSize));
        }
        else
        {
            top.setLookLeft(mazePanel.getPosition(x, y - 1, mazeSize));
            top.setLookStraight(mazePanel.getPosition(x - 1, y, mazeSize));
            top.setLookRight(mazePanel.getPosition(x, y + 1, mazeSize));
        }
        return top;
    }

    public static HAL9000 push(HAL9000 top, HAL9000 temp)
    {
        //pushes object onto stack
        temp.pointerBelow = top;
        top = temp;
        return top;
    }
    
    public static HAL9000 pull(HAL9000 top)
    {
        /************* Variable Dictionary *************
         * deletePointerBecauseDrBrownDoesntBelieveInGarbageCollection - just incase garbage collection doesn't work
         */
        //pulls top object off stack
        if(top.pointerBelow != null){
            HAL9000 deletePointerBecauseDrBrownDoesntBelieveInGarbageCollection = top;
            top = top.pointerBelow;
            deletePointerBecauseDrBrownDoesntBelieveInGarbageCollection = null;
        }
        return top;
    }
}
