package com.nmims.util;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.itextpdf.text.Image;

public interface ImageRotationUtil {
	
	public static Image getImageRotatedByOrientation(Image studentPhoto,String studentUrl) throws Exception{
		URL url  =  new URL(studentUrl);
		InputStream input = url.openStream();
		Metadata metadata = ImageMetadataReader.readMetadata( new BufferedInputStream(input));
		Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
		int orientation = 1;
		   
        orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
        studentPhoto = getExifTransformation(orientation,studentPhoto);
        return studentPhoto;
	}

	public static Image getExifTransformation(int orient,Image image) {

		  //  AffineTransform t = new AffineTransform();

	switch(orient) {
	case 1:
	    break;
	case 2:
		
	   // image.flip();
	    break;
	case 3:
		 image.setRotationDegrees(180f);
	    break;
	case 4:
	  //  image.flip();
		 image.setRotationDegrees(-180f);
	    break;
	case 5:
		 image.setRotationDegrees(-90f);
	    break;
	case 6:
		 //image.rotate();
		 image.setRotationDegrees(-90f);
	    break;
	case 7:

		 image.setRotationDegrees(-270f);
	    break;
	case 8:
		 image.setRotationDegrees(-270f);
	}


	return image;
		}
}
