//Book pages
import processing.net.*;
import processing.video.*;
import processing.net.*;
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
String incomingData = "";
String incomingDataUser = "";
String toDisplay = "";
String toDisplay2 = "";
String para1 = "{{gender1}} couldn’t remember the last time {{gender2}} got on the subway without getting elbowed.";
String para2 = "{{gender1}} frowned. Listening to {{music}} made {{gender3}} feel disconnected from the world.";
String para3 = "";
String para4 = "“What’s on your mind?” He asked.";
String para5 = "{{gender1}} looked at {{gender4}} phone. A part of {{gender3}} wished {{gender2}} teleported back to {{hometown}}.";
String para6 = "“What is the point of existence? When we are just doing what society tells us?”";
String para7 = "Beep. “Yeah. I wonder too. Sometimes it makes me want to quit my job.”";
String para8 = "It was not the first time {{gender2}} has heard that.";
String para9 = "“I’m thinking how I should spend my birthday. Can't believe it's another year gone.”";
String para10 = "“Well, you could spend it with me. I have {{birthday}} locked down.”";
String para11 = "{{gender1}} smirked. “Wow I’m impressed, you remember my birthday.”";
String para12 = "“You underestimate me {{nickname}}. I remember a lot of things about you.”";
String para13 = "{{gender1}} started to feel less alone on the crowded subway. “I hate growing old.”";
String para14 = "“That's such a typical {{ethnicity}} mentality.”";
String para15 = "“Haha. At least I'm doing what I love. Maybe that would make my life more purposeful.”";
String para16 = "“Oh yes. Because {{occupation}} does makes the world a better place.”";
String para17 = "{{gender1}} got off the train and walked home.";
String para18 = "“Have you arrived at {{location}} yet?”";
String para19 = "It was late at night, yet people were rushing all the time. Everyone seemed to have a destination.";
String para20 = "“Yes {{friend}}. I can’t wait to see you!”";
String para21 = "“Me too {{nickname}}. Although.. the last time I saw you was while you were sleeping.”";
String para22 = "{{gender1}} felt something amiss. They were never that close for comfort.";
String para23 = "“What are you talking about, {{friend}}?”";
String para24 = "“{{friendgender}}’s not here. But I’m waiting for you.”";
String para25 = "“What? Who are you?”";
String para26 = "“I’m your big brother, {{fullname}}.”";
String para27 = "This must be a joke.";
String para28 = "Searching for {{gender4}} keys, {{gender2}} opened {{gender4}} apartment door. {{friend}} has not arrived yet. “Where are you {{friend}}?”";
String para29 = "Beep. “{{friendgender}}’s not coming.”";
String para30 = "“I’m going to wash up first.” {{gender1}} turned {{gender4}} phone off. The conversation was getting ridiculous.";
String para31 = "{{gender1}} locked the bathroom door and splashed water onto {{gender4}} face.";
String para32 = "Looking into the mirror, someone else was looking back at {{gender3}}.";

String movie1 = "crowd.mp4";
int[] movie1Size = {1440, 810};
int[] movie1Pos = {0, 200};
String movie2 = "lightbulb.mp4";
int[] movie2Size = {500, 540};
int[] movie2Pos = {50, 200};
String movie5 = "candle.mp4";
int[] movie5Size = {250, 300};
int[] movie5Pos = {900, 400};
String movie8 = "birds.mp4";
int[] movie8Size = {1440, 811};
int[] movie8Pos = {0, 0};
String movie10 = "birdsfly.mp4";
int[] movie10Size = {1440, 640};
int[] movie10Pos = {0, 200};
String movie11 = "moon.mp4";
int[] movie11Size = {500, 400};
int[] movie11Pos = {900, 0};
String movie13 = "lightsmoke.mp4";
int[] movie13Size = {1200, 900};
int[] movie13Pos = {0, 0};
String movie16 = "ripple.mp4";
int[] movie16Size = {1440, 672};
int[] movie16Pos = {0, 0};

String currentMovie = "";
String newMovie = "";
int[] movieSize = {640, 480};
int[] moviePos = {0, 0};

void setup() {
  size(displayWidth, displayHeight);  
  font = createFont("Baskerville",50,true);
  textFont(font);
  c = new Client(this, "127.0.0.1", 12345);
  frameRate(30);

  // instantiate objects for face
  cols = width/videoScale;
  rows = height/videoScale;
  video = new Capture(this,cols,rows,30);
  video.start(); 
  f = createFont("Courier",20,true);
  textFont(f);
}

void movieEvent(Movie m) {
  m.read();
}

void draw(){  
  if (c.available() > 0) {
    input = c.readString();
    if (incomingData != input) {
      incomingData = input;
      String[] list = split(input, "|");
      String[] data = split (list[1], ".");
      if (list[0].length() > 0 ) {
        incomingDataUser = list[1];
      }
      String markerID = list[0];
      String chars = list[1];
     if (int(markerID) != 0){ 
      currentPage =  int(markerID);
      switch(int(markerID)) {
        case 1: 
          toDisplay = para1.replace("{{gender1}}", data[0]);
          toDisplay = toDisplay.replace("{{gender2}}", data[1]);
          toDisplay2= para2.replace("{{gender1}}", data[0]);
          toDisplay2= toDisplay2.replace("{{gender3}}", data[2]);
          toDisplay2= toDisplay2.replace("{{music}}", removeUnderscore(data[10]));
          newMovie = movie1;
          movieSize = movie1Size;
          moviePos = movie1Pos;
          break;
        case 2: 
          toDisplay = para3;
          toDisplay2 = para4;
          newMovie = movie2;
          movieSize = movie2Size;
          moviePos = movie2Pos;
          break;
        case 3: 
          toDisplay = para5.replace("{{gender1}}", data[0]);
          toDisplay = toDisplay.replace("{{gender2}}", data[1]);          
          toDisplay = toDisplay.replace("{{gender3}}", data[2]);
          toDisplay = toDisplay.replace("{{gender4}}", data[14]);          
          toDisplay = toDisplay.replace("{{hometown}}", data[7]);
          toDisplay2 = para6;   
          newMovie = "";
          currentMovie = "";
          break;
        case 4: 
          toDisplay = para7;
          toDisplay2 = para8.replace("{{gender2}}", data[1]);
          newMovie = "";
          currentMovie = "";
          break;
        case 5: 
          toDisplay = para9;
          toDisplay2 = para10.replace("{{birthday}}", removeUnderscore(data[5]));
          newMovie = "";
          currentMovie = "";
          newMovie = movie5;
          movieSize = movie5Size;
          moviePos = movie5Pos;
          break;
        case 6: 
          toDisplay = para11.replace("{{gender1}}", data[0]);
          toDisplay2 = para12.replace("{{nickname}}", removeUnderscore(data[4]));
          newMovie = "";
          currentMovie = "";
          break;
        case 7: 
          toDisplay = para13.replace("{{gender1}}", data[0]);
          toDisplay2 = para14.replace("{{ethnicity}}", data[6]);
          newMovie = "";
          currentMovie = "";
          break;  
        case 8: 
          toDisplay = para15;
          toDisplay2 = para16.replace("{{occupation}}", removeUnderscore(data[9]));
          newMovie = movie8;
          movieSize = movie8Size;
          moviePos = movie8Pos; 
          break;
        case 9: 
          toDisplay = para17.replace("{{gender1}}", data[0]);
          toDisplay2 = para18.replace("{{location}}", removeUnderscore(data[8]));
          newMovie = movie1;
          movieSize = movie1Size;
          moviePos = movie1Pos;          
          break;
        case 10: 
          toDisplay = para19;
          toDisplay2 = para20.replace("{{friend}}", removeUnderscore(data[11]));
          newMovie = movie10;
          movieSize = movie10Size;
          moviePos = movie10Pos;  
          break;
        case 11: 
          toDisplay = para21.replace("{{nickname}}", removeUnderscore(data[4]));
          toDisplay2 = para22.replace("{{gender1}}", data[0]);
          newMovie = movie11;
          movieSize = movie11Size;
          moviePos = movie11Pos;  
          break;
        case 12: 
          toDisplay = para23.replace("{{friend}}", removeUnderscore(data[11]));
          toDisplay2 = para24.replace("{{friendgender}}", data[13]);
          newMovie = "";
          currentMovie = "";
          break;
        case 13: 
          toDisplay = para25;
          toDisplay2 = para26.replace("{{fullname}}", removeUnderscore(data[3]));
          newMovie = movie13;
          movieSize = movie13Size;
          moviePos = movie13Pos;  
          break;
        case 14: 
          toDisplay = para27;
          toDisplay2 = para28.replace("{{gender4}}", data[14]);
          toDisplay2 = toDisplay2.replace("{{gender2}}", data[1]);
          toDisplay2 = toDisplay2.replace("{{friend}}", removeUnderscore(data[11]));
          newMovie = "";
          currentMovie = "";
          break;
        case 15: 
          toDisplay = para29.replace("{{friendgender}}", data[13]);
          toDisplay2 = para30.replace("{{gender4}}", data[14]);
          toDisplay2 = toDisplay2.replace("{{gender1}}", data[0]);
          newMovie = "";
          currentMovie = "";
          break;
        case 16: 
          toDisplay = para31.replace("{{gender4}}", data[14]);
          toDisplay = toDisplay.replace("{{gender1}}", data[0]);
          toDisplay2 = para32.replace("{{gender3}}", data[2]);
          newMovie = movie16;
          movieSize = movie16Size;
          moviePos = movie16Pos;
          break;             
        default:          
          break;
        }
      }
    }  
  }  
  background(0); 
  fill(255);
  //println("current :" + currentMovie + ", new movie :" + newMovie);
  if ( currentMovie != newMovie) {
    myMovie = new Movie(this, newMovie);
    myMovie.loop();
    currentMovie = newMovie;
  }
  if (currentMovie != "") {
    image(myMovie, moviePos[0], moviePos[1], movieSize[0], movieSize[1]); 
  }
  
  textFont(font);
  text(toDisplay, 150, 250, 550, 900);
  text(toDisplay2, 800, 250, 550, 900);  

  if (currentPage == 17 && incomingDataUser.length() > 0 && video.available()) {
    video.read();
    video.loadPixels();
    int charcount = 0;
    String tempString = removeUnderscore3(incomingDataUser);
    tempString = tempString.toUpperCase();
    tempString = removeUnderscore2(tempString);
    println (incomingData);
    textFont(f);
    for (int j = 0; j < rows; j ++ ) {
      for (int i = 0; i < cols; i ++ ) {
        int x = i*videoScale;
        int y = j*videoScale;
        float b = brightness(video.pixels[i + j*video.width]);
        float fontSize = 15 * (b / 255);
        textSize(fontSize);
        String chars = tempString;
        text(chars.charAt(charcount),x,y);
        //println(" char :" + b);
        charcount = (charcount + 1) % chars.length();       
      } 
    }
  }
}

String removeUnderscore (String input) {
  return input.replace("_", " ");
}

String removeUnderscore2 (String input) {
  return input.replace(".", "");
}

String removeUnderscore3 (String input) {
  return input.replace("_", "");
}


