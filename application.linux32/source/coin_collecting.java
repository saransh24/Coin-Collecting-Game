import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class coin_collecting extends PApplet {


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
final float GRAVITY_POWER = 0.5f;
public void setup() 
{
  size(600,480);  
  font = loadFont("SansSerif-20.vlw");
  guy_stand = loadImage("guy.png");
  guy_run1 = loadImage("run1.png");
  guy_run2 = loadImage("run2.png");  
  cameraOffsetX = 0.0f;  
  minim = new Minim(this);
  music = minim.loadFile("PinballSpring.mp3", 1024);
  music.loop();
  int buffersize = 256;
  sndJump = minim.loadSample("jump.wav", buffersize);
  sndCoin = minim.loadSample("coin.wav", buffersize);  
  frameRate(24);
  resetGame(); 
}
public void resetGame() 
{
  thePlayer.reset();   
  theWorld.reload(); 
  gameCurrentTimeSec = gameStartTimeSec = millis()/1000;
}
public Boolean gameWon() 
{
   return (thePlayer.coinsCollected == theWorld.coinsInStage);
}
public void outlinedText(String sayThis, float atX, float atY) 
{
  textFont(font);
  fill(255);
  text(sayThis, atX,atY);
}
public void updateCameraPosition() 
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
public void draw() 
{
  pushMatrix();
  translate(-cameraOffsetX,0.0f); 
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

public void keyPressed() {
  theKeyboard.pressKey(key,keyCode);
}

public void keyReleased() {
  theKeyboard.releaseKey(key,keyCode);
}
public void stop()
{ 
  music.close();
  sndJump.close();
  sndCoin.close(); 
  minim.stop();
  super.stop();
}
class Keyboard 
{
  Boolean holdingUp,holdingRight,holdingLeft,holdingSpace;  
  Keyboard() 
  {
    holdingUp=holdingRight=holdingLeft=holdingSpace=false;
  }
  public void pressKey(int key,int keyCode) 
  {
    if(key == 'r')
    {
      if(gameWon()) 
      {
        resetGame(); 
      }
    }
   
    if (keyCode == UP)
    {
      holdingUp = true;
    }
    if (keyCode == LEFT) 
    {
      holdingLeft = true;
    }
    if (keyCode == RIGHT) 
    {
      holdingRight = true;
    }
    if (key == ' ') 
    {
      holdingSpace = true;
    }
  }
  public void releaseKey(int key,int keyCode) {
    if (keyCode == UP) 
    {
      holdingUp = false;
    }
    if (keyCode == LEFT) 
    {
      holdingLeft = false;
    }
    if (keyCode == RIGHT) 
    {
      holdingRight = false;
    }
    if (keyCode == ' ') 
    {
      holdingSpace = false;
    }
  }
}  
class Player 
{
  PVector position,velocity;

  Boolean isOnGround;
  Boolean facingRight;
  int animDelay; 
  int animFrame; 
  int coinsCollected;
  
  static final float JUMP_POWER = 11.0f;
  static final float RUN_SPEED = 5.0f; 
  static final float AIR_RUN_SPEED = 2.0f;
  static final float SLOWDOWN_PERC = 0.6f;
  static final float AIR_SLOWDOWN_PERC = 0.85f; 
  static final int RUN_ANIMATION_DELAY = 3; 
  static final float TRIVIAL_SPEED = 1.0f; 
  
  Player()
  {
    isOnGround = false;
    facingRight = true;
    position = new PVector();
    velocity = new PVector();
    reset();
  }  
  public void reset() 
  {
    coinsCollected = 0;
    animDelay = 0;
    animFrame = 0;
    velocity.x = 0;
    velocity.y = 0;
  }  
  public void inputCheck() 
  {    
    float speedHere = (isOnGround ? RUN_SPEED : AIR_RUN_SPEED);
    float frictionHere = (isOnGround ? SLOWDOWN_PERC : AIR_SLOWDOWN_PERC);
    if(theKeyboard.holdingLeft) 
    {
      velocity.x -= speedHere;
    }
    else if(theKeyboard.holdingRight) 
    {
      velocity.x += speedHere;
    } 
    velocity.x *= frictionHere;
    if(isOnGround) 
    { 
      if(theKeyboard.holdingSpace || theKeyboard.holdingUp) 
      {
        sndJump.trigger();
        velocity.y = -JUMP_POWER;
        isOnGround = false; 
      }
    }
  }
  
  public void checkForWallBumping() {
    int guyWidth = guy_stand.width;
    int guyHeight = guy_stand.height;
    int wallProbeDistance = PApplet.parseInt(guyWidth*0.3f);
    int ceilingProbeDistance = PApplet.parseInt(guyHeight*0.95f);
    PVector leftSideHigh,rightSideHigh,leftSideLow,rightSideLow,topSide;
    leftSideHigh = new PVector();
    rightSideHigh = new PVector();
    leftSideLow = new PVector();
    rightSideLow = new PVector();
    topSide = new PVector();    
    leftSideHigh.x = leftSideLow.x = position.x - wallProbeDistance; 
    rightSideHigh.x = rightSideLow.x = position.x + wallProbeDistance;
    leftSideLow.y = rightSideLow.y = position.y-0.2f*guyHeight; 
    leftSideHigh.y = rightSideHigh.y = position.y-0.8f*guyHeight;
    topSide.x = position.x; 
    topSide.y = position.y-ceilingProbeDistance;
    if( theWorld.worldSquareAt(topSide)==World.TILE_KILLBLOCK ||theWorld.worldSquareAt(leftSideHigh)==World.TILE_KILLBLOCK ||theWorld.worldSquareAt(leftSideLow)==World.TILE_KILLBLOCK ||theWorld.worldSquareAt(rightSideHigh)==World.TILE_KILLBLOCK ||theWorld.worldSquareAt(rightSideLow)==World.TILE_KILLBLOCK ||theWorld.worldSquareAt(position)==World.TILE_KILLBLOCK) 
    {
      resetGame();
      return;
    }
    if( theWorld.worldSquareAt(topSide)==World.TILE_SOLID) 
    {
      if(theWorld.worldSquareAt(position)==World.TILE_SOLID) 
      {
        position.sub(velocity);
        velocity.x=0.0f;
        velocity.y=0.0f;
      }
      else
      {
        position.y = theWorld.bottomOfSquare(topSide)+ceilingProbeDistance;
        if(velocity.y < 0) 
        {
          velocity.y = 0.0f;
        }
      }
    }
    
    if( theWorld.worldSquareAt(leftSideLow)==World.TILE_SOLID) 
    {
      position.x = theWorld.rightOfSquare(leftSideLow)+wallProbeDistance;
      if(velocity.x < 0) 
      {
        velocity.x = 0.0f;
      }
    }
   
    if( theWorld.worldSquareAt(leftSideHigh)==World.TILE_SOLID) 
    {
      position.x = theWorld.rightOfSquare(leftSideHigh)+wallProbeDistance;
      if(velocity.x < 0) 
      {
        velocity.x = 0.0f;
      }
    }
   
    if( theWorld.worldSquareAt(rightSideLow)==World.TILE_SOLID) 
    {
      position.x = theWorld.leftOfSquare(rightSideLow)-wallProbeDistance;
      if(velocity.x > 0) 
      {
        velocity.x = 0.0f;
      }
    }
   
    if( theWorld.worldSquareAt(rightSideHigh)==World.TILE_SOLID) 
    {
      position.x = theWorld.leftOfSquare(rightSideHigh)-wallProbeDistance;
      if(velocity.x > 0) 
      {
        velocity.x = 0.0f;
      }
    }
  }

  public void checkForCoinGetting() 
  {
    PVector centerOfPlayer;
    centerOfPlayer = new PVector(position.x,position.y-guy_stand.height/2);
    if(theWorld.worldSquareAt(centerOfPlayer)==World.TILE_COIN) 
    {
      theWorld.setSquareAtToThis(centerOfPlayer, World.TILE_EMPTY);
      sndCoin.trigger();
      coinsCollected++;
    }
  }

  public void checkForFalling() 
  {  
    if(theWorld.worldSquareAt(position)==World.TILE_EMPTY ||theWorld.worldSquareAt(position)==World.TILE_COIN) 
    {
       isOnGround=false;
    }    
    if(isOnGround==false) 
    {     
      if(theWorld.worldSquareAt(position)==World.TILE_SOLID) 
      {
        isOnGround = true;
        position.y = theWorld.topOfSquare(position);
        velocity.y = 0.0f;
      } 
      else 
      {
        velocity.y += GRAVITY_POWER;
      }
    }
  }

  public void move()
  {
    position.add(velocity);    
    checkForWallBumping();    
    checkForCoinGetting();    
    checkForFalling();
  }  
  public void draw() 
  {
    int guyWidth = guy_stand.width;
    int guyHeight = guy_stand.height;    
    if(velocity.x<-TRIVIAL_SPEED) 
    {
      facingRight = false;
    }
    else if(velocity.x>TRIVIAL_SPEED)
    {
      facingRight = true;
    }
    
    pushMatrix();
    translate(position.x,position.y);
    if(facingRight==false)
    {
      scale(-1,1); 
    }
    translate(-guyWidth/2,-guyHeight); 
    if(isOnGround==false) 
    {
      image(guy_run1, 0,0); 
    } 
    else if(abs(velocity.x)<TRIVIAL_SPEED)
    { 
      image(guy_stand, 0,0);
    } 
    else 
    { 
      if(animDelay--<0) 
      {
        animDelay=RUN_ANIMATION_DELAY;
        if(animFrame==0) 
        {
          animFrame=1;
        }
        else 
        {
          animFrame=0;
        }
      }      
      if(animFrame==0) 
      {
        image(guy_run1, 0,0);
      }
      else 
      {
        image(guy_run2, 0,0);
      }
    }    
    popMatrix();
  }
}
class World 
{
  int coinsInStage; 
  int coinRotateTimer;
  static final int TILE_EMPTY = 0;
  static final int TILE_SOLID = 1;
  static final int TILE_COIN = 2;
  static final int TILE_KILLBLOCK = 3;
  static final int TILE_START = 4;  
  static final int GRID_UNIT_SIZE = 60;
  static final int GRID_UNITS_WIDE = 28;
  static final int GRID_UNITS_TALL = 8;
  int[][] worldGrid = new int[GRID_UNITS_TALL][GRID_UNITS_WIDE];
  int[][] start_Grid = { {2, 0, 2, 0, 0, 0, 2, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 2, 0, 0, 0},
                         {0, 2, 0, 0, 0, 0, 2, 2, 1, 4, 0, 0, 0, 2, 2, 2, 2, 0, 0, 0, 0, 2, 2, 0, 2, 0, 2, 0},
                         {2, 0, 2, 0, 4, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0},
                         {0, 0, 0, 2, 1, 2, 0, 0, 2, 2, 2, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 2, 3, 1, 0},
                         {1, 0, 0, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 2, 0, 1, 3},
                         {1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 0, 3, 1, 0, 0, 0, 2, 0, 1, 3},
                         {3, 2, 0, 0, 1, 3, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 1, 3},
                         {3, 2, 0, 1, 1, 3, 1, 3, 3, 3, 1, 3, 2, 1, 3, 1, 3, 2, 0, 0, 0, 0, 1, 2, 1, 2, 1, 3} 
                       };
  World() 
  {
    coinRotateTimer = 0; 
  }
  public int worldSquareAt(PVector thisPosition)
  {
    float gridSpotX = thisPosition.x/GRID_UNIT_SIZE;
    float gridSpotY = thisPosition.y/GRID_UNIT_SIZE;  
    if(gridSpotX<0) 
    {
      return 1; 
    }
    if(gridSpotX>=GRID_UNITS_WIDE) 
    {
      return 1; 
    }
    if(gridSpotY<0) 
    {
      return 1; 
    }
    if(gridSpotY>=GRID_UNITS_TALL) 
    {
      return 1;
    }    
    return worldGrid[PApplet.parseInt(gridSpotY)][PApplet.parseInt(gridSpotX)];
  }
  public void setSquareAtToThis(PVector thisPosition, int newTile) 
  {
    int gridSpotX = PApplet.parseInt(thisPosition.x/GRID_UNIT_SIZE);
    int gridSpotY = PApplet.parseInt(thisPosition.y/GRID_UNIT_SIZE);  
    if(gridSpotX<0 || gridSpotX>=GRID_UNITS_WIDE ||gridSpotY<0 || gridSpotY>=GRID_UNITS_TALL) 
    {
      return;
    }    
    worldGrid[gridSpotY][gridSpotX] = newTile;
  }
  public float topOfSquare(PVector thisPosition) 
  {
    int thisY = PApplet.parseInt(thisPosition.y);
    thisY /= GRID_UNIT_SIZE;
    return PApplet.parseFloat(thisY*GRID_UNIT_SIZE);
  }
  public float bottomOfSquare(PVector thisPosition) 
  {
    if(thisPosition.y<0) 
    {
      return 0;
    }
    return topOfSquare(thisPosition)+GRID_UNIT_SIZE;
  }
  public float leftOfSquare(PVector thisPosition) 
  {
    int thisX = PApplet.parseInt(thisPosition.x);
    thisX /= GRID_UNIT_SIZE;
    return PApplet.parseFloat(thisX*GRID_UNIT_SIZE);
  }
  public float rightOfSquare(PVector thisPosition) 
  {
    if(thisPosition.x<0) 
    {
      return 0;
    }
    return leftOfSquare(thisPosition)+GRID_UNIT_SIZE;
  }  
  public void reload() 
  {
    coinsInStage = 0;     
    for(int i=0;i<GRID_UNITS_WIDE;i++) 
    {
      for(int j=0;j<GRID_UNITS_TALL;j++) 
      {
        if(start_Grid[j][i] == TILE_START) 
        {
          worldGrid[j][i] = TILE_EMPTY;
          thePlayer.position.x = i*GRID_UNIT_SIZE+(GRID_UNIT_SIZE/2);
          thePlayer.position.y = j*GRID_UNIT_SIZE+(GRID_UNIT_SIZE/2);
        } 
        else
        {
          if(start_Grid[j][i]==TILE_COIN)
          {
            coinsInStage++;
          }
          worldGrid[j][i] = start_Grid[j][i];
        }
      }
    }
  }
  
  public void render() 
  {
    coinRotateTimer--;
    if(coinRotateTimer<-GRID_UNIT_SIZE/3) 
    {
      coinRotateTimer = GRID_UNIT_SIZE/3;
    }    
    for(int i=0;i<GRID_UNITS_WIDE;i++)
    {
      for(int j=0;j<GRID_UNITS_TALL;j++) 
      {
        switch(worldGrid[j][i]) 
        {
          case TILE_SOLID:
            stroke(40);
            fill(0); 
            break;
          case TILE_KILLBLOCK:
            stroke(255,0,0); 
            fill(255,0,0); 
            break;
          default:
            stroke(245); 
            fill(255); 
            break;
        }       
        rect(i*GRID_UNIT_SIZE,j*GRID_UNIT_SIZE,
             GRID_UNIT_SIZE-1,GRID_UNIT_SIZE-1);        
        if(worldGrid[j][i]==TILE_COIN) 
        {
          stroke(0);          
          fill(255,255,0); 
          ellipse(i*GRID_UNIT_SIZE+(GRID_UNIT_SIZE/2),j*GRID_UNIT_SIZE+(GRID_UNIT_SIZE/2),
                  abs(coinRotateTimer),GRID_UNIT_SIZE/2);
        }
      }
    }
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "coin_collecting" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
