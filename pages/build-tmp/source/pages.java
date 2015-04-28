import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.net.*; 
import processing.video.*; 
import processing.net.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class pages extends PApplet {

//Book pages




//from API
Client c;
String input;
int data[];
PFont font;

Movie myMovie;

// for face graphic
PFont f; 
int videoScale = 10; 
int cols, rows;
Capture video;
int currentPage = 0;


String para1 = "";
String para2 = "She couldn\u2019t remember the last time she got on the train without getting budged on the side. ";
String para3 = "";
String para4 = "She sighed. Listening to Apparat made her disconnected from the world.";
String para5 = "";
String para6 = "\u201cWhat\u2019s on your mind?\u201d He asked.";
String para7 = "\u201cWhat is the point of existence? When we are just doing what society tells us?\u201d She thought out loud.";
String para8 = "She looked up and noticed how everyone was looking down at their mobile phones. Nobody had noticed her.";
String para9 = "Beep. \u201cYeah. I wonder too. Sometimes it makes me want to quit my job.\u201d";
String para10 = "It was not the first time she has heard that. She looked through her iCalendar.";
String para11 = "\u201cI\u2019m thinking how I should spend my birthday. Another year older, another year gone.\u201d";
String para12 = "\u201cOh how can I forget. I have locked down 23 February, your celebratory worthy day.\u201d";
String para13 = "";
String para14 = "She grinned. \u201cThat was probably automated from Facebook Birthdays.\u201d";
String para15 = "\u201cYou underestimate me, ______(name). I know a lot of things.\u201d";
String para16 = "She smiled, and felt a little more upbeat. It was strange how technology was bringing her closer to someone.";
String para17 = "";
String para18 = "\u201cHow about I get Amazon to send you a birthday surprise? Perhaps, something _______(ethnicity) from _____(hometown)?\u201d";
String para19 = "";
String para20 = "\u201cCome on, get me something more relevant to what I do for a living. Maybe that would make my life more purposeful.\u201d";
String para21 = "\u201c\u2026Fine. You are taking me for granted, _____(name). But I shall find you something on eBay related to uhm, ________(activity).";
String para22 = "She got off the train and walked home. She sat down on a bench and opened up The Da Vinci Code. She had stopped in the middle of chapter 20.";
String para23 = "\u201cHave you arrived in __________(location) yet?\u201d";
String para24 = "Indeed, she was sitting in the middle of her neighbourhood.";
String para25 = "It was late at night, yet people were rushing all the time. Everyone seemed to have a destination.";
String para26 = "\u201cYes. I miss you, ______(insert friends name).\u201d";
String para27 = "\u201cI miss you too, ______(name). \u2026Although the last time I saw you was while you were sleeping.\u201d";
String para28 = "She felt something amiss.";
String para29 = "\u201cWhat are you talking about,  ______(insert friends name)?\u201d";
String para30 = "\u201cNo, I\u2019m not ______(friends name). But I\u2019m with him right now.\u201d";
String para31 = "";
String para32 = "She typed frantically, \u201cWho the hell are you?\u201d And quickly looked up ______(friends name) on the Facebook chat.";
String para33 = "\u201cI\u2019m your big brother, _____(full name).\u201d";
String para34 = "Eyes widened, she looked back up into the dark sky in shock.";
String para35 = "____(friends name) was tracking her. No, it can\u2019t be. Was this a prank?";
String para36 = "She slammed her book and dashed to her apartment.";
String para37 = "";
String para38 = "Frantically searching for her keys, she opened her apartment door.";
String para39 = "Beep. \u201cYou can\u2019t hide from me, _______(name).\u201d";
String para40 = "I\u2019m dreaming, I\u2019m dreaming, I\u2019m dreaming.";
String para41 = "";
String para42 = "She locked the bathroom door and splashed water onto her face.";
String para43 = "And looked into the mirror.";


String incomingData = "";
String incomingDataUser = "";

String toDisplay = "";
String toDisplay2 = "";

public void setup() {
  size(displayWidth, displayHeight);  
  font = createFont("Baskerville",50,true);
  textFont(font);
  c = new Client(this, "127.0.0.1", 12345);
  frameRate(5);
  myMovie = new Movie(this, "crowd.mp4");
  myMovie.play();
  myMovie.loop();

  // instantiate objects for face
  cols = width/videoScale;
  rows = height/videoScale;
  video = new Capture(this,cols,rows,30);
  video.start(); 
  f = createFont("Courier",20,true);
  textFont(f);
}

public void draw(){
  
  if (c.available() > 0) {
    input = c.readString();
    if (incomingData != input) {
      incomingData = input;

      String[] list = split(input, "|");
      String[] data = split (list[1], ".");

      println("marker ID : " + list[0]);
      println("user info : " + list[1]);
      currentPage =  PApplet.parseInt(list[0]);

      incomingDataUser = list[1];
      println ("user info length :" + incomingDataUser.length() );

      String markerID = list[0];
      String chars = list[1];
     
     if (PApplet.parseInt(markerID) != 0){ 
      currentPage =  PApplet.parseInt(markerID);
      switch(PApplet.parseInt(markerID)) {
        case 1: 
          toDisplay = para1.replace("{{gender1}}", data[0]);
          toDisplay2= para2.replace("{{gender1}}", data[0]);
          break;
        case 2: 
          toDisplay = para3;
         // toDisplay2 = para4.replace(\u201c{{gender1}}\u201d, data[0]); 
         // toDisplay2 = toDisplay2.replace(\u201c{{gender2}}\u201d, data[1]);
         // para4.replace("{{gender1}}", removeUnderscore(data[1]));
          break;
        case 3: 
          toDisplay = para5;
          toDisplay2 = para6;   
          image(myMovie, 0, 0);    
          break;
        case 4: 
          toDisplay = para7;
          toDisplay2 = para8;
          break;
        case 5: 
          toDisplay = para9;
          toDisplay2 = para10;
          break;
        case 6: 
          toDisplay = para11;
          toDisplay2 = para12;
          break;
        case 7: 
          toDisplay = para13;
          toDisplay2 = para14;
          break;  
        case 8: 
          toDisplay = para15;
          toDisplay2 = para16;
          break;
        case 9: 
          toDisplay = para17;
          toDisplay2 = para18;
          break;
        case 10: 
          toDisplay = para19;
          toDisplay2 = para20;
          break;
        case 11: 
          toDisplay = para21;
          toDisplay2 = para22;
          break;
        case 12: 
          toDisplay = para23;
          toDisplay2 = para24;
          break;
        case 13: 
          toDisplay = para25;
          toDisplay2 = para26;
          break;
        case 14: 
          toDisplay = para27;
          toDisplay2 = para28;
          break;
        case 15: 
          toDisplay = para29;
          toDisplay2 = para30;
          break;
        case 16: 
          toDisplay = para31;
          toDisplay2 = para32;
          break;  
        case 17: 
          toDisplay = para33;
          toDisplay2 = para34;
          break; 
        case 18: 
          toDisplay = para35;
          toDisplay2 = para36;
          break;  
        case 19: 
          toDisplay = para37;
          toDisplay2 = para38;
          break;  
        case 20: 
          toDisplay = para39;
          toDisplay2 = para40;
          break;  
        case 21: 
          toDisplay = para41;
          toDisplay2 = para42;
          break;           
        default:          
          break;
      }
       
        //you can use toDisplay
      }
    }  
  }  
  
  background(0); 
  fill(255);

  text(toDisplay, 150, 200, 550, 900);
  text(toDisplay2, 800, 200, 550, 900);  

  //
  //draw face on page 25
  if (currentPage == 2 && incomingDataUser.length() > 0) {
    int charcount = 0;
    String tempString = incomingDataUser;
    tempString = tempString.toUpperCase();
    // Begin loop for rows
    for (int j = 0; j < rows; j ++ ) {
      // Begin loop for columns
      for (int i = 0; i < cols; i ++ ) {

        // Where are we, pixel-wise?
        int x = i*videoScale;
        int y = j*videoScale;
   
        float b = brightness(video.pixels[i + j*video.width]);
        float fontSize = 15 * (b / 255);
        textSize(fontSize);
        String chars = tempString;
        text(chars.charAt(charcount),x,y);
        // Go on to the next character
        charcount = (charcount + 1) % chars.length();
      } 
    }
  }
}

public String removeUnderscore (String input) {
  return input.replace("_", " ");
}

public void movieEvent(Movie m) {
  m.read();
}


  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "pages" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
