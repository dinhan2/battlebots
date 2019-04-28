package bots;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

import java.awt.*;
import java.nio.channels.ClosedSelectorException;

public  class JerryBot extends Bot {


    /**
     * The strategy for my bot was planned out to be a vulture-like strategy, where it would avoid combat
     * until half players remain, then it would become aggressive and track down the nearest bot to hunt them down.
     * However, because of time and some problems i've encountered during the process, i changed my bots strategy to be much simpler.
     * It adopts a hermit strategy, it does not move but deems the space around it to be its "territory", if any bots get close, it will
     * unleash a flurry of shots. Admittedly, I believe this could be very more well-refined. I got a bit frustrated after spending countless
     * hours on this and not being able to achieve my desired product.
     */

    /**
     * Constructor for BotHelper class.
     */
    BotHelper botAssist = new BotHelper();


    /**
     * Double that stores the value that is considered a danger zone for other bots coming too close.
     */
    private int botDangerZone = 150;


    /**
     * Double tha stores the value that is considered a danger zone for incoming bullets.
     */
    private int bulletDangerZone = 50;


    /**
     * Stores the movement the bot took
     */
    private int move;






    /**
     * name for my bot. called by getName method
     */
    String botName = "Mavus";


    /**
     * This method is called at the start to obtain the number of all living bots currently.
     * @param liveBots - bots that are alive and not dead.
     * @return How many bots are alive at the start of the game.
     */
    public  int newRound(BotInfo[] liveBots) {


        return liveBots.length;
    }

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


        BotInfo closestBot = botAssist.findClosest(me, liveBots);
        Bullet closestBullet = botAssist.findClosest(me, bullets);

    // makes sure theres bots that are alive or bullets before working
        if (liveBots.length > 0 && bullets.length > 0) {


            /**
             * Bullet avoidance code
             */
            // Checks if a bullet is getting near the bot
            if (botAssist.calcDistance(me.getX(), me.getY(), closestBullet.getX(), closestBullet.getY()) < bulletDangerZone) {
                //Ensures its a bullet coming horizontally
                if (closestBullet.getXSpeed() != 0 && closestBullet.getYSpeed() == 0) {
                    //Moves up if the bullet is below the bot
                    if (me.getY() + Bot.RADIUS < closestBullet.getY()) {
                        return BattleBotArena.UP;
                    }
                    // if a bullet is on the exact same Y axis as the bot, this statement will trigger.
                    // mainly for error-trapping as it's possible for my bot to be stuck at the top or bottom then do nothing
                    //when an incoming shot comes.
                    if (me.getY() + Bot.RADIUS == closestBullet.getY()) {
                        //if at bottom of the screen and a bullet is coming, move up, otherwise stay
                        if (me.getY() >= BattleBotArena.BOTTOM_EDGE - 30) {
                            move = BattleBotArena.UP;
                            return move;
                        }
                        //if at top of the screen and a bullet is coming, move up, otherwise stay
                        if (me.getY() <= BattleBotArena.TOP_EDGE + 5) {
                            move = BattleBotArena.DOWN;
                            return move;
                        }
                        //there are rare cases from testing that if a bullet is exactly on the same axis as the bot, it will
                        // do nothing. this return statement will auto-dodge upwards no matter what if that phenomenon occurs.
                        move = BattleBotArena.UP;
                        return move;

                    }
                    //since the bullet incoming isn't below the bot, the bot will dodge downwards, as it must be above us.
                    else {
                        return BattleBotArena.DOWN;
                    }
                }
                //Ensures incoming bullet is coming vertically
                if (closestBullet.getYSpeed() != 0 && closestBullet.getXSpeed() == 0) {
                    // moves left if incoming bullet is to the right of the bot
                    if (me.getX() + Bot.RADIUS < closestBullet.getX()) {
                        move = BattleBotArena.LEFT;
                        return move;
                    }

                    // if bots position is stuck at the right screen when a bullet is coming, move to the left
                    if (me.getX() + Bot.RADIUS == closestBullet.getX()) {
                        if (me.getX() >= BattleBotArena.RIGHT_EDGE - 30) {
                            move = BattleBotArena.LEFT;
                            return move;

                        }
                        // if bots position is stuck to the left screen whena  bullet is coming, move to the right
                        if (me.getX() <= BattleBotArena.LEFT_EDGE + 5) {
                            move = BattleBotArena.RIGHT;
                            return move;
                        }
                        //bot auto-dodges left if a bullet is on the exact same X axis without being in a corner, this prevents a problem of the bot
                        //standing still if this were to happen.
                        move = BattleBotArena.LEFT;
                        return move;

                    } else { // if incoming bullet is not to  the right of the bot, will move left
                        move = BattleBotArena.RIGHT;
                        return move;
                    }

                }
                return BattleBotArena.STAY;
            }

            /**
             * Territorial/Combat code
             */
            int ShotRange = 20;
            // if the closeset bot is entering the danger zone, get ready to shoot
            if (BotHelper.manhattanDist(me.getX(), me.getY(), closestBot.getX(), closestBot.getY()) <= botDangerZone) {

            //if  the closest bot is above me, shoot upwards
                if (shotOK && me.getY() > closestBot.getY()) {
                    return BattleBotArena.FIREUP;
                }
                // if the closest bot is below me and within shot range, shoot downwards
                // this code uses manhattan difference because of some weird problems i encountered -- the bot would only
                // shoot downwards no matter where the closest bot is. this line of code and the requirement of shot range
                // is a messy fix for it but it works.
                else if (shotOK && BotHelper.manhattanDist(me.getX(), 0, closestBot.getX(), 0) < ShotRange) {
                    return BattleBotArena.FIREDOWN;
                }

                //if the closest bot is to the left of us, shoot to the left
                if (shotOK && me.getX() > closestBot.getX()) {
                    return BattleBotArena.FIRELEFT;
                }
                // if the closest bot is to the right of us, shoot to the right
                else if (shotOK && me.getX() < closestBot.getX()) {
                    return BattleBotArena.FIRERIGHT;
                }

            }
        }
        // when nothing is happening the bot stays still
        move = BattleBotArena.STAY;
        return move;
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
     * @param y The y location of the top left corner of th e drawing area
     */
    @Override
    public void draw(Graphics g, int x, int y) {
        g.setColor(Color.CYAN);
        g.fillOval(x+2, y+2, RADIUS*2, RADIUS*2);
    }

    /**
     * This method will only be called once, just after your Bot is created,
     * to set your name permanently for the entire match.
     *
     * @return The Bot's name
     */
    @Override
    public String getName() {
        return botName;
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
