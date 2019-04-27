package bots;
import java.awt.*;
import java.awt.event.ActionEvent;
import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

/**
 * <b>Introduction</b> <br><br>
 *
 * GAME PLAN [This plan I made is simple, track the targets and shoot them, 
 * all while dodging bullets, my bot follows the closest bot, dodges the closest bullet, and also shoots at the closest bot]
 * Sambot created week of April 22nd, 2019.
 *
 * This is an abstract class for a BattleBots player. All Bots to be used in the arena
 * <i>must</i> extend this class, and <i>must</i> be part of the <i>bots</i>
 * package.<br><Br>
 *
 * The Bots decide at each frame whether to move, shoot, and message one another,
 * but they do <i>not</i> get to decide where they are, and they do <i>not</i> get to do
 * any processing except when their methods are called by the BattleBotArena (i.e. no
 * Timers or extra Threads allowed). Bots are also limited in the amount of processing
 * they can do in the course of a match (see the rules on this in the BattleBotArena
 * class). <br><br>
 *
 * The arena is the referee and decides where the Bots are based on the moves they request.
 * It also decides when they are allowed to make those moves, when they overheat, and when
 * they die. The Bots do draw themselves, but are honour-bound to draw themselves at the
 * location the Arena gives them when it calls their <i>draw()</i> method. <br><br>
 *
 * <b>Rules</b><ol>
 *
 * <li>All code for a single Bot must be contained within a single class within a single file.
 * No inner classes or anonymous inner classes are allowed either.</li>
 *
 * <li>Bots may not spawn threads or use timers or any other method to get around
 * the CPU limits imposed by the arena.</li>
 *
 * <li>Bots should draw themselves (mostly) within a circle inscribed in
 * a square of side length equal to RADIUS * 2.</li>
 *
 * <li>You may draw your Bots using images (see <i>imageNames()</i> and <i>loadedImages()</i>
 * below) but these images should be resized to be squares of side length RADIUS * 2 so as
 * not to take up too much memory (most of the available memory is needed for the instant
 * replay system).</li>
 *
 * <li>Messages to other Bots will be truncated to 200 characters. For display purposes,
 * they will be truncated to whatever fits on the screen.</li></ol>
 * <br>
 *
 * <b>Rubric</b><br><br>
 *
 * To get an A+, your robot should meet all of the following criteria:
 * <ol>
 * <li>Your code must meet all the standards and rules set out in this documentation.</li>
 * <li>Your code must be fully documented to javadoc standards.</li>
 * <li>Your strategy must be completely described in the class header. It should be well thought out
 * and ambitious.</li>
 * <li>Your code must be commented in a way that makes it clear where and how the different
 * parts of your strategy are implemented.</li>
 * <li>Your strategy must make use of at least two of the three arrays of information provided
 * by the Arena in each call to <i>getMove()</i>.</li>
 * <li>Your code must be as efficient as possible. There should be no superfluous or unnecessary
 * comparison or assignment operations.</li>
 * </ol>
 * <br>
 *
 * <b>More Information</b><br><br>
 *
 * For a more complete set of rules, see the BattleBotArena class. For more info about this class,
 * see the comments on each of its methods.
 *
 * @author Sam Scott
 * @version 1.0 (March 3, 2011)
 * @version 1.1 (Nov 6 2017) ROWBOTTOM changed bot radius to 15
 */
public class SamBot extends Bot{
    BotHelper helpbot = new BotHelper(); //new bothelper
    private String name; //bot name string
    private String[] killMessage = {"SamBot #1!!!!", "EPIC", "HEHEHEHEHE","BYE BYE!!!"}; //trashtalk messages
    private int msgCounter = 0; //message counter
    private int move; //message maker
    private String nextMessage = null; //more stuff for making messages
    private Image image = null; //bot image
    /**
     * The radius of a Bot. Each Bot should fit into a circle inscribed into a
     * square with height and width equal to RADIUS * 2.
     */
    public static final int RADIUS = 13;
    /**
     * This is your Bot's number, a unique identifier assigned at the beginning of each round.
     */
    protected int botNumber;
    /**
     * This method is called at the beginning of each round. Use it to perform
     * any initialization that you require when starting a new round.
     */
    public void newRound(){
    }
    /**
     * This method is called at every time step to find out what you want your
     * Bot to do. The legal moves are defined in constants in the BattleBotArena
     * class (UP, DOWN, LEFT, RIGHT, FIREUP, FIREDOWN, FIRELEFT, FIRERIGHT, STAY,
     * SEND_MESSAGE). <br><br>
     *
     * The <b>FIRE</b> moves cause a bullet to be created (if there are
     * not too many of your bullets on the screen at the moment). Each bullet
     * moves at speed set by the BULLET_SPEED constant in BattleBotArena. <br><br>
     *
     * The <b>UP</b>, <b>DOWN</b>, <b>LEFT</b>, and <b>RIGHT</b> moves cause the
     * bot to move BOT_SPEED
     * pixels in the requested direction (BOT_SPEED is a constant in
     * BattleBotArena). However, if this would cause a
     * collision with any live or dead bot, or would move the Bot outside the
     * playing area defined by TOP_EDGE, BOTTOM_EDGE, LEFT_EDGE, and RIGHT_EDGE,
     * the move will not be allowed by the Arena.<br><Br>
     *
     * The <b>SEND_MESSAGE</b> move (if allowed by the Arena) will cause a call-back
     * to this Bot's <i>outgoingMessage()</i> method, which should return the message
     * you want the Bot to broadcast. This will be followed with a call to
     * <i>incomingMessage(String)</i> which will be the echo of the broadcast message
     * coming back to the Bot.
     *
     * @param-me		A BotInfo object with all publicly available info about this Bot
     * @param-shotOK	True iff a FIRE move is currently allowed
     * @param-liveBots	An array of BotInfo objects for the other Bots currently in play
     * @param-deadBots	An array of BotInfo objects for the dead Bots littering the arena
     * @param-bullets	An array of all Bullet objects currently in play
     * @return			A legal move (use the constants defined in BattleBotArena)
     *
     * The core of Sambots functions, its dodging, shooting, and chasing occur in this method
     */
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        if (liveBots.length > 0 && bullets.length > 0) {  //Prevents all errors
            BotInfo closest = helpbot.findClosest(me, liveBots); //closest alive bot
            Bullet closestbullet = helpbot.findClosest(me, bullets); //closest bullet
            if (--msgCounter == 0) { //trash talking
                move = BattleBotArena.SEND_MESSAGE;
                return move; //sending the trash talk
            }
        if (shotOK) { //if its ok to shoot
            if (me.getX() < closest.getX() && me.getY() > closest.getY() - Bot.RADIUS) { //if closest enemy is to the right shoot right, used - and + radius for precise shooting and gives it a larger window to shoot instead of ==
                return BattleBotArena.FIRERIGHT;
            }
            else if (me.getX() < closest.getX() && me.getY() > closest.getY() + Bot.RADIUS) { //if closest enemy is to the right shoot right
                return BattleBotArena.FIRERIGHT;
            }else if (me.getX() > closest.getX() && me.getY() < closest.getY() + Bot.RADIUS) { //if closest enemy is to the left shoot left
                return BattleBotArena.FIRELEFT;
            }
            else if (me.getX() > closest.getX() && me.getY() < closest.getY() - Bot.RADIUS) { //if closest enemy is to the left shoot left
                return BattleBotArena.FIRELEFT;
            }
                if (me.getY() > closest.getY() && me.getX() < closest.getX() - Bot.RADIUS) { //if closest enemy is above it shoot up
                return BattleBotArena.FIREUP;
            } else if (me.getY() > closest.getY() && me.getX() < closest.getX() + Bot.RADIUS) { //if closest enemy is below shoot down
                return BattleBotArena.FIREUP;
            }
            else if (me.getY() < closest.getY() && me.getX() > closest.getX() - Bot.RADIUS) { //if closest enemy is above it shoot up
                return BattleBotArena.FIREDOWN;
            } else if (me.getY() < closest.getY() && me.getX() > closest.getX() + Bot.RADIUS) { //if closest enemy is below shoot down
                return BattleBotArena.FIREDOWN;
            }

        }
            double b = BotHelper.manhattanDist(closestbullet.getX(), closestbullet.getY(), me.getX(), me.getY()); //tracking closest bullet distance
        if (b <= 50) { //if the distance is or is below 30
            if (closestbullet.getX() > me.getX() || closestbullet.getX() < me.getX()) { //if closest bullet is to the left or right of it go up
                return BattleBotArena.UP;
            } else if (closestbullet.getY() < me.getY() || closestbullet.getY() > me.getY()) { //if closest bullet is above or below go left
                return BattleBotArena.LEFT;
            }
        }
            if (me.getX() > closest.getX() - Bot.RADIUS) { //if closest enemy is to the left go left
                return BattleBotArena.LEFT;
            } else if (me.getX() < closest.getX() + Bot.RADIUS) { //if closest enemy is to the right go right
                return BattleBotArena.RIGHT;
            } else if (me.getY() > closest.getY() - Bot.RADIUS) { //if closest enemy is to up go up
                return BattleBotArena.UP;
            } else if (me.getY() < closest.getY() + Bot.RADIUS) { //if closest enemy is down go down
                return BattleBotArena.DOWN;
            }
        }
        return 0; //return nothing
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
     *
     * configuring bot size and drawing it
     */
    public void draw (Graphics g, int x, int y){
        if (image != null)
            g.drawImage(image, x, y, Bot.RADIUS*2, Bot.RADIUS*2, null); //bot  maker

    }
    /**
     * This method will only be called once, just after your Bot is created,
     * to set your name permanently for the entire match.
     *
     * @return The Bot's name
     *
     * Naming my bot
     */
    public String getName(){
        if (name == null)
            name = "SamBot"; //setting name
        return name;
    }
    /**
     * This method is called at every time step to find out what team you are
     * currently on. Of course, there can only be one winner, but you can
     * declare and change team allegiances throughout the match if you think
     * anybody will care. Perhaps you can send coded broadcast message or
     * invitation to other Bots to set up a temporary team...
     *
     * @return The Bot's current team name
     *
     * Basic team name
     */
    public String getTeamName()
    {
        return "Team Sam"; //setting team name
    }
    /**
     * This is only called after you have requested a SEND_MESSAGE move (see
     * the documentation for <i>getMove()</i>). However if you are already over
     * your messaging cap, this method will not be called. Messages longer than
     * 200 characters will be truncated by the arena before being broadcast, and
     * messages will be further truncated to fit on the message area of the screen.
     *
     * @return The message you want to broadcast
     *
     */
    public String outgoingMessage(){
        String msg = nextMessage;
        nextMessage = null;
        return msg;
    }
    /**
     * This is called whenever the referee or a Bot sends a broadcast message.
     *
     * @param botNum The ID of the Bot who sent the message, or <i>BattleBotArena.SYSTEM_MSG</i> if the message is from the referee.
     * @param msg The text of the message that was broadcast.
     *
     *  Checking to see if bot can trashtalk
     */
    public void incomingMessage(int botNum, String msg){
        if (botNum == BattleBotArena.SYSTEM_MSG && msg.matches(".*destroyed by "+getName()+".*"))
        {
            int msgNum = (int)(Math.random()*killMessage.length);
            nextMessage = killMessage[msgNum];
            msgCounter = (int)(Math.random()*30 + 30);
        }
    }
    /**
     * This is called by the arena at startup to find out what image names you
     * want it to load for you. All images must be stored in the <i>images</i>
     * folder of the project, but you only have to return their names (not
     * their paths).<br><br>
     *
     * PLEASE resize your images in an image manipulation
     * program. They should be squares of size RADIUS * 2 so that they don't
     * take up much memory.
     *
     * @return An array of image names you want the arena to load.
     *
     * Setting my image
     */
    public  String[] imageNames(){
        {
            String[] paths = {"sambot.png"}; //my custom image
            return paths;
        }
    }
    /**
     * Once the arena has loaded your images (see <i>imageNames()</i>), it
     * calls this method to pass you the images it has loaded for you. Store
     * them and use them in your draw method.<br><br>
     *
     * PLEASE resize your images in an
     * image manipulation program. They should be squares of size RADIUS * 2 so
     * that they don't take up much memory.<br><br>
     *
     * CAREFUL: If you got the file names wrong, the image array might be null
     * or contain null elements.
     *
     * @param images The array of images (or null if there was a problem)
     *
     *
     */
    public void loadedImages(Image[] images){
        if (images != null)
        {

            if (images != null && images.length > 0)
                image = images[0];

        }
    }
    /**
     * Called by the arena to assign your unique id number at the start of each round.
     * There is probably no need to override this method.
     *
     * @param botNum Your ID number
     */
    public void assignNumber(int botNum)
    {
        this.botNumber = botNum;
    }
    /**
     * Stops Bot developers from cheating by spawning a Thread. The human referee
     * should also check to make sure they are only using a single class and no
     * inner classes (check to make sure there is only one .class file per Bot).
     */
    final public void run()
    {

    }
    /**
     * Stops Bot developers from cheating by using a Timer. The human referee
     * should also check to make sure they are only using a single class and no
     * inner classes (check to make sure there is only one .class file per Bot).
     */
   final public void actionPerformed(ActionEvent e)
    {
    }
}
