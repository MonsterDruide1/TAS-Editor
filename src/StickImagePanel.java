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

    private final JSpinner xSpinner;
    private final JSpinner ySpinner;
    private final JSpinner radiusSpinner;
    private final JSpinner angleSpinner;

    private StickPosition stickPosition;

    private BufferedImage stickImage;

    private final int STICK_IMAGE_SIZE = 200;
	private final double RADIUS = STICK_IMAGE_SIZE / 2.3;

	public StickImagePanel(StickPosition stickPosition) {

		this.stickPosition = stickPosition;

		try {
			stickImage = ImageIO.read(new File("Pictures/stick.png"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		addMouseListener(this);
		addMouseMotionListener(this);

		SpinnerModel xModel = new SpinnerNumberModel(0, -32767, 32767, 100);
        SpinnerModel yModel = new SpinnerNumberModel(0, -32767, 32767, 100);
        SpinnerModel radiusModel = new SpinnerNumberModel(0.5, 0, 1, 0.1);
        SpinnerModel angleModel = new SpinnerNumberModel(0, 0, 360, 1);

        xSpinner = new JSpinner(xModel);
        ySpinner = new JSpinner(yModel);
        radiusSpinner = new JSpinner(radiusModel);
        angleSpinner = new JSpinner(angleModel);

        xSpinner.addChangeListener(e -> {
            stickPosition.setX((int) xSpinner.getValue());
            repaint();
        });
        ySpinner.addChangeListener(e -> {
            stickPosition.setY((int) ySpinner.getValue());
            repaint();
        });
        radiusSpinner.addChangeListener(e -> {
            stickPosition.setRadius((double) radiusSpinner.getValue());
            repaint();
        });
        angleSpinner.addChangeListener(e -> {
            stickPosition.setTheta(Math.toRadians((int) angleModel.getValue()));
            repaint();
        });

        add(xSpinner);
        add(ySpinner);
        add(radiusSpinner);
        add(angleSpinner);
    }


	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.drawImage(stickImage, 0, 0, STICK_IMAGE_SIZE, STICK_IMAGE_SIZE, this);

		g.setColor(new Color(255, 0, 0));
//        System.out.println("Stick position: " + stickPosition);
        // https://stackoverflow.com/q/929103/
        int x = (stickPosition.getX() + 32767) * STICK_IMAGE_SIZE / 65534;
        int y = (stickPosition.getY() + 32767) * STICK_IMAGE_SIZE / 65534;
        g.fillOval(x - (int) RADIUS / 5, y - (int) RADIUS / 5, (int) RADIUS / 5, (int) RADIUS / 5);

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		updateStickPosition(e);
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
		updateStickPosition(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

	private void updateStickPosition(MouseEvent e) {
        int x = (e.getX() * 65534 / STICK_IMAGE_SIZE) - 32767;
        int y = (e.getY() * 65534 / STICK_IMAGE_SIZE) - 32767;

        xSpinner.setValue(x);
        ySpinner.setValue(y);
        radiusSpinner.setValue(stickPosition.getRadius());
        angleSpinner.setValue((int) Math.toDegrees(stickPosition.getTheta()));

		repaint();
	}
}
