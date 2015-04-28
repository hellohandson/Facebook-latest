import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.net.*; 
import processing.video.*; 
import jp.nyatla.nyar4psg.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class marker extends PApplet {





Capture cam;
MultiMarker nya;
int cr,cg,cb;
Server mserver;
Client mclient;
String input;
int data[];

public void setup() {
  size(640,480,P3D);
  colorMode(RGB, 100);
  println(MultiMarker.VERSION);
  nya=new MultiMarker(this,width,height,"camera_para.dat",NyAR4PsgConfig.CONFIG_PSG);
  nya.addARMarker("patt.hiro",80);//id=0
  nya.setARPerspective();
  cam=new Capture(this,640,480);
  cr=cg=cb=100;
  cam.start();
 // mserver = new Server(this, 12345);
}

int c=0;
public void draw()
{
  c++;
  if (cam.available() !=true) {
      return;
  }
  cam.read();
  nya.detect(cam);
  background(0);
  nya.drawBackground(cam);
  if(!nya.isExistMarker(0)){
    return;
  }
  //mserver.write("1");
  println("1");

  nya.setARPerspective();
  pushMatrix();
  setMatrix(nya.getMarkerMatrix(0)); 
  fill(cr,cg,cb);
  translate(0,0,20);
  box(40);
  popMatrix();
}

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "marker" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
