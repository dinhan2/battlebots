package bots;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

import java.awt.*;
import java.lang.*;


/*****************************************************
 * BensonBot dodges incoming bullets with a range of
 * 250 to ensure a safe amount of time to escape the
 * bullets path. BensonBot only moves towards other
 * bots in the horizontal direction, as moving
 * BensonBot in both the horizontal and vertical
 * direction interferes with BensonBot's ability to
 * dodge bullets. BensonBot's ability to shoot back
 * depends on if another bot is aligned within a
 * certain range, whether vertically or horizontally.
 * BensonBot does not have a specific distance needed
 * to fire, but will shoot bullets at any distance if
 * aligned.
 *
 * @author Matthew Benson
 * April 28th, 2019
 *****************************************************/



public class BensonBot extends Bot {
    //using a bot helper to use methods such as findClosest
    private BotHelper help = new BotHelper();

    //the radius of the zone that if a bullet is detected within, BensonBot will dodge
    private static int BulletDangerClose = 250;

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
        //tests if at least 1 bot and bullet is present to avoid errors
        if (liveBots.length > 0 && bullets.length > 0) {

            //finds closest bot to BensonBot and stores in variable
            BotInfo botnearby = help.findClosest(me, liveBots);

            //finds closest bullet to BensonBot and stores in variable
            Bullet bulletnearby = help.findClosest(me, bullets);

            //calculates the distance from BensonBot to nearest bullet
            double distance = help.calcDistance(me.getX(), me.getY(), bulletnearby.getX(), bulletnearby.getY());

            //********************************************************************************************************//
            // Correctly wraps BensonBot around screen creating bounce
            //********************************************************************************************************//
            if (me.getX() <= BattleBotArena.LEFT_EDGE + 1) {
                return BattleBotArena.RIGHT;
            }
            if (me.getX() >= BattleBotArena.RIGHT_EDGE - 26) {
                return BattleBotArena.LEFT;
            }
            if (me.getY() <= BattleBotArena.TOP_EDGE + 1) {
                return BattleBotArena.DOWN;
            }
            if (me.getY() >= BattleBotArena.BOTTOM_EDGE - 26) {
                return BattleBotArena.UP;
            }

            //********************************************************************************************************//
            // Shooting mechanism of BensonBot independent of any range
            //********************************************************************************************************//
            //tests if BensonBot has an x-value close to that of a nearest bot
            if (me.getX() >= botnearby.getX() - Bot.RADIUS && me.getX() <= botnearby.getX() + Bot.RADIUS) {
                //tests position vertically to fire bullets in which direction
                if (me.getY() + Bot.RADIUS < botnearby.getY() + Bot.RADIUS) {
                    return BattleBotArena.FIREDOWN;
                } else if (me.getY() + Bot.RADIUS > botnearby.getY() + Bot.RADIUS) {
                    return BattleBotArena.FIREUP;
                }
                //tests if BensonBot has an y-value close to that of a nearest bot
            } else if (me.getY() >= botnearby.getY() - Bot.RADIUS && me.getY() <= botnearby.getY() + Bot.RADIUS) {
                //tests position horizontally to fire bullets in which direction
                if (me.getX() + Bot.RADIUS < botnearby.getX() + Bot.RADIUS) {
                    return BattleBotArena.FIRERIGHT;
                } else if (me.getX() + Bot.RADIUS > botnearby.getX() + Bot.RADIUS) {
                    return BattleBotArena.FIRELEFT;
                }
            }

            //********************************************************************************************************//
            // Incorporation of absolute value signs to dodge bullets
            // If no bullet is within danger zone to BensonBot, BensonBot will move horizontally
            //********************************************************************************************************//
            //tests if no bullets are within a certain radius of BensonBot
            if (distance > BulletDangerClose) {
                //tests position horizontally to determine which way to proceed
                if (me.getX() < botnearby.getX()) {
                    return BattleBotArena.RIGHT;
                } else if (me.getX() > botnearby.getX()) {
                    return BattleBotArena.LEFT;
                }
                //tests if bullets are within a certain radius of BensonBot
            } else if (distance < BulletDangerClose) {
                //tests if the displacement in the y is less than that in the x
                if (Math.abs(me.getY() - bulletnearby.getY()) < Math.abs(me.getX() - bulletnearby.getX())) {
                    if (me.getY() < bulletnearby.getY()) {
                        return BattleBotArena.UP;
                    } else if (me.getY() > bulletnearby.getY()) {
                        return BattleBotArena.DOWN;
                    }
                    //tests if the displacement in the y is more than that in the x
                } else if (Math.abs(me.getY() - bulletnearby.getY()) > Math.abs(me.getX() - bulletnearby.getX())) {
                    if (me.getX() < bulletnearby.getX()) {
                        return BattleBotArena.LEFT;
                    } else if (me.getX() > bulletnearby.getX()) {
                        return BattleBotArena.RIGHT;
                    }
                }
            }
        }
        //makes BensonBot stationary
        return BattleBotArena.STAY;
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
        g.setColor(Color.blue);
        g.fillRect(x + 2, y + 2, RADIUS * 2 - 4, RADIUS * 2 - 4);
    }

    /**
     * This method will only be called once, just after your Bot is created,
     * to set your name permanently for the entire match.
     *
     * @return The Bot's name
     */
    @Override
    public String getName() {
        return "BensonBot";
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
        return "Team 1";
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