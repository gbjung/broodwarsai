package bot;

/**
 * Example of a Java AI Client that does nothing.
 */
import jnibwapi.*;
import jnibwapi.types.UnitType;
import jnibwapi.util.BWColor;

import java.util.HashSet;
import java.util.LinkedList;

public class MinimalAIClient implements BWAPIEventListener {
	private JNIBWAPI bwapi;
	int o=0;
	private final HashSet<Unit> claimedMinerals = new HashSet<>();

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
	int location;
	int override=0;
	Position origin;
	Position distance;
	Position runover;
	Position runoverorigin;

	Position buildpylon;
	public static void main(String[] args) {
		new MinimalAIClient();
	}

	public MinimalAIClient() {
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
	public void matchFrame() {

		//if we're running out of supply and have enough minerals ...
		Player x=bwapi.getSelf();

		probe();
		attack();
		// spawn probes
		for (Unit unit : bwapi.getMyUnits()) {
			if (unit.getType() == UnitType.UnitTypes.Protoss_Nexus) {
				//Train probes until 8 supply
				if (x.getMinerals() >= 50 && accurate_supply < 8) {
					unit.train(UnitType.UnitTypes.Protoss_Probe);
					accurate_supply ++;
					System.out.println(accurate_supply);
					break;
				}
				//Train probes until 13 supply once the Gateway is started
				else if (x.getMinerals() >= 50 &&firstPylon == true && accurate_supply < 12 && firstGateway == false) {
					unit.train(UnitType.UnitTypes.Protoss_Probe);
					accurate_supply ++;
					System.out.println(accurate_supply);
					break;
				}
			}





			}
		//Assimilator build section
		/**
		 if(accurate_supply==12 && x.getMinerals()>100 && !firstAssimilator){
		 mainbuild(UnitType.UnitTypes.Protoss_Assimilator);
		 firstAssimilator=true;
		 }
		 **/
		for(Unit unit:bwapi.getMyUnits()){
			//Trains Zealots once supply is 13 or if a zealot hasnt been produced yet
			if(unit.getType()==UnitType.UnitTypes.Protoss_Gateway){
				if((accurate_supply==13 && x.getMinerals()>100 && firstZealot ==false)||firstZealot==false){
					unit.train(UnitType.UnitTypes.Protoss_Zealot);
					accurate_supply = accurate_supply + 2;
					firstZealot=true;
					System.out.println(accurate_supply);
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

		//build pylon at 9 supply
		if(accurate_supply<=9&&!firstPylon){
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
			System.out.println("Building first Pylon");
		}

		//build gateway when the first pylon is completed
		if((x.getMinerals()>150&&firstPylon&&firstGateway==false)||accurate_supply==12){
			mainbuild(UnitType.UnitTypes.Protoss_Gateway);
			firstGateway = true;
			System.out.println("building gateway");
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
			}
		}
	}

	public int mainbuild(UnitType x){
		int z=0;


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
		Map map=bwapi.getMap();
		Position y=map.getSize();

		int Mapx=y.getX(Position.PosType.PIXEL);
		int Mapy=y.getY(Position.PosType.PIXEL);
		int Start_X=Start.getX(Position.PosType.PIXEL);
		int Start_Y=Start.getY(Position.PosType.PIXEL);


		System.out.println("Start X"+Start_X + " "+ "MapX "+Mapx);
		System.out.println("Start Y"+Start_Y+" "+"MapY "+Mapy);
		if(Math.abs(Mapx-Start_X)<Start_X){
			if(Math.abs(Mapy-Start_Y)<Start_Y){
				System.out.println("Bottom Right");
				location=4;
			}
			else{
				System.out.println("Top Right");
				location=1;
			}
		}
		else{
			if(Math.abs(Mapy-Start_Y)<Start_Y){
				location=3;
				System.out.println("Bottom Left");
			}
			else{
				location=2;
				System.out.println("Top Left");
			}
		}

		for(Unit builder :bwapi.getUnits(bwapi.getSelf())){
			if (builder.getType()==UnitType.UnitTypes.Protoss_Probe) {
				if(supply_pylon.getX(Position.PosType.PIXEL)==0){
					if(location==2||location==3){
						for(int i=300;i<500;i++) {
							int NewPylonX = Start_X + i;
							Position NewPylon = new Position(NewPylonX, Start_Y);
							if(bwapi.canBuildHere(NewPylon,UnitType.UnitTypes.Protoss_Pylon,true)==true) {
								builder.build(NewPylon, UnitType.UnitTypes.Protoss_Pylon);
								pylons.add(NewPylon);
								supply_pylon = NewPylon;
								origin=NewPylon;
								return 1;
							}}}
					if (location==4|| location==1){
						for(int i=300;i<500;i++){
							int NewPylonX=Start_X-i;
							Position NewPylon=new Position(NewPylonX,Start_Y);
							if(bwapi.canBuildHere(NewPylon,UnitType.UnitTypes.Protoss_Pylon,true)==true) {
								pylons.add(NewPylon);
								builder.build(NewPylon, UnitType.UnitTypes.Protoss_Pylon);
								supply_pylon = NewPylon;
								origin= NewPylon;
								return 1;
							}}}

				}
				else{
					int PylonY=supply_pylon.getY(Position.PosType.PIXEL);
					int Pylonx=supply_pylon.getX(Position.PosType.PIXEL);
					if(supply_count>4) {
						if ((location == 1 || location == 2) && override == 0) {
							Position Pylon = origin;
							int Pylony = Pylon.getY(Position.PosType.PIXEL);
							int PylonXV = Pylon.getX(Position.PosType.PIXEL);
							for (int i = 75; i < 300; i++) {
								PylonY = origin.getY(Position.PosType.PIXEL) - UnitType.UnitTypes.Protoss_Pylon.getDimensionUp() - i;
								Position NewPylon = new Position(PylonXV, PylonY);
								if (bwapi.canBuildHere(NewPylon, UnitType.UnitTypes.Protoss_Pylon, true) == true) {
									builder.build(NewPylon, UnitType.UnitTypes.Protoss_Pylon);
									override = 1;
									runover = NewPylon;
									origin=NewPylon;
									runoverorigin=NewPylon;
									return 1;
								}
							}
						} else if ((location == 3 || location == 4) && override == 0) {
							Position Pylon = origin;
							int Pylony = Pylon.getY(Position.PosType.PIXEL);
							int PylonXV = Pylon.getX(Position.PosType.PIXEL);
							for (int i = 75; i < 300; i++) {
								PylonY = origin.getY(Position.PosType.PIXEL) + UnitType.UnitTypes.Protoss_Pylon.getDimensionUp() + i;
								Position NewPylon = new Position(PylonXV, PylonY);
								if (bwapi.canBuildHere(NewPylon, UnitType.UnitTypes.Protoss_Pylon, true) == true) {
									builder.build(NewPylon, UnitType.UnitTypes.Protoss_Pylon);
									override = 1;
									runover = NewPylon;
									origin=NewPylon;
									runoverorigin=NewPylon;
									return 1;
								}}}
						else if((location == 3 || location == 4) && override == 1){
							Position Pylon = origin;
							int Pylony = Pylon.getY(Position.PosType.PIXEL);
							int PylonXV = Pylon.getX(Position.PosType.PIXEL);
							for (int i = 75; i < 300; i++) {
								PylonY = runover.getY(Position.PosType.PIXEL) + UnitType.UnitTypes.Protoss_Pylon.getDimensionUp() + i;
								Position NewPylon = new Position(PylonXV, PylonY);
								if (bwapi.canBuildHere(NewPylon, UnitType.UnitTypes.Protoss_Pylon, true) == true) {
									builder.build(NewPylon, UnitType.UnitTypes.Protoss_Pylon);
									runover = NewPylon;
									return 1;
								}}

						}
						else if((location == 1 || location == 2) && override == 1){
							Position Pylon = origin;
							int Pylony = Pylon.getY(Position.PosType.PIXEL);
							int PylonXV = Pylon.getX(Position.PosType.PIXEL);
							for (int i = 75; i < 300; i++) {
								PylonY = runover.getY(Position.PosType.PIXEL) - UnitType.UnitTypes.Protoss_Pylon.getDimensionUp()  -i;
								Position NewPylon = new Position(PylonXV, PylonY);
								if (bwapi.canBuildHere(NewPylon, UnitType.UnitTypes.Protoss_Pylon, true) == true) {
									builder.build(NewPylon, UnitType.UnitTypes.Protoss_Pylon);
									runover = NewPylon;
									return 1;
								}}
							int redoy=runover.getY(Position.PosType.PIXEL);
							int redox=runover.getX(Position.PosType.PIXEL);



						}
						int redoy=runoverorigin.getY(Position.PosType.PIXEL);
						int redox=runoverorigin.getX(Position.PosType.PIXEL);
						if(location==1||location==4){
							for (int i = 0; i < 50; i++) {
								int PylonXi = redox + UnitType.UnitTypes.Protoss_Pylon.getDimensionRight()  +i;
								Position NewPylon = new Position(PylonXi, redoy);
								if (bwapi.canBuildHere(NewPylon, UnitType.UnitTypes.Protoss_Pylon, true) == true) {
									builder.build(NewPylon, UnitType.UnitTypes.Protoss_Pylon);
									runover = NewPylon;
									runoverorigin=NewPylon;
									return 1;
								}}
						}
						else if(location==2||location==3){
							for (int i = 0; i < 50; i++) {
								int PylonXi = redox - UnitType.UnitTypes.Protoss_Pylon.getDimensionRight()  -i;
								Position NewPylon = new Position(PylonXi, redoy);
								if (bwapi.canBuildHere(NewPylon, UnitType.UnitTypes.Protoss_Pylon, true) == true) {
									builder.build(NewPylon, UnitType.UnitTypes.Protoss_Pylon);
									runover = NewPylon;
									runoverorigin=NewPylon;
									return 1;
								}}


						}

						if(location==1||location==4){
							for (int i = 0; i < 50; i++) {
								int PylonXi = origin.getX(Position.PosType.PIXEL) - UnitType.UnitTypes.Protoss_Pylon.getDimensionRight()  -i;
								Position NewPylon = new Position(PylonXi, redoy);
								if (bwapi.canBuildHere(NewPylon, UnitType.UnitTypes.Protoss_Pylon, true) == true) {
									builder.build(NewPylon, UnitType.UnitTypes.Protoss_Pylon);
									runover = NewPylon;
									runoverorigin=NewPylon;
									return 1;
								}}
						}
						else if(location==2||location==3){
							for (int i = 0; i < 50; i++) {
								int PylonXi = origin.getX(Position.PosType.PIXEL) + UnitType.UnitTypes.Protoss_Pylon.getDimensionRight()  +i;
								Position NewPylon = new Position(PylonXi, redoy);
								if (bwapi.canBuildHere(NewPylon, UnitType.UnitTypes.Protoss_Pylon, true) == true) {
									builder.build(NewPylon, UnitType.UnitTypes.Protoss_Pylon);
									runover = NewPylon;
									runoverorigin=NewPylon;
									return 1;
								}}


						}}





					if(location==1 || location==2){
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
					else if(location==3||location==4){
						for(int i=0;i<200;i++){
							PylonY=supply_pylon.getY(Position.PosType.PIXEL)-UnitType.UnitTypes.Protoss_Pylon.getDimensionUp()-i;
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
		for (Unit unit:bwapi.getMyUnits()){
			if(unit.getType()== UnitType.UnitTypes.Protoss_Zealot){
				if(Zealots.contains(unit)==false&&Attack.contains(unit)==false){
					Zealots.add(unit);
				}
			}
		}
		if(Zealots.size()>=10){
			for(int i=0;i<(Math.round(Zealots.size()*.5));i++){
				Zealots.get(i);
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
