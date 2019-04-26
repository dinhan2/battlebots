package bots;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

import java.awt.*;

public class QirongBot extends Bot {
    boolean debug = true;
    boolean bulletDetectionDebug = false;
    BotHelper botHelper = new BotHelper();
    static double DANGERZONE = 50;
    static double bulletDodgeZone = 100;
    int moveTimer = 0;
    int bulletTrackTimer = 0;
    int dodgeVariationTimer = 0;
    int moveVariationTimer = 5;
    int fireDelayTimer = 0;
    double bulletInitialX;
    double bulletInitialY;
    double bulletFinalX;
    double bulletFinalY;
    double bulletDeltaX;
    double bulletDeltaY;
    boolean requiredDodge;
    /**
     * This method is called at the beginning of each round. Use it to perform
     * any initialization that you require when starting a new round.
     */
    @Override
    public void newRound() {

    }

    /**
     * This method is called at every time step to find out what you want your
     * Bot to do. The legal moves are defined in constants in the BattleBotArena
     * class (UP, DOWN, LEFT, RIGHT, FIREUP, FIREDOWN, FIRELEFT, FIRERIGHT, STAY,
     * SEND_MESSAGE). <br><br>
     * <p>
     * The <b>FIRE</b> moves cause a bullet to be created (if there are
     * not too many of your bullets on the screen at the moment). Each bullet
     * moves at speed set by the BULLET_SPEED constant in BattleBotArena. <br><br>
     * <p>
     * The <b>UP</b>, <b>DOWN</b>, <b>LEFT</b>, and <b>RIGHT</b> moves cause the
     * bot to move BOT_SPEED
     * pixels in the requested direction (BOT_SPEED is a constant in
     * BattleBotArena). However, if this would cause a
     * collision with any live or dead bot, or would move the Bot outside the
     * playing area defined by TOP_EDGE, BOTTOM_EDGE, LEFT_EDGE, and RIGHT_EDGE,
     * the move will not be allowed by the Arena.<br><Br>
     * <p>
     * The <b>SEND_MESSAGE</b> move (if allowed by the Arena) will cause a call-back
     * to this Bot's <i>outgoingMessage()</i> method, which should return the message
     * you want the Bot to broadcast. This will be followed with a call to
     * <i>incomingMessage(String)</i> which will be the echo of the broadcast message
     * coming back to the Bot.
     *
     * @param me       A BotInfo object with all publicly available info about this Bot
     * @param shotOK   True iff a FIRE move is currently allowed
     * @param liveBots An array of BotInfo objects for the other Bots currently in play
     * @param deadBots An array of BotInfo objects for the dead Bots littering the arena
     * @param bullets  An array of all Bullet objects currently in play
     * @return A legal move (use the constants defined in BattleBotArena)
     */
    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {

        Bullet closestBullet = botHelper.findClosest(me,bullets);

        if(botHelper.manhattanDist(me.getX(),me.getY(), closestBullet.getX(),closestBullet.getY()) <bulletDodgeZone) {//calculates the velocity of the nearby bullet
            if (bulletTrackTimer == 1) {
                bulletFinalX = closestBullet.getX();
                bulletFinalY = closestBullet.getY();
                bulletDeltaX = bulletFinalX - bulletInitialX;//positive deltaX means bullet is moving to the right, negative deltaX means bullet is moving to the left
                bulletDeltaY = bulletFinalY - bulletInitialY;//positive deltaY means bullet is moving down, negative deltaY means bullet is moving up
                if (bulletDetectionDebug) {
                    System.out.println("deltaX " + bulletDeltaX);
                    System.out.println("deltaY " + bulletDeltaY);
                }
                bulletTrackTimer = 0;
            }
            if (bulletTrackTimer == 0) {
                bulletInitialX = closestBullet.getX();
                bulletInitialY = closestBullet.getY();
                bulletTrackTimer++;

            }

        }
        else{
            requiredDodge = false;
        }
        /*if (bulletDeltaX > 0){
            if(me.getX() >closestBullet.getX() ) { //detects whether the bullet needs to be dodged
                if (bulletDetectionDebug) {System.out.println("incoming RIGHT " + moveTimer);}
                requiredDodge = true;
                if (me.getY() < closestBullet.getY()){//either moves the robot up or down to evade the bullet
                    return BattleBotArena.UP;
                }else{
                    return BattleBotArena.DOWN;
                }
            }
        }else if(bulletDeltaX < 0){
            if(me.getX() < closestBullet.getX()) {
                {
                    if (bulletDetectionDebug){System.out.println("incoming LEFT " + moveTimer);}
                    requiredDodge = true;
                    if (me.getY() < closestBullet.getY()){//either moves the robot up or down to evade the bullet
                        return BattleBotArena.UP;
                    }else{
                        return BattleBotArena.DOWN;
                    }
                }
            }
        if (bulletDeltaY > 0) {
            if (me.getY() > closestBullet.getY()) {

                if (bulletDetectionDebug) {
                    System.out.println("incoming DOWN " + moveTimer);
                }
                requiredDodge = true;
                if (me.getX() < closestBullet.getX()){//either moves the robot up or down to evade the bullet
                    return BattleBotArena.LEFT;
                }else{
                    return BattleBotArena.RIGHT;
                }
            }
        }
        }else if(bulletDeltaY < 0) {

            if (me.getY() < closestBullet.getY()) {
                if (bulletDetectionDebug) {System.out.println("incoming UP " + moveTimer); }
                requiredDodge = true;
            }
        }*/
        if(botHelper.manhattanDist(me.getX()+1,me.getY(),closestBullet.getX(),closestBullet.getY())<bulletDodgeZone){//code for bullet dodge
            if (bulletDeltaX != 0 ) {//if bulletDeltaX is not zero, the bullet is moving horizontally
                if (me.getY() + 1 <= closestBullet.getY()) {//move up if bot is already above the bullet
                    if (bulletDetectionDebug) {System.out.println("dodging UP " + moveTimer); }
                        return BattleBotArena.UP;
                    }
                 else if (me.getY() + 1 >closestBullet.getY()) {//move down if bot is below bullet{
                    if (bulletDetectionDebug) {System.out.println("dodging DOWN " + moveTimer); }
                    return BattleBotArena.DOWN;
                }
            }
            if (bulletDeltaY != 0){//if bulletDeltaY is not zero, the bullet is moving vertically
                if(me.getX() + 1 <= closestBullet.getX()){
                    if (bulletDetectionDebug) {System.out.println("dodging LEFT " + moveTimer); }
                    return BattleBotArena.LEFT;
                }else if(me.getX() +1 > closestBullet.getX()){
                    if (bulletDetectionDebug) {System.out.println("dodging RIGHT " + moveTimer); }
                    return BattleBotArena.RIGHT;
                }
            }
        }
    BotInfo closestBot = botHelper.findClosest(me, liveBots);


            if (moveTimer > 10) {
                moveTimer = 0;
            }
            if ((me.getX() - closestBot.getX() < 5)&& (me.getX() - closestBot.getX() > -5)) {
                if (me.getY() < closestBot.getY()) {
                    if(debug){System.out.println("firingDown "+moveTimer);}
                        return BattleBotArena.FIREDOWN;
                } else {
                    if(debug){System.out.println("firingUp "+moveTimer);}
                    return BattleBotArena.FIREUP;
                }
            }
            if ((me.getY() - closestBot.getY()< 5)&&(me.getY() - closestBot.getY()> -5)) {
                if (me.getX()< closestBot.getX()){
                    if(debug){System.out.println("firingRight "+moveTimer);}
                    return BattleBotArena.FIRERIGHT;
                }
                else {
                    if(debug){System.out.println("firingLeft "+moveTimer);}
                    return BattleBotArena.FIRELEFT;
                }
            }
        if(botHelper.manhattanDist(me.getX(),me.getY(),closestBot.getX(),closestBot.getY())<DANGERZONE){
            if (moveTimer <= 10){
                if (me.getX() < closestBot.getX()) {//fleeing left or right to keep a distance from other robots
                    moveTimer++;
                    if(debug){System.out.println("fleeingLeft "+moveTimer);}
                    return BattleBotArena.LEFT;
                } else {
                    moveTimer++;
                    if(debug){System.out.println("fleeingRight "+moveTimer);}
                    return BattleBotArena.RIGHT;
                }

            }
            if (moveTimer > 10) {
                if (me.getY() < closestBot.getY()) {//fleeing up or down to match the enemy robot's Y coordinates
                    moveTimer++;
                    if(debug){System.out.println("fleeingUp "+moveTimer);}
                    return BattleBotArena.UP;
                } else {
                    moveTimer++;
                    if(debug){System.out.println("fleeingDown "+moveTimer);}
                    return BattleBotArena.DOWN;
                }
            }
        }
            if (moveTimer <= 5) {

                if (me.getX() < closestBot.getX()) {//moving left or right to match enemy robot's X coordinates
                    moveTimer++;
                    if(debug){System.out.println("movingRight "+moveTimer);}
                    return BattleBotArena.RIGHT;
                } else {
                    moveTimer++;
                    if(debug){System.out.println("movingLeft "+moveTimer);}
                    return BattleBotArena.LEFT;
                }
            }
            if (moveTimer > 5) {
                if (me.getY() < closestBot.getY()) {//moving up or down to match the enemy robot's Y coordinates
                    moveTimer++;
                    if(debug){System.out.println("movingDown "+moveTimer);}
                    return BattleBotArena.DOWN;
                } else {
                    moveTimer++;
                    if(debug){System.out.println("movingUp "+moveTimer);}
                    return BattleBotArena.UP;
                }
                }






        return 0;
    }

    /**
     * Called when it is time to draw the Bot. Your Bot should be (mostly)
     * within a circle inscribed inside a square with top left coordinates
     * <i>(x,y)</i> and a size of <i>RADIUS * 2</i>. If you are using an image,
     * just put <i>null</i> for the ImageObserver - the arena has some special features
     * to make sure your images are loaded before you will use them.
     *
     * @param g The Graphics object to draw yourself on.
     * @param x The x location of the top left corner of the drawing area
     * @param y The y location of the top left corner of the drawing area
     */
    @Override
    public void draw(Graphics g, int x, int y) {
        g.setColor(Color.BLUE);
        g.fillRect(x+2, y+2, RADIUS*2-4, RADIUS*2-4);
    }

    /**
     * This method will only be called once, just after your Bot is created,
     * to set your name permanently for the entire match.
     *
     * @return The Bot's name
     */
    @Override
    public String getName() {
        return null;
    }

    /**
     * This method is called at every time step to find out what team you are
     * currently on. Of course, there can only be one winner, but you can
     * declare and change team allegiances throughout the match if you think
     * anybody will care. Perhaps you can send coded broadcast message or
     * invitation to other Bots to set up a temporary team...
     *
     * @return The Bot's current team name
     */
    @Override
    public String getTeamName() {
        return null;
    }

    /**
     * This is only called after you have requested a SEND_MESSAGE move (see
     * the documentation for <i>getMove()</i>). However if you are already over
     * your messaging cap, this method will not be called. Messages longer than
     * 200 characters will be truncated by the arena before being broadcast, and
     * messages will be further truncated to fit on the message area of the screen.
     *
     * @return The message you want to broadcast
     */
    @Override
    public String outgoingMessage() {
        return null;
    }

    /**
     * This is called whenever the referee or a Bot sends a broadcast message.
     *
     * @param botNum The ID of the Bot who sent the message, or <i>BattleBotArena.SYSTEM_MSG</i> if the message is from the referee.
     * @param msg    The text of the message that was broadcast.
     */
    @Override
    public void incomingMessage(int botNum, String msg) {

    }

    /**
     * This is called by the arena at startup to find out what image names you
     * want it to load for you. All images must be stored in the <i>images</i>
     * folder of the project, but you only have to return their names (not
     * their paths).<br><br>
     * <p>
     * PLEASE resize your images in an image manipulation
     * program. They should be squares of size RADIUS * 2 so that they don't
     * take up much memory.
     *
     * @return An array of image names you want the arena to load.
     */
    @Override
    public String[] imageNames() {
        return new String[0];
    }

    /**
     * Once the arena has loaded your images (see <i>imageNames()</i>), it
     * calls this method to pass you the images it has loaded for you. Store
     * them and use them in your draw method.<br><br>
     * <p>
     * PLEASE resize your images in an
     * image manipulation program. They should be squares of size RADIUS * 2 so
     * that they don't take up much memory.<br><br>
     * <p>
     * CAREFUL: If you got the file names wrong, the image array might be null
     * or contain null elements.
     *
     * @param images The array of images (or null if there was a problem)
     */
    @Override
    public void loadedImages(Image[] images) {

    }
    public void calculateBulletVelocity(){


    }
}
