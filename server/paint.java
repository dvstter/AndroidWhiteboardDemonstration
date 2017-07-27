// File Cretor : Hmh

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
public class paint extends JComponent{
    // 用于绘画的Image对象
    Image image;
    
    // 绘画接口
    Graphics2D graphics2D;
    
    // 保存当前的和之前的坐标
    int currentX, currentY, oldX, oldY;
    
    // 控制面板的大小
    public final static int PANEL_SIZE_WIDTH = 70;
    public final static int PANEL_SIZE_HEIGHT = 68;
    
    // 用于实现undo、redo功能的栈
    SizedStack<BufferedImage> undoStack = new SizedStack<BufferedImage>(100);
    SizedStack<BufferedImage> redoStack = new SizedStack<BufferedImage>(100);
    
    // 主函数
    public static void main(String[] args)throws IOException {
        
        String clientSentence;
        String capitalizedSentence;
        
        // 用于保存分隔好后的客户端的命令
        String list[] = {"","","","",""};
        int counter = 0;
        String k = null;
        String tmp = null;
        
        // 用于保存画刷类型
        String brushType = "";
        // 用于保存事件类型
        String event = "";
        double a = 0.0; // x坐标
        double b = 0.0; // y坐标
        int c = 0;
        int color = 0;
        int width;
        int height;
        
        // 创建一个JFrame，用于显示
        JFrame frame = new JFrame("Whiteboard Server by:Minghang He");
        
        // 创建一个Container用于放置控件
        Container content = frame.getContentPane();
        
        content.setLayout(new BorderLayout());
        
        // 创建一个paint对象，这个对象将是绘图程序的核心对象
        final paint drawPad = new paint();
        
        // 把绘图对象放置在中间
        content.add(drawPad, BorderLayout.CENTER);
        
        JPanel panel = new JPanel();
        
        // 设置控制面板的大小
        panel.setPreferredSize(new Dimension(paint.PANEL_SIZE_WIDTH, paint.PANEL_SIZE_HEIGHT));
        panel.setMinimumSize(new Dimension(paint.PANEL_SIZE_WIDTH, paint.PANEL_SIZE_HEIGHT));
        panel.setMaximumSize(new Dimension(paint.PANEL_SIZE_WIDTH, paint.PANEL_SIZE_HEIGHT));
        
        // 创建Clear Button，并设置事件回调函数
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                drawPad.clear();
            }
        });
        
        // 创建Red Button，并设置回调函数
        JButton redButton = new JButton("Red");
        redButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                drawPad.red();
            }
        });
        
        // Black Button
        JButton blackButton = new JButton("Black");
        blackButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                drawPad.black();
            }
        });
        
        // Magenta Button
        JButton magentaButton = new JButton("Magen");
        magentaButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                drawPad.magenta();
            }
        });
        
        // Blue Button
        JButton blueButton = new JButton("Blue");
        blueButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                drawPad.blue();
            }
        });
        
        // Green Button
        JButton greenButton = new JButton("Green");
        greenButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                drawPad.green();
            }
        });
        
        // 把所有的Button加进控制面板里面
        panel.add(greenButton);
        panel.add(blueButton);
        panel.add(magentaButton);
        panel.add(blackButton);
        panel.add(redButton);
        panel.add(clearButton);
        
        // 把控制面板放在左边
        content.add(panel, BorderLayout.WEST);
        
        // 设置frame的大小
        frame.setSize(1864, 1085);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
 
        // 创建一个套接字监听来自客户端的请求
        ServerSocket welcomeSocket = new ServerSocket(12345);
        welcomeSocket.setSoTimeout(500);
        while(true){
            try{
                Socket connectionSocket = welcomeSocket.accept();
                
                // 创建一个缓冲区用于接受从客户端传来的信息
                BufferedReader msg = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                
                // 获取一行客户端传来的信息
                k = "";
                while ((tmp = msg.readLine())!=null)
                    k += tmp;
                
                // 创建一个用逗号来分隔字符串的StringTokenizer对象，用于对一行信息参数参数处理
                StringTokenizer st = new StringTokenizer(k,",");
                // 将一行参数信息（利用逗号分隔开的）分隔开来，获取每个信息元
                while(st.hasMoreTokens()){
                    String key = st.nextToken();
                    list[counter] = new String(key);
                    counter++;
                }
                
                // 对每一个参数进行处理
                a = Double.parseDouble(list[0]); // 获取屏幕点按的x坐标
                b = Double.parseDouble(list[1]); // 获取屏幕点按的y坐标
                color = Integer.parseInt(list[2]); // 获取客户端使用的颜色信息
                brushType = list[3]; // 获取客户端使用的画刷种类
                event = list[4]; // 获取客户端的事件宗磊
                
                // 设置服务器的颜色与客户端一致
                drawPad.changeColor((color&0x00ff0000)>>16, (color&0x0000ff00)>>8, (color&0x000000ff));
            
                // 处理服务器传来的各种事件
                // 绘画矩形
                if (event.equals("down") && brushType.equals("rect")) {
                    drawPad.oldX = (int)a;
                    drawPad.oldY = (int)b;
                }
                else if (event.equals("up") && brushType.equals("rect")) {
                    drawPad.saveUndo();
                    
                    drawPad.currentX = (int)a;
                    drawPad.currentY = (int)b;
                    
                    width = drawPad.currentX - drawPad.oldX;
                    height = drawPad.currentY - drawPad.oldY;
                    
                    drawPad.graphics2D.drawRect(drawPad.oldX, drawPad.oldY, width, height);
                }
                
                // 绘画线段
                else if (event.equals("down") && brushType.equals("pen")) {
                    drawPad.oldX = (int)a;
                    drawPad.oldY = (int)b;
                }
                else if (event.equals("move") && brushType.equals("pen")) {
                    drawPad.currentX = (int)a;
                    drawPad.currentY = (int)b;
                    
                    drawPad.graphics2D.drawLine(drawPad.oldX, drawPad.oldY, drawPad.currentX, drawPad.currentY);
                    
                    drawPad.oldX = drawPad.currentX;
                    drawPad.oldY = drawPad.currentY;
                }
                else if (event.equals("up") && brushType.equals("pen")) {
                    drawPad.saveUndo();
                }
                
                // 绘画圆形
                else if (event.equals("down") && brushType.equals("circle")) {
                    drawPad.oldX = (int)a;
                    drawPad.oldY = (int)b;
                }
                else if (event.equals("up") && brushType.equals("circle")) {
                    drawPad.saveUndo();
                    
                    drawPad.currentX = (int)a;
                    drawPad.currentY = (int)b;
                    
                    width = height = (int)Math.sqrt((drawPad.currentX-drawPad.oldX)*(drawPad.currentX-drawPad.oldX)+(drawPad.currentY-drawPad.oldY)*(drawPad.currentY-drawPad.oldY));
                    
                    drawPad.graphics2D.drawOval(drawPad.oldX - width, drawPad.oldY - height, width, height);
                }
                
                // 清除界面
                else if (event.equals("clear")) {
                    drawPad.clear();
                }
                
                // 调整屏幕大小信息
                else if (event.equals("resize")) {
                    width = (int)a + paint.PANEL_SIZE_WIDTH;
                    height = (int)b + paint.PANEL_SIZE_HEIGHT;
                    
                    frame.setSize(width, height);
                }
                
                // undo功能实现
                else if (event.equals("undo")) {
                    drawPad.undo();
                }
                
                // redo功能实现
                else if (event.equals("redo")) {
                    drawPad.redo();
                }
                
                else if (event.equals("flush")) {
                    // 这里什么都不做
                }
                
                counter = 0;
            }
            catch(Exception e){
                c = 2;
            }
        }
    } // 主函数的结束
    
    // paint类的构造函数
    public paint(){
        setDoubleBuffered(false);
        
        // 下面是实现服务器端自己的绘画功能
        
        // 设置鼠标事件的监听，当第一次接触时，保存当时的坐标
        addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                oldX = e.getX();
                oldY = e.getY();
            }
            
        });
        
        // 当拖动的时候，画线，并更新oldX和oldY
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
    
    // 创建Image、graphics对象等
    public void paintComponent(Graphics g){
        if(image == null){
            image = createImage(getSize().width, getSize().height);
            graphics2D = (Graphics2D)image.getGraphics();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            clear();
        }
        g.drawImage(image, 0, 0, null);
    }
    
    // 清除方法
    public void clear(){
        
        graphics2D.setPaint(Color.white);
        
        graphics2D.fillRect(0, 0, getSize().width, getSize().height);
        
        graphics2D.setPaint(Color.black);
        
        repaint();
        
    }
    
    // 这个方法可以设置任何颜色，red、green、blue为一个字节的整数
    public void changeColor(int red, int green, int blue) {
        Color color = new Color(red, green, blue);
        graphics2D.setPaint(color);
        repaint();
    }
    
    // 设置画笔为红色
    public void red(){
        graphics2D.setPaint(Color.red);
        repaint();
    }
    
    // 设置黑色
    public void black(){
        graphics2D.setPaint(Color.black);
        repaint();
    }

    public void magenta(){
        graphics2D.setPaint(Color.magenta);
        repaint();
    }
    
    public void blue(){
        graphics2D.setPaint(Color.blue);
        repaint();
    }
    
    public void green(){
        graphics2D.setPaint(Color.green);
        repaint();
    }
    
    // 实现undo和redo功能的代码
    
    // undo的功能接口
    public void undo() {
        if (!undoStack.empty()) {
            saveRedo();
            BufferedImage tmpImage = undoStack.pop();
            setImage(tmpImage);
        }
    }
    
    // redo的功能接口
    public void redo() {
        if (!redoStack.empty()) {
            saveUndo();
            BufferedImage tmpImage = redoStack.pop();
            setImage(tmpImage);
        }
    }

    // 将当前的image保存在redo中
    public void saveRedo() {
        BufferedImage copyOfImage = copyImage(image);
        saveToStack(redoStack, copyOfImage);
    }
    
    // 将当前的image保存在undo中
    public void saveUndo() {
        BufferedImage copyOfImage = copyImage(image);
        saveToStack(undoStack, copyOfImage);
    }
    
    // 设置新的image
    private void setImage(Image img) {
        graphics2D = (Graphics2D)img.getGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setPaint(Color.black);
        image = img;
        repaint();
    }
    
    // 产生一份儿关于image的深拷贝
    private BufferedImage copyImage(Image img) {
        BufferedImage copyOfImage = new BufferedImage(getSize().width, getSize().height, BufferedImage.TYPE_INT_RGB);
        Graphics g = copyOfImage.createGraphics();
        g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
        return copyOfImage;
    }
    
    // 保存某张图片到栈中，主要是为saveRedo和saveRedo提供功能
    private void saveToStack(SizedStack<BufferedImage> stack, BufferedImage img) {
        BufferedImage imageForStack = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        Graphics2D g2d = imageForStack.createGraphics();
        g2d.drawImage(img, 0, 0, null);
        stack.push(imageForStack);
    }
}
