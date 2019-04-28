package bots;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

import java.awt.*;

/**
 * The strategy of LukeBot is to play both defensively and offensively.
 * If it detects a bullet within 60 Manhattan distance, it will turn on its defensive mode
 * by moving in the opposite direction of the bullet to avoid it. When LukeBot is safe,
 * it will go on the offensive side by moving vertically to the closest bot and firing when it is
 * lined up with that bot. What makes LukeBot truely intelligent is that it is able to
 * distinguish other intelligent bots and random bots. If LukeBot is facing another intelligent bot,
 * it will play more defensively by not approaching it too closely before firing.
 * By combining both the offensive and defensive power, LukeBot will be unbeatable.
 *
 * @author Luke Liu
 * @version April 28, 2019
 */
public class LukeBot extends Bot {

    //Initiate a bot helper
    private BotHelper botHelper = new BotHelper();
    private int DANGERZONE = 60; //The distance at which that LukeBot will avoid the bullet
    private int SAFETYRANGE; //The distance that must be kept between LukeBot and other intelligent bots

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
        //Make sure there must be one live bot and one bullet (otherwise an exception would be thrown when finding the closest bot/bullet)
        if (liveBots.length > 0 && bullets.length > 0) {

            //Find the closest bot and the closest bullet
            BotInfo closestBot = botHelper.findClosest(me, liveBots);
            Bullet closestBullet = botHelper.findClosest(me, bullets);

            //************************************************************************************************
            // If a bullet is detected within the danger zone, LukeBot dodges.
            // This is placed first in the method because dodging takes a higher priority than firing.
            //************************************************************************************************

            if (BotHelper.manhattanDist(me.getX(), me.getY(), closestBullet.getX(), closestBullet.getY()) < DANGERZONE) {
                if (closestBullet.getXSpeed() != 0) { //If the bullet is moving horizontally
                    //Move up if LukeBot is above the bullet, vice versa
                    //Make sure that LukeBot is not trapped on the edge
                    if (me.getY() - Bot.RADIUS < closestBullet.getY() && me.getY() > 50 || me.getY() > 400) {
                        return BattleBotArena.UP;
                    } else {
                        return BattleBotArena.DOWN;
                    }
                } else if (closestBullet.getYSpeed() != 0) { //If the bullet is moving vertically
                    //Move right if LukeBot is to the right of the bullet, vice versa
                    //Make sure that LukeBot is not trapped on the edge
                    if (me.getX() + Bot.RADIUS > closestBullet.getX() && me.getX() < 600 || me.getX() < 50) {
                        return BattleBotArena.RIGHT;
                    } else {
                        return BattleBotArena.LEFT;
                    }
                }
            }

            //************************************************************************************************
            // If LukeBot lines up with another bot, LukeBot fires.
            //************************************************************************************************

            //When facing other intelligent bots, LukeBot takes a more defensive method by not approaching them too closely before firing.
            //When facing random bots, LukeBot takes a more offensive method by approaching their edge and securing the kill.
            if (closestBot.getName().substring(0, 4).equals("Rand")) {
                SAFETYRANGE = 0;
            } else {
                SAFETYRANGE = 4;
            }

            //If LukeBot lines up vertically with another bot
            if (me.getX() < closestBot.getX() + SAFETYRANGE + Bot.RADIUS && me.getX() > closestBot.getX() - SAFETYRANGE - Bot.RADIUS) {
                //If LukeBot is below another bot, fire upwards, and vice versa
                if (me.getY() > closestBot.getY()) {
                    return BattleBotArena.FIREUP;
                } else if (me.getY() < closestBot.getY()) {
                    return BattleBotArena.FIREDOWN;
                }
            //If LukeBot lines up horizontally with another bot
            } else if (me.getY() < closestBot.getY() + SAFETYRANGE + Bot.RADIUS && me.getY() > closestBot.getY() - SAFETYRANGE - Bot.RADIUS) {
                //If LukeBot is to the right of another bot, fire left, and vice versa
                if (me.getX() > closestBot.getX()) {
                    return BattleBotArena.FIRELEFT;
                } else if (me.getX() < closestBot.getX()) {
                    return BattleBotArena.FIRERIGHT;
                }
            }

            //************************************************************************************************
            // If Lukebot does not dodge or fire, it will attempt to move to the nearest bot.
            // For simplicity purposes, it will only move vertically.
            //************************************************************************************************

            //If LukeBot is above the closest bot, move down
            if (me.getY() < closestBot.getY()) {
                return BattleBotArena.DOWN;
            //If LukeBot is below the closest bot, move up
            } else if (me.getY() > closestBot.getY()) {
                return BattleBotArena.UP;
            }
        }

        //If there are no bullets or live bots, stay in the current position.
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
        g.setColor(Color.red);
        g.fillOval(x, y, RADIUS * 2, RADIUS * 2);
    }

    /**
     * This method will only be called once, just after your Bot is created,
     * to set your name permanently for the entire match.
     *
     * @return The Bot's name
     */
    @Override
    public String getName() {
        return "LukeBot";
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
        return "2366";
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
