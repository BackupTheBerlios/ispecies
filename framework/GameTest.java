//******************************************************************************
// GameTestFrame.java:	
//
//******************************************************************************
import java.awt.*;
import java.awt.event.*;
import java.util.*;

//==============================================================================
// STANDALONE APPLICATION SUPPORT
// 	This frame class acts as a top-level window in which the applet appears
// when it's run as a standalone application.
//==============================================================================
public class GameTest extends Frame
	implements ActionListener
{
	protected Universe game;

	// GameTest constructor
	//--------------------------------------------------------------------------
	public GameTest(String str)
	{
		super (str);

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
		setMenuBar( CreateMenuBar() );

		// give frame a reasonable size
		setSize(600,200);

		// add top panel with the toolbar and the command bar
		add( "North", new TopPanel(game) );

		// Start the game
		game.start();
		
	}
	
	protected MenuBar CreateMenuBar()
	{
		MenuBar menu = new MenuBar();
			Menu viewportsMenu = new Menu("Viewports");
				MenuItem item = new MenuItem("Radar");
				item.addActionListener(this);
				viewportsMenu.add( item );
				item = new MenuItem("Satellite");
				item.addActionListener(this);
				viewportsMenu.add( item );
				item = new MenuItem("Isometric");
				item.addActionListener(this);
				viewportsMenu.add( item );
				item = new MenuItem("Add moving object");
				item.addActionListener(this);
				viewportsMenu.add( item );

				item = new MenuItem("Reload map");
				item.addActionListener(
					new ActionListener() {
						public void actionPerformed( ActionEvent ev ) {
							game.setMap(game.readMap("Terrain.map"));
						}
					}
				);
				viewportsMenu.add( item );
			menu.add(viewportsMenu);
		return menu;
	}		

    public void quit() {
        System.exit(0);
    }
	
	public void actionPerformed( ActionEvent ev ) 
	{
		String label = ev.getActionCommand();

		if (label.equals("Radar"))
		{
			new RadarViewport(game,new Radar(game));
		}
		if (label.equals("Satellite"))
		{
			new SatelliteViewport(game,new Satellite(game));
		}
		if (label.equals("Isometric"))
		{
			new IsometricEditViewport(game,new IsometricDataSource(game));
		}
		if (label.equals("Add moving object"))
		{
			// create tmp object to watch in the radar
			new RandomGameObjectMover ( 
				new BaseGameObject(
					game.getMap(),
					(int)Math.round(Math.random()*game.getMap().mMapWidth),
					(int)Math.round(Math.random()*game.getMap().mMapHeight)
				), 
				game,
				(int)Math.round(Math.random()*10)
			) ;
		}
    } // actionPerformed
	
	public void log(String msg) {
		
	} // log

	
	// entry point of the application
	public static void main(String args[])
	{
		// Create Toplevel Window
		GameTest frame = new GameTest("GameTest");

		// Show Frame
		frame.setLayout(new GridBagLayout());
		frame.show();
		/*
		Console console = new Console();
		console.setBounds(100, 100, 420, 300);
		console.show();
		for (int i=0; i < 50; i++) {
			console.log(""+i);
		}
		*/
		//new IsometricEditViewport(frame.game,new IsometricDataSource(frame.game));
		Frame aSatellite = new SatelliteViewport(frame.game, new Satellite(frame.game));
		aSatellite.move(100, 240);
		// create tmp object to watch in the radar
		GameObject obj = new TargettableGameObject(
			new RandomGameObjectMover ( 
				new BaseGameObject(
					"first",
					frame.game.getMap(),
					(int)Math.round(Math.random()*frame.game.getMap().mMapWidth),
					(int)Math.round(Math.random()*frame.game.getMap().mMapHeight)
				), 
				frame.game,
				(int)Math.round(Math.random()*10)
			)
		);
		System.out.println("obj.getInterface(\"BaseGameObject\") = "+(Targettable)obj);
		PathFinder mover = new PathFinder(
			"finder",
			frame.game.getMap(),
			frame.game,
			new Point(10, 10)
		);
		mover.setTarget(new Point(400, 400));
	}

} // GameTest

class Console extends Frame {
	final int MAX_MESSAGE_COUNT = 500;
	String[] mMessages = new String[MAX_MESSAGE_COUNT];
	int mNextMessageIndex = 0;
	
	Console() {
		super();
		// set layout manager
		//this.setLayout(new BorderLayout());
		// command bar
		add( createCommandBar(), BorderLayout.NORTH, -1 );
		// output area
		add( createOutputArea(), BorderLayout.CENTER, -1 );
	}
	Panel createCommandBar() {
		Panel aResult = new Panel();
		aResult.setBounds(0,0,100,20);
		aResult.setBackground(Color.pink);
		return aResult;
	}
	Panel createOutputArea() {
		Panel aResult = new Panel();
		aResult.setBounds(0,0,400,400);
		aResult.setBackground(Color.gray);
		return aResult;
	}
	void log(String msg) {
		mMessages[mNextMessageIndex] = msg;
		mNextMessageIndex++;
		if (mNextMessageIndex >= MAX_MESSAGE_COUNT) {
			mNextMessageIndex = 0;
		}
		repaint();
	}
	public void paint(Graphics g) {
		for (int linenr=0; linenr < 20; linenr++) {
			int messageindex = mNextMessageIndex+20-linenr;
			if (messageindex < 0) {
				messageindex = messageindex + MAX_MESSAGE_COUNT;
			}
			if (mMessages[messageindex] != null) {
				g.drawString( mMessages[messageindex], 0, linenr * 10 );
			}
		}
	}
}