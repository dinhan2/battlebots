
/** The Rahim bot
 *
 *     This is a bot that looks towards defence equally as offence
 *     It focuses on bullet dodging before it focuses on shooting others
 *     This is the superior strategy since there is no way it is possible to shoot everyone around you
 *     when you are surrounded by bullets. It would be like suicide
 *
 *
 *      additional improvements
 *
 *      - hide behind dead bots since bullets can't go through dead bots if no ammo left
 *      - find whether is it more efficient to move towards the right/left or up/down by
 *      referencing how far the bot is from the edge of the screen
 *      - check whether it is more efficient to follow the x or y axis towards another bot
 *      by calculating the horizontal and vertical distance 
 *
 *
 *
 *     */






package bots;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

import java.awt.*;




public class BotRahim extends Bot {


    BotHelper helpMe = new BotHelper();
    BotHelper helpMe2 = new BotHelper();

    static double dodgeZone = 200;

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

    // The dodging ifs will run before anything else to ensure that it is made a priority
    // over other ifs.
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {




        // find out the closest bullet
        Bullet closestBullet = helpMe.findClosest(me, bullets);

        // check to see how close the closest bullet is to my bot and determine if it is dangerous
        if ((helpMe.calcDistance(me.getX(), me.getY(), closestBullet.getX(), closestBullet.getY()) < dodgeZone*1.5)) {



            // check to see if the bot will get hit by the bullet, if yes, dodge
            if (Math.abs(me.getY() - closestBullet.getY()) < (Bot.RADIUS*2))  {


                // All these ifs will check which direction the bullet is coming from and then do perform appropriate dodge
                if (me.getY() < closestBullet.getY()) {

                    if (closestBullet.getYSpeed() < 0) {



                        if (BattleBotArena.RIGHT_EDGE - me.getX() > me.getX()) {

                            return BattleBotArena.RIGHT;

                        } else if (BattleBotArena.RIGHT_EDGE - me.getX() < me.getX()) {

                            return BattleBotArena.LEFT;

                        }

                    }
                }
                else if (me.getY() > closestBullet.getY()) {


                    if (closestBullet.getYSpeed() > 0) {

                        if (BattleBotArena.RIGHT_EDGE - me.getX() > me.getX()) {

                            return BattleBotArena.RIGHT;

                        } else if (BattleBotArena.RIGHT_EDGE - me.getX() < me.getX()) {

                            return BattleBotArena.LEFT;

                        }

                    }
                }


                if (me.getX() < closestBullet.getX()) {

                    if (closestBullet.getXSpeed() < 0) {



                        if (BattleBotArena.BOTTOM_EDGE - me.getY() > me.getY()) {

                            return BattleBotArena.DOWN;

                        } else if (BattleBotArena.BOTTOM_EDGE - me.getY() < me.getY()) {

                            return BattleBotArena.UP;

                        }

                    }
                }
                else if (me.getX() > closestBullet.getX()) {


                    if (closestBullet.getXSpeed() > 0) {

                        if (BattleBotArena.BOTTOM_EDGE - me.getY() > me.getY()) {

                            return BattleBotArena.DOWN;

                        } else if (BattleBotArena.BOTTOM_EDGE - me.getY() < me.getY()) {

                            return BattleBotArena.UP;

                        }

                    }
                }
            }

        }


        // find the  alive closest bot
        BotInfo closestBot = helpMe.findClosest(me, liveBots);

        // find the x and y distances between the bot and me
        double xDist = Math.abs(me.getX() - closestBot.getX());
        double yDist = Math.abs(me.getY() - closestBot.getY());


        // is the bot in danger range and is alive
        if ((((BotHelper.manhattanDist(me.getX(), me.getY(), closestBot.getX(), closestBot.getY())) < dodgeZone)) && !(closestBot.isDead())) {



//          check to see if it is in the shooting range
//          by subtracting the y values, we can see if it is more or less than the radius which would tell us if it can be hit by a bullet
            if (Math.abs(me.getY() - closestBot.getY()) < (Bot.RADIUS*2))  {


                // find a new bot if the previous once is dead
               if (closestBot.isDead()) {

                   closestBot = helpMe.findClosest(me, liveBots);

               }

               // fire appropriately
                if (me.getX() < closestBot.getX()) {
                    return BattleBotArena.FIRERIGHT;
                }
                else if (me.getX() > closestBot.getX()) {
                    return BattleBotArena.FIRELEFT;

                }

            }
            if (Math.abs(me.getX() - closestBot.getX()) < (Bot.RADIUS*2))  {

                if (closestBot.isDead()) {

                    closestBot = helpMe.findClosest(me, liveBots);

                }

                if (me.getY() < closestBot.getY()) {
                    return BattleBotArena.FIREDOWN;

                }
                else if (me.getY() > closestBot.getY()) {
                    return BattleBotArena.FIREUP;

                }



            }


        }

            // find the most efficient route to the nearest bot
            else if (xDist < yDist) {

                if (me.getX() < closestBot.getX()) {

                    return BattleBotArena.RIGHT;

                } else if (me.getX() > closestBot.getX()) {

                    return BattleBotArena.LEFT;

                }

            }

            else if (yDist < xDist) {

                if (me.getY() < closestBot.getY()) {

                    return BattleBotArena.DOWN;

                }
                else if (me.getY() > closestBot.getY()) {

                    return BattleBotArena.UP;

                }

                // if there are no bullets left, try to hide behind a dead bot with the same logic
                // used to get near bots that are wanted dead
                if (!shotOK) {

                    //System.out.println("reload");
                    BotInfo deadBot = helpMe.findClosest(me, deadBots);

                    double xDist2 =  Math.abs(me.getX() - deadBot.getX());
                    double yDist2 =  Math.abs(me.getY() - deadBot.getY());

                    if ((BotHelper.manhattanDist(me.getX(), me.getY(), deadBot.getX(), deadBot.getY()) < dodgeZone-100)){

                        if ((me.getX() < closestBot.getX()) && (me.getX() < deadBot.getX()) && (deadBot.getX() < closestBot.getX())) {

                            if (xDist2 < yDist2) {

                                if (me.getX() < deadBot.getX()) {

                                    return BattleBotArena.RIGHT;

                                } else if (me.getX() > deadBot.getX()) {

                                    return BattleBotArena.LEFT;

                                }

                                else if (yDist2 < xDist2) {

                                if (me.getY() < deadBot.getY()) {

                                    return BattleBotArena.DOWN;

                                } else if (me.getY() > deadBot.getY()) {

                                    return BattleBotArena.UP;

                                }

                            }
                        }
                    }
                }
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

        g.setColor(Color.green);
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
}
