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
	protected Image				m_greenTileMap;
	protected ImageStrip		m_greenTiles;

	public TimerTriggerPool	heartBeat; // counts game ticks, not real time

	Universe() 
	{
		heartBeat = new TimerTriggerPool();
		try {
			MapBuilder builder = new MapBuilder();
			map = builder.readGameMap("Terrain.map");
		}
		catch (Exception e){
			System.err.println("Could not read map: "+e.getMessage());
		}
		//map = new GameMap(640, 640);
		// load images. TODO: start some kind of resource manager
		m_greenTileMap	= Toolkit.getDefaultToolkit().getImage("GreenTiles_65x65.gif");
		m_greenTiles	= new ImageStrip(m_greenTileMap,65,65,null);
	}

	GameMap Map() { return map; }

	public void run() 
	{
		while (true) 
		{
			try
			{
				sleep(50); // msec per tick
				heartBeat.tick();
			}
			catch (InterruptedException e)
			{
				System.out.println("ServerUniverse: somebody woke me, "+e);
			}
		}
	}
} // Universe

