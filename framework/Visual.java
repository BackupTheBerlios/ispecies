import java.awt.*;
import java.util.*;

// Something visible. No game logic.
interface Visual {
	// class constants
	public void paint ( Graphics g, Point org );
}

// Does not change. Ever.
class IndexedVisual 
	implements Visual 
{
	ImageStrip	m_tileset;
	int			m_nIndex;
	
	IndexedVisual( ResourceManager _rm, int _nId, int _nIndex ) { 
		m_tileset = _rm.getTileSet(_nId);
		m_nIndex = _nIndex;
	}
	
	IndexedVisual( ImageStrip _tileset, int _nIndex ) { 
		m_tileset = _tileset;
		m_nIndex = _nIndex;
	}
	
	public int getIndex() {
		return m_nIndex;
	}
	
	public void setIndex(int _nIndex) {
		m_nIndex = _nIndex;
	}
	
	public void paint ( Graphics g, Point org ) 
	{
		g.drawImage(m_tileset.getTile(m_nIndex), org.x, org.y, null);
	};
}

// provides a mapping.
class StateVisual 
	implements Visual 
{
	ImageStrip	m_tileset;
	Visual		m_visual[];
	int			m_state;
	
	StateVisual ( ImageStrip _tileset ) { 
		m_tileset = _tileset;
	}

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
	
	public void paint (Graphics g, Point org ) 
	{
		m_visual[m_state].paint(g, org);
	};
}

// Changes appearance. A lot.
class AnimatedVisual 
	implements Visual 
{
	ImageStrip images;
	public void paint ( Graphics g, Point org ) {};
}
