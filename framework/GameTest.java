/*
 *  GameTest.java:
 */
 
import java.awt.*;
import java.awt.event.*;

import util.Profiler;

/**
 *  STANDALONE APPLICATION SUPPORT
 * 	This frame class acts as a top-level window in which the applet appears
 *  when it's run as a standalone application.
 *
 *@created    5 november 2002
 */
public class GameTest extends Frame implements ActionListener {
	/**
	 *  A reference to the object that holds the representation of our "universe" 
	 */
	protected Universe game;


	/**
	 *  Constructor for the GameTest object
	 *
	 *@param  str  The name of the Frame
	 */
	public GameTest(String str) {
		super(str);

		// add handler to quit the app when the frame window is closed
		addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					quit();
				}
			}
		);

		// Create the universe, which is where all the action takes place.
		game = new Universe();

		// Create a menu bar
		setMenuBar(CreateMenuBar());

		// give frame a reasonable size
		setSize(600, 200);

		// add top panel with the toolbar and the command bar
		add("North", new TopPanel(game));

		// Start the game
		game.start();

	}


	/**
	 *  Creates the menu bar that lets the user interact with the Universe
	 *
	 *@return    A new MenuBar object initialized with the options available to the user
	 */
	protected MenuBar CreateMenuBar() {
		MenuBar menu = new MenuBar();
		Menu viewportsMenu = new Menu("Viewports");
		MenuItem item = new MenuItem("Radar");
		item.addActionListener(this);
		viewportsMenu.add(item);
		item = new MenuItem("Satellite");
		item.addActionListener(this);
		viewportsMenu.add(item);
		item = new MenuItem("Isometric");
		item.addActionListener(this);
		viewportsMenu.add(item);
		item = new MenuItem("Add moving object");
		item.addActionListener(this);
		viewportsMenu.add(item);

		item = new MenuItem("Reload map");
		item.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					game.setMap(game.readMap("Terrain.map"));
				}
			}
		);
		viewportsMenu.add(item);
		menu.add(viewportsMenu);
		return menu;
	}


	/**
	 *  Terminates the application
	 */
	public void quit() {
		Profiler.printTotals();
		System.exit(0);
	}


	/**
	 *  Handles the action events of the user interface
	 *
	 *@param  ev  Description of the Parameter
	 */
	public void actionPerformed(ActionEvent ev) {
		String label = ev.getActionCommand();

		if (label.equals("Radar")) {
			new FramedViewportContainer(game, new RadarViewport(new Radar(game)));
		}
		if (label.equals("Satellite")) {
			new FramedViewportContainer(game, new SatelliteViewport(new Satellite(game)));
		}
		if (label.equals("Isometric")) {
			new IsometricEditViewport(game, new IsometricDataSource(game));
		}
		if (label.equals("Add moving object")) {
			// create tmp object to watch in the radar
			new RandomGameObjectMover(
					new BaseGameObject(
						game.getMap(),
						(int)Math.round(Math.random() * game.getMap().mMapWidth),
						(int)Math.round(Math.random() * game.getMap().mMapHeight)
						),
						game,
						(int)Math.round(Math.random() * 10)
					);
		}
	}


	/**
	 *  Entry point of the application
	 *
	 *@param  args  The arguments passed to the application
	 */
	public static void main(String args[]) {
		// Create Toplevel Window
		GameTest frame = new GameTest("GameTest");

		// Show Frame
		frame.setLayout(new GridBagLayout());
		frame.show();

		//new IsometricEditViewport(frame.game,new IsometricDataSource(frame.game));
		Frame aSatellite = new FramedViewportContainer(frame.game, new SatelliteViewport(new Satellite(frame.game)));
		aSatellite.setLocation(100, 240);

		// create tmp object to watch
		/*
		GameObject obj = new TargettableGameObject(
				new RandomGameObjectMover(
					new BaseGameObject(
						"first",
						frame.game.getMap(),
						(int)Math.round(Math.random() * frame.game.getMap().mMapWidth),
						(int)Math.round(Math.random() * frame.game.getMap().mMapHeight)
					),
					frame.game,
					(int)Math.round(Math.random() * 10)
				)
		);
		*/
		PathFinder mover = new PathFinder(
				"finder",
				frame.game.getMap(),
				frame.game,
				new Point(10, 10)
		);
		mover.setTarget(new Point(400, 400));
	}

}

/*
 *  Revision history, maintained by CVS.
 *  $Log: GameTest.java,v $
 *  Revision 1.7  2003/06/05 15:20:28  puf
 *  Added call to display totals gathered by the profiler.
 *  Disabled adding random moving game objects to the universe for the moment.
 *
 *  Revision 1.6  2002/11/07 00:54:38  quintesse
 *  Now uses the niew Viewport/ViewportContainer system for Radar and Satellite models.
 *  Changed deprecated method call move() to setLocation().
 *
 *  Revision 1.5  2002/11/05 12:51:52  quintesse
 *  Added Javadoc comments.
 *  Moved log() method to the Universe class.
 *
 */

