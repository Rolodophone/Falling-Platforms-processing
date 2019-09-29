import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.Iterator; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Falling_Platforms extends PApplet {




final int BG_COLOUR       = color(220);
final int PLATFORM_COLOUR = color(72, 68, 200);
final int PLAYER_COLOUR   = color(13, 183, 11);

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


public void setup() {
  
  
  //ensure platform width does not exceed window width
  assert Platform.MAX_WIDTH <= width : "MAX_PLATFORM_WIDTH must be less than the width of the window";
  
  frameRate(FPS);
  
  keysPressed.put("left", false);
  keysPressed.put("right", false);
  keysPressed.put("up", false);
  keysPressed.put("pause", false);
  
  highscore = PApplet.parseInt(loadStrings("highscore.txt")[0]);
  
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
public void draw() { 
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



public void startGame() {
  //set up variables
  platforms = new ArrayList<Platform>();
  
  generateInitialPlatforms();
  
  Platform spawnPlatform = platforms.get(0);
  player = new Player((spawnPlatform.x+spawnPlatform.w) / 2, spawnPlatform.y - Player.HEIGHT);
  
  lava = new Lava();
}



//generate new platforms randomly
public void generatePlatforms() {
  if (PApplet.parseInt(random(Platform.SPAWN_CHANCE)) == 0 || framesSincePlatform >= Platform.MAX_FRAMES_BETWEEN_SPAWNING) {
    framesSincePlatform = 0;
    
    int w = PApplet.parseInt(random(Platform.MIN_WIDTH/Platform.TILE_WIDTH, Platform.MAX_WIDTH/Platform.TILE_WIDTH)) * Platform.TILE_WIDTH;
    int x = PApplet.parseInt(random(width - w));
    int y = -Platform.HEIGHT;
    int s = PApplet.parseInt(random(Platform.MIN_SPEED, Platform.MAX_SPEED));
    
    platforms.add(new Platform(x, y, w, s));
  }
  
  else {
    framesSincePlatform++;
  }
}


//generate initial platforms randomly
public void generateInitialPlatforms() {
  for (int i = 0; i < random(Platform.MIN_INITIAL_NUM, Platform.MAX_INITIAL_NUM); i++) {
  
    int w = PApplet.parseInt(random(Platform.MIN_WIDTH/Platform.TILE_WIDTH, Platform.MAX_WIDTH/Platform.TILE_WIDTH)) * Platform.TILE_WIDTH;
    int x = PApplet.parseInt(random(width - w));
    int y = PApplet.parseInt(random(Platform.MAX_INITIAL_Y));
    int s = PApplet.parseInt(random(Platform.MIN_SPEED, Platform.MAX_SPEED));
    
    platforms.add(new Platform(x, y, w, s));
  }
}
/**
 * Data class for storing data about collisions
 * 
 * @param x     x coordinate of left of collision point
 * @param y     y coordinate of right of collision point
 * @param s     the side that the collision took place on
 */
final public class Collision {
  float x, y;
  String side;
  
  public Collision(Float x, Float y, String s) {
    this.x = x;
    this.y = y;
    this.side = s;
  }
}




/**
 * Finds the entrance point of a rect-rect collision using ray-casting. All coordinates are from bottom left.
 * 
 * @param ix   x coordinate of rectangle in previous frame
 * @param iy   y coordinate of rectangle in previous frame
 * @param fx   x coordinate of rectangle in current frame
 * @param fy   y coordinate of rectangle in current frame
 * @param w    width of rectangle
 * @param h    height of rectangle
 * @param bx   x coordinate of other rectangle
 * @param by   y coordinate of other rectangle
 * @param bw   width of other rectangle
 * @param bh   height of other rectangle
 * 
 * @return Collision: Collision class containing data about the collision
 */
public Collision findEntranceCheckForTunnelling(float ix, float iy, float fx, float fy, float w, float h, float bx, float by, float bw, float bh) {
  //--------SET UP VARS--------------
  //bl means base left
  float bl = bx;
  float br = bx + bw;
  float bt = by;
  float bb = by + bh;
  
  Float contactX;
  Float contactY;
  
  
  
  //---------ADD CONTACT POINTS TO THE ARRAY-------------
  //check for left collision
  contactX = bl-w;
  contactY = computePoint(ix, fx, contactX, iy, fy, false);
  if (contactY != null && contactY+h > bt && contactY < bb) {
    return new Collision(contactX, contactY, "left");
  }
  
  //check for right collision
  contactX = br;
  contactY = computePoint(ix, fx, contactX, iy, fy, true);
  if (contactY != null && contactY+h > bt && contactY < bb) {
    return new Collision(contactX, contactY, "right");
  }
  
  //check for top collision
  contactY = bt-h;
  contactX = computePoint(iy, fy, contactY, ix, fx, false);
  if (contactX != null && contactX+w > bl && contactX < br) {
    return new Collision(contactX, contactY, "top");
  }
  
  //check for bottom collision
  contactY = bb;
  contactX = computePoint(iy, fy, contactY, ix, fx, true);
  if (contactX != null && contactX+w > bl && contactX < br) {
    return new Collision(contactX, contactY, "bottom");
  }
  

  // return null if there are no contacts
  return null;
}



final static private Float computePoint(float ix, float fx, float cx, float iy, float fy, boolean distShouldBePositive) {
  float xDist = ix - fx; // the distance between ix and fx
  
  //perform checks in case of no collision
  if (xDist == 0) return null;
  if (distShouldBePositive) {
    if (xDist < 0) return null;
  } else {
    if (xDist > 0) return null;
  }
  
  //calculation
  float xFrac = (ix-cx) / xDist; // the fraction ix to cx, out of the whole x distance
  if (xFrac >= 1 || xFrac <= 0) return null; // if xFrac > 1, that means contactRect is not between initialRect and finalRect, so there is no collision
  
  float yDist = (fy-iy) * xFrac; // times the distance fy to iy by the same fraction to get the y distance from iy to cy
  
  float cy = iy + yDist; // Add that distance to iy to get cy
  
  return cy;
}
class Lava {
  static final int HEIGHT = 50;
  static final int TILE_WIDTH = 32;
  static final int TILE_HEIGHT = 32;
  
  
  public void render() {
    //iterate through x values
    for (int x = 0; x < width; x += Lava.TILE_WIDTH) {
      
      //render top of lava
      if ((frameCount / FPS) % lavaT.length < 1) {
        image(lavaT[0], x, height - Lava.HEIGHT);
      }
      else if ((frameCount / FPS) % lavaT.length < 2) {
        image(lavaT[1], x, height - Lava.HEIGHT);
      }
      else {
        image(lavaT[2], x, height - Lava.HEIGHT);
      }
      
      //render rest of lava
      for (int y = height - Lava.HEIGHT + Lava.TILE_HEIGHT; y < height; y += Lava.TILE_HEIGHT) {
        image(lavaM, x, y);
      }
    }
  }
}
class Platform {
  static final int MAX_WIDTH        = 600; //reverts to window size if window size is lower
  static final float SPAWN_CHANCE   = 60; //average num of frames between platform spawning
  static final int MIN_WIDTH        = 240;
  static final int HEIGHT           = 30;
  static final int MIN_SPEED        = 1;
  static final int MAX_SPEED        = 6;
  static final int EXTRA_GAME_HEIGHT= 400; // extra width after which platforms despawn
  static final int MAX_FRAMES_BETWEEN_SPAWNING = 60;
  static final int TILE_WIDTH       = 40;
  static final int MIN_INITIAL_NUM  = 8;
  static final int MAX_INITIAL_NUM  = 20;
  static final int MAX_INITIAL_Y    = 600;
  
  private int x;
  private int y = -Platform.HEIGHT; //ensures platform is hidden before moving into view
  private int w; //width
  private float speed;
  
  private boolean isOnScreen = true;
  
  
  Platform(int x, int y, int w, int s) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.speed = s;
  }
  
  public void move() {
    y += speed;
  }
  
  public void render() {
    image(platformL, this.x, this.y);
    
    for (int tile = 1; tile <= (this.w / Platform.TILE_WIDTH) - 2; tile++) {
      image(platformM, this.x + (tile * Platform.TILE_WIDTH), this.y);
    }
    
    image(platformR, this.x+this.w-Platform.TILE_WIDTH, this.y);
  }
  
  public void handleCollisions() {
    if (this.y > height + EXTRA_GAME_HEIGHT) {
      isOnScreen = false;
    }
  }
  
  public void update() {
    move();
    handleCollisions();
    render();
  }
}
class Player {
  static final float GRAVITY          = 1;
  static final int WIDTH              = 50;
  static final int HITBOX_WIDTH       = 30;
  static final int HITBOX_X_OFFSET    = 10;
  static final int HEIGHT             = 90;
  static final float LATERAL_VELOCITY = 5;
  static final float JUMP_SPEED       = 20;
  
  static final boolean GRAVITY_ENABLED = true;
  
  float x;
  float y;
  int score;
  private float gravity = Player.GRAVITY;
  private float velocityY = 0;
  
  private Platform landed = null;
  
  boolean isDead = false;
  
  Player(int x, int y) {
    this.x = x;
    this.y = y;
  }
  
  public void render() {
    image(playerImg, this.x, this.y);
  }
  
  public void renderScore() {
    fill(0);
    textSize(80);
    
    textAlign(LEFT, TOP);
    text(this.score, 0, -15);
    
    textAlign(RIGHT, TOP);
    text(highscore, width, -15);
  }
  
  public void move() {
    if (GRAVITY_ENABLED) {
      velocityY += gravity;
    }
    
    y += velocityY;
  }

  public void update() {
    score++;
    
    float ix = x + Player.HITBOX_X_OFFSET;
    float iy = y;
    this.move();
    float fx = x + Player.HITBOX_X_OFFSET;
    float fy = y;
    
    this.handleCollisions(ix, iy, fx, fy);
    this.checkDead();
    this.render();
    this.renderScore();
  }
  
  public void handleCollisions(float ix, float iy, float fx, float fy) {
    landed = null;
    
    for (Platform platform : platforms) {
      Collision newCoords = findEntranceCheckForTunnelling(ix, iy, fx, fy, Player.HITBOX_WIDTH, Player.HEIGHT, platform.x, platform.y, platform.w, Platform.HEIGHT);
      
      if (newCoords != null && newCoords.side == "top") {
        landed = platform;
        fx = newCoords.x;
        fy = newCoords.y;
      }
    }
    
    if (landed != null) {
      this.x = fx - Player.HITBOX_X_OFFSET;
      this.y = fy;
      
      this.velocityY = landed.speed;
    }
  }
  
  
  public void checkDead() {
    if (this.y > height - Lava.HEIGHT - Player.HEIGHT) {
      paused = true;
      isDead = true;
      
      if (score > highscore) {
        highscore = score;
        saveStrings("data/highscore.txt", new String[] {str(highscore)});
      }

      pushStyle();
      fill(0xffB40000);
      textSize(200);
      textAlign(CENTER, CENTER);
      text("DEAD", width/2, height/2);
      popStyle();
    }
  }
}
public void keyPressed() {
  if (key == CODED) {
    if (keyCode == LEFT) {
      keysPressed.put("left", true);
    }
    if (keyCode == RIGHT) {
      keysPressed.put("right", true);
    }
    if (keyCode == UP) {
      keysPressed.put("up", true);
    }
  }
  else {
    if (key == 'a') {
      keysPressed.put("left", true);
    }
    if (key == 'd') {
      keysPressed.put("right", true);
    }
    if (key == 'w') {
      keysPressed.put("up", true);
    }
  }
}



public void keyReleased() {
  if (key == CODED) {
    if (keyCode == LEFT) {
      keysPressed.put("left", false);
    }
    if (keyCode == RIGHT) {
      keysPressed.put("right", false);
    }
    if (keyCode == UP) {
      keysPressed.put("up", false);
    }
  }
  else {
    if (key == 'a') {
      keysPressed.put("left", false);
    }
    if (key == 'd') {
      keysPressed.put("right", false);
    }
    if (key == 'w') {
      keysPressed.put("up", false);
    }
    if (key == ' ' || key == 'p') {
      paused = !paused;
      if (player.isDead) {
        startGame();
      }
    }
  }
}



public void handleKeys() {
  if (keysPressed.get("left")) {
    player.x -= Player.LATERAL_VELOCITY;
  }
  
  if (keysPressed.get("right")) {
    player.x += Player.LATERAL_VELOCITY;
  }
  
  if (keysPressed.get("up") && player.landed != null) {
    player.velocityY = -Player.JUMP_SPEED;
  }
}
  public void settings() {  fullScreen(SCREEN_NUM); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Falling_Platforms" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
