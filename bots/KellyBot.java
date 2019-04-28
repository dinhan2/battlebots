package bots;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

import java.awt.*;

/*
*My bot is defensive, prioritizing survival to maximize killing chances over time
* instead of trying to suicide-kill one or two bots.
* This mirrors how I actually play most games.
* My bot will align with another bot on one axis, then wait for the other bot to
* move into where I can shoot it, in order to be more safe from other bullets.
 */

public class KellyBot extends Bot {

    BotHelper botHelper = new BotHelper();

    // Keeping a track of my bot's last move to select a picture to reflect the move
    private int lastMove;
    private static int dangerZone = 75; //how close bullets can get before dodging
    private static int targetZone = 100; //target zone for shooting other bots
    private boolean bulletClose; //if a bullet is close

    @Override
    public void newRound() {
    }

    String name; //bot's name
    String nextMessage = null; //next message or null
    private String[] messages = {"I am a bot", "Murdering is fun!", "I am content", "I like to kill bots", "La la la la la...", "I like genocide of randbots"}; //array of messages
    Image up, down, left, right, current; //image for drawing
    private int move = BattleBotArena.UP; //current move
    private double x, y; //last location, used for detecting when stuck

    private void lastMovement(int lastMoveIn) {
        lastMove = lastMoveIn;
    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        bulletClose = false; //resetting
        BotInfo closestBot = botHelper.findClosest(me, liveBots); //find closest bot

        for (Bullet bullet : bullets) { //checking all bullets

            //dodging bullets
            if (botHelper.calcDistance(me.getX(), me.getY(), bullet.getX(), bullet.getY()) <= dangerZone) { //dodge when a bullet is in danger zone
                bulletClose = true;
                
                if (bullet.getX() >= me.getX() + Bot.RADIUS || bullet.getX() + Bot.RADIUS <= me.getX()) { //bullet approaching horizontally
                    if (bullet.getXSpeed() != 0 && bullet.getYSpeed() == 0) { //horizontal bullets
                        
                        if (me.getY() + Bot.RADIUS > bullet.getY()) { //if bullet is approaching from below
                            return BattleBotArena.DOWN;
                        } else { //if bullet is above
                            return BattleBotArena.UP;
                        }
                    }
                }

                if (bullet.getY() >= me.getY() + Bot.RADIUS || bullet.getY() + Bot.RADIUS <= me.getY()) { //if bullet is coming vertically
                    if (bullet.getYSpeed() != 0 && bullet.getXSpeed() == 0) { //check that bullet is moving vertically

                        if (me.getX() + Bot.RADIUS > bullet.getX()) { //if bullet is coming from top-left

                            lastMovement(BattleBotArena.RIGHT); //move away
                            return BattleBotArena.RIGHT;
                        } else {
                            lastMovement(BattleBotArena.LEFT);
                            return BattleBotArena.LEFT;
                        }
                    }
                }
            }
        }

        //if stuck to wall
        if (!bulletClose) { //checking if it is safe
            
            if (me.getX() <= BattleBotArena.LEFT_EDGE + 5) { //if stuck at left wall
                lastMovement(BattleBotArena.RIGHT);
                return BattleBotArena.RIGHT;
            }
            
            if (me.getX() >= BattleBotArena.RIGHT_EDGE - 35) { //if stuck at right wall
                lastMovement(BattleBotArena.LEFT);
                return BattleBotArena.LEFT;
                
            } else if (me.getY() <= BattleBotArena.TOP_EDGE + 5) { //if stuck at top wall
                lastMovement(BattleBotArena.DOWN);
                return BattleBotArena.DOWN;
                
            } else if (me.getY() >= BattleBotArena.BOTTOM_EDGE - 35) { //if stuck at top wall
                lastMovement(BattleBotArena.UP);
                return BattleBotArena.UP;
            }
        }

        //if stuck to a bot
        if (BotHelper.manhattanDist(me.getX(), me.getY(), closestBot.getX(), closestBot.getY()) <= Bot.RADIUS * 2 + 10) {
            if (!bulletClose) { //checking if it is safe
                
                if (closestBot.getLastMove() == BattleBotArena.LEFT || closestBot.getLastMove() == BattleBotArena.RIGHT) { //if stuck to a bot horizontally
                    
                    if (me.getX() + Bot.RADIUS > closestBot.getX()) {
                        lastMovement(BattleBotArena.RIGHT);
                        return BattleBotArena.RIGHT;
                    } else {
                        lastMovement(BattleBotArena.LEFT);
                        return BattleBotArena.LEFT;
                    }
                }
            } else { //if stuck to a bot vertically
                
                if (me.getY() + Bot.RADIUS > closestBot.getY()) {
                    lastMovement(BattleBotArena.DOWN);
                    return BattleBotArena.DOWN;
                } else {
                    lastMovement(BattleBotArena.UP);
                    return BattleBotArena.UP;
                }
            }
        }
        
        //shooting other bots
        double distanceX = botHelper.calcDisplacement(me.getX() + Bot.RADIUS, closestBot.getX()); //finding horizontal distance to target
        double distanceY = botHelper.calcDisplacement(me.getY() + Bot.RADIUS, closestBot.getY()); //finding vertical distance to target
        double distanceFromBot = botHelper.calcDistance(me.getX() + Bot.RADIUS, me.getY() + Bot.RADIUS, closestBot.getX(), closestBot.getY()); //total distance

        if (distanceFromBot > targetZone && liveBots.length != 0) {
            if (!bulletClose) { //if it is safe
                if (me.getX() >= closestBot.getX() - 100 && me.getX() <= closestBot.getX() + 100) { //if close to another bot on x axis

                    if (distanceY > 0) { //target is below, get within shooting range
                        lastMovement(BattleBotArena.DOWN);
                        return BattleBotArena.DOWN;
                    } else if (distanceY < 0) { //target is above, get within shooting range
                        lastMovement(BattleBotArena.UP);
                        return BattleBotArena.UP;
                    } } else { //not close to another bot on x axis

                    if (distanceX > 0) { //target is to right, get within shooting range
                        lastMovement(BattleBotArena.RIGHT);
                        return BattleBotArena.RIGHT;
                    } else if (distanceX < 0) { //target is to left, get within shooting range
                        lastMovement(BattleBotArena.LEFT);
                        return BattleBotArena.LEFT;
                    }
                }
            }
        } else { //once within shooting distance

            if (me.getX() >= closestBot.getX() - 15 && me.getX() <= closestBot.getX() + 15) { //if close on x axis
                if (shotOK && me.getY() + Bot.RADIUS > closestBot.getY() + Bot.RADIUS) { //target is above
                    return BattleBotArena.FIREUP;
                } else if (shotOK && me.getY() + Bot.RADIUS < closestBot.getY() + Bot.RADIUS) { //target is below
                    return BattleBotArena.FIREDOWN;
                }
            } else if (me.getY() >= closestBot.getY() - 15 && me.getY() <= closestBot.getY() + 15) { //if close on y axis
                if (shotOK && me.getX() + Bot.RADIUS > closestBot.getX() + Bot.RADIUS) { //bot is left
                    return BattleBotArena.FIRELEFT;
                } else if (shotOK && me.getX() + Bot.RADIUS < closestBot.getX() + Bot.RADIUS) { //bot is right
                    return BattleBotArena.FIRERIGHT;
                }
            }
        }
        return 0; //just to satisfy return statement, never gets here
    }

    @Override
    public void draw(Graphics g, int x, int y) { //drawing the bot. This can change colour, size, etc.
        g.setColor(Color.red);
        g.fillRect(x+2, y+2, RADIUS*2-4, RADIUS*2-4);
        // Updating the image to reflect the bot's direction of movement
        g.drawImage(current, x, y, Bot.RADIUS * 2, Bot.RADIUS * 2, null);
    }

    @Override
    public String getName() { //where the bot finds it's name. For now, just KellyBot.
        if (name == null)
            name = "KellyBot";
        return name;
    }

    @Override
    public String getTeamName() { //we don't really need team names yet since teams aren't implemented until part 2 maybe?
        return "Arena";
    }

    @Override
    public String outgoingMessage() { //normal message stuff
        String msg = nextMessage;
        nextMessage = null;
        return msg;
    }

    @Override
    public void incomingMessage(int botNum, String msg) { //more message stuff
    }

    @Override
    public String[] imageNames() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void loadedImages(Image[] images) { //how to find which image to use (not used)
        if (images != null) {
            current = up = images[0];
            down = images[1];
            left = images[2];
            right = images[3];
        }
    }
}
