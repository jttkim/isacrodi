package org.isacrodi.struts;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;

import javax.imageio.ImageIO;

import javax.naming.NamingException;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.isacrodi.ejb.entity.*;


/**
 * Action to support inline images from image descriptors, to be used
 * in HTML img elements.
 *
 * <p>If there are problems creating an image stream (no descriptor
 * ID, invalid descriptor ID etc.), a stream containing a PNG image
 * with a message describing the problem is provided.</p>
 */
public class ShowInlineImageAction extends IsacrodiActionSupport
{
  private Integer imageDescriptorId;
  private ImageDescriptor imageDescriptor;
  private InputStream inputStream;
  private String contentType;


  private static final long serialVersionUID = 1;


  public ShowInlineImageAction() throws NamingException
  {
    super();
  }


  public void setImageDescriptorId(Integer imageDescriptorId)
  {
    this.imageDescriptorId = imageDescriptorId;
  }


  public Integer getImageDescriptorId()
  {
    return (this.imageDescriptorId);
  }


  private static ByteArrayInputStream messageImagePngData(String message) throws IOException
  {
    // FIXME: should calculate width and height dynamically
    BufferedImage bufferedImage = new BufferedImage(320, 50, BufferedImage.TYPE_INT_RGB);
    Graphics graphics = bufferedImage.getGraphics();
    graphics.setFont(new Font("Monospaced", Font.PLAIN, 10));
    graphics.setColor(Color.WHITE);
    graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
    graphics.setColor(Color.BLUE);
    graphics.drawString(message, 10, 40);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ImageIO.write(bufferedImage, "png", out);
    return (new ByteArrayInputStream(out.toByteArray()));
  }


  public InputStream getInputStream() throws IOException
  {
    return (this.inputStream);
  }


  public String getContentType()
  {
    return (this.contentType);
  }


  private void createMessageImage(String message) throws IOException
  {
    this.LOG.info(String.format("inline image message: %s", message));
    this.inputStream = messageImagePngData(message);
    this.contentType = "image/png";
  }


  public String execute() throws IOException
  {
    if (this.imageDescriptorId == null)
    {
      this.createMessageImage("no image descriptor ID");
    }
    else
    {
      this.imageDescriptor = this.entityAccess.findEntity(ImageDescriptor.class, this.imageDescriptorId);
      if (this.imageDescriptor == null)
      {
	this.createMessageImage(String.format("image descriptor %d not found", this.imageDescriptorId.intValue()));
      }
      else
      {
	this.inputStream = new ByteArrayInputStream(this.imageDescriptor.getImageData());
	this.contentType = this.imageDescriptor.getMimeType();
      }
    }
    return (SUCCESS);
  }
}
