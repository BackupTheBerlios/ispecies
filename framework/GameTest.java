//******************************************************************************
// GameTestFrame.java:	
//
//******************************************************************************
import java.awt.*;
import java.awt.event.*;

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

		// give frame a reasonable size
		setSize(320,240);

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
				item = new MenuItem("Isometric");
				item.addActionListener(this);
				viewportsMenu.add( item );
				item = new MenuItem("Add moving object");
				item.addActionListener(this);
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
			button = new Button("Isometric");
			button.addActionListener(this);
			buttonBar.add( button );
			button = new Button("Add moving object");
			button.addActionListener(this);
			buttonBar.add( button );
		return buttonBar;
	}
		

    public void quit() {
        System.exit(0);
    }
	
	public void actionPerformed( ActionEvent ev ) 
	{
		String label = ev.getActionCommand();

		if (label.equals("Radar"))		{
			new RadarViewport(game,new Radar(game));
		}
		if (label.equals("Satellite"))		{
			new SatelliteViewport(game,new Satellite(game));
		}
		if (label.equals("Isometric"))		{
			new IsometricViewport(game,new IsometricDataSource(game));
		}
		if (label.equals("Add moving object"))
		{
			// create tmp object to watch in the radar
			new RandomGameObjectMover ( 
				new BaseGameObject(
					game.Map(),
					(int)Math.round(Math.random()*game.Map().mMapWidth),
					(int)Math.round(Math.random()*game.Map().mMapHeight)
				), 
				game,
				(int)Math.round(Math.random()*10)
			) ;
		}
    } // actionPerformed			

	// entry point of the application
	public static void main(String args[])
	{
		// Create Toplevel Window
		GameTest frame = new GameTest("GameTest");

		// Show Frame
		frame.show();
	}

} // GameTest
