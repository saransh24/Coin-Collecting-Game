import ddf.minim.*;
Minim minim;
PImage guy_stand, guy_run1, guy_run2;
AudioPlayer music;
AudioSample sndJump, sndCoin;
float cameraOffsetX;
Player thePlayer = new Player();
World theWorld = new World();
Keyboard theKeyboard = new Keyboard();
PFont font;
int gameStartTimeSec,gameCurrentTimeSec;
final float GRAVITY_POWER = 0.5;
void setup() 
{
  size(600,480);  
  font = loadFont("SansSerif-20.vlw");
  guy_stand = loadImage("guy.png");
  guy_run1 = loadImage("run1.png");
  guy_run2 = loadImage("run2.png");  
  cameraOffsetX = 0.0;  
  minim = new Minim(this);
  music = minim.loadFile("PinballSpring.mp3", 1024);
  music.loop();
  int buffersize = 256;
  sndJump = minim.loadSample("jump.wav", buffersize);
  sndCoin = minim.loadSample("coin.wav", buffersize);  
  frameRate(24);
  resetGame(); 
}
void resetGame() 
{
  thePlayer.reset();   
  theWorld.reload(); 
  gameCurrentTimeSec = gameStartTimeSec = millis()/1000;
}
Boolean gameWon() 
{
   return (thePlayer.coinsCollected == theWorld.coinsInStage);
}
void outlinedText(String sayThis, float atX, float atY) 
{
  textFont(font);
  fill(255);
  text(sayThis, atX,atY);
}
void updateCameraPosition() 
{
  int rightEdge = World.GRID_UNITS_WIDE*World.GRID_UNIT_SIZE-width;
  cameraOffsetX = thePlayer.position.x-width/2;
  if(cameraOffsetX < 0) 
  {
    cameraOffsetX = 0;
  }  
  if(cameraOffsetX > rightEdge) 
  {
    cameraOffsetX = rightEdge;
  }
}
void draw() 
{
  pushMatrix();
  translate(-cameraOffsetX,0.0); 
  updateCameraPosition();
  theWorld.render();    
  thePlayer.inputCheck();
  thePlayer.move();
  thePlayer.draw();  
  popMatrix();
  if(focused == false)
  {
    textAlign(CENTER);
    outlinedText("Click this area to play.\n\nUse arrows to move.\nSpacebar to jump.",width/2, height-90);
  }
  else
  {
    textAlign(LEFT); 
    outlinedText("Coins:"+thePlayer.coinsCollected +"/"+theWorld.coinsInStage,8, height-10);    
    textAlign(RIGHT);
    if(gameWon() == false) 
    {
      gameCurrentTimeSec = millis()/1000; 
    }
    int minutes = (gameCurrentTimeSec-gameStartTimeSec)/60;
    int seconds = (gameCurrentTimeSec-gameStartTimeSec)%60;
    if(seconds < 10)
    {
      outlinedText(minutes +":0"+seconds,width-8, height-10);
    } 
    else
    {
      outlinedText(minutes +":"+seconds,width-8, height-10);
    }    
    textAlign(CENTER);   
    if(gameWon()) 
    {
      outlinedText("All Coins Collected!\nPress R to Reset.",width/2, height/2-12);
    }
  }
}

void keyPressed() {
  theKeyboard.pressKey(key,keyCode);
}

void keyReleased() {
  theKeyboard.releaseKey(key,keyCode);
}
void stop()
{ 
  music.close();
  sndJump.close();
  sndCoin.close(); 
  minim.stop();
  super.stop();
}
