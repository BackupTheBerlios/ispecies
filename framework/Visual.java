import java.awt.*;
import java.util.*;

// Something visible. No game logic.
interface Visual {
	// class constants
	public void paint ( Graphics g, ImageStrip tileset, Point org );
}

// Does not change. Ever.
class StaticVisual 
	implements Visual 
{
	int			m_index;
	
	StaticVisual ( int _index )
	{
		m_index = _index;
	}
	
	public void paint ( Graphics g, ImageStrip tileset, Point org ) 
	{
		g.drawImage(tileset.getTile(m_index),org.x,org.y,null);
	};
}

// provides a mapping.
class StateVisual 
	implements Visual 
{
	Visual	m_visual[];
	int		m_state;

	public void setStateVisual(int _state, Visual _visual)
	{
		if (_state > m_visual.length) {
			Visual old[] = m_visual;
			m_visual = new Visual[_state];
			/*
			for (int i=0; i<old.length; i++)
				m_visual[i] = old[i];
			*/
			System.arraycopy(old,0,m_visual,0,old.length);
		}
		m_visual[_state] = _visual;
	};
	
	public void setState ( int _state ) 
	{
		m_state = _state;
	};
	
	public void paint (Graphics g, ImageStrip tileset, Point org ) 
	{
		m_visual[m_state].paint(g,tileset,org);
	};
}

// Changes appearance. A lot.
class AnimatedVisual 
	implements Visual 
{
	ImageStrip images;
	public void paint ( Graphics g, ImageStrip tileset, Point org ) {};
}

