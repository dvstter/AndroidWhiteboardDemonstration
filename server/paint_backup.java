import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
public class paint extends JComponent{
    //this is gonna be your image that you draw on
    Image image;
    //this is what we'll be using to draw on
    Graphics2D graphics2D;
    //these are gonna hold our mouse coordinates
    int currentX, currentY, oldX, oldY;
    
    public static void main(String[] args)throws IOException {
        
        String clientSentence;
        String capitalizedSentence;
        
        //this section need modified
        String list[] = {"","",""};
        int counter = 0;
        String k = null;
        
        String command = "";
        String event = "";
        double a = 0.0;
        double b = 0.0;
        int c = 0;
        int color = 0;
        
        int rectX1 = 0;
        int rectX2 = 0;
        int rectY1 = 0;
        int rectY2 = 0;
        
        //Creates a frame with a title of "Paint it"
        JFrame frame = new JFrame("Whiteboard Server by:Minghang He");
        
        //Creates a new container
        Container content = frame.getContentPane();
        
        //sets the layout
        content.setLayout(new BorderLayout());
        
        //creates a new padDraw, which is pretty much the paint program
        final paint drawPad = new paint();
        
        //sets the padDraw in the center
        content.add(drawPad, BorderLayout.CENTER);
        
        //creates a JPanel
        JPanel panel = new JPanel();
        
        //This sets the size of the panel
        panel.setPreferredSize(new Dimension(70, 68));
        panel.setMinimumSize(new Dimension(70, 68));
        panel.setMaximumSize(new Dimension(70, 68));
        
        //creates the clear button and sets the text as "Clear"
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                drawPad.clear();
            }
        });
        
        //creates the red button and sets the icon we created for red
        JButton redButton = new JButton("Red");
        redButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                drawPad.red();
            }
        });
        
        //same thing except this is the black button
        JButton blackButton = new JButton("Black");
        blackButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                drawPad.black();
            }
        });
        
        //magenta button
        JButton magentaButton = new JButton("Magen");
        magentaButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                drawPad.magenta();
            }
        });
        
        //blue button
        JButton blueButton = new JButton("Blue");
        blueButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                drawPad.blue();
            }
        });
        
        //green button
        JButton greenButton = new JButton("Green");
        greenButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                drawPad.green();
            }
        });
        
        //adds the buttons to the panel
        panel.add(greenButton);
        panel.add(blueButton);
        panel.add(magentaButton);
        panel.add(blackButton);
        panel.add(redButton);
        panel.add(clearButton);
        
        //sets the panel to the left
        content.add(panel, BorderLayout.WEST);
        
        //sets the size of the frame
        frame.setSize(1500, 800);
        
        //makes it so you can close
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //makes it so you can see it
        frame.setVisible(true);
 
        ServerSocket welcomeSocket = new ServerSocket(12345);
        welcomeSocket.setSoTimeout(500);
        while(true){
            try{
                Socket connectionSocket = welcomeSocket.accept();
                BufferedReader msg =
                new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                
                k = msg.readLine();
                StringTokenizer st = new StringTokenizer(k,",");
                while(st.hasMoreTokens()){
                    String key = st.nextToken();
                    list[counter] = new String(key);
                    counter++;
                }
                
                a = Double.parseDouble(list[0]);
                b = Double.parseDouble(list[1]);
                color = Integer.parseInt(list[2]);
                
                //set color same with the client
                drawPad.changeColor((color&0x00ff0000)>>16, (color&0x0000ff00)>>8, (color&0x000000ff));
                
                if(c == 2){
                    drawPad.oldX = (int)a;
                    drawPad.oldY = (int)b;
                    drawPad.graphics2D.drawLine(drawPad.oldX, drawPad.oldY, drawPad.oldX, drawPad.oldY);
                    c--;
                }
                if(c == 0 || c == 1){
                    drawPad.currentX = (int)a;
                    drawPad.currentY = (int)b;
                    
                    if(drawPad.graphics2D != null ){
                        drawPad.graphics2D.setStroke(new BasicStroke(5));
                        drawPad.graphics2D.drawLine(drawPad.oldX, drawPad.oldY, drawPad.currentX, drawPad.currentY);
                        drawPad.repaint();
                        
                        drawPad.oldX = drawPad.currentX;
                        drawPad.oldY = drawPad.currentY;
                    }
                    else
                        c -- ;
                }
                counter = 0;
            }
            catch(Exception e){
                c = 2;
            }
        }
    } // end of main
    
    //Now for the constructors*/
    public paint(){
        //getX()
        setDoubleBuffered(false);
        
        //if the mouse is pressed it sets the oldX & oldY
        //coordinates as the mouses x & y coordinates
        addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                oldX = e.getX();
                oldY = e.getY();
            }
            
        });
        
        //while the mouse is dragged it sets currentX & currentY as the mouses x and y
        //then it draws a line at the coordinates
        //it repaints it and sets oldX and oldY as currentX and currentY
        addMouseMotionListener(new MouseMotionAdapter(){
            public void mouseDragged(MouseEvent e){
                currentX = e.getX();
                currentY = e.getY();
                if(graphics2D != null)
                    graphics2D.drawLine(oldX, oldY, currentX, currentY);
                repaint();
                oldX = currentX;
                oldY = currentY;
            }
        });
    }
    
    //this is the painting bit
    //if it has nothing on it then
    //it creates an image the size of the window
    //sets the value of Graphics as the image
    //sets the rendering
    //runs the clear() method
    //then it draws the image
    public void paintComponent(Graphics g){
        if(image == null){
            image = createImage(getSize().width, getSize().height);
            graphics2D = (Graphics2D)image.getGraphics();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            clear();
        }
        g.drawImage(image, 0, 0, null);
    }
    
    //this is the clear
    //it sets the colors as white
    //then it fills the window with white
    //thin it sets the color back to black
    public void clear(){
        
        graphics2D.setPaint(Color.white);
        
        graphics2D.fillRect(0, 0, getSize().width, getSize().height);
        
        graphics2D.setPaint(Color.black);
        
        repaint();
        
    }
    
    //this can change any color
    public void changeColor(int red, int green, int blue) {
        Color color = new Color(red, green, blue);
        graphics2D.setPaint(color);
        repaint();
    }
    
    //this is the red paint
    public void red(){
        graphics2D.setPaint(Color.red);
        repaint();
    }
    
    //black paint
    public void black(){
        graphics2D.setPaint(Color.black);
        repaint();
    }

    //magenta paint
    public void magenta(){
        graphics2D.setPaint(Color.magenta);
        repaint();
    }
    
    //blue paint
    public void blue(){
        graphics2D.setPaint(Color.blue);
        repaint();
    }
    
    //green paint
    public void green(){
        graphics2D.setPaint(Color.green);
        repaint();
    }
} //end of class
