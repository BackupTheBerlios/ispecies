import java.awt.*;
import java.awt.image.*;

/****************************************************************************
 * ImageStrip
 ****************************************************************************/

public class ImageStrip implements ImageObserver {
	Image m_Strip, m_vImages[];
	ImageFilter m_vFilters[];
	int m_nImgStartX, m_nImgStartY;
	int m_nImgWidth, m_nImgHeight;
	int m_nInterImgWidth, m_nInterImgHeight;
	Component m_Comp;

	ImageStrip(Image img, int startx, int starty, int imgw, int imgh, int iimgw, int iimgh, Component c) {
		init(img, startx, starty, imgw, imgh, iimgw, iimgh, c);
	}

	ImageStrip(Image img, int imgw, int imgh, int iimgw, int iimgh, Component c) {
		init(img, 0, 0, imgw, imgh, iimgw, iimgh, c);
	}

	ImageStrip(Image img, int imgw, int imgh, Component c) {
		init(img, 0, 0, imgw, imgh, 0, 0, c);
	}

	void init(Image img, int startx, int starty, int imgw, int imgh, int iimgw, int iimgh, Component c) {
		m_Strip = img;
		m_nImgStartX = startx;
		m_nImgStartY = starty;
		m_nImgWidth = imgw;
		m_nImgHeight = imgh;
		m_nInterImgWidth = iimgw;
		m_nInterImgHeight = iimgh;
		m_Comp = c;

		int w = img.getWidth(this);
		int h = img.getHeight(this);
		if (w != -1 && h != -1) {
			cropframes(w, h);
		}
	}

	void cropframes(int width, int height) {
		if (m_vImages == null) {
			int xcount = width / m_nImgWidth;
			int ycount = height / m_nImgHeight;

			m_vImages = new Image[xcount * ycount];
			m_vFilters = new ImageFilter[xcount * ycount];

			int nr = 0;
			int yp = m_nImgStartY;
			for (int j=0; j < ycount; j++) {
				int xp = m_nImgStartX;
				for (int i=0; i < xcount; i++) {
					m_vFilters[nr] = new CropImageFilter(xp, yp, m_nImgWidth, m_nImgHeight);
					m_vImages[nr] = /*m_Comp*/Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(m_Strip.getSource(), m_vFilters[nr]));
					xp += m_nImgWidth + m_nInterImgWidth;
					nr++;
				}
				yp += m_nImgHeight + m_nInterImgHeight;
			}
		}
	}

	public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
		if ((infoflags & 3/*ImageObserver.PROPERTIES*/) != 0) {
			cropframes(width, height);
			return false;
		}
		else {
			return true;
		}
	}

	public Image getTile(int nr) {
		if (m_vImages != null) {
			return m_vImages[nr];
		} else {
			return null;
		}
	}

	public int getTileWidth() {
		return m_nImgWidth;
	}
	
	public int getTileHeight() {
		return m_nImgHeight;
	}
}

