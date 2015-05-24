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
  int worldSquareAt(PVector thisPosition)
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
    return worldGrid[int(gridSpotY)][int(gridSpotX)];
  }
  void setSquareAtToThis(PVector thisPosition, int newTile) 
  {
    int gridSpotX = int(thisPosition.x/GRID_UNIT_SIZE);
    int gridSpotY = int(thisPosition.y/GRID_UNIT_SIZE);  
    if(gridSpotX<0 || gridSpotX>=GRID_UNITS_WIDE ||gridSpotY<0 || gridSpotY>=GRID_UNITS_TALL) 
    {
      return;
    }    
    worldGrid[gridSpotY][gridSpotX] = newTile;
  }
  float topOfSquare(PVector thisPosition) 
  {
    int thisY = int(thisPosition.y);
    thisY /= GRID_UNIT_SIZE;
    return float(thisY*GRID_UNIT_SIZE);
  }
  float bottomOfSquare(PVector thisPosition) 
  {
    if(thisPosition.y<0) 
    {
      return 0;
    }
    return topOfSquare(thisPosition)+GRID_UNIT_SIZE;
  }
  float leftOfSquare(PVector thisPosition) 
  {
    int thisX = int(thisPosition.x);
    thisX /= GRID_UNIT_SIZE;
    return float(thisX*GRID_UNIT_SIZE);
  }
  float rightOfSquare(PVector thisPosition) 
  {
    if(thisPosition.x<0) 
    {
      return 0;
    }
    return leftOfSquare(thisPosition)+GRID_UNIT_SIZE;
  }  
  void reload() 
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
  
  void render() 
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
