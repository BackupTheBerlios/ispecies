//
//
// Universe
//
//

import java.io.*;
import java.util.*;
import java.awt.*;

class Universe extends Thread
// Game state object
{
	protected GameMap			map;
	protected ResourceManager	rm;

	public TimerTriggerPool	heartBeat; // counts game ticks, not real time

	Universe() 
	{
		heartBeat = new TimerTriggerPool();
		rm = new ResourceManager();
		map = readMap("Terrain.map");
	}

	GameMap getMap() { 
		return map; 
	}
	
	void setMap(GameMap _map) { 
		map = _map; 
	}
	
	GameMap readMap(String _mapFilename) {
		try {
			return new MapBuilder(rm).readGameMap("Terrain.map");
		}
		catch (IOException e){
			System.err.println("Could not read map: "+e.getMessage());
			e.printStackTrace(System.err);
		}
		return null;
	}

	public void run() 
	{
		while (true) 
		{
			try
			{
				sleep(50); // 50 msec per tick = 20 ticks per sec
				heartBeat.tick();
			}
			catch (InterruptedException e)
			{
				System.out.println("ServerUniverse: somebody woke me, "+e);
			}
		}
	}
} // Universe

