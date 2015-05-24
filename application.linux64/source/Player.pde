class Player 
{
  PVector position,velocity;

  Boolean isOnGround;
  Boolean facingRight;
  int animDelay; 
  int animFrame; 
  int coinsCollected;
  
  static final float JUMP_POWER = 11.0;
  static final float RUN_SPEED = 5.0; 
  static final float AIR_RUN_SPEED = 2.0;
  static final float SLOWDOWN_PERC = 0.6;
  static final float AIR_SLOWDOWN_PERC = 0.85; 
  static final int RUN_ANIMATION_DELAY = 3; 
  static final float TRIVIAL_SPEED = 1.0; 
  
  Player()
  {
    isOnGround = false;
    facingRight = true;
    position = new PVector();
    velocity = new PVector();
    reset();
  }  
  void reset() 
  {
    coinsCollected = 0;
    animDelay = 0;
    animFrame = 0;
    velocity.x = 0;
    velocity.y = 0;
  }  
  void inputCheck() 
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
  
  void checkForWallBumping() {
    int guyWidth = guy_stand.width;
    int guyHeight = guy_stand.height;
    int wallProbeDistance = int(guyWidth*0.3);
    int ceilingProbeDistance = int(guyHeight*0.95);
    PVector leftSideHigh,rightSideHigh,leftSideLow,rightSideLow,topSide;
    leftSideHigh = new PVector();
    rightSideHigh = new PVector();
    leftSideLow = new PVector();
    rightSideLow = new PVector();
    topSide = new PVector();    
    leftSideHigh.x = leftSideLow.x = position.x - wallProbeDistance; 
    rightSideHigh.x = rightSideLow.x = position.x + wallProbeDistance;
    leftSideLow.y = rightSideLow.y = position.y-0.2*guyHeight; 
    leftSideHigh.y = rightSideHigh.y = position.y-0.8*guyHeight;
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
        velocity.x=0.0;
        velocity.y=0.0;
      }
      else
      {
        position.y = theWorld.bottomOfSquare(topSide)+ceilingProbeDistance;
        if(velocity.y < 0) 
        {
          velocity.y = 0.0;
        }
      }
    }
    
    if( theWorld.worldSquareAt(leftSideLow)==World.TILE_SOLID) 
    {
      position.x = theWorld.rightOfSquare(leftSideLow)+wallProbeDistance;
      if(velocity.x < 0) 
      {
        velocity.x = 0.0;
      }
    }
   
    if( theWorld.worldSquareAt(leftSideHigh)==World.TILE_SOLID) 
    {
      position.x = theWorld.rightOfSquare(leftSideHigh)+wallProbeDistance;
      if(velocity.x < 0) 
      {
        velocity.x = 0.0;
      }
    }
   
    if( theWorld.worldSquareAt(rightSideLow)==World.TILE_SOLID) 
    {
      position.x = theWorld.leftOfSquare(rightSideLow)-wallProbeDistance;
      if(velocity.x > 0) 
      {
        velocity.x = 0.0;
      }
    }
   
    if( theWorld.worldSquareAt(rightSideHigh)==World.TILE_SOLID) 
    {
      position.x = theWorld.leftOfSquare(rightSideHigh)-wallProbeDistance;
      if(velocity.x > 0) 
      {
        velocity.x = 0.0;
      }
    }
  }

  void checkForCoinGetting() 
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

  void checkForFalling() 
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
        velocity.y = 0.0;
      } 
      else 
      {
        velocity.y += GRAVITY_POWER;
      }
    }
  }

  void move()
  {
    position.add(velocity);    
    checkForWallBumping();    
    checkForCoinGetting();    
    checkForFalling();
  }  
  void draw() 
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
