import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractor;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class VideoThread implements Runnable {

    private VideoCapture videoCapture = null;
    private JLabel videoLabel;
    private JLabel imageLabel;

    private JFrame frame;

    BackgroundSubtractor backgroundSubtractor;
    Mat mask;

    Mat previousFrame;

    int currentLastElementsIndex = 0;
    private double[] lastFiveValues = new double[5];
    private Mat[] lastFiveImages = new Mat[5];


    public VideoThread(VideoCapture videoCapture, JLabel videoLabel, JLabel imageLabel, JFrame frame) {
        this.videoCapture = videoCapture;
        this.videoLabel = videoLabel;
        this.imageLabel = imageLabel;

        this.frame = frame;

        for (int i = 0; i < lastFiveValues.length; ++i)
            lastFiveValues[i] = 0.0;
    }

    @Override
    public void run() {
        Mat mat = new Mat();

        backgroundSubtractor = Video.createBackgroundSubtractorKNN();
        mask = new Mat();
        previousFrame = new Mat();

        while (!Thread.currentThread().isInterrupted()) {
            videoCapture.read(mat);

            if (!mat.empty()) {
                BufferedImage processedImage = processImage_V5(mat);
                showImageInLabel(processedImage, videoLabel.getHeight(), videoLabel.getWidth());
            } else
                break;

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        videoCapture.release();
    }

    private BufferedImage processImage_V5(Mat src) {
        Mat originalImage = new Mat();
        src.copyTo(originalImage);

        Imgproc.pyrDown(src, src, new Size(src.cols() / 2, src.rows() / 2));
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);

        backgroundSubtractor.apply(src, mask, 0.1);

        double val = 0;
        for (int i = 0; i < mask.rows(); ++i) {
            for (int j = 0; j < mask.cols(); ++j){
                val = val + mask.get(i, j)[0];
            }
        }

        lastFiveValues[currentLastElementsIndex] = val;
        lastFiveImages[currentLastElementsIndex] = originalImage;
        currentLastElementsIndex = currentLastElementsIndex == 4 ? 0 : ++currentLastElementsIndex;

        double averageValue = 0.0;
        boolean saveScreenshot = true;
        for (int i = 0; i < lastFiveValues.length; ++i) {
            if (lastFiveValues[i] == 0) {
                saveScreenshot = false;
                break;
            }

            averageValue = averageValue + lastFiveValues[i];
        }
        averageValue = averageValue / lastFiveValues.length;

        if (averageValue < 100000)
            saveScreenshot = false;

        if (val > 1000000 && !saveScreenshot)
            saveScreenshot(originalImage, val);

        if (saveScreenshot)
            saveScreenshot(originalImage, val);
        

        return toBufferedImage(mask);
    }

    private void saveScreenshot(Mat originalImage, double value) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd HH_mm_ss");
        Date date = new Date();

        String path = "C:\\Empty files\\IMAGES\\" + dateFormat.format(date) + "-" + value + ".jpg";

        Imgcodecs.imwrite(path, originalImage);
    }

//    private BufferedImage processImage_V4(Mat src) {
////        backgroundSubtractor.apply(src, mask);
//        Mat originalImage = new Mat();
//        src.copyTo(originalImage);
//
////        Mat erode = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
////        Mat dilate = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
//
//        Imgproc.pyrDown(src, src, new Size(src.cols() / 2, src.rows() / 2));
////
//        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);
////        Photo.fastNlMeansDenoising(src, src);
////        Imgproc.blur(src, src, new Size(2, 2));
////        Imgproc.adaptiveThreshold(src, src, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);
////        double val = Imgproc.threshold(src, new Mat(), 0, 255, Imgproc.THRESH_OTSU);
////        Imgproc.adaptiveThreshold(src, src, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 11, 2);
//
////        Imgproc.erode(src, src, erode);
////        Imgproc.erode(src, src, erode);
////        Imgproc.dilate(src, src, dilate);
////        Imgproc.dilate(src, src, dilate);
//
////        Imgproc.Canny(src, src, 50, 100);
////        backgroundSubtractor.apply(src, mask, 0.1);
////        Imgproc.erode(mask, mask, erode);
////        Imgproc.dilate(mask, mask, dilate);
//
//
//        double val = 0;
//        for (int i = 0; i < mask.rows(); ++i) {
//            for (int j = 0; j < mask.cols(); ++j){
//                val = val + mask.get(i, j)[0];
//            }
//        }
//        System.out.println(val);
//
//        if (val > 10000) {
//            DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd HH_mm_ss");
//            Date date = new Date();
//
//            String path = "C:\\Empty files\\IMAGES\\" + dateFormat.format(date) + ".jpg";
//
//            Imgcodecs.imwrite(path, originalImage);
//        }
//
//
////        System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43
//
//
////        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);
////        Imgproc.threshold(src, src, 50, 255, 0);
////
////        Mat erode = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
////        Mat dilate = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(8, 8));
////
////        Imgproc.erode(src, src, erode);
////        Imgproc.erode(src, src, erode);
////
////        Imgproc.dilate(src, src, dilate);
////        Imgproc.dilate(src, src, dilate);
//
//        return toBufferedImage(mask);
//    }

    private BufferedImage processImage_V3(Mat src) {
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);
        Imgproc.blur(src, src, new Size(2, 2));

//        Mat mask = new Mat();
//        BackgroundSubtractor backgroundSubtractor = Video.createBackgroundSubtractorMOG2();
//        backgroundSubtractor.apply(src, mask);

        return toBufferedImage(src);
    }

    private BufferedImage processImage_V2(Mat src) {
//        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2HSV);

        List<Mat> channels = new ArrayList<>();
        Core.split(src, channels);

        Mat zeros = new Mat(channels.get(0).rows(), channels.get(0).cols(), CvType.CV_8UC1);

        List<Mat> newChannels = new ArrayList<>();
        newChannels.add(channels.get(0));
        newChannels.add(zeros);
        newChannels.add(channels.get(2));

        Mat finalImage = new Mat();

        Core.merge(channels, finalImage);

        return toBufferedImage(finalImage);
    }

    private BufferedImage processImage(Mat src) {
//        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);
//        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2HSV);
//        Imgproc.equalizeHist(src, src);

        java.util.List<Mat> images = new ArrayList<>();
        Core.split(src, images);

        MatOfInt histSize = new MatOfInt(256);
        MatOfInt channels = new MatOfInt(0);
        MatOfFloat histRange = new MatOfFloat(0, 256);

        Mat hist_b = new Mat();
        Mat hist_g = new Mat();
        Mat hist_r = new Mat();

        Imgproc.calcHist(images.subList(0, 1), channels, new Mat(), hist_b, histSize, histRange, false);
        Imgproc.calcHist(images.subList(1, 2), channels, new Mat(), hist_g, histSize, histRange, false);
        Imgproc.calcHist(images.subList(2, 3), channels, new Mat(), hist_r, histSize, histRange, false);

        int hist_w = 150;
        int hist_h = 150;
        int bin_w = (int) Math.round(hist_w / histSize.get(0, 0)[0]);

        Mat histImage = new Mat(hist_h, hist_w, CvType.CV_8UC3, new Scalar(0, 0, 0));

        Core.normalize(hist_b, hist_b, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());
        Core.normalize(hist_g, hist_g, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());
        Core.normalize(hist_r, hist_r, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());

        for (int i = 1; i < histSize.get(0, 0)[0]; ++i) {
            Imgproc.line(histImage, new Point(bin_w * (i - 1), hist_h - Math.round(hist_b.get(i - 1, 0)[0])),
                    new Point(bin_w * (i), hist_h - Math.round(hist_b.get(i, 0)[0])),
                    new Scalar(255, 0, 0), 2, 8, 0);

            Imgproc.line(histImage, new Point(bin_w * (i - 1), hist_h - Math.round(hist_g.get(i - 1, 0)[0])),
                    new Point(bin_w * (i), hist_h - Math.round(hist_g.get(i, 0)[0])),
                    new Scalar(0, 255, 0), 2, 8, 0);

            Imgproc.line(histImage, new Point(bin_w * (i - 1), hist_h - Math.round(hist_r.get(i - 1, 0)[0])),
                    new Point(bin_w * (i), hist_h - Math.round(hist_r.get(i, 0)[0])),
                    new Scalar(0, 0, 255), 2, 8, 0);
        }

        showHistogramInLabel(toBufferedImage(histImage), imageLabel.getHeight(), imageLabel.getWidth());

        BufferedImage bufferedImage = toBufferedImage(src);
        return bufferedImage;
    }

    private void showImageInLabel(BufferedImage bufferedImage, int height, int width) {
        Image image = bufferedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        videoLabel.setIcon(new ImageIcon(image));
    }

    private void showHistogramInLabel(BufferedImage bufferedImage, int height, int width) {
        Image image = bufferedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(image));
    }


    private BufferedImage toBufferedImage(Mat m){
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if ( m.channels() > 1 ) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels() * m.cols() * m.rows();
        byte [] b = new byte[bufferSize];
        m.get(0,0,b); // get all the pixels
        BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }




    //    private BufferedImage processImage(Mat mat) {
//        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);
//
//        BufferedImage bufferedImage = toBufferedImage(mat);
//        return bufferedImage;
//    }
}
