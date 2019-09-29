class Lava {
  static final int HEIGHT = 50;
  static final int TILE_WIDTH = 32;
  static final int TILE_HEIGHT = 32;
  
  
  void render() {
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
