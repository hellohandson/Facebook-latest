//server
import rekognition.faces.*;
import processing.video.*;
import gab.opencv.*;
import http.requests.*;
import java.awt.Rectangle;
import processing.net.*;
import jp.nyatla.nyar4psg.*;

OpenCV opencv;
Capture cam;
Capture cam2;

Server s;
Client c;
String input;
int data[];

// We will need a smaller image for fast real-time detection
PImage smaller;
PImage img;

int openCVScale = 4; 
float windowScale;    
float scl;           


Rekognition rekog;
FaceDetector detector;
// For the user to type in their name
// This is awkard and needs to be improved
String typed = "";

int vw = 640;
int vh = 480;

String patternPath = "/Users/handson/Documents/Handson/10_code/facebook/facebook_final/api/markers/";
MultiMarker nya;
int cr,cg,cb;
int recogniseMarkerID = 0;

String recogniseName = "";

void setup() {
  size(vw, vh, P3D);

  windowScale = width/float(vw);
  scl = windowScale * openCVScale;

  cam = new Capture(this, vw, vh, "FaceTime HD Camera");
  cam.start();
  
  cam2 = new Capture(this, 640, 480, "Webcam C170", 30);
  cam2.start();

  opencv = new OpenCV(this, vw/openCVScale, vh/openCVScale);
  opencv.loadCascade(OpenCV.CASCADE_FRONTALFACE); 

  smaller = createImage(opencv.width, opencv.height, RGB);
  String[] keys = loadStrings("key.txt");
  String k = keys[0];
  String secret = keys[1];
  rekog = new Rekognition(this, k, secret);
  rekog.setNamespace("demo2");
  rekog.setUserID("processing");

  detector = new FaceDetector();
  
  frameRate(12); 
  s = new Server(this, 12345); 

  nya=new MultiMarker(this,vw, vh,"camera_para.dat",NyAR4PsgConfig.CONFIG_PSG);
  nya.addARMarker("patt.hiro",80); //01
  nya.addARMarker(patternPath+"05.patt",80); //02
  nya.addARMarker(patternPath+"10.patt",80); //03
  nya.addARMarker(patternPath+"15.patt",80); //04
  nya.addARMarker(patternPath+"20.patt",80); //05
  nya.addARMarker(patternPath+"25.patt",80); //06
  nya.addARMarker(patternPath+"30.patt",80); //07
  nya.addARMarker(patternPath+"35.patt",80); //08
  nya.addARMarker(patternPath+"40.patt",80); //09
  nya.addARMarker(patternPath+"45.patt",80); //10
  nya.addARMarker(patternPath+"50.patt",80); //11
  nya.addARMarker(patternPath+"55.patt",80); //12
  nya.addARMarker(patternPath+"60.patt",80); //13
  nya.addARMarker(patternPath+"65.patt",80); //14
  nya.addARMarker(patternPath+"70.patt",80); //15
  nya.addARMarker(patternPath+"75.patt",80); //16
  nya.addARMarker(patternPath+"80.patt",80); //17
  cr=cg=cb=100;
}

void captureEvent(Capture cam2) {
}

void draw() {

  if (cam.available() !=true) {
    return;
  }
  background(0);
  cam.read();
  image(cam, 0, 0);
  smaller.copy(cam, 0, 0, cam.width, cam.height, 0, 0, smaller.width, smaller.height);
  smaller.updatePixels();
  // Scale down video and pass to OpenCV
  
  opencv.loadImage(smaller);

  // Get an array of rectangles and send to the detector
  Rectangle[] faces = opencv.detect();
  detector.detect(faces);

  detector.showFaces();
  detector.checkRequests();
  detector.rollover(mouseX, mouseY);

  if (cam2.available() == true) {
    cam2.read();
  }
  image(cam2, 300, 0, vw*3, vh*3);
  
  nya.detect(cam2);
  nya.drawBackground(cam2);
  
  int tempMarker = 0;
  if(nya.isExistMarker(0)){
    // execute marker scripts here
    //set the recogniseMarkerID to what ever number you want when it recognises the corresponding marker
    //tempMarker = 1;
  }
  for (int i=0; i<17; i++) {
    if (nya.isExistMarker(i)) {
      tempMarker = i+1;
    }
  }

  String tempName = detector.nameString;
  
  if (recogniseMarkerID != tempMarker || recogniseName != tempName) {

    recogniseMarkerID = tempMarker;
    recogniseName = tempName;

    // combining the data to send over the server
    String sendString = "";
    sendString += str(recogniseMarkerID);
    // use the | symbol as a delimiter to separate the data, note that you cannot use that symbol in the face info if not it'll screw up;
    sendString += "|";
    sendString += tempName;
    s.write(sendString);
    println(sendString);
  }
}

void mousePressed() {
  detector.click(mouseX, mouseY);
}

void keyPressed() {

  if (detector.selected) {
    if (key == '\n') {
      detector.enter(typed, true);
      typed = "";
    } 
    else if (key == 8) {
      if (typed.length() > 0) {
        typed = typed.substring(0, typed.length()-1);
      }
      detector.enter(typed, false);
    }
    else if (key > 31 && key < 127) {
      typed = typed + key;
      detector.enter(typed, false);
    }
  }
}



