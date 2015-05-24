class Keyboard 
{
  Boolean holdingUp,holdingRight,holdingLeft,holdingSpace;  
  Keyboard() 
  {
    holdingUp=holdingRight=holdingLeft=holdingSpace=false;
  }
  void pressKey(int key,int keyCode) 
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
  void releaseKey(int key,int keyCode) {
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
