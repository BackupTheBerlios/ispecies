/*
 * TopPanel.java
 *
 * Created on 27 oktober 2002, 18:04
 */

import java.util.*;
import java.awt.*;

/**
 *
 * @author  puf
 */
public class TopPanel extends java.awt.Panel {
	Universe game;
	
	/** Creates new form TopPanel */
	public TopPanel(Universe game) {
		this.game = game;
		initComponents();
	}
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    private void initComponents() {//GEN-BEGIN:initComponents
        toolbar = new java.awt.Panel();
        showRadarBtn = new java.awt.Button();
        showSatelliteBtn = new java.awt.Button();
        showIsometricBtn = new java.awt.Button();
        panel1 = new java.awt.Panel();
        addMovingObjectBtn = new java.awt.Button();
        addPathFinderBtn = new java.awt.Button();
        reloadMapBtn = new java.awt.Button();
        commandbar = new java.awt.Panel();
        actionLbl = new java.awt.Label();
        actionTxt = new java.awt.TextField();
        goBtn = new java.awt.Button();

        setLayout(new java.awt.BorderLayout());

        showRadarBtn.setActionCommand("Radar");
        showRadarBtn.setLabel("Radar");
        showRadarBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showRadarBtnActionPerformed(evt);
            }
        });

        toolbar.add(showRadarBtn);

        showSatelliteBtn.setLabel("Satellite");
        showSatelliteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showSatelliteBtnActionPerformed(evt);
            }
        });

        toolbar.add(showSatelliteBtn);

        showIsometricBtn.setActionCommand("Isometric");
        showIsometricBtn.setLabel("Isometric");
        showIsometricBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showIsometricBtnActionPerformed(evt);
            }
        });

        toolbar.add(showIsometricBtn);

        toolbar.add(panel1);

        addMovingObjectBtn.setLabel("Add moving object");
        addMovingObjectBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addMovingObjectBtnActionPerformed(evt);
            }
        });

        toolbar.add(addMovingObjectBtn);

        addPathFinderBtn.setLabel("Add path finder");
        addPathFinderBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addPathFinderBtnActionPerformed(evt);
            }
        });

        toolbar.add(addPathFinderBtn);

        reloadMapBtn.setLabel("Reload map");
        reloadMapBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reloadMapBtnActionPerformed(evt);
            }
        });

        toolbar.add(reloadMapBtn);

        add(toolbar, java.awt.BorderLayout.NORTH);

        actionLbl.setText("Action: ");
        commandbar.add(actionLbl);

        actionTxt.setColumns(30);
        actionTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actionTxtActionPerformed(evt);
            }
        });

        commandbar.add(actionTxt);

        goBtn.setLabel("Go");
        goBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goBtnActionPerformed(evt);
            }
        });

        commandbar.add(goBtn);

        add(commandbar, java.awt.BorderLayout.CENTER);

    }//GEN-END:initComponents

	private static int mFinderIndex = 0;
	private void addPathFinderBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPathFinderBtnActionPerformed
		PathFinder mover = new PathFinder(
			"finder_"+mFinderIndex++,
			game.getMap(),
			game,
			new Point(10, 10)
		);
		mover.setTarget(new Point(400, 400));
	}//GEN-LAST:event_addPathFinderBtnActionPerformed

	private void goBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goBtnActionPerformed
		String command = actionTxt.getText();
		Logger.log("command = '"+command+"'");
		StringTokenizer parser = new StringTokenizer(command);
		String action = parser.nextToken();
		Logger.log("action = '"+action+"'");
		if ("go".equals(action)) {
			if (parser.hasMoreTokens()) {
				Logger.log("next token = '"+parser.nextToken()+"'");
			}
			else {
				// list game objects
				ObjectEnumeration objects = game.map.getRange().getObjectEnumeration();
				while (objects.hasMoreElements()) {
					GameObject obj = (GameObject)objects.nextElement();
					Logger.log("name = "+obj.getName());
					Logger.log("position = "+obj.getPosition());
					/*
					Object t = obj.getInterface("Targettable");
					if (obj.getInterface("Targettable") != null) {
						Logger.log("target = "+((Targettable)obj.getInterface("Targettable")).getTarget());
					}
					*/
					if (obj instanceof Targettable) {
						Logger.log("target = "+((Targettable)obj).getTarget());
					}
				}
			}
		}
		else if ("fm".equals(action)) {
			/*
			Logger.log("Flag map for PathFinder class:");
			Logger.log("FLAGMAP = "+PathFinder.FLAGMAP.length+" x "+PathFinder.FLAGMAP[0].length);
			for (int y=0; y < PathFinder.FLAGMAP[1].length; y++) {
				for (int x=0; x < PathFinder.FLAGMAP.length; x++) {
					System.out.print(PathFinder.FLAGMAP[x][y]);
				}
				Logger.log("");
			}
			*/
		}
	}//GEN-LAST:event_goBtnActionPerformed

	private void actionTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actionTxtActionPerformed
		goBtnActionPerformed(evt);
	}//GEN-LAST:event_actionTxtActionPerformed

	private void reloadMapBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadMapBtnActionPerformed
		game.setMap(game.readMap("Terrain.map"));
	}//GEN-LAST:event_reloadMapBtnActionPerformed

	private void addMovingObjectBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addMovingObjectBtnActionPerformed
		new RandomGameObjectMover ( 
			new BaseGameObject(
				game.getMap(),
				(int)Math.round(Math.random()*game.getMap().mMapWidth),
				(int)Math.round(Math.random()*game.getMap().mMapHeight)
			), 
			game,
			(int)Math.round(Math.random()*10)
		) ;
	}//GEN-LAST:event_addMovingObjectBtnActionPerformed

	private void showIsometricBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showIsometricBtnActionPerformed
		new IsometricViewport(game,new IsometricDataSource(game));
	}//GEN-LAST:event_showIsometricBtnActionPerformed

	private void showSatelliteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showSatelliteBtnActionPerformed
			new SatelliteViewport(game,new Satellite(game));
	}//GEN-LAST:event_showSatelliteBtnActionPerformed

	private void showRadarBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showRadarBtnActionPerformed
		new RadarViewport(game, new Radar(game));
	}//GEN-LAST:event_showRadarBtnActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Button showRadarBtn;
    private java.awt.Button goBtn;
    private java.awt.Panel toolbar;
    private java.awt.Label actionLbl;
    private java.awt.Button addMovingObjectBtn;
    private java.awt.Panel panel1;
    private java.awt.Panel commandbar;
    private java.awt.TextField actionTxt;
    private java.awt.Button reloadMapBtn;
    private java.awt.Button showIsometricBtn;
    private java.awt.Button addPathFinderBtn;
    private java.awt.Button showSatelliteBtn;
    // End of variables declaration//GEN-END:variables
	
}

/*
 *  Revision history, maintained by CVS.
 *  $Log: TopPanel.java,v $
 *  Revision 1.6  2002/11/05 15:30:42  quintesse
 *  Using Logger.log() instead of System.out.writeln();
 *  Added CVS history section.
 *
 */

