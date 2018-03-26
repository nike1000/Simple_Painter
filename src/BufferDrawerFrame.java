import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.*;

public class BufferDrawerFrame extends JFrame
{
	private JLabel label1 = new JLabel("[Painting Tool]");
	private String comboString[] = {"Pen","Line","Oval","Rectangle","Rounded Rectangle","Eraser"};
	private JComboBox comboBox = new JComboBox(comboString);
	private JLabel label2 = new JLabel("[Thickness]");
	private JRadioButton Rbuttonsmall = new JRadioButton("Thin",true);
	private JRadioButton Rbuttonmiddum = new JRadioButton("Medium",false);
	private JRadioButton Rbuttonbig = new JRadioButton("Thick",false);
	private ButtonGroup radioGroup = new ButtonGroup();
	private JCheckBox fullcheck = new JCheckBox("Fill");
	private JButton forground = new JButton("Forground Color");
	private JButton background = new JButton("Background Color");
	private JButton clean = new JButton("Clean");
	private JButton save = new JButton("Save");
	private JButton load = new JButton("Load");
	private JButton undo = new JButton("Undo");
	private JLabel stateslabel = new JLabel("states");
	private JPanel Toolpanel = new JPanel();
	private BorderLayout borderlayout = new BorderLayout();
	private BufferDrawerPanel drawpanel = new BufferDrawerPanel();
	private MouseMotionAdapter mousehandler = new MouseMotionAdapter();
	private JFileChooser fileChooser = new JFileChooser();
	private File filename;
	private File storefile;
	private BufferedImage input;
	
	
	public Color ColorChooce = Color.GRAY;
	public Color BackColorChooce = Color.GRAY;
	
	private Handler handler=new Handler();
	
	public BufferDrawerFrame()
	{
		super("Simple_Painter");
		radioGroup.add(Rbuttonsmall);
		radioGroup.add(Rbuttonmiddum);
		radioGroup.add(Rbuttonbig);

		Toolpanel.setLayout(new GridLayout(0,1));
		Toolpanel.add(label1);
		Toolpanel.add(comboBox);
		Toolpanel.add(label2);
		Toolpanel.add(Rbuttonsmall);
		Toolpanel.add(Rbuttonmiddum);
		Toolpanel.add(Rbuttonbig);
		Toolpanel.add(fullcheck);
		Toolpanel.add(forground);
		Toolpanel.add(background);
		Toolpanel.add(clean);
		Toolpanel.add(save);
		Toolpanel.add(load);
		Toolpanel.add(undo);
		
		setLayout(new BorderLayout());
		add(Toolpanel,borderlayout.WEST);
		add(stateslabel,borderlayout.SOUTH);
		add(drawpanel,borderlayout.CENTER);
		
		comboBox.addActionListener(handler);
		Rbuttonsmall.addActionListener(handler);
		Rbuttonmiddum.addActionListener(handler);
		Rbuttonbig.addActionListener(handler);
		fullcheck.addActionListener(handler);
		forground.addActionListener(handler);
		background.addActionListener(handler);
		clean.addActionListener(handler);
		save.addActionListener(handler);
		load.addActionListener(handler);
		undo.addActionListener(handler);
		drawpanel.addMouseMotionListener(mousehandler);	
		drawpanel.setBackground(Color.WHITE);
	}
	private class Handler implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			if(event.getSource() == comboBox)
			{
				drawpanel.function = comboBox.getSelectedIndex();
			}
			else if(event.getSource() == Rbuttonsmall)
			{
				drawpanel.size = 4;
			}
			else if(event.getSource() == Rbuttonmiddum)
			{
				drawpanel.size = 8;
			}
			else if(event.getSource() == Rbuttonbig)
			{
				drawpanel.size = 12;
			}
			else if(event.getSource() == fullcheck)
			{
				if(fullcheck.isSelected())
				{
					drawpanel.full = true;
				}
				else
				{
					drawpanel.full = false;
				}
			}
			else if(event.getSource() == forground)
			{
				ColorChooce = JColorChooser.showDialog(null, "Choose a color", Color.GRAY);
				drawpanel.ColorChooce = ColorChooce;
				forground.setBackground(ColorChooce);
			}
			else if(event.getSource() == background)
			{
				BackColorChooce = JColorChooser.showDialog(null,"Choose a color", Color.GRAY);
				drawpanel.BackColorChooce = BackColorChooce;
				background.setBackground(BackColorChooce);
				drawpanel.setBackground(BackColorChooce);
				drawpanel.draw = true;
				drawpanel.repaint();
			}
			else if(event.getSource() == clean)
			{
				drawpanel.image = null;
				drawpanel.image = new BufferedImage(800,700,BufferedImage.TYPE_4BYTE_ABGR_PRE);
				background.setBackground(Color.WHITE);
				drawpanel.setBackground(Color.WHITE);
				drawpanel.x1 = 0;
				drawpanel.y1 = 0;
				drawpanel.x2 = 0;
				drawpanel.y2 = 0;
				drawpanel.imageList.add(new BufferedImage(800,700,BufferedImage.TYPE_4BYTE_ABGR));
				drawpanel.draw = true;
				drawpanel.repaint();
				
			}
			else if(event.getSource() == save)
			{
				OutputPic();
				
				try
				{
					BufferedImage IOimage = new BufferedImage(drawpanel.getWidth(),drawpanel.getHeight(),BufferedImage.TYPE_4BYTE_ABGR);
					Graphics gIO = IOimage.createGraphics();
					drawpanel.paint(gIO);
					if(storefile != null)
					{
						ImageIO.write(IOimage,"png",storefile);
					}
					
				}
				catch(IOException ioException)
				{
					System.err.println("Error");
				}
			}
			else if(event.getSource() == load)
			{
				if(drawpanel.g2D == null)
				{
					System.out.print("null\n");
				}
				
				InputPic();
				
				try
				{
					if(filename != null)
					{
						input = ImageIO.read(filename);
					}
					
				}
				catch(IOException ioException)
				{
					System.err.println("Error");
				}
				
				drawpanel.g2D.drawImage(input,0,0,null);
				drawpanel.draw = true;
				drawpanel.repaint();
			}
			else if(event.getSource() == undo)
			{
				if(drawpanel.imageList.isEmpty() == false)
				{
					drawpanel.image = drawpanel.imageList.get(drawpanel.imageList.size()-1);
					drawpanel.imageList.remove(drawpanel.imageList.size()-1);
				}
				drawpanel.stepback = true;
				drawpanel.draw = true;
				drawpanel.repaint();
			}
		}
	}
	
	public void InputPic()
	{
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.showOpenDialog(this);
		filename=fileChooser.getSelectedFile();
	}
	public void OutputPic()
	{
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.showSaveDialog(this);
		storefile = fileChooser.getSelectedFile();//connect selectFile to storefile		
	}
	
	private class MouseMotionAdapter extends MouseAdapter
	{
		public void mouseMoved(MouseEvent event)
		{
			stateslabel.setText(String.format("Mouse at [%d,%d]",event.getX(),event.getY()));
		}
	}
}
