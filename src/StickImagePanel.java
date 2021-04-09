import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class StickImagePanel extends JPanel implements MouseListener, MouseMotionListener {


    private BufferedImage stickImage;

    private int visualX, visualY;

    private StickPosition stickPosition;

    private final int STICK_IMAGE_SIZE = 200;
    private final double RADIUS = STICK_IMAGE_SIZE/2.3;


    public StickImagePanel (StickPosition stickPosition){

        this.stickPosition = stickPosition;

        visualX = (int)(((stickPosition.getX()/(double)StickPosition.getMaxSize())*(RADIUS)) + RADIUS);
        visualY = (int)((-(stickPosition.getY()/(double)StickPosition.getMaxSize())*(RADIUS)) + RADIUS);

        try {
            stickImage = ImageIO.read(new File("Pictures/stick.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        addMouseListener(this);
        addMouseMotionListener(this);

    }




    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(stickImage, 0, 0, STICK_IMAGE_SIZE, STICK_IMAGE_SIZE,this);

        g.setColor(new Color(255, 0, 0));
        g.fillOval(visualX - (int)(RADIUS/10),visualY - (int)(RADIUS/10), (int)RADIUS/5, (int)RADIUS/5);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        update(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        update(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    private void update (MouseEvent e){

        stickPosition.setTheta(stickPosition.getAngle(new Point((int)RADIUS,(int)RADIUS),e));
        System.out.println(stickPosition.getAngle(new Point((int)RADIUS,(int)RADIUS),e));

        repaint();
    }

    private double distToCircle (){
        return Math.sqrt(Math.pow(visualX - RADIUS, 2) + Math.pow(visualY - RADIUS,2)) - RADIUS;
    }
}
