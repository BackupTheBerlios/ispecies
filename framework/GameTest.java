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

		// Create a menu bar
		setMenuBar( CreateMenuBar() );

		// Create a button bar
		add( "North", CreateButtonBar() );

		// Create a command bar
		add( "North", CreateCommandBar() );

		// give frame a reasonable size
		setSize(400,240);

		// Create the universe, which is where all the action takes place.
		game = new Universe();
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
				item = new MenuItem("Isometric viewer");
				item.addActionListener(this);
				viewportsMenu.add( item );
				item = new MenuItem("Isometric editor");
				item.addActionListener(this);
				viewportsMenu.add( item );
				item = new MenuItem("Add moving object (random)");
				item.addActionListener(this);
				viewportsMenu.add( item );
				item = new MenuItem("Add moving object (targeted)");
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
	protected Panel CreateButtonBar()
	{
		// Create a button bar
		Panel buttonBar = new Panel();
			Button button = new Button("Radar");
			button.addActionListener(this);
			buttonBar.add( button );
			button = new Button("Satellite");
			button.addActionListener(this);
			buttonBar.add( button );
			button = new Button("Isometric viewer");
			button.addActionListener(this);
			buttonBar.add( button );
			button = new Button("Isometric editor");
			button.addActionListener(this);
			buttonBar.add( button );
			button = new Button("Add moving object (random)");
			button.addActionListener(this);
			buttonBar.add( button );
			button = new Button("Add moving object (targeted)");
			button.addActionListener(this);
			buttonBar.add( button );

			button = new Button("Reload map");
			button.addActionListener(
				new ActionListener() {
					public void actionPerformed( ActionEvent ev ) {
						game.setMap(game.readMap("Terrain.map"));
					}
				}
			);
			buttonBar.add( button );
		return buttonBar;
	}
	protected Panel CreateCommandBar()
	{
		// Create a command bar
		Panel commandBar = new Panel();
			commandBar.add(new Label("Action"));
			final TextField text = new TextField(30);
			text.addActionListener(
				new ActionListener() {
					public void actionPerformed( ActionEvent ev ) {
						String command = text.getText();
						System.out.println("command = '"+command+"'");
						StringTokenizer parser = new StringTokenizer(command);
						String action = parser.nextToken();
						System.out.println("action = '"+action+"'");
						if ("go".equals(action)) {
							if (parser.hasMoreTokens()) {
								System.out.println("next token = '"+parser.nextToken()+"'");
							}
							else {
								// list game objects
								ObjectEnumeration objects = game.map.getRange().getObjectEnumeration();
								while (objects.hasMoreElements()) {
									GameObject obj = (GameObject)objects.nextElement();
									System.out.println("name = "+obj.getName());
									System.out.println("position = "+obj.getPosition());
									/*
									Object t = obj.getInterface("Targettable");
									if (obj.getInterface("Targettable") != null) {
										System.out.println("target = "+((Targettable)obj.getInterface("Targettable")).getTarget());
									}
									*/
									if (obj instanceof Targettable) {
										System.out.println("target = "+((Targettable)obj).getTarget());
									}
								}
							}
						}
						else if ("fm".equals(action)) {
							System.out.println("Flag map for PathFinder class:");
							System.out.println("FLAGMAP = "+PathFinder.FLAGMAP.length+" x "+PathFinder.FLAGMAP[0].length);
							for (int x=0; x < PathFinder.FLAGMAP.length; x++) {
								for (int y=0; y < PathFinder.FLAGMAP[x].length; y++) {
									System.out.print(PathFinder.FLAGMAP[x][y]);
								}
								System.out.println("");
							}
						}
					}
				}
			);
			commandBar.add(text);
			Button button = new Button("Go");
			/*
			button.addActionListener(
				new ActionListener() {
					public void actionPerformed( ActionEvent ev ) {
						game.setMap(game.readMap("Terrain.map"));
					}
				}
			);
			*/
			commandBar.add( button );
		return commandBar;
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
		if (label.equals("Isometric viewer"))
		{
			new IsometricViewport(game,new IsometricDataSource(game));
		}
		if (label.equals("Isometric editor"))
		{
			new IsometricEditViewport(game,new IsometricDataSource(game));
		}
		if (label.equals("Add moving object (random)"))
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
		if (label.equals("Add moving object (targeted)"))
		{
			PathFinder mover = new PathFinder(
				"finder",
				game.getMap(),
				game,
				getRandomMapPoint(game.getMap())
			);
			mover.setTarget(getRandomMapPoint(game.getMap()));
		}
    } // actionPerformed

	public void log(String msg) {

	} // log


	private static Point getRandomMapPoint(GameMap _map) {
		return new Point((int)Math.round(Math.random()*_map.mMapWidth), (int)Math.round(Math.random()*_map.mMapHeight));
	}

	// entry point of the application
	public static void main(String args[])
	{
		// Create Toplevel Window
		GameTest frame = new GameTest("GameTest");

		// Show Frame
		frame.show();

		Console console = new Console();
		console.show();
		for (int i=0; i < 50; i++) {
			console.log(""+i);
		}

		//new IsometricEditViewport(frame.game,new IsometricDataSource(frame.game));
		Frame aSatellite = new SatelliteViewport(frame.game, new Satellite(frame.game));
		aSatellite.move(0, 240);
		// create tmp object to watch in the radar
		GameObject obj = new TargettableGameObject(
			new RandomGameObjectMover (
				new BaseGameObject(
					"first",
					frame.game.getMap(),
					getRandomMapPoint(frame.game.getMap())
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
			getRandomMapPoint(frame.game.getMap())
		);
		mover.setTarget(getRandomMapPoint(frame.game.getMap()));
	}

} // GameTest

class Console extends Frame {
	final int MAX_MESSAGE_COUNT = 500;
	String[] mMessages = new String[MAX_MESSAGE_COUNT];
	int mNextMessageIndex = 0;

	Console() {
		super();
		// command bar
		add( createCommandBar(), BorderLayout.NORTH, -1 );
		// output area
		add( createOutputArea(), BorderLayout.CENTER, -1 );
	}
	Panel createCommandBar() {
		Panel aResult = new Panel();
		aResult.setBounds(0,0,0,20);
		aResult.setBackground(Color.pink);
		return aResult;
	}
	Panel createOutputArea() {
		Panel aResult = new Panel();
		aResult.setBounds(0,0,0,400);
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