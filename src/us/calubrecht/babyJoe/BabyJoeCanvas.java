package us.calubrecht.babyJoe;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

//import us.calubrecht.util.Queue;

import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.universe.*;

public class BabyJoeCanvas extends Canvas3D
{
	Random r = new Random();
	BranchGroup root_;
	BranchGroup letterRoot_;
	boolean forceUpperCase_;
	int numberOfLetters_ = 0;

	static final double ZSIZE = .2;
	static final int QUEUE_SIZE = 15;

	public BabyJoeCanvas()
	{
		super(SimpleUniverse.getPreferredConfiguration());
		createSceneGraph();
		root_.compile();
		SimpleUniverse simpleU = new SimpleUniverse(this);
		simpleU.getViewingPlatform().setNominalViewingTransform();
		simpleU.addBranchGraph(root_);

	}

	public void setUpperCase(boolean upper)
	{
		forceUpperCase_ = upper;
	}

	private void createSceneGraph()
	{
		root_ = new BranchGroup();

		addLight(root_);

		ListenKeys behave = new ListenKeys();
		behave.setSchedulingBounds(new BoundingSphere());
		root_.addChild(behave);
		letterRoot_ = new BranchGroup();
		root_.addChild(letterRoot_);
		letterRoot_.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		letterRoot_.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
	}

	void addLight(BranchGroup root)
	{
		AmbientLight lightA = new AmbientLight();
		lightA.setInfluencingBounds(new BoundingSphere(new Point3d(0, 0, 0), 20));
		root.addChild(lightA);
		PointLight lightP = new PointLight();
		lightP.setInfluencingBounds(new BoundingSphere(new Point3d(0, 0, 0), 20));
		Transform3D translate = new Transform3D();
		translate.setTranslation(new Vector3d(0, 0, 2));
		TransformGroup positionLight = new TransformGroup(translate);
		positionLight.addChild(lightP);
		root.addChild(positionLight);
	}

	Group get3DText(String text)
	{
		Transform3D resizeLetter = new Transform3D();
		resizeLetter.setScale(new Vector3d(.2, .2, .5));
		TransformGroup group = new TransformGroup(resizeLetter);

		Font3D font = new Font3D(new Font("Helvetica", Font.BOLD, 1),
				new FontExtrusion());
		Shape3D shape = new Shape3D(new Text3D(font, text,
				new Point3f(0, 0, 0), Text3D.ALIGN_CENTER, Text3D.PATH_RIGHT),
				createAppearance(getRColor()));
		group.addChild(shape);
		return group;
	}

	int nextColor = 0;

	Color3f getRColor()
	{
		float scale = .9f;
		Color3f[] colors = { new Color3f(scale, 0, 0),
				new Color3f(0, scale, 0), new Color3f(0, 0, scale),
				new Color3f(scale, scale, 0), new Color3f(0, scale, scale),
				new Color3f(scale, 0, scale),
				new Color3f(scale, 0.5f * scale, 0f),
				new Color3f(1 * scale, 0, .5f * scale),
				new Color3f(.5f * scale, scale, 0),
				new Color3f(.5f * scale, 0, scale),
				new Color3f(0, scale, .5f * scale),
				new Color3f(0, .5f * scale, scale) };
		Color3f ret = colors[nextColor];
		nextColor++;
		if (nextColor >= colors.length)
		{
			nextColor = 0;
		}
		return ret;
	}

	Appearance createAppearance(Color3f color)
	{
		Appearance appear = new Appearance();
		Material material = new Material();
		material.setDiffuseColor(color);

		appear.setMaterial(material);

		return appear;
	}

	public void addLetter(char keyChar)
	{
		if (Character.isWhitespace(keyChar)
				|| Character.isIdentifierIgnorable(keyChar))
		{
			return;
		}
		if (keyChar == 65535 /*Alt*/) //KeyEvent.VK_ALT)
		{
			return;
		}
		if (forceUpperCase_ && Character.isLetter(keyChar)
				&& Character.isLowerCase(keyChar))
		{
			keyChar = Character.toUpperCase(keyChar);
		}
		Dimension d = getSize();
		double x = (r.nextDouble() * 2 - 1) * .85;
		double y = (r.nextDouble() * 2 - 1) * .75 * d.height / d.width;

		double z = r.nextDouble() * ZSIZE - ZSIZE / 2;

		Transform3D translateLetter = new Transform3D();
		translateLetter.setTranslation(new Vector3d(x, y, z));
		TransformGroup positionLetter = new TransformGroup(translateLetter);

		positionLetter.addChild(get3DText(Character.toString(keyChar)));
		BranchGroup branch = new BranchGroup();
		branch.addChild(positionLetter);
		branch.setCapability(BranchGroup.ALLOW_DETACH);
		letterRoot_.addChild(branch);
		numberOfLetters_++;
		if (numberOfLetters_ >= QUEUE_SIZE)
		{
			// We're full
			letterRoot_.removeChild(0);
		}

	}

	public class ListenKeys extends Behavior
	{
		public void initialize()
		{
			this.wakeupOn(new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED));
		}

		public void processStimulus(@SuppressWarnings("rawtypes") Enumeration criteria)
		{
			try
			{
				while (criteria.hasMoreElements())
				{
					WakeupOnAWTEvent wakeEvent = (WakeupOnAWTEvent) criteria
							.nextElement();
					for (AWTEvent eAWT : wakeEvent.getAWTEvent())
					{
						KeyEvent kEvent = (KeyEvent) eAWT;
						addLetter(kEvent.getKeyChar());
					}
				}
			} finally
			{
				this.wakeupOn(new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED));
			}
		}
	}
}
