import java.util.Iterator;


final color BG_COLOUR       = color(220);
final color PLATFORM_COLOUR = color(72, 68, 200);
final color PLAYER_COLOUR   = color(13, 183, 11);

static int SCREEN_NUM = 2;

final static int FPS = 60;

boolean paused = false;

PImage playerImg, platformL, platformM, platformR, lavaM, lavaTsheet;
PImage[] lavaT = new PImage[3];

int highscore;
int framesSincePlatform = 0;


HashMap<String, Boolean> keysPressed = new HashMap<String, Boolean>(5);


ArrayList<Platform> platforms;
Player player;
Lava lava;


void setup() {
  fullScreen(SCREEN_NUM);
  
  //ensure platform width does not exceed window width
  assert Platform.MAX_WIDTH <= width : "MAX_PLATFORM_WIDTH must be less than the width of the window";
  
  frameRate(FPS);
  
  keysPressed.put("left", false);
  keysPressed.put("right", false);
  keysPressed.put("up", false);
  keysPressed.put("pause", false);
  
  highscore = int(loadStrings("highscore.txt")[0]);
  
  //load images
  playerImg = loadImage("player.png");
  platformL = loadImage("platformLeft.png");
  platformM = loadImage("platformMiddle.png");
  platformR = loadImage("platformRight.png");
  lavaM     = loadImage("lavaMiddle.png");
  lavaTsheet= loadImage("lavaTop.png");
  for (int i = 0; i < lavaT.length; i++) {
    lavaT[i] = lavaTsheet.get(0, i * Lava.TILE_HEIGHT, Lava.TILE_WIDTH, Lava.TILE_HEIGHT);
  }
  
  startGame();
}



 //<>//
void draw() { 
  if (!paused) {
    
    background(BG_COLOUR); //<>//
   
    generatePlatforms();
    
    //----------TESTING----------
    
    //println(frameCount);
    //println(frameCount / float(FPS));
    //println((frameCount / float(FPS)) * lavaT.length);
    //image(lavaT[2], 300, 300);
    
    //------------UPDATE STUFF--------------
    
    handleKeys();
    
    //update platforms
    for (Platform platform : platforms) {
      platform.update();
    }
    
    //remove dead platforms
    for(Iterator<Platform> iterator = platforms.iterator(); iterator.hasNext(); ) {
      if(! iterator.next().isOnScreen) {
          iterator.remove();
      }
    }
    
    player.update();
    lava.render();
  }
}



void startGame() {
  //set up variables
  platforms = new ArrayList<Platform>();
  
  generateInitialPlatforms();
  
  Platform spawnPlatform = platforms.get(0);
  player = new Player((spawnPlatform.x+spawnPlatform.w) / 2, spawnPlatform.y - Player.HEIGHT);
  
  lava = new Lava();
}



//generate new platforms randomly
void generatePlatforms() {
  if (int(random(Platform.SPAWN_CHANCE)) == 0 || framesSincePlatform >= Platform.MAX_FRAMES_BETWEEN_SPAWNING) {
    framesSincePlatform = 0;
    
    int w = int(random(Platform.MIN_WIDTH/Platform.TILE_WIDTH, Platform.MAX_WIDTH/Platform.TILE_WIDTH)) * Platform.TILE_WIDTH;
    int x = int(random(width - w));
    int y = -Platform.HEIGHT;
    int s = int(random(Platform.MIN_SPEED, Platform.MAX_SPEED));
    
    platforms.add(new Platform(x, y, w, s));
  }
  
  else {
    framesSincePlatform++;
  }
}


//generate initial platforms randomly
void generateInitialPlatforms() {
  for (int i = 0; i < random(Platform.MIN_INITIAL_NUM, Platform.MAX_INITIAL_NUM); i++) {
  
    int w = int(random(Platform.MIN_WIDTH/Platform.TILE_WIDTH, Platform.MAX_WIDTH/Platform.TILE_WIDTH)) * Platform.TILE_WIDTH;
    int x = int(random(width - w));
    int y = int(random(Platform.MAX_INITIAL_Y));
    int s = int(random(Platform.MIN_SPEED, Platform.MAX_SPEED));
    
    platforms.add(new Platform(x, y, w, s));
  }
}
