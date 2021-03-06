

import com.googlecode.javacv.FrameGrabber;
import static com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;
import javax.swing.text.View;
import javax.swing.JTextField;


public class MainWindow {

	private JFrame frame;
	FrameGrabber grabber;
	JLabel lblwebcam;
	JLabel lblwebcam2;
	Thread thCam1;
	Thread thView;
	//public CvPoint origin = new CvPoint();
	CvRect[] selection = new CvRect[2];
	//CvRect track_window = new CvRect();
	int p1X, p1Y, p2X, p2Y;
	//boolean markerSet, trackNow, ggg;
	IplImage img1, img2, histimg;
	CvBox2D track_box = new CvBox2D();
	boolean isTracking=false;
	int[] trackObject = new int[2];
	private JLabel lblCam;
	private JLabel lblCam_1;
	private JTextField text_cam1;
	private JTextField text_cam2;
	View3D view;
	int x, y, z;    
	
	public int[] getPosition()
	{	
		return new int[]{x,640-y,480-z};
	}
	
	public int getTrackObject(int id) {
		return trackObject[id];
	}

	public void setTrackObject(int trackObject, int id) {
		this.trackObject[id] = trackObject;
	}

	public boolean isTracking() {
		return isTracking;
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
					
					window.startWebcam();
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}
	
	private void startWebcam() {
		GrabberShow gs = new GrabberShow(this, new int[]{0,1});
		View3D view = new View3D(this);
		
		//view.initialize();
		
		thView = new Thread(view);
		thCam1 = new Thread(gs);
		thCam1.start();
		thView.start();
	}
	
	/*
	private void stopWebcam() {
		thCam1.interrupt();
		thCam1=null;
	}
	*/
	
	public void newFrame(IplImage newFrame, int id, int x, int y)
	{
		
		if(id==0)
		{	
			this.x=x;
			this.z=y;
			lblwebcam.setIcon(new ImageIcon(newFrame.getBufferedImage() ));
			text_cam1.setText(Integer.toString(x) + '/' + Integer.toString(y));
		}
		if(id==1)
		{
			this.y=x;
			//view.setNewPosition(x, y, z);
			lblwebcam2.setIcon(new ImageIcon(newFrame.getBufferedImage() ));
			text_cam2.setText(Integer.toString(x) + '/' + Integer.toString(y));
		}
	}
	
	
	
	CvScalar hsv2rgb(float hue) {
        int[] rgb = new int[3];
        int p, sector;
        int[][] sector_data = {{0, 2, 1}, {1, 2, 0}, {1, 0, 2}, {2, 0, 1}, {2, 1, 0}, {0, 1, 2}};
        hue *= 0.033333333333333333333333333333333f;
        sector = (int) Math.floor(hue);
        p = Math.round(255 * (hue - sector));
        p = p ^ 1;
        int temp = 0;
        if ((sector & 1) == 1) {
            temp = 255;
        } else {
            temp = 0;
        }
        p ^= temp;

        rgb[sector_data[sector][0]] = 255;
        rgb[sector_data[sector][1]] = 0;
        rgb[sector_data[sector][2]] = p;

        return cvScalar(rgb[2], rgb[1], rgb[0], 0);
    }

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(0, 0, 1600, 900);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		//selection = new CvRect();
		
		lblwebcam = new JLabel("Waiting for webcam...");
		lblwebcam2 = new JLabel("Waiting for webcam...");
		lblwebcam.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				
				//markerSet=false;
				p2X=0;
				p2Y=0;
				p1X=e.getX();
				p1Y=e.getY();
				
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				
				p2X=e.getX();
				p2Y=e.getY();
								
				selection[0] = cvRect(p1X, p2Y, p2X-p1X, p1Y-p2Y);
				//selection[1] = cvRect(p1X, p2Y, p2X-p1X, p1Y-p2Y);
				//isTracking=true;
				trackObject[0]=-1;
				//trackObject[1]=-1;
			}
		});
		
		lblwebcam2.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				
				//markerSet=false;
				p2X=0;
				p2Y=0;
				p1X=e.getX();
				p1Y=e.getY();
				
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				
				p2X=e.getX();
				p2Y=e.getY();
								
				selection[1] = cvRect(p1X, p2Y, p2X-p1X, p1Y-p2Y);
				//selection[1] = cvRect(p1X, p2Y, p2X-p1X, p1Y-p2Y);
				isTracking=true;
				//trackObject[0]=-1;
				trackObject[1]=-1;
			}
		});
		lblwebcam.setBorder(new LineBorder(Color.BLACK, 2));
		lblwebcam.setForeground(Color.BLACK);
		lblwebcam.setBackground(Color.WHITE);
		lblwebcam.setBounds(12, 12, 640, 480);
		frame.getContentPane().add(lblwebcam);
		
		
		lblwebcam2.setForeground(Color.BLACK);
		lblwebcam2.setBorder(new LineBorder(Color.BLACK, 2));
		lblwebcam2.setBackground(Color.WHITE);
		lblwebcam2.setBounds(664, 12, 640, 480);
		frame.getContentPane().add(lblwebcam2);
		
		lblCam = new JLabel("Cam1");
		lblCam.setBounds(26, 539, 70, 15);
		frame.getContentPane().add(lblCam);
		
		lblCam_1 = new JLabel("Cam2");
		lblCam_1.setBounds(130, 539, 70, 15);
		frame.getContentPane().add(lblCam_1);
		
		text_cam1 = new JTextField();
		text_cam1.setBounds(26, 566, 56, 19);
		frame.getContentPane().add(text_cam1);
		text_cam1.setColumns(10);
		
		text_cam2 = new JTextField();
		text_cam2.setColumns(10);
		text_cam2.setBounds(130, 566, 56, 19);
		frame.getContentPane().add(text_cam2);
	}

	public CvRect getSelection(int id) {
		return selection[id];
	}
}
