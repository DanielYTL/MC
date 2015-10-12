package usefull;
//********************************************************

//Example : loop through a set of image files

//there are several ways to do this, but this one is simple 
//to setup

//Author : Toby Breckon, toby.breckon@durham.ac.uk

//Copyright (c) 2015 Durham University
//License : LGPL - http://www.gnu.org/licenses/lgpl.html

//version 0.2

//********************************************************

//import required OpenCV components

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG;

import com.sun.rowset.internal.Row;

import java.awt.image.PixelGrabber;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

//********************************************************

public class LoopImageFiles {
	
 public static void main(String[] args) throws InterruptedException {

     // define the path to my files - clearly and obviously 
 	
     // **** CHANGE THIS TO YOUR OWN DIRECTORY ****/
     
     String IMAGE_FILES_DIRECTORY_PATH = "files/combinations/";
 	
     // load the Core OpenCV library by name

     System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
 	
 	 // create a display window using an Imshow object

     Imshow ims = new Imshow("Next Image ...");
     
     Size frame = new Size(704, 480);
     
     List<Mat> backgroundImg = new ArrayList<Mat>();
     backgroundImg.add(Highgui.imread("files/1.png"));
     backgroundImg.add(Highgui.imread("files/2.png"));
     backgroundImg.add(Highgui.imread("files/3.png"));
     backgroundImg.add(Highgui.imread("files/4.png"));
     backgroundImg.add(Highgui.imread("files/5.png"));
     backgroundImg.add(Highgui.imread("files/6.png"));
     backgroundImg.add(Highgui.imread("files/7.png"));
     
     for(Mat bgImg:backgroundImg)
     {
    	 Mat grey = new Mat();
         Imgproc.cvtColor(bgImg, grey, Imgproc.COLOR_BGR2GRAY);
         Imgproc.adaptiveThreshold(grey, bgImg, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, 
    				Imgproc.THRESH_BINARY_INV, 7, 10);
         
         Size ksize = new Size(21,21);
         Mat kernel =  Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, ksize);
         
         Imgproc.morphologyEx(bgImg, bgImg, Imgproc.MORPH_CLOSE, kernel);
         
         Size filter = new Size(21,21);
         Imgproc.GaussianBlur(bgImg, bgImg, filter, 0, 0, Imgproc.BORDER_DEFAULT); 
     }
     
     BackgroundSubtractorMOG MoG = new BackgroundSubtractorMOG();
     
     List<Mat> fg_masks = new ArrayList<Mat>();
     
     Imshow imsS = new Imshow("input ... ");
     Imshow imsF = new Imshow("background");

     // get a listing of files in that directory
     Imshow ims_diff = new Imshow("Difference");
     File dir = new File(IMAGE_FILES_DIRECTORY_PATH);
     File[] directoryListing = dir.listFiles();
     if (directoryListing != null) {
         for (File imgFile : directoryListing) {
         	
         	// if the file name ends with .jpg (JPEG) or .png (Portable Network Graphic)

         	if ((imgFile.getName().endsWith(".png")) || (imgFile.getName().endsWith(".jpg")))
         	{
         	// load an image from each file (read and decode image file)
                Mat inputImage = new Mat();
                Mat iImg = new Mat();
                Size filter = new Size(15,15);
                iImg = Highgui.imread(IMAGE_FILES_DIRECTORY_PATH + "/" + imgFile.getName());
                Imgproc.resize(iImg, inputImage, frame);
                Imgproc.GaussianBlur(inputImage, inputImage, filter, 0, 0, Imgproc.BORDER_DEFAULT);
                //Imgproc.GaussianBlur(inputImage, inputImage, filter, 0, 0, Imgproc.BORDER_DEFAULT);
                //Imgproc.GaussianBlur(inputImage, inputImage, filter, 0, 0, Imgproc.BORDER_DEFAULT);
                Imgproc.medianBlur(inputImage, inputImage, 9);
                
                Mat fg_mask = new Mat();
                
                Mat fg_mask1 = new Mat();
                Mat fg_mask2 = new Mat();
                Mat fg_mask3 = new Mat();
                Mat fg_mask4 = new Mat();
                Mat fg_mask5 = new Mat();
                Mat fg_mask6 = new Mat();
                Mat fg_mask7 = new Mat();
                
                
         		ArrayList<Integer> countAll = new ArrayList<Integer>();

         		for(int i = 0; i< 7; i++)
                {       		
         			// add it to the background model with a learning rate of 0.1
         			
         			MoG.apply(inputImage, backgroundImg.get(i), 0.1);
             		
         			// extract the foreground mask (1 = foreground / 0 - background), 
             		// and convert/expand it to a 3-channel version of the same 
         			
         			Imgproc.cvtColor(backgroundImg.get(i), backgroundImg.get(i), Imgproc.COLOR_GRAY2BGR);
                
                }
         		
         			// logically AND it with the original frame to extract colour 
         			// pixel only in the foreground regions
         			
         			Core.bitwise_and(inputImage, backgroundImg.get(0), fg_mask1); 
         			Core.bitwise_and(inputImage, backgroundImg.get(1), fg_mask2); 
         			Core.bitwise_and(inputImage, backgroundImg.get(2), fg_mask3); 
         			Core.bitwise_and(inputImage, backgroundImg.get(3), fg_mask4); 
         			Core.bitwise_and(inputImage, backgroundImg.get(4), fg_mask5); 
         			Core.bitwise_and(inputImage, backgroundImg.get(5), fg_mask6); 
         			Core.bitwise_and(inputImage, backgroundImg.get(6), fg_mask7); 
         			
         		
         		for(int i=0;i<7;i++)
         		{
         			countAll.add(countP(backgroundImg.get(i)));
         		}
                
         		
         		double meanPixels = average(countAll);
         		double[] temp = new double[5];
         		for(int i=0;i<5;i++)
         		{
         			temp[i]=Math.abs(countAll.get(i)-meanPixels);
         		}
         		
         		double min = temp[0];
         		int mark = 0;
         		for(int i=1;i<5;i++)
         		{
         			if(temp[i]<min)
         			{
         				mark = i;
         				min = temp[i];
         			}
         		}
         		
         		fg_mask = backgroundImg.get(mark);


         	    // Create a contour ZONE B top-left
                Point[] contourPoints1 = new Point[5];
                contourPoints1[0] = new Point(55,22);
                contourPoints1[1] = new Point(200,22);
                contourPoints1[2] = new Point(330,175);
                contourPoints1[3] = new Point(200,430);
                contourPoints1[4] = new Point(55,110);
                
                // convert it to a java list of OpenCV MatOfPoint
                // objects as this is what the draw function requires
                
                MatOfPoint contour1 = new MatOfPoint(contourPoints1);
                List<MatOfPoint> contourList1 = new ArrayList<MatOfPoint>();
                contourList1.add(contour1);
                
                // Create a contour ZONE C top-right
                Point[] contourPoints2 = new Point[4];
                
                contourPoints2[0] = new Point(55,130);
                contourPoints2[1] = new Point(195,435);
                contourPoints2[2] = new Point(125,570);
                contourPoints2[3] = new Point(55,340);
                
                MatOfPoint contour2 = new MatOfPoint(contourPoints2);
                List<MatOfPoint> contourList2 = new ArrayList<MatOfPoint>();
                contourList2.add(contour2);
                
                
                // Create a contour ZONE A
                Point[] contourPoints3 = new Point[5];
                
                contourPoints3[0] = new Point(420,22);
                contourPoints3[1] = new Point(465,22);
                contourPoints3[2] = new Point(463,295);
                contourPoints3[3] = new Point(190,695);
                contourPoints3[4] = new Point(65,695);
                
                MatOfPoint contour3 = new MatOfPoint(contourPoints3);
                List<MatOfPoint> contourList3 = new ArrayList<MatOfPoint>();
                contourList3.add(contour3);
                
                // Create a contour ZONE C button
                Point[] contourPoints4 = new Point[4];
                
                contourPoints4[0] = new Point(310,545);
                contourPoints4[1] = new Point(463,320);
                contourPoints4[2] = new Point(463,695);
                contourPoints4[3] = new Point(400,695);
                
                MatOfPoint contour4 = new MatOfPoint(contourPoints4);
                List<MatOfPoint> contourList4 = new ArrayList<MatOfPoint>();
                contourList4.add(contour4);
                
                
                // Create a contour ZONE b right
                Point[] contourPoints5 = new Point[3];
                
                contourPoints5[0] = new Point(300,555);
                contourPoints5[2] = new Point(385,695);
                contourPoints5[1] = new Point(200,695);
                
                
                MatOfPoint contour5 = new MatOfPoint(contourPoints5);
                List<MatOfPoint> contourList5 = new ArrayList<MatOfPoint>();
                contourList5.add(contour5);
                
                // Create a contour ZONE RACT
                Point[] contourPoints6 = new Point[4];
                
                contourPoints6[0] = new Point(210,22);
                contourPoints6[1] = new Point(285,22);
                contourPoints6[2] = new Point(275,100);
                contourPoints6[3] = new Point(225,100);
                
                MatOfPoint contour6 = new MatOfPoint(contourPoints6);
                List<MatOfPoint> contourList6 = new ArrayList<MatOfPoint>();
                contourList1.add(contour6);
                
                // Create a contour ZONE RACT
                Point[] contourPoints7 = new Point[4];
                
                contourPoints7[0] = new Point(400,22);
                contourPoints7[1] = new Point(465,22);
                contourPoints7[2] = new Point(465,180);
                contourPoints7[3] = new Point(400,125);
                
                MatOfPoint contour7 = new MatOfPoint(contourPoints7);
                List<MatOfPoint> contourList7 = new ArrayList<MatOfPoint>();
                contourList7.add(contour7);
                
             // Create a contour ZONE RACT
                Point[] contourPoints8 = new Point[3];
                
                contourPoints8[0] = new Point(0,340);
                contourPoints8[1] = new Point(0,695);
                contourPoints8[2] = new Point(150,695);
                
                MatOfPoint contour8 = new MatOfPoint(contourPoints8);
                List<MatOfPoint> contourList8 = new ArrayList<MatOfPoint>();
                contourList8.add(contour8);
         		
                //put point to the list
                List<Point> list1 = new ArrayList<Point>();
                List<Point> list2 = new ArrayList<Point>();
                List<Point> list3 = new ArrayList<Point>();
                List<Point> list4 = new ArrayList<Point>();
                List<Point> list5 = new ArrayList<Point>();
                List<Point> list6 = new ArrayList<Point>();
                List<Point> list7 = new ArrayList<Point>();
                List<Point> list8 = new ArrayList<Point>();
                
                
                Point[] contourPoints10 = new Point[5];
                
                contourPoints10[0] = new Point(22,55);
                contourPoints10[1] = new Point(22,200);
                contourPoints10[2] = new Point(175,330);
                contourPoints10[3] = new Point(430,200);
                contourPoints10[4] = new Point(110,55);
                
                // convert it to a java list of OpenCV MatOfPoint
                // objects as this is what the draw function requires
                
                MatOfPoint contour10 = new MatOfPoint(contourPoints10);
                List<MatOfPoint> contourList10 = new ArrayList<MatOfPoint>();
                contourList10.add(contour10);
                
                // Create a contour ZONE C top-right
                Point[] contourPoints20 = new Point[4];
                
                contourPoints20[0] = new Point(130,55);
                contourPoints20[1] = new Point(435,195);
                contourPoints20[2] = new Point(570,125);
                contourPoints20[3] = new Point(340,55);
                
                MatOfPoint contour20 = new MatOfPoint(contourPoints20);
                List<MatOfPoint> contourList20 = new ArrayList<MatOfPoint>();
                contourList20.add(contour20);
                
                
                // Create a contour ZONE A
                Point[] contourPoints30 = new Point[5];
                
                contourPoints30[0] = new Point(22,420);
                contourPoints30[1] = new Point(22,465);
                contourPoints30[2] = new Point(295,463);
                contourPoints30[3] = new Point(695,190);
                contourPoints30[4] = new Point(695,65);
                
                MatOfPoint contour30 = new MatOfPoint(contourPoints30);
                List<MatOfPoint> contourList30 = new ArrayList<MatOfPoint>();
                contourList30.add(contour30);
                
                // Create a contour ZONE C button
                Point[] contourPoints40 = new Point[4];
                
                contourPoints40[0] = new Point(545,310);
                contourPoints40[1] = new Point(320,463);
                contourPoints40[2] = new Point(695,463);
                contourPoints40[3] = new Point(695,400);
                
                MatOfPoint contour40 = new MatOfPoint(contourPoints40);
                List<MatOfPoint> contourList40 = new ArrayList<MatOfPoint>();
                contourList40.add(contour40);
                
                
                // Create a contour ZONE b right
                Point[] contourPoints50 = new Point[3];
                
                contourPoints50[0] = new Point(555,300);
                contourPoints50[2] = new Point(695,385);
                contourPoints50[1] = new Point(695,200);
                
                
                MatOfPoint contour50 = new MatOfPoint(contourPoints50);
                List<MatOfPoint> contourList50 = new ArrayList<MatOfPoint>();
                contourList50.add(contour50);
                
                // Create a contour ZONE RACT
                Point[] contourPoints60 = new Point[4];
                
                contourPoints60[0] = new Point(22,210);
                contourPoints60[1] = new Point(22,285);
                contourPoints60[2] = new Point(100,275);
                contourPoints60[3] = new Point(100,225);
                
                MatOfPoint contour60 = new MatOfPoint(contourPoints60);
                List<MatOfPoint> contourList60 = new ArrayList<MatOfPoint>();
                contourList60.add(contour60);
                
             // Create a contour ZONE RACT
                Point[] contourPoints70 = new Point[4];
                
                contourPoints70[0] = new Point(22,400);
                contourPoints70[1] = new Point(22,465);
                contourPoints70[2] = new Point(180,465);
                contourPoints70[3] = new Point(125,400);
                
                MatOfPoint contour70 = new MatOfPoint(contourPoints70);
                List<MatOfPoint> contourList70 = new ArrayList<MatOfPoint>();
                contourList70.add(contour70);
                
             // Create a contour ZONE RACT
                Point[] contourPoints80 = new Point[3];
                
                contourPoints80[0] = new Point(340,0);
                contourPoints80[1] = new Point(695,0);
                contourPoints80[2] = new Point(695,150);
                
                MatOfPoint contour80 = new MatOfPoint(contourPoints80);
                List<MatOfPoint> contourList80 = new ArrayList<MatOfPoint>();
                contourList80.add(contour80);
                
                Imgproc.drawContours(fg_mask, contourList10, -1, new Scalar(255,255,255), 2);
                Imgproc.drawContours(fg_mask, contourList20, -1, new Scalar(255,255,255), 2);
                Imgproc.drawContours(fg_mask, contourList30, -1, new Scalar(255,255,255), 2);
                Imgproc.drawContours(fg_mask, contourList40, -1, new Scalar(255,255,255), 2);
                Imgproc.drawContours(fg_mask, contourList50, -1, new Scalar(255,255,255), 2);
                Imgproc.drawContours(fg_mask, contourList60, -1, new Scalar(255,255,255), 2);
                Imgproc.drawContours(fg_mask, contourList70, -1, new Scalar(255,255,255), 2);
                Imgproc.drawContours(fg_mask, contourList80, -1, new Scalar(255,255,255), 2);
    
                
                Point point = new Point();
                for(int row=0; row<fg_mask.rows();row++){
                    for(int col=0; col<fg_mask.cols();col++){
                        
                        point = new Point(row, col);
                        // perform point in polygon test
                        MatOfPoint2f contourPoint1f = new MatOfPoint2f(contourPoints1);
                        MatOfPoint2f contourPoint2f = new MatOfPoint2f(contourPoints2);
                        MatOfPoint2f contourPoint3f = new MatOfPoint2f(contourPoints3);
                        MatOfPoint2f contourPoint4f = new MatOfPoint2f(contourPoints4);
                        MatOfPoint2f contourPoint5f = new MatOfPoint2f(contourPoints5);
                        MatOfPoint2f contourPoint6f = new MatOfPoint2f(contourPoints6);
                        MatOfPoint2f contourPoint7f = new MatOfPoint2f(contourPoints7);
                        MatOfPoint2f contourPoint8f = new MatOfPoint2f(contourPoints8);
                        
                        if (Imgproc.pointPolygonTest(contourPoint1f,point, false) >= 0)
                        {
                            list1.add(point);
                        }
                        if (Imgproc.pointPolygonTest(contourPoint2f,point, false) >= 0)
                        {
                            list2.add(point);
                        }
                        if (Imgproc.pointPolygonTest(contourPoint3f,point, false) >= 0)
                        {
                            list3.add(point);
                        }
                        if (Imgproc.pointPolygonTest(contourPoint4f,point, false) >= 0)
                        {
                            list4.add(point);
                        }
                        if (Imgproc.pointPolygonTest(contourPoint5f,point, false) >= 0)
                        {
                            list5.add(point);
                        }
                        if (Imgproc.pointPolygonTest(contourPoint6f,point, false) >= 0)
                        {
                            list6.add(point);
                        }
                        if (Imgproc.pointPolygonTest(contourPoint7f,point, false) >= 0)
                        {
                            list7.add(point);
                        }
                        if (Imgproc.pointPolygonTest(contourPoint8f,point, false) >= 0)
                        {
                            list8.add(point);
                        }
                        
                    }
                   
                }
                

                boolean flag[]={false,false,false,false,false,false,false};

                
                int events = 0;
                String events_name="";
                
                int count1 = 0;
                for(Point p:list1)
                {
                    int x1 = (int) p.x;
                    int y1 = (int) p.y;
                    double[] n = fg_mask.get(x1,y1);
                    double value1 = n[0];
                    if(value1 == 255.0){
                        count1++;
                    }
                }
                System.out.println(" 1 " + count1);
                
                if(count1 > 2000){
                    flag[0] = true;
                }
                
                
                int count2 = 0;
                for(Point p:list2)
                {
                    int x2 = (int) p.x;
                    int y2 = (int) p.y;
                    double[] n = fg_mask.get(x2,y2);
                    double value2 = n[0];
                    if(value2 == 255.0){
                        count2++;
                    }
                }
                System.out.println(" 2 " + count2);
                
                if(count2 > 1500){
                    flag[1] = true;
                }
                
                int count3 = 0;
                for(Point p:list3)
                {
                    int x3 = (int) p.x;
                    int y3 = (int) p.y;
                    double[] n = fg_mask.get(x3,y3);
                    double value3 = n[0];
                    if(value3 == 255.0){
                        count3++;
                    }
                }
                System.out.println(" 3 "+ count3);
                
                if(count3 > 20000){
                	flag[4] = true;
                }
                if(count3 > 4000){
                    flag[2] = true;
                }
                
                int count4 = 0;
                for(Point p:list4)
                {
                    int x4 = (int) p.x;
                    int y4 = (int) p.y;
                    double[] n = fg_mask.get(x4,y4);
                    double value4 = n[0];
                    if(value4 == 255.0){
                        count4++;
                    }
                }
                System.out.println(" 4 " + count4);
                
                if(count4 > 1700){
                    flag[1] = true;
                }
                
                int count5=0;
                for(Point p:list5)
                {
                    int x5 = (int) p.x;
                    int y5 = (int) p.y;
                    double[] n = fg_mask.get(x5,y5);
                    double value5 = n[0];   
                    if(value5 == 255.0){
                        count5++;
                    }
                }
                System.out.println(" 5 " + count5);
                
                if(count5 > 1000){
                    flag[0] = true;
                }
                
                int count6 = 0;
                for(Point p:list6)
                {
                    int x6 = (int) p.x;
                    int y6 = (int) p.y;
                    double[] n = fg_mask.get(x6,y6);
                    double value6 = n[0]; 
                    if(value6 == 255.0){
                        count6++;
                    }
                }
                System.out.println(" 6 " + count6);
                
                if(count6 > 750){
                    flag[3] = true;
                }
                
                int count7 = 0;
                for(Point p:list7)
                {
                    int x7 = (int) p.x;
                    int y7 = (int) p.y;
                    double[] n = fg_mask.get(x7,y7);
                    double value7 = n[0]; 
                    if(value7 == 255.0){
                        count7++;
                    }
                }
                System.out.println(" 7 " + count7);
                
                if(count7 > 1100){
                    flag[5] = true;
                }
                
                int count8 = 0;
                for(Point p:list8)
                {
                    int x8 = (int) p.x;
                    int y8 = (int) p.y;
                    double[] n = fg_mask.get(x8,y8);
                    double value8 = n[0]; 
                    if(value8 == 255.0){
                        count8++;
                    }
                }
                System.out.println(" 8 " + count8);
                
                if(count8 > 2000){
                    flag[6] = true;
                }
                
         		imsS.showImage(inputImage);
         		imsF.showImage(fg_mask);
         		imsF.showImage(backgroundImg.get(0));
         		System.out.print(imgFile.getName()+" ");
         		
         		
         		//Identifying events
         		if((!flag[0]) && (!flag[1]) && (!flag[2]) && (!flag[3]) && (!flag[4])){
         			System.out.print("Empty ");
         		}else if(flag[4]||(!flag[4]&&flag[5])||(!flag[4]&&flag[6])){
         			System.out.print("Train ");
         		}
         		
         		if(flag[0]&&!(flag[4]||(!flag[4]&&flag[5])||(!flag[4]&&flag[6]))){
         			System.out.print("Enter ");
         		}
         		
         		if(flag[1]&&!(flag[4]||(!flag[4]&&flag[5])||(!flag[4]&&flag[6]))){
         			System.out.print("Leave ");
         		}
         		
         		if((flag[2]&&(!flag[5])&&(!flag[6]))&&!(flag[3]&&(!(flag[4]||(!flag[4]&&flag[5])||(!flag[4]&&flag[6]))))){
         			System.out.print("On track ");
         		}
         		
         		if(flag[3]&&(!(flag[4]||(!flag[4]&&flag[5])||(!flag[4]&&flag[6])))){
         			System.out.print("Barrier");
         		}
         		
         		System.out.println();
         		
         		Thread.sleep(40);
         	}
         }
       } else {
         
     	  System.out.println( "Could not get listing for directory: " + IMAGE_FILES_DIRECTORY_PATH);
       }
 }
 
 public static double average(ArrayList<Integer> data) {  
	    int sum = 0;
	    double ave;
	    data.remove(Collections.max(data));
	    data.remove(Collections.min(data));

	    for(int i=0; i < data.size()-1; i++){
	        sum = sum + data.get(i);
	    }
	    ave = (double)sum/data.size();
	    
	    if(ave>50000)
	    {
	    	ave=(double)Collections.min(data);
	    }
	    if(ave<15000)
	    {
	    	ave=(double)Collections.max(data);
	    }
	    return ave;    
	}
 
 public static int countP(Mat f){
	 int countAll=0;
	 for(int m = 0; m < f.cols(); m++){
			for(int n = 0; n < f.rows(); n++){
				 double[] t = f.get(n, m);
              double value2 = t[0];
              if(value2 == 255.0){
                  countAll++;
              }
			}
		}
	 return countAll;
 }
}

//********************************************************
