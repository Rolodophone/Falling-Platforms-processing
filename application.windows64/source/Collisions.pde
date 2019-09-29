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
