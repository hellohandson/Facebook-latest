import processing.video.*;

Capture cam;
Capture cam2;

void setup() {
  size(640, 480);

  String[] cameras = Capture.list();

  if (cameras == null) {
    println("Failed to retrieve the list of available cameras, will try the default...");
    cam = new Capture(this, 320, 240, "Webcam C170");
  } if (cameras.length == 0) {
    println("There are no cameras available for capture.");
    exit();
  } else {
    println("Available cameras:");
    for (int i = 0; i < cameras.length; i++) {
      println(cameras[i]);
    }

    cam = new Capture(this, 512, 384, "Webcam C170", 30);
    cam.start();
    cam2 = new Capture(this, 320, 180, "FaceTime HD Camera", 30);
    cam2.start();
  }
}

void draw() {
  if (cam.available() == true) {
    cam.read();
  }
  image(cam, 0, 0);
  
  if (cam2.available() == true) {
    cam2.read();
  }
  image(cam2, 300, 0);
}

