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
  
  void render() {
    image(playerImg, this.x, this.y);
  }
  
  void renderScore() {
    fill(0);
    textSize(80);
    
    textAlign(LEFT, TOP);
    text(this.score, 0, -15);
    
    textAlign(RIGHT, TOP);
    text(highscore, width, -15);
  }
  
  void move() {
    if (GRAVITY_ENABLED) {
      velocityY += gravity;
    }
    
    y += velocityY;
  }

  void update() {
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
  
  void handleCollisions(float ix, float iy, float fx, float fy) {
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
  
  
  void checkDead() {
    if (this.y > height - Lava.HEIGHT - Player.HEIGHT) {
      paused = true;
      isDead = true;
      
      if (score > highscore) {
        highscore = score;
        saveStrings("data/highscore.txt", new String[] {str(highscore)});
      }

      pushStyle();
      fill(#B40000);
      textSize(200);
      textAlign(CENTER, CENTER);
      text("DEAD", width/2, height/2);
      popStyle();
    }
  }
}
