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

    /** the probe that'll warp stuff in */
    private Unit warperProbe;

    /** Milestone booleans */
    private boolean firstPylon, firstGateway, firstZealot,
            firstAssimilator, secondPylon, cyberCore, firstDragoon;

    /** Number of probes at the first Assimilator */
    private int gasProbes;

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
        warperProbe = null;
        firstPylon = false;
        firstGateway = false;
        firstZealot = false;
        firstAssimilator = false;
        secondPylon = false;
        cyberCore = false;
        firstDragoon = false;
        gasProbes = 0;

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
                //Train probes until 8 supply
                if (bwapi.getSelf().getMinerals() >= 50 && bwapi.getSelf().getSupplyUsed() < 16) {
                    unit.train(UnitTypes.Protoss_Probe);
                }
                //Train probes until 10 supply once the firstPylon is started
                if (bwapi.getSelf().getMinerals() >= 50 && bwapi.getSelf().getSupplyUsed() < 20 && firstPylon == true) {
                    unit.train(UnitTypes.Protoss_Probe);
                }
                //Train probes until 13 supply once the Gateway is started
                if (bwapi.getSelf().getMinerals() >= 50 && bwapi.getSelf().getSupplyUsed() < 26 && firstGateway == true) {
                    unit.train(UnitTypes.Protoss_Probe);
                }
                //Trains another probe after training first Zealot to hit 16 supply
                if (bwapi.getSelf().getMinerals() >= 50 && bwapi.getSelf().getSupplyUsed() < 34 && firstZealot == true){
                    unit.train(UnitTypes.Protoss_Probe);
                }
            }
            //if the Assimilator is built, assign probes to mine gas until there are three probes getting gas
            if (unit.getType() == UnitTypes.Protoss_Probe && firstAssimilator == true && gasProbes < 3 && unit != warperProbe){
                for (Unit gas : bwapi.getNeutralUnits()) {
                    if (gas.getType().isRefinery()) {
                        double distance = unit.getDistance(gas);

                        if (distance < 300) {
                            unit.rightClick(gas, false);
                            gasProbes++;
                            break;
                        }
                    }
                }


            }
            if (unit.getType() == UnitTypes.Protoss_Gateway){
                //at 13 supply, train a zealot
                if (bwapi.getSelf().getSupplyUsed() == 26) {
                    if(bwapi.getSelf().getMinerals() >= 100 && firstGateway == true){
                        if (unit.getType() == UnitTypes.Protoss_Gateway){
                            unit.train(UnitTypes.Protoss_Zealot);
                            firstZealot = true;
                        }
                    }
                }
                //after the cybernetics core, build the first dragoon
                if (cyberCore == true && firstDragoon == false && bwapi.getSelf().getMinerals() >= 125
                        && bwapi.getSelf().getGas() >= 50){
                    unit.train(UnitTypes.Protoss_Dragoon);
                    firstDragoon = true;

                }

            }
        }

        //At 8 supply, assign the last spawned probe as the warper probe and have it build a pylon if minerals are sufficient
        if (bwapi.getSelf().getSupplyUsed() == 16) {
            for (Unit unit :  bwapi.getMyUnits()){
                if (unit.getType() == UnitTypes.Protoss_Probe){
                    if(unit.isIdle() && warperProbe == null){
                        warperProbe = unit;
                    }
                    if(bwapi.getSelf().getMinerals() >= 100){
                        warperProbe.build(); //BUILD PYLON
                        firstPylon = true;
                    }
                }
            }
        }
        //At 10 supply, have the warper probe build a gateway if enough minerals
        if (bwapi.getSelf().getSupplyUsed() == 20) {
            if(bwapi.getSelf().getMinerals() >= 150){
                warperProbe.build(); //BUILD GATEWAY
                firstGateway = true;
            }
        }
        //at 12 supply, have the warper probe build an assimilator
        if (bwapi.getSelf().getSupplyUsed() == 24) {
            if(bwapi.getSelf().getMinerals() >= 100){
                warperProbe.build(); //BUILD ASSIMILATOR
                firstAssimilator = true;
            }
        }
        //at 16 supply, build a pylon
        if (bwapi.getSelf().getSupplyUsed() == 32) {
            if(bwapi.getSelf().getMinerals() >= 100){
                warperProbe.build(); //BUILD SECOND PYLON
                secondPylon = true;
            }
        }
        //at 17 supply after our second pylon build a Cybernetics Core
        if (secondPylon == true && bwapi.getSelf().getMinerals() >= 100 && cyberCore == false) {
            warperProbe.build(); //BUILD CYBERNETICS CORE
            cyberCore = true;
        }

        //have all idle probes mine minerals
        for (Unit unit : bwapi.getMyUnits()) {
            if (unit.getType() == UnitTypes.Protoss_Probe) {
                if (unit.isIdle()) {
                    for (Unit minerals : bwapi.getNeutralUnits()) {
                        if (minerals.getType().isMineralField()) {
                            double distance = unit.getDistance(minerals);

                            if (distance < 300) {
                                unit.rightClick(minerals, false);
                                break;
                            }
                        }
                    }
                }
            }
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

