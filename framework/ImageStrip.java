/*
 *  ImageStrip.java
 */
 
import java.awt.*;
import java.awt.image.*;

/**
 *  This class can cut up an image comprised of a matrix of smaller images (frames)
 *  and act as an indexable source for those sub-images (frames). Usefull for animations
 *  or tile sets for example.
 *
 *@author     Tako
 *@created    5 november 2002
 */
public class ImageStrip implements ImageObserver {
	Image m_Strip, m_vImages[];
	ImageFilter m_vFilters[];
	int m_nImgStartX, m_nImgStartY;
	int m_nImgWidth, m_nImgHeight;
	int m_nInterImgWidth, m_nInterImgHeight;


	/**
	 *  Constructor for the ImageStrip object where every parameter can be set.
	 *
	 *@param  img     The source image (collage)
	 *@param  startx  The x position within the source image where to start cutting
	 *@param  starty  The y position within the source image where to start cutting
	 *@param  imgw    The width of each frame
	 *@param  imgh    The height of each frame
	 *@param  iimgw   The horizontal space between each frame
	 *@param  iimgh   The vertical space between each frame
	 */
	ImageStrip(Image img, int startx, int starty, int imgw, int imgh, int iimgw, int iimgh) {
		init(img, startx, starty, imgw, imgh, iimgw, iimgh);
	}


	/**
	 *  Constructor for the ImageStrip object using (0, 0) for the starting coordinate
	 *
	 *@param  img    Description of the Parameter
	 *@param  imgw    The width of each frame
	 *@param  imgh    The height of each frame
	 *@param  iimgw   The horizontal space between each frame
	 *@param  iimgh   The vertical space between each frame
	 */
	ImageStrip(Image img, int imgw, int imgh, int iimgw, int iimgh) {
		init(img, 0, 0, imgw, imgh, iimgw, iimgh);
	}


	/**
	 *  Constructor for the ImageStrip object using (0, 0) for the starting coordinate
	 *  and no spacing between the images.
	 *
	 *@param  img    Description of the Parameter
	 *@param  imgw    The width of each sub-image
	 *@param  imgh    The height of each sub-image
	 */
	ImageStrip(Image img, int imgw, int imgh) {
		init(img, 0, 0, imgw, imgh, 0, 0);
	}


	/**
	 *  Initializes the ImageStrip object
	 *
	 *@param  img     The source image (collage)
	 *@param  startx  The x position within the source image where to start cutting
	 *@param  starty  The y position within the source image where to start cutting
	 *@param  imgw    The width of each sub-image
	 *@param  imgh    The height of each sub-image
	 *@param  iimgw   The horizontal space between each image
	 *@param  iimgh   The vertical space between each image
	 */
	void init(Image img, int startx, int starty, int imgw, int imgh, int iimgw, int iimgh) {
		m_Strip = img;
		m_nImgStartX = startx;
		m_nImgStartY = starty;
		m_nImgWidth = imgw;
		m_nImgHeight = imgh;
		m_nInterImgWidth = iimgw;
		m_nInterImgHeight = iimgh;

		int w = img.getWidth(this);
		int h = img.getHeight(this);
		if (w != -1 && h != -1) {
			cropframes(w, h);
		}
	}


	/**
	 *  Cuts up the source image into its constituent frames
	 *
	 *@param  width   The width of the source image
	 *@param  height  The height of the source image
	 */
	void cropframes(int width, int height) {
		if (m_vImages == null) {
			int xcount = width / m_nImgWidth;
			int ycount = height / m_nImgHeight;

			m_vImages = new Image[xcount * ycount];
			m_vFilters = new ImageFilter[xcount * ycount];

			int nr = 0;
			int yp = m_nImgStartY;
			for (int j = 0; j < ycount; j++) {
				int xp = m_nImgStartX;
				for (int i = 0; i < xcount; i++) {
					m_vFilters[nr] = new CropImageFilter(xp, yp, m_nImgWidth, m_nImgHeight);
					m_vImages[nr] = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(m_Strip.getSource(), m_vFilters[nr]));
					xp += m_nImgWidth + m_nInterImgWidth;
					nr++;
				}
				yp += m_nImgHeight + m_nInterImgHeight;
			}
		}
	}


	/**
	 *  Implements the ImageObserver's imageUpdate() method
	 *
	 *@param  img        A reference to the observed image
	 *@param  infoflags  Flags defining which information about the Image is available
	 *@param  x          Not used
	 *@param  y          Not used
	 *@param  width      The width of the image (when the flags say the information is available)
	 *@param  height     The height of the image (when the flags say the information is available)
	 *@return            A boolean indicating if further notification is required
	 */
	public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
		if ((infoflags & 3
		/*
		 *  ImageObserver.PROPERTIES
		 */
				) != 0) {
			cropframes(width, height);
			return false;
		} else {
			return true;
		}
	}


	/**
	 *  Returns the specified frame Image from the ImageStrip
	 *
	 *@param  nr  The index of the specified image (0 being the top left and increasing to the right and down)
	 *@return     The frame image
	 */
	public Image getFrame(int nr) {
		if (m_vImages != null) {
			return m_vImages[nr];
		} else {
			return null;
		}
	}


	/**
	 *  Gets the width of a single frame in the ImageStrip
	 *
	 *@return    The width of a frame
	 */
	public int getFrameWidth() {
		return m_nImgWidth;
	}


	/**
	 *  Gets the height of a single frame in the ImageStrip
	 *
	 *@return    The height of a frame
	 */
	public int getFrameHeight() {
		return m_nImgHeight;
	}
}

/*
 *  Revision history, maintained by CVS.
 *  $Log: ImageStrip.java,v $
 *  Revision 1.2  2002/11/05 12:53:28  quintesse
 *  Added Javadoc comments.
 *  Removed references to unnecessary Component objects.
 *  Replaced all references to the word Tile with Frame.
 *
 */

