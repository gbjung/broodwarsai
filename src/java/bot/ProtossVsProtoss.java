package bot;

import java.util.HashSet;
import java.util.LinkedList;

import jnibwapi.*;
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
            firstAssimilator, secondPylon, cyberCore, firstDragoon, completion, worker
            ;

    /** Number of probes at the first Assimilator */
    private int gasProbes;
    int o=0;
    Position supply_pylon=new Position(0,0);
    int supply_count=0;
    LinkedList pylons=new LinkedList();
    int gateway=0;
    int check=0;
    Position distance;
    int building;
    int reset=0;

    Position buildpylon;

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
                        mainbuild(UnitTypes.Protoss_Pylon); //BUILD PYLON
                        firstPylon = true;
                    }
                }
            }
        }
        //At 10 supply, have the warper probe build a gateway if enough minerals
        if (bwapi.getSelf().getSupplyUsed() == 20) {
            if(bwapi.getSelf().getMinerals() >= 150){
                mainbuild(UnitTypes.Protoss_Gateway); //BUILD GATEWAY
                firstGateway = true;
            }
        }
        //at 12 supply, have the warper probe build an assimilator
        if (bwapi.getSelf().getSupplyUsed() == 24) {
            if(bwapi.getSelf().getMinerals() >= 100){
                mainbuild(UnitTypes.Protoss_Assimilator); //BUILD ASSIMILATOR
                firstAssimilator = true;
            }
        }
        //at 16 supply, build a pylon
        if (bwapi.getSelf().getSupplyUsed() == 32) {
            if(bwapi.getSelf().getMinerals() >= 100){
                mainbuild(UnitTypes.Protoss_Pylon); //BUILD SECOND PYLON
                secondPylon = true;
            }
        }
        //at 17 supply after our second pylon build a Cybernetics Core
        if (secondPylon == true && bwapi.getSelf().getMinerals() >= 100 && cyberCore == false) {
            mainbuild(UnitTypes.Protoss_Cybernetics_Core); //BUILD CYBERNETICS CORE
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

    public int mainbuild(UnitType x){
    int z=0;
    if (supply_count==0&&completion==true){
        z=1;
    }
    else{
        z=supply_count;
    }
    for(int t=0;t<z;t++){
       Position pylon= (Position) pylons.get(t);
       int Start_X=pylon.getX(Position.PosType.PIXEL);
       int Start_Y=pylon.getY(Position.PosType.PIXEL);
       for(Unit builder :bwapi.getUnits(bwapi.getSelf())){
           if (builder.getType()==UnitType.UnitTypes.Protoss_Probe) {
              for(int i=0;i<300;i++){
                int Newbuilding = Start_X + x.getDimensionUp()+i;
                int Newbuilding1=Start_X - x.getDimensionUp()+i;
                int NewBuilding2=Start_Y+x.getDimensionUp()+i;
                int NewBuilding3=Start_Y-x.getDimensionUp()-i;

                Position Newbuildingleft=new Position(Start_X,NewBuilding3);
                Position Newbuildingright=new Position(Start_X,NewBuilding2);
                Position Newbuildingup=new Position(Newbuilding,Start_Y);
                Position Newbuildingdown=new Position(Newbuilding1,Start_Y);
                worker=builder;
                building=1;
                if(bwapi.canBuildHere(Newbuildingleft,x,true)){
                   builder.build(Newbuildingleft,x);
                   distance=Newbuildingleft;

                   return 1;
                }
                else if(bwapi.canBuildHere(Newbuildingright,x,true)){
                   builder.build(Newbuildingright,x);
                   distance=Newbuildingright;
                   return 1;
                }
                else if(bwapi.canBuildHere(Newbuildingup,x,true)){
                   builder.build(Newbuildingup,x);
                   distance=Newbuildingup;
                   return 1;
                }
                else if(bwapi.canBuildHere(Newbuildingdown,x,true)){
                   builder.build(Newbuildingdown,x);
                   distance=Newbuildingdown;
                   return 1;
                }
              }

           }}}return 0;}
  public int supply(){
    Position Start=bwapi.getSelf().getStartLocation();
    int Start_X=Start.getX(Position.PosType.PIXEL);
    int Start_Y=Start.getY(Position.PosType.PIXEL);
    for(Unit builder :bwapi.getUnits(bwapi.getSelf())){
       if (builder.getType()==UnitType.UnitTypes.Protoss_Probe) {
           if(supply_pylon.getX(Position.PosType.PIXEL)==0){
              if(Start_Y>3000){
                System.out.println(4);
                for(int i=300;i<500;i++) {
                   int NewPylonX = Start_X + i;
                   Position NewPylon = new Position(NewPylonX, Start_Y);
                   if(bwapi.canBuildHere(NewPylon,UnitType.UnitTypes.Protoss_Pylon,true)==true) {
                       builder.build(NewPylon, UnitType.UnitTypes.Protoss_Pylon);
                       pylons.add(NewPylon);
                       supply_pylon = NewPylon;
                       return 1;
                   }}}
              if (Start_Y<3000){
                System.out.println(3);
                for(int i=300;i<500;i++){
                   int NewPylonX=Start_X-i;
                   Position NewPylon=new Position(NewPylonX,Start_Y);
                   if(bwapi.canBuildHere(NewPylon,UnitType.UnitTypes.Protoss_Pylon,true)==true) {
                       pylons.add(NewPylon);
                       builder.build(NewPylon, UnitType.UnitTypes.Protoss_Pylon);
                       supply_pylon = NewPylon;
                       return 1;
                   }}}}
           else{
              int PylonY=supply_pylon.getY(Position.PosType.PIXEL);
              int Pylonx=supply_pylon.getX(Position.PosType.PIXEL);
              if(Start_Y>3000){
                System.out.println(2);
                for(int i=0;i<200;i++){
                   PylonY=supply_pylon.getY(Position.PosType.PIXEL)+UnitType.UnitTypes.Protoss_Pylon.getDimensionUp()+i;
                   Position Newpylon=new Position(Pylonx,PylonY);
                   if(bwapi.canBuildHere(Newpylon,UnitType.UnitTypes.Protoss_Pylon,true)==true){
                       builder.build(Newpylon, UnitType.UnitTypes.Protoss_Pylon);
                       supply_pylon = Newpylon;
                       pylons.add(Newpylon);
                       return 1;
                   }
                }
              }
              else if(Start_Y<3000){
                System.out.println(1);
                for(int i=0;i<200;i++){
                   PylonY=supply_pylon.getY(Position.PosType.PIXEL)+UnitType.UnitTypes.Protoss_Pylon.getDimensionUp()+i;
                   Position Newpylon=new Position(Pylonx,PylonY);
                   if(bwapi.canBuildHere(Newpylon,UnitType.UnitTypes.Protoss_Pylon,true)==true) {
                       builder.build(Newpylon, UnitType.UnitTypes.Protoss_Pylon);
                       supply_pylon = Newpylon;
                       pylons.add(Newpylon);
                       return 1;
                   }}}


           }
       }}return 0;}


  public Position find(int count,Unit builder){
    Player player=bwapi.getSelf();
    int startY=0;
    for(int i=300;i<500;i++){
       int startX=player.getStartLocation().getX(Position.PosType.PIXEL);
       if (player.getStartLocation().getX(Position.PosType.PIXEL)>3000){
           startY=player.getStartLocation().getY(Position.PosType.PIXEL)-(count*10);}
       else{
           startY=player.getStartLocation().getY(Position.PosType.PIXEL)-(count*10);
       }
       if(startX>3000){
           startX-=i;

       }
       else{
           startX+=i;}

       Position Test=new Position(startX,startY);

       if (bwapi.canBuildHere(builder,Test,UnitType.UnitTypes.Protoss_Pylon,false)==true) {

           System.out.println(i);
           return Test;
       }}
    return new Position(0,0);
  }

  public void attack() {
    if(Zealots.size()>=10){
       for(int i=0;i<(Math.round(Zealots.size()*.5));i++){
           Attack.add(Zealots.get(i));
           Zealots.remove(i);

    }
  }
    for(int i=0;i<Attack.size();i++){
       Unit attacker=(Unit)Attack.get(i);
       attacker.attack(base,false);
    }
  }


  public void probe(){
       bwapi.getEnemyUnits();
           for (Unit enemies:bwapi.getEnemyUnits()){
              if(race==0) {
                if (enemies.getType() == UnitType.UnitTypes.Terran_Command_Center) {
                   race = 1;
                   base=enemies.getPosition();
                } else if (enemies.getType() == UnitType.UnitTypes.Protoss_Nexus) {
                   race = 2;
                   base=enemies.getPosition();
                } else if (enemies.getType() == UnitType.UnitTypes.Zerg_Hatchery) {
                   race = 3;
                   base=enemies.getPosition();
                }

              }
              if(race==1){
                if(air==false){
                   if(enemies.getType()==UnitType.UnitTypes.Terran_Starport){
                       air=true;
                   }
                }
              }
              if(race==2){
                if(cloaked==0){
                   if(enemies.getType()==UnitType.UnitTypes.Protoss_Dark_Templar){
                       cloaked=1;
                   }}
                else if(air==false){
                   if(enemies.getType()==UnitType.UnitTypes.Protoss_Stargate){
                       air=true;
                   }
                }
              }
              if(race==3){
                if(air==false){
                   if(enemies.getType()==UnitType.UnitTypes.Zerg_Mutalisk){
                       air=true;
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

