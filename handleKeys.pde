void keyPressed() {
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



void keyReleased() {
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



void handleKeys() {
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
