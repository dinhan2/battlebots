package bots;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

import java.awt.*;

public class GornikBot extends Bot {
    // Creating a bot helper object to make a use of its methods
    private BotHelper pikachuHelper = new BotHelper();

    // Stores all the pictures that are used to reflect the bot's movement
    Image up, down, left, right, current;

    // Keeping a track of my bot's last move to select a picture to reflect the move
    private int lastMove;

    // Setting up the danger zone range for my bot
    private static int disDanger = 60;

    // Setting up the shooting range for my bot
    private static int disShooting = 100;

    // Variable in charge of checking if a bullet is close to my bot
    private boolean bulletClose;

    /**
     * This method is called at the beginning of each round. Use it to perform
     * any initialization that you require when starting a new round.
     */
    @Override
    public void newRound() {

    }

    /**
     * Stores the bot's last move in a variable to choose the appropriate
     * picture to reflect its movement
     * @param lastMoveIn Takes the bot's last move
     */
    private void setLastDir(int lastMoveIn) {
        lastMove = lastMoveIn;
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
        // Resetting the variable every time the method gets called
        bulletClose = false;

        // Storing the closest bot to me in a variable
        BotInfo closestBot = pikachuHelper.findClosest(me, liveBots);

        //************************************************************************************************
        // Looping through all bullets in the world instead of just getting the closest bullet
        // (in case 2+ bullets are approaching the bot simultaneously) and dodging them.
        //************************************************************************************************

        for (Bullet bullet : bullets) {

            // Trying to dodge when a bullet is shot and is within the danger zone range
            if (pikachuHelper.calcDistance(me.getX(), me.getY(), bullet.getX(), bullet.getY()) <= disDanger) {
                // A bullet is nearby
                bulletClose = true;

                // If bullet is approaching from the right or left
                if (bullet.getX() >= me.getX() + Bot.RADIUS || bullet.getX() + Bot.RADIUS <= me.getX()) {

                    // Ensuring that only bullets that move horizontally are targeted
                    if (bullet.getXSpeed() != 0 && bullet.getYSpeed() == 0) {

                        // If my bot is above the bullet
                        if (me.getY() + Bot.RADIUS > bullet.getY()) {
                            // Dodging up
                            return BattleBotArena.DOWN;
                        }

                        // If my bot is below the bullet
                        else {
                            // Dodging down
                            return BattleBotArena.UP;
                        }
                    }
                }

                // If bullet is approaching from above or below
                if (bullet.getY() >= me.getY() + Bot.RADIUS || bullet.getY() + Bot.RADIUS <= me.getY()) {

                    // Ensuring that only bullets that move vertically are targeted
                    if (bullet.getYSpeed() != 0 && bullet.getXSpeed() == 0) {

                        // If the bullet is approaching from above and to my left
                        if (me.getX() + Bot.RADIUS > bullet.getX()) {

                            // Moving in opposite direction
                            setLastDir(BattleBotArena.RIGHT);
                            return BattleBotArena.RIGHT;
                        }
                        // If the bullet is approaching from above and to my right
                        else {
                            setLastDir(BattleBotArena.LEFT);
                            return BattleBotArena.LEFT;
                        }
                    }
                }
            }
        }

        //************************************************************************************************
        // If my bot is stuck at either edge of the screen, loosing it free by moving it to the
        // opposite direction.
        //************************************************************************************************

        // Ensuring that no bullets are nearby before checking if the bot is stuck
        if (!bulletClose) {

            // If stuck at the left edge of the screen, moving to the right
            if (me.getX() <= BattleBotArena.LEFT_EDGE + 5) {
                setLastDir(BattleBotArena.RIGHT);
                return BattleBotArena.RIGHT;
            }

            // If stuck at the right edge of the screen, moving to the left
            if (me.getX() >= BattleBotArena.RIGHT_EDGE - 35) {
                setLastDir(BattleBotArena.LEFT);
                return BattleBotArena.LEFT;

                // If stuck at the top edge of the screen, moving down
            } else if (me.getY() <= BattleBotArena.TOP_EDGE + 5) {
                setLastDir(BattleBotArena.DOWN);
                return BattleBotArena.DOWN;

                // If stuck at the bottom edge of the screen, moving up
            } else if (me.getY() >= BattleBotArena.BOTTOM_EDGE - 35) {
                setLastDir(BattleBotArena.UP);
                return BattleBotArena.UP;
            }
        }

        //************************************************************************************************
        // If my bot is stuck to a different bot, breaking it free.
        //************************************************************************************************

        //checking if im stuck or if im not moving [ FIX ]
        if (BotHelper.manhattanDist(me.getX(), me.getY(), closestBot.getX(), closestBot.getY()) <= Bot.RADIUS * 2 + 10) { //|| prevCoordinates[0] == me.getX() && prevCoordinates[1] == me.getY()
            // Ensuring that no bullets are nearby before breaking free
            if (!bulletClose) {

                // Checking if the bot approached my bot from either the left or right before getting unstuck
                if (closestBot.getLastMove() == BattleBotArena.LEFT || closestBot.getLastMove() == BattleBotArena.RIGHT) {

                    // Determining if the bot is stuck to my left and moving to the opposite direction
                    if (me.getX() + Bot.RADIUS > closestBot.getX()) {
                        setLastDir(BattleBotArena.RIGHT);
                        return BattleBotArena.RIGHT;
                    }

                    // Determining if the bot is stuck to my right and moving to the opposite direction
                    else {
                        setLastDir(BattleBotArena.LEFT);
                        return BattleBotArena.LEFT;
                    }
                }
            }

            // Checking if the bot approached my bot from either above or below before getting unstuck
            else {

                // Determining if the bot is stuck above me and moving down
                if (me.getY() + Bot.RADIUS > closestBot.getY()) {
                    setLastDir(BattleBotArena.DOWN);
                    return BattleBotArena.DOWN;
                }

                // Determining if the bot is stuck below me and moving up
                else {
                    setLastDir(BattleBotArena.UP);
                    return BattleBotArena.UP;
                }
            }
        }

        //************************************************************************************************
        // Setting up variables used to determine what direction my bot needs to move in to
        // be at its shooting range and following the bot.

        // My bot plays safe since it waits until the other bot aligns with my bot and only
        // then shoots to maximize success rate
        //************************************************************************************************

        // Calculating the X displacement between my bot and the target bot
        double dispX = pikachuHelper.calcDisplacement(me.getX() + Bot.RADIUS, closestBot.getX());

        // Calculating the Y displacement between my bot and the target bot
        double dispY = pikachuHelper.calcDisplacement(me.getY() + Bot.RADIUS, closestBot.getY());

        // Calculating the distance between my bot and the target bot
        double distanceFromBot = pikachuHelper.calcDistance(me.getX() + Bot.RADIUS, me.getY() +
                Bot.RADIUS, closestBot.getX(), closestBot.getY());

        // If my bot is within shooting range
        if (distanceFromBot > disShooting && liveBots.length != 0) {

            // Ensuring no bullets are nearby before aligning following other bots
            if (!bulletClose) {

                // If my bot has almost the "same" Y value (using range of 80 since the target bot is mostly on
                // the move) as the target bot, moving accordingly (When bots are aligned over the X axis,
                // they have the same Y value)
                if (me.getX() >= closestBot.getX() - 80 && me.getX() <= closestBot.getX() + 80) {

                    // If my bot's Y displacement is greater than 0 which means the target bot is below me,
                    // follow it until within shooting range
                    if (dispY > 0) {
                        setLastDir(BattleBotArena.DOWN);
                        return BattleBotArena.DOWN;

                        // If my bot's Y displacement is smaller than 0 which means the target bot is above me,
                        // follow it until within shooting range
                    } else if (dispY < 0) {
                        setLastDir(BattleBotArena.UP);
                        return BattleBotArena.UP;
                    }

                    // If the bots do not have almost the "same" Y value, move accordingly
                } else {

                    // If my bot's X displacement is greater than 0 which means the target bot is to my right
                    // follow it until within shooting range
                    if (dispX > 0) {
                        setLastDir(BattleBotArena.RIGHT);
                        return BattleBotArena.RIGHT;

                        // If my bot's X displacement is smaller than 0 which means the target bot is to my left,
                        // follow it until within shooting range
                    } else if (dispX < 0) {
                        setLastDir(BattleBotArena.LEFT);
                        return BattleBotArena.LEFT;
                    }
                }

            }
        }

        // Shooting occurs here once the required distance has met
        else {
            // If my bot has almost the "same" Y value (using a range of 15 to maximize the accuracy of shot)
            // as the target bot, moving accordingly (When bots are aligned over the X axis,
            // they have almost the same Y value)

            if (me.getX() >= closestBot.getX() - 15 && me.getX() <= closestBot.getX() + 15) { // aligned Y axis
                // If the bot is above me, shoot upwards
                if (shotOK && me.getY() + Bot.RADIUS > closestBot.getY() + Bot.RADIUS) {
                    return BattleBotArena.FIREUP;

                    // If the bot is below me, shoot downwards
                } else if (shotOK && me.getY() + Bot.RADIUS < closestBot.getY() + Bot.RADIUS) {
                    return BattleBotArena.FIREDOWN;
                }

                // If my bot has almost the "same" X value (using a range of 15 to maximize the accuracy of shot)
                // as the target bot, moving accordingly (When bots are aligned over the Y axis,
                // they have almost the same X value)
            } else if (me.getY() >= closestBot.getY() - 15 && me.getY() <= closestBot.getY() + 15) {

                // If the bot is to my left, shoot to its direction
                if (shotOK && me.getX() + Bot.RADIUS > closestBot.getX() + Bot.RADIUS) { // farther apart from the bot
                    return BattleBotArena.FIRELEFT;

                    // If the bot is to my right, shoot to its direction
                } else if (shotOK && me.getX() + Bot.RADIUS < closestBot.getX() + Bot.RADIUS) {
                    return BattleBotArena.FIRERIGHT;
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
        if (lastMove == BattleBotArena.UP) {
            current = up;

        } else if (lastMove == BattleBotArena.LEFT) {
            current = left;

        } else if (lastMove == BattleBotArena.DOWN) {
            current = down;
        }
        else if (lastMove == BattleBotArena.RIGHT) {
            current = right;
        }
        // Updating the image to reflect the bot's direction of movement
        g.drawImage(current, x, y, Bot.RADIUS * 2, Bot.RADIUS * 2, null);
    }

    /**
     * This method will only be called once, just after your Bot is created,
     * to set your name permanently for the entire match.
     *
     * @return The Bot's name
     */
    @Override
    public String getName() {
        return "Pikachu";
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
        return "Chiefs";
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
        String[] picPath = new String[]{"pikachu_up.png", "pikachu_down.png", "pikachu_right.png", "pikachu_left.png"};
        return picPath;
        //return new String[0];
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
        up = images[0];
        down = images[1];
        right = images[2];
        left = images[3];
        current = up;
    }
}
