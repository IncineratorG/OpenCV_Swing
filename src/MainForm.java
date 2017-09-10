import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.ml.LogisticRegression;
import org.opencv.ml.SVM;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;


public class MainForm {
    private JPanel panel;
    private JButton button;
    private JLabel videoLabel;
    private JLabel imageLabel;

    private static JFrame staticFrame;

    private static VideoCapture videoCapture;
    private static VideoThread videoThread;

//    private static final String VIDEO_FOLDER = "C:\\Empty files\\video_2.avi";
    private static final String VIDEO_FOLDER = "C:\\Empty files\\VIDEOS\\video_2.avi";
    private static final String IMAGE_FOLDER = "C:\\Empty files\\image_2.jpg";


    public static void main(String[] args) {
        try {
//            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            System.load("C:\\OpenCV_3.3.0\\build\\java\\x64\\opencv_java330.dll");
        } catch (UnsatisfiedLinkError e) {
            JFrame frame = new JFrame("UNSATISFIED");
            frame.setContentPane(new MainForm().panel);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);

            return;
        }

        JFrame frame = new JFrame("MainForm");
        frame.setContentPane(new MainForm().panel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        staticFrame = frame;

        staticFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("CLOSE");

                if (videoCapture != null) {
                    if (videoCapture.isOpened())
                        videoCapture.release();
                }
            }
        });
    }

    public MainForm() {
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("CLICKED");
                startVideo();
                setImage();
            }
        });


    }

    private void startVideo() {
        System.out.println("SET_VIDEO_TO_LABEL");

//        VideoCapture videoCapture = new VideoCapture(VIDEO_FOLDER);
        videoCapture = new VideoCapture(0);

        videoThread = new VideoThread(videoCapture, videoLabel, imageLabel, staticFrame);

        Thread thread = new Thread(videoThread);
        thread.setDaemon(true);
        thread.start();
    }

    private void setImage() {
        try {
            BufferedImage bufferedImage = ImageIO.read(new File(IMAGE_FOLDER));

            Image image = bufferedImage.getScaledInstance(imageLabel.getWidth(), imageLabel.getHeight(), Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(image));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



