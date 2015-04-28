//client

import processing.video.*;
import processing.net.*;

int videoScale = 10;
int cols, rows;
Capture video;

//String chars = "INITIALIZE" ; 

Client c;
String input ="";
int data[];


PFont f;
String incomingUserData = "";


void setup() {
size(displayWidth, displayHeight);
// Set up columns and rows
  cols = width/videoScale;
  rows = height/videoScale;
  video = new Capture(this,cols,rows,30);

  video.start(); 

  f = createFont("Courier",20,true);
  textFont(f);
  
   frameRate(5); // Slow it down a little
  // Connect to the server's IP address and port
  c = new Client(this, "127.0.0.1", 12345); // Replace with your server's IP and port

}

void draw() {
  background(0);
  


  // Read image from the camera
  if (video.available()) {
    video.read();
  }
  video.loadPixels();

 

      // Receive data from server
  if (c.available() > 0) {
    String incomingInput = c.readString();
    String[] list = split(incomingInput, "|");

    input = removeUnderscore(list[1]);
    input = input.toUpperCase();
    input = removeUnderscore2(input);
  }

  int charcount = 0;
    // Begin loop for rows
  if (input.length() > 0 ) {
    for (int j = 0; j < rows; j ++ ) {
      // Begin loop for columns
      for (int i = 0; i < cols; i ++ ) {

        // Where are we, pixel-wise?
        int x = i*videoScale;
        int y = j*videoScale;
   
        float b = brightness(video.pixels[i + j*video.width]);
        float fontSize = 15 * (b / 255);
        textSize(fontSize);
        String chars = input;
        text(chars.charAt(charcount),x,y);

        // Go on to the next character
        charcount = (charcount + 1) % chars.length();
      } 

    }
  }
}

String removeUnderscore (String input) {
  return input.replace("_", "");
}

String removeUnderscore2 (String input) {
  return input.replace(".", "");
}

