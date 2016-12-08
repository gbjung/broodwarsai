package bot;

/**
 * Example of a Java AI Client that does nothing.
 */
import javafx.scene.layout.TilePane;

import jnibwapi.*;

import jnibwapi.types.UnitType;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;


public class ExampleAIClient implements BWAPIEventListener {
	private JNIBWAPI bwapi;
	private final HashSet<Unit> claimedMinerals = new HashSet<>();
	int o=0;

	/** Milestone booleans */
	private boolean firstPylon, firstGateway, firstZealot,
			firstAssimilator, secondPylon, cyberCore, firstDragoon;

	/** Number of probes at the first Assimilator */
	private int gasProbes;

	Position supply_pylon=new Position(0,0);
	int supply_count=0;
	int accurate_supply = 4;
	LinkedList pylons=new LinkedList();
	int check=0;
	int cloaked=0;
	boolean air=false;
	Position base;
	LinkedList Attack=new LinkedList();
	int race=0;
	LinkedList Zealots=new LinkedList();
	int counter=0;
	int building=0;
	Unit worker;
	Position distance;

	Position buildpylon;
	public static void main(String[] args) {

		new ExampleAIClient();
	}

	public ExampleAIClient() {
		bwapi = new JNIBWAPI(this, false);
		bwapi.start();
	}

	@Override
	public void connected() {}

	@Override
	public void matchStart() {
		System.out.println("Game Started");

		bwapi.enableUserInput();
		bwapi.enablePerfectInformation();
		bwapi.setGameSpeed(20);
		claimedMinerals.clear();
		// reset agent state
		firstPylon = false;
		firstGateway = false;
		firstZealot = false;
		firstAssimilator = false;
		secondPylon = false;
		cyberCore = false;
		firstDragoon = false;
		gasProbes = 0;

	}

	private int mainbuild(UnitType x){
		int z;
		if (supply_count==0&&firstPylon){
			z=1;
		}
		else{
			z=supply_count;
		}
		for(int t=0;t<z;t++){
			Position pylon= (Position) pylons.get(t);
			int Start_X=pylon.getX(Position.PosType.PIXEL);
			int Start_Y=pylon.getY(Position.PosType.PIXEL);
			for(Unit builder :bwapi.getMyUnits()){
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
	private int supply(){
		Position Start=bwapi.getSelf().getStartLocation();
		int Start_X=Start.getX(Position.PosType.PIXEL);
		int Start_Y=Start.getY(Position.PosType.PIXEL);
		for(Unit builder :bwapi.getMyUnits()){
			if (builder.getType()==UnitType.UnitTypes.Protoss_Probe) {
				if(supply_pylon.getX(Position.PosType.PIXEL)==0){
					if(Start_Y>3000){
						System.out.println(4);
						for(int i=300;i<500;i++) {
							int NewPylonX = Start_X + i;
							Position NewPylon = new Position(NewPylonX, Start_Y);
							if(bwapi.canBuildHere(NewPylon,UnitType.UnitTypes.Protoss_Pylon,true)) {
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
							if(bwapi.canBuildHere(NewPylon,UnitType.UnitTypes.Protoss_Pylon,true)) {
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
		int startY;
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

			if (bwapi.canBuildHere(builder,Test,UnitType.UnitTypes.Protoss_Pylon,false)) {

				System.out.println(i);
				return Test;
			}}
		return new Position(0,0);
	}

	private void attack() {
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


	private void probe(){
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
				if(!air){
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
				else if(!air){
					if(enemies.getType()==UnitType.UnitTypes.Protoss_Stargate){
						air=true;
					}
				}
			}
			if(race==3){
				if(!air){
					if(enemies.getType()==UnitType.UnitTypes.Zerg_Mutalisk){
						air=true;
					}
				}
			}


		}
	}


	@Override
	public void matchFrame() {

		//if we're running out of supply and have enough minerals ...
		Player x=bwapi.getSelf();

		probe();
		attack();
		// spawn probes
		for (Unit unit : bwapi.getMyUnits()) {
			if (unit.getType() == UnitType.UnitTypes.Protoss_Nexus) {
				//Train probes until 8 supply
				if (bwapi.getSelf().getMinerals() >= 50 && accurate_supply < 9) {
					unit.train(UnitType.UnitTypes.Protoss_Probe);
					accurate_supply ++;
					break;
				}
				//Train probes until 10 supply once the firstPylon is started
				else if (bwapi.getSelf().getMinerals() >= 50 && accurate_supply < 10 && firstPylon == true) {
					unit.train(UnitType.UnitTypes.Protoss_Probe);
					accurate_supply ++;
					break;
				}
				//Train probes until 13 supply once the Gateway is started
				else if (bwapi.getSelf().getMinerals() >= 50 && accurate_supply < 13 && firstGateway == true) {
					unit.train(UnitType.UnitTypes.Protoss_Probe);
					accurate_supply ++;
					break;
				}
				//Trains another probe after training first Zealot to hit 16 supply
				else if (bwapi.getSelf().getMinerals() >= 50 && accurate_supply < 16 && firstZealot == true) {
					unit.train(UnitType.UnitTypes.Protoss_Probe);
					accurate_supply ++;
					break;
				}
			}
			//if the Assimilator is built, assign probes to mine gas until there are three probes getting gas
			if (unit.getType() == UnitType.UnitTypes.Protoss_Probe && firstAssimilator == true && gasProbes < 3){
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
		}
		if(accurate_supply==12 && x.getMinerals()>100 && !firstAssimilator){
			mainbuild(UnitType.UnitTypes.Protoss_Assimilator);
			firstAssimilator=true;
		}

		if((accurate_supply==13 && x.getMinerals()>100 && firstZealot ==false)||(x.getMinerals()>400)){
			for(Unit unit:bwapi.getMyUnits()){
				if(unit.getType()==UnitType.UnitTypes.Protoss_Gateway){
					unit.train(UnitType.UnitTypes.Protoss_Zealot);
					accurate_supply = accurate_supply + 2;
					firstZealot=true;

				}
			}
		}

		//building check and countdown
		if(building==1){
			if(worker.getDistance(distance)<50){
				building=2;}}
		if(building==2) {
			counter += 1;
		}if(counter==500){
			counter=0;
			building=0;
			for (Unit materials : bwapi.getNeutralUnits()){
				if(materials.getType().isMineralField()){
					double distance= worker.getDistance(materials);
					if (distance<500){
						worker.rightClick(materials,false);
						claimedMinerals.add(materials);
						break;
					}}}}
		if (!firstPylon){
			for (Unit units:bwapi.getMyUnits()){
				if(units.getType()==UnitType.UnitTypes.Protoss_Pylon && units.isCompleted()) {
					firstPylon = true;
				}}}
		//ends here

		//build gateway when the first pylon is completed
		if(x.getMinerals()>150&&firstPylon&&firstGateway==false){
			mainbuild(UnitType.UnitTypes.Protoss_Gateway);
			firstGateway = true;
		}

		//keep building pylons
		if(accurate_supply==8){
			int count=0;
			for (Unit units:bwapi.getMyUnits()){
				if(units.getType()==UnitType.UnitTypes.Protoss_Pylon && units.isCompleted()){
					count+=1;
				}
			}
			if(count==supply_count+1){
				o=0;
				supply_count+=1;

			}
		}
		if(accurate_supply==8&&x.getMinerals()>150){
			supply();
			o=1;
		}

		if(accurate_supply==16 && firstZealot==true){
			int count=0;
			for (Unit units:bwapi.getMyUnits()){
				if(units.getType()==UnitType.UnitTypes.Protoss_Pylon && units.isCompleted()){
					count+=1;
				}
			}
			if(count==supply_count+1){
				o=0;
				supply_count+=1;

			}
		}
		if(accurate_supply==16&& firstZealot==true&&x.getMinerals()>150){
			supply();
			o=1;
			secondPylon=true;
		}
		if(accurate_supply==17&& secondPylon==true&&x.getMinerals()>100&&cyberCore==false){
			mainbuild(UnitType.UnitTypes.Protoss_Cybernetics_Core);
			cyberCore = true;
		}
		for (Unit myUnit : bwapi.getMyUnits()) {
			if (myUnit.getType() == UnitType.UnitTypes.Protoss_Probe) {

				if (myUnit.isIdle()){
					for (Unit materials : bwapi.getNeutralUnits()){

						if(materials.getType().isMineralField()){
							double distance= myUnit.getDistance(materials);
							if (distance<300){
								myUnit.rightClick(materials,false);
								claimedMinerals.add(materials);
								break;
							}
						}
					}
				}
			}}
		System.out.println(accurate_supply);
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