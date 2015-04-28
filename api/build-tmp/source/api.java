import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import rekognition.faces.*; 
import processing.video.*; 
import gab.opencv.*; 
import http.requests.*; 
import java.awt.Rectangle; 
import processing.net.*; 
import jp.nyatla.nyar4psg.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class api extends PApplet {

//server








OpenCV opencv;
Capture cam;

Server s;
Client c;
String input;
int data[];

// We will need a smaller image for fast real-time detection
PImage smaller;
PImage img;

int openCVScale = 4;  // Scale Capture to OpenCV
float windowScale;    // Window to Capture
float scl;            // Overall scale for drawing faces


Rekognition rekog;
FaceDetector detector;
// For the user to type in their name
// This is awkard and needs to be improved
String typed = "";

int vw = 640;
int vh = 480;

//
// Marker varibles
String patternPath = "/Users/handson/Documents/Handson/10_code/facebook/facebook_final/api/markers/";
MultiMarker nya;
int cr,cg,cb;
int recogniseMarkerID = 0;

String recogniseName = "";

public void setup() {
  size(vw, vh, P3D);

  windowScale = width/PApplet.parseFloat(vw);
  scl = windowScale * openCVScale;

  cam = new Capture(this, vw, vh);
  cam.start();

  // OpenCV object
  opencv = new OpenCV(this, vw/openCVScale, vh/openCVScale);
  opencv.loadCascade(OpenCV.CASCADE_FRONTALFACE); 

  // Scaled down image
  smaller = createImage(opencv.width, opencv.height, RGB);
  // Larger capture object

  // Setting up Rekognition API
  String[] keys = loadStrings("key.txt");
  String k = keys[0];
  String secret = keys[1];
  rekog = new Rekognition(this, k, secret);
  // You can have different databases of faces for different applications
  rekog.setNamespace("demo2");
  rekog.setUserID("processing");

  // A generic time-based face detector
  detector = new FaceDetector();
  
  frameRate(12); // Slow it down a little
  s = new Server(this, 12345); // Start a simple server on a port

  nya=new MultiMarker(this,vw, vh,"camera_para.dat",NyAR4PsgConfig.CONFIG_PSG);
  nya.addARMarker("patt.hiro",80);
  nya.addARMarker(patternPath+"05.patt",80);
  nya.addARMarker(patternPath+"10.patt",80);
  cr=cg=cb=100;
}

public void captureEvent(Capture cam) {
  //cam.read();
}

public void draw() {

  if (cam.available() !=true) {
    return;
  }
  background(0);
  cam.read();
  smaller.copy(cam, 0, 0, cam.width, cam.height, 0, 0, smaller.width, smaller.height);
  smaller.updatePixels();
  // Scale down video and pass to OpenCV
  
  opencv.loadImage(smaller);

  // Get an array of rectangles and send to the detector
  Rectangle[] faces = opencv.detect();
  detector.detect(faces);

  // Draw the faces
  detector.showFaces();
  // Check for any requests to Rekognition API
  detector.checkRequests();
  // Check to see if user is rolling over faces
  detector.rollover(mouseX, mouseY);
  
  
  //image(cam, 0, 0);
  //cam.read();
  nya.detect(cam);
  //background(0);
  nya.drawBackground(cam);
  
  //
  //
  int tempMarker = 0;
  if(nya.isExistMarker(0)){
    // execute marker scripts here
    //set the recogniseMarkerID to what ever number you want when it recognises the corresponding marker
    //tempMarker = 1;
  }
  for (int i=0; i<3; i++) {
    if (nya.isExistMarker(i)) {
      tempMarker = i+1;
    }
  }
  println(str(tempMarker));

  //s.write(name);
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
  }
  
  

}

public void mousePressed() {
  // Check to see if user clicked on a face
  detector.click(mouseX, mouseY);
}

public void keyPressed() {

  // This should really be improved, super basic keyboard input for name
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



// Face Recognizer App
// OpenCV + Rekognition API

// This class keeps track of a Face that is currently on screen
// It can ask Rekognition who it is or tell Rekognition who it is

// It also matches itself with any new faces from OpenCV to guess which
// face is which over time

class Face {

  // Let's not use the rectangle, ints in limiting
  // Rectangle r;
  float x, y, w, h;

  // Am I available?
  boolean available;

  // Should I be deleted?
  boolean delete;

  // How long should I live if I have disappeared?
  int timer = 127;

  // Assign a number to each face
  int id;


  // Keep track of a separate PImage and path to a file
  PImage img;
  String path;

  // Dictionary of name probabilities
  FloatDict matches;

  // User interaction: Am I selected or being rolled over?
  boolean selected = false;
  boolean rollover = false;

  // Who am I really?
  String name = "";
  // Who doe Rekognition think I am?
  String guess = "";

  // Threaded requests to API
  RecognizeRequest rreq;
  TrainRequest treq;



  // Constuctor
  Face(Rectangle r, int faceCount) {
    x = r.x;
    y = r.y;
    w = r.width;
    h = r.height;

    // We are initially available to OpenCV and should not be deleted
    available = true;
    delete = false;
    id = faceCount;

    // Ask Rekognition who I am
    recognize();
  }

  // Save face as an image
  // (This is temporary and overwritten each time)
  public void saveFace(PImage i) {
    img = i;
    path = "faces/face-"+id+".jpg";
    img.save(path);
  }

  // Crop the image into a smaller PImage
  public PImage cropFace(PImage source) {
    PImage img = createImage(PApplet.parseInt(w*openCVScale), PApplet.parseInt(h*openCVScale), RGB);
    img.copy(source, PApplet.parseInt(x*openCVScale), PApplet.parseInt(y*openCVScale), PApplet.parseInt(w*openCVScale), PApplet.parseInt(h*openCVScale), 0, 0, PApplet.parseInt(w*openCVScale), PApplet.parseInt(h*openCVScale));
    img.updatePixels();
    return img;
  }

  // What requests are active and are any done
  public void checkRequests() {
    // Has a rekognition request finished?
    if (rreq != null && rreq.done) {
      // Get the matches and set back to null
      matches = rreq.getMatches();
      rreq = null;
      // As long as it found a match the guess it the first one
      if (matches.size() > 0) {
        guess = matches.keyArray()[0];
      } 
      // Otherwise no matches, set to null to restart checking
      else {
        matches = null;
      }
    }

    // If a training request has completed set that to null and 
    // start a rekognition request
    if (treq != null && treq.done) {
      treq = null;
      recognize();
    }

    // If we have no matches and no active request
    // Let's make a request
    if (matches == null && rreq == null) {
      recognize();
    }
  }

  // Copy the image of the face and start a recognition request
  public void recognize() {
    PImage cropped = cropFace(cam);
    saveFace(cropped);
    rreq = new RecognizeRequest(path);
    rreq.start();
  }


  // make a training request
  public void train() {
    treq = new TrainRequest(path, name);
    treq.start();
  }

  // Display method
  public void display() {
    noFill();
    stroke(255,255,255);
    strokeWeight(1);


    // Fade color out over time
    //fill(0, 0, 255, timer);
    if (rollover) {
      //fill(255, 0, 255, timer);
    } 
    else if (selected) {
      //fill(255, 0, 0, timer);
    }
    // Draw the face
    stroke(0, 0, 255);
    rect(x*scl, y*scl, w*scl, h*scl);
    //fill(255);

    // Draw the ID and guess
    text("id: "+id, x*scl+10, y*scl+30);
    text("Guess: "+guess, x*scl+10, y*scl+45);
    // Send data to other processing sketch
    //s.write(guess);
    //println(guess);
//hello!!!
    if (treq != null) {
      String dots = "";
      for (int i = 0; i < frameCount/15 % 4; i++) {
        dots += ".";
      }
      text("Training"+dots, x*scl+10, y*scl+h*scl-15);   
      // Display info based on selection status
    } 
    else if (selected) {
      text("Enter actual name: " + name, x*scl+10, y*scl+h*scl-15);
    } 
    else if (rollover) {
      text("Click to enter name.", x*scl+10, y*scl+h*scl-15);
    }


    if (rreq != null) {
      String dots = "";
      for (int i = 0; i < frameCount/15 % 4; i++) {
        dots += ".";
      }
      text("Loading"+dots, x*scl+10, y*scl+75);
      // Display matches and guess info
    } 
    else if (matches != null) {
      String display = "";
      for (String key : matches.keys()) {
        float likely = matches.get(key);
        display += key + ": " + PApplet.parseInt(likely*100) + "%\n";
        // We could also get Age, Gender, Smiling, Glasses, and Eyes Closed data like in the FaceDetect example
        text(display, x*scl+10, y*scl+75);
      }
    }
  }

  // Methods below are for keeping track of rectangles over time

  // Give me a new location / size
  // Oooh, it would be nice to lerp here!
  public void update(Rectangle newR) {
    //r = (Rectangle) newR.clone();
    x = lerp(x, newR.x, 0.1f);
    y = lerp(y, newR.y, 0.1f);
    w = lerp(w, newR.width, 0.1f);
    h = lerp(h, newR.height, 0.1f);

    // If it lives you should get a new timer
    timer = 127;
  }

  // Count me down, I am gone
  public void countDown() {
    timer--;
  }

  // I am dead, delete me
  public boolean dead() {
    if (timer < 0) return true;
    return false;
  }

  // Check it mouse is inside
  public boolean inside(float px, float py) {
    px = px/scl;
    py = py/scl;
    return (px > x && px < x + w && py > y && py < y + h);
  }

  // Set rollover to true or false
  public void rollover(boolean b) {
    rollover = b;
  }


  // Set selection
  public void selected(boolean b) {
    selected = b;
    if (selected) {
      name = ""; 
    }
  }  

  // Set name
  public void setName(String s) {
    name = s;
  }
}

// Face Recognizer App
// OpenCV + Rekognition API

// This class keeps track of the list of faces on screen
// It also knows which faces are new this frame for recognition

class FaceDetector {
  // A list of my Face objects
  ArrayList<Face> faceList;
  ArrayList<Face> newFaces;

  // How many have I found over all time
  int faceCount = 0;

  // Is a face selected?
  boolean selected = false;

  String nameString = "";

  FaceDetector() {
    faceList = new ArrayList<Face>();
    newFaces = new ArrayList<Face>();
  }

  // Update the list of faces based on current Rectangles from OpenCV
  public void detect(Rectangle[] faces) {

    // Assume no new faces
    newFaces.clear();

    // SCENARIO 1: faceList is empty
    if (faceList.isEmpty()) {
      // Just make a Face object for every face Rectangle
      for (int i = 0; i < faces.length; i++) {
        Face f = new Face(faces[i], faceCount);
        newFace(f);
      }
      // SCENARIO 2: We have fewer Face objects than face Rectangles found from OPENCV
    } 
    else if (faceList.size() <= faces.length) {
      boolean[] used = new boolean[faces.length];
      // Match existing Face objects with a Rectangle
      for (Face f : faceList) {
        // Find faces[index] that is closest to face f
        // set used[index] to true so that it can't be used twice
        float record = 50000;
        int index = -1;
        for (int i = 0; i < faces.length; i++) {
          float d = dist(faces[i].x, faces[i].y, f.x, f.y);
          if (d < record && !used[i]) {
            record = d;
            index = i;
          }
        }
        // Update Face object location
        used[index] = true;
        f.update(faces[index]);
      }
      // Add any unused faces
      for (int i = 0; i < faces.length; i++) {
        if (!used[i]) {
          Face f = new Face(faces[i], faceCount);
          newFace(f);
        }
      }
      // SCENARIO 3: We have more Face objects than face Rectangles found
    } 
    else {
      // All Face objects start out as available
      for (Face f : faceList) {
        f.available = true;
      } 
      // Match Rectangle with a Face object
      for (int i = 0; i < faces.length; i++) {
        // Find face object closest to faces[i] Rectangle
        // set available to false
        float record = 50000;
        int index = -1;
        for (int j = 0; j < faceList.size(); j++) {
          Face f = faceList.get(j);
          float d = dist(faces[i].x, faces[i].y, f.x, f.y);
          if (d < record && f.available) {
            record = d;
            index = j;
          }
        }
        // Update Face object location
        Face f = faceList.get(index);
        f.available = false;
        f.update(faces[i]);
      } 
      // Start to kill any left over Face objects
      for (Face f : faceList) {
        if (f.available) {
          f.countDown();
          if (f.dead()) {
            f.delete = true;
          }
        }
      }
    }

    // Delete any that should be deleted
    for (int i = faceList.size()-1; i >= 0; i--) {
      Face f = faceList.get(i);
      if (f.delete) {
        faceList.remove(i);
      }
    }
  }

  // See if we've clicked on a Face
  public void click(float x, float y) {
    for (Face f : faceList) {
      if (f.inside(x, y)) {
        f.selected(true);
        selected = true;
      }
    }
  }

  // See if we are rolling over a Face
  public void rollover(float x, float y) {
    for (Face f : faceList) {
      if (f.inside(x, y)) {
        f.rollover(true);
      } 
      else {
        f.rollover(false);
      }
    }
  }


  // Set the name of the face that is selected
  public void enter(String s, boolean finished) {
    for (Face f : faceList) {
      if (f.selected) {
        f.setName(s);
        if (finished) {
          selected = false;
          f.selected(false);
          f.train();
        }
      }
    }
  }

  // Add a new face to the world
  public void newFace(Face f) {
    faceList.add(f);
    newFaces.add(f);
    faceCount++;
  }


  // Draw all the faces
  public void showFaces() {
    for (Face f : faceList) {
      f.display();
    }
  }
  
  // Check any requests for any faces
  public void checkRequests() {
    //String name = "";
    for (Face f : faceList) {
      f.checkRequests();
      nameString = f.guess;
    }
    if (nameString != "") {
      //s.write(nameString);
      //println(nameString);
    }
  }

}
// Face Recognizer App
// OpenCV + Rekognition API

// This class is a separate thread to recognize a face

class RecognizeRequest extends Thread {
  
  // path to recognize
  String path;
  
  // Is the thread done?
  boolean done;
  
  // What matches are there?
  FloatDict matches;
  
  // Create the request
  RecognizeRequest(String s) {
    path = s;
    matches = new FloatDict();
    done = false;
  }
  
  // Perform the request
  public void run () {
    RFace[] faces = rekog.recognize(path);
    if (faces != null && faces.length > 0) {
      // We are assuming there is just one face in each image
      matches = faces[0].getMatches();
      // Sort by most likely
      matches.sortValuesReverse();
    }
    // Request is complete
    done = true;
  }
  
  // Return the matches
  public FloatDict getMatches() {
    return matches; 
  }

}

// Face Recognizer App
// OpenCV + Rekognition API

// This class is a separate thread to train a face

class TrainRequest extends Thread {
  
  // Path to face image
  String path;
  // Name of face
  String name;
  // Is the thread complete?
  boolean done;

  // Create the request
  TrainRequest (String s, String n) {
    path = s;
    name = n;
    done = false;
  }
  
  // Perform the request
  public void run () {
    rekog.addFace(path, name);

    // We need a second API call to train Rekognition of whatever faces have been added
    // Here it's one face, then train, but you could add a lot of faces before training
    rekog.train();
    
    // The request is complete
    done = true;
  }

}

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "api" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
