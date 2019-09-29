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
  
  void move() {
    y += speed;
  }
  
  void render() {
    image(platformL, this.x, this.y);
    
    for (int tile = 1; tile <= (this.w / Platform.TILE_WIDTH) - 2; tile++) {
      image(platformM, this.x + (tile * Platform.TILE_WIDTH), this.y);
    }
    
    image(platformR, this.x+this.w-Platform.TILE_WIDTH, this.y);
  }
  
  void handleCollisions() {
    if (this.y > height + EXTRA_GAME_HEIGHT) {
      isOnScreen = false;
    }
  }
  
  void update() {
    move();
    handleCollisions();
    render();
  }
}
