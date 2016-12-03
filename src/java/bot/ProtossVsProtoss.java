package bot;

import java.util.HashSet;

import jnibwapi.BWAPIEventListener;
import jnibwapi.JNIBWAPI;
import jnibwapi.Position;
import jnibwapi.Unit;
import jnibwapi.types.TechType;
import jnibwapi.types.TechType.TechTypes;
import jnibwapi.types.UnitType;
import jnibwapi.types.UnitType.UnitTypes;
import jnibwapi.types.UpgradeType;
import jnibwapi.types.UpgradeType.UpgradeTypes;


/**
 * Created by GB on 12/3/16.
 */
public class ProtossVsProtoss implements BWAPIEventListener {
    /** reference to JNI-BWAPI */
    private final JNIBWAPI bwapi;

    /** used for mineral splits */
    private final HashSet<Unit> claimedMinerals = new HashSet<>();

    /** Spawn the Pylon at 8 supply */
    private boolean firstPylon;

    /** the probe that'll warp stuff in */
    private Unit warperProbe;

    /** Keep track of supply cap */
    private int supplyCap;

    /**
     * Create a Java AI.
     */
    public static void main(String[] args) {
        new ExampleAIClient();
    }

    /**
     * Instantiates the JNI-BWAPI interface and connects to BWAPI.
     */
    public ProtossVsProtoss() {
        bwapi = new JNIBWAPI(this, true);
        bwapi.start();
    }

    /**
     * Connection to BWAPI established.
     */
    @Override
    public void connected() {
        System.out.println("Connected");
    }

    /**
     * Called at the beginning of a game.
     */
    @Override
    public void matchStart() {
        System.out.println("Game Started");

        bwapi.enableUserInput();
        bwapi.enablePerfectInformation();
        bwapi.setGameSpeed(0);

        // reset agent state
        claimedMinerals.clear();
        firstPylon = false;
        warperProbe = null;
        supplyCap = 0;
    }

    /**
     * Called each game cycle.
     */
    @Override
    public void matchFrame() {
        // print out some info about any upgrades or research happening
        String msg = "=";
        for (TechType t : TechTypes.getAllTechTypes()) {
            if (bwapi.getSelf().isResearching(t)) {
                msg += "Researching " + t.getName() + "=";
            }
            // Exclude tech that is given at the start of the game
            UnitType whatResearches = t.getWhatResearches();
            if (whatResearches == UnitTypes.None) {
                continue;
            }
            if (bwapi.getSelf().isResearched(t)) {
                msg += "Researched " + t.getName() + "=";
            }
        }
        for (UpgradeType t : UpgradeTypes.getAllUpgradeTypes()) {
            if (bwapi.getSelf().isUpgrading(t)) {
                msg += "Upgrading " + t.getName() + "=";
            }
            if (bwapi.getSelf().getUpgradeLevel(t) > 0) {
                int level = bwapi.getSelf().getUpgradeLevel(t);
                msg += "Upgraded " + t.getName() + " to level " + level + "=";
            }
        }
        bwapi.drawText(new Position(0, 20), msg, true);

        // draw the terrain information
        bwapi.getMap().drawTerrainData(bwapi);

        earlyGame();

    }

    public void earlyGame(){
        // spawn probes
        for (Unit unit : bwapi.getMyUnits()) {
            if (unit.getType() == UnitTypes.Protoss_Nexus) {
                if (bwapi.getSelf().getMinerals() >= 50 && bwapi.getSelf().getSupplyTotal() < 8) {
                    unit.train(UnitTypes.Protoss_Probe);
                }
            }
        }

        for (Unit unit : bwapi.getMyUnits()){
            if (bwapi.getSelf().getSupplyTotal() == 8) {
                warperProbe = unit;
            }
        }

        // collect minerals
        for (Unit unit : bwapi.getMyUnits()) {
            if (unit.getType() == UnitTypes.Protoss_Probe) {
                // You can use referential equality for units, too
                if (unit.isIdle() && unit != warperProbe) {
                    for (Unit minerals : bwapi.getNeutralUnits()) {
                        if (minerals.getType().isMineralField()
                                && !claimedMinerals.contains(minerals)) {
                            double distance = unit.getDistance(minerals);

                            if (distance < 300) {
                                unit.rightClick(minerals, false);
                                claimedMinerals.add(minerals);
                                break;
                            }
                        }
                    }
                }
            }
                //if (unit == warperProbe){
                //    if(bwapi.getSelf().getMinerals() >= 100 && firstPylon == false){

                  //  }
                //}
        }


    }

    @Override
    public void keyPressed(int keyCode) {}
    @Override
    public void matchEnd(boolean winner) {}
    @Override
    public void sendText(String text) {}
    @Override
    public void receiveText(String text) {}
    @Override
    public void nukeDetect(Position p) {}
    @Override
    public void nukeDetect() {}
    @Override
    public void playerLeft(int playerID) {}
    @Override
    public void unitCreate(int unitID) {}
    @Override
    public void unitDestroy(int unitID) {}
    @Override
    public void unitDiscover(int unitID) {}
    @Override
    public void unitEvade(int unitID) {}
    @Override
    public void unitHide(int unitID) {}
    @Override
    public void unitMorph(int unitID) {}
    @Override
    public void unitShow(int unitID) {}
    @Override
    public void unitRenegade(int unitID) {}
    @Override
    public void saveGame(String gameName) {}
    @Override
    public void unitComplete(int unitID) {}
    @Override
    public void playerDropped(int playerID) {}
}

